package core.filter;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FilterExecutor {
    static class Progress {
        private final AtomicInteger cap;
        private final AtomicReference<Float> accumulator;
        private final Consumer<Float> listener;

        public Progress(int capacity, Consumer<Float> listener) {
            this.cap = new AtomicInteger(capacity);
            this.listener = listener;
            this.accumulator = new AtomicReference<>(0f);
        }

        void submit(float chunk) {
            float progress = accumulator.accumulateAndGet(chunk, Float::sum);
            listener.accept(progress);
        }


        private static class SharedProgress {
            private final float capacity;
            private final Consumer<Float> consumer;

            SharedProgress(int capacity, Consumer<Float> listener) {
                this.capacity = capacity;
                this.consumer = listener;
            }

            void submit() {
                float chunk = 1 / capacity;
                consumer.accept(chunk);
            }
        }

        SharedProgress share(int capacity) {
            final var get = cap.decrementAndGet();
            if (get < 0) {
                throw new IllegalStateException("it is not possible to request more shares");
            }
            return new SharedProgress(capacity, this::submit);
        }
    }

    public static BufferedImage run(BufferedImage image, Filter filter) throws ExecutionException, InterruptedException {
        final var future = of(image).with(filter).process();
        return future.get();
    }

    public static Builder of(BufferedImage image) {
        return new Builder(image);
    }

    static public class Builder {
        private final BufferedImage image;
        private final Collection<Filter> filters;
        private ExecutorService executorService;
        private int numberOfThreads = 0;
        private Consumer<Float> listener = null;

        private Builder(BufferedImage image) {
            Objects.requireNonNull(image, "image must not be null");
            this.image = image;
            filters = new ArrayList<>();
        }

        public Builder with(Filter filter) {
            Objects.requireNonNull(filter, "filter must not be null");
            this.filters.add(filter);
            return this;
        }

        public Builder executor(ExecutorService executorService) {
            Objects.requireNonNull(executorService, "executor service must not be null");
            this.executorService = executorService;
            return this;
        }

        public Builder progress(Consumer<Float> listener) {
            Objects.requireNonNull(listener, "progress listener must not be null");
            this.listener = listener;
            return this;
        }

        public Builder threads(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("number of threads must be positive");
            }
            this.numberOfThreads = n;
            return this;
        }

        public CompletableFuture<BufferedImage> process() {
            if (numberOfThreads <= 0) {
                numberOfThreads = Runtime.getRuntime().availableProcessors();
            }

            if (executorService == null) {
                executorService = Executors.newFixedThreadPool(numberOfThreads);
            }

            return buildPipeline();
        }

        private CompletableFuture<BufferedImage> buildPipeline() {
            CompletableFuture<BufferedImage> future = CompletableFuture.completedFuture(image);

            var progress = listener == null ? null : new Progress(filters.size(), listener);

            for (Filter filter : filters) {
                future = future.thenCompose(image -> processFilter(image, filter, numberOfThreads, executorService, progress));
            }

            return future;
        }

        private static CompletableFuture<BufferedImage> processFilter(BufferedImage image, Filter filter, int numberOfThreads, ExecutorService executorService, @Nullable Progress progress) {
            if (filter instanceof MatrixFilter matrixFilter) {
                return processMatrixFilter(image, matrixFilter, numberOfThreads, executorService, progress);
            }

            if (filter instanceof CustomFilter customFilter) {
                return processCustomFilter(image, customFilter, executorService);
            }

            throw new IllegalStateException("unsupported type of model.filter");
        }

        private static CompletableFuture<BufferedImage> processMatrixFilter(BufferedImage image, MatrixFilter filter, int numberOfThreads, ExecutorService executorService, @Nullable Progress progress) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Image wrappedOrigin = new Image(image);
            final var jobs = buildMatrixFilterJobs(wrappedOrigin, result, filter, numberOfThreads, progress);
            for (var job : jobs) {
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(job, executorService);
                futures.add(future);
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> {
                futures.forEach(CompletableFuture::join);
                return result;
            });
        }

        private static List<Supplier<Void>> buildMatrixFilterJobs(Image origin, BufferedImage result, MatrixFilter filter, int maxNumberOfThreads, @Nullable Progress progress) {
            final int batch = origin.height() / maxNumberOfThreads == 0 ? 1 : origin.height() / maxNumberOfThreads;
            final var sharedProgress = progress == null ? null : progress.share(maxNumberOfThreads * batch); // Может не дать в сумме 1 при неравномерной нагрузке на последний поток.
            List<Supplier<Void>> suppliers = new ArrayList<>();

            for (int i = 0; i < maxNumberOfThreads - 1; i++) {
                final var job = buildMatrixFilterJob(result, origin, filter, i * batch, (i + 1) * batch, sharedProgress);
                suppliers.add(job);
            }
            suppliers.add(
                    buildMatrixFilterJob(result, origin, filter, (maxNumberOfThreads - 1) * batch, origin.height(), sharedProgress)
            );
            return suppliers;
        }

        private static Supplier<Void> buildMatrixFilterJob(BufferedImage result, Image origin, MatrixFilter filter, int from, int to, @Nullable Progress.SharedProgress sharedProgress) {
            return () -> {
                for (int y = from; y < to; y++) {
                    for (int x = 0; x < origin.width(); x++) {
                        final var applied = filter.apply(origin, x, y);
                        result.setRGB(x, y, applied);
                    }
                    if (sharedProgress != null) {
                        sharedProgress.submit();
                    }
                }
                return null;
            };
        }

        private static CompletableFuture<BufferedImage> processCustomFilter(BufferedImage image, CustomFilter filter, ExecutorService executorService) {
            return CompletableFuture.supplyAsync(() -> {
                final var wrapper = new Image(image);
                return filter.apply(wrapper);
            }, executorService);
        }
    }
}
