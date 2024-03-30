package view.filters;

import core.filter.Filter;
import core.filter.FilterExecutor;
import core.filter.Image;
import core.options.Setting;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class FilterViewUnit {
    protected final String filterName;
    protected final String tipText;
    protected final String iconPath;
    protected final Consumer<Float> progressFilterListener;

    protected FilterViewUnit(String filterName,
                             String tipText,
                             String iconPath,
                             Consumer<Float> progressFilterListener) {
        this.filterName = filterName;
        this.tipText = tipText;
        this.iconPath = iconPath;
        this.progressFilterListener = progressFilterListener;
    }

    abstract public CompletableFuture<BufferedImage> applyFilter(BufferedImage image);

    @Nullable
    abstract public List<Setting<?>> getSettings();

    public String getFilterName() {
        return filterName;
    }

    public String getTipText() {
        return tipText;
    }

    public String getIconPath() {
        return iconPath;
    }

    protected CompletableFuture<BufferedImage> applyFilters(BufferedImage image, List<Filter> filters) {
//        boolean containsFitFilter = filters.stream()
//                .anyMatch(filter -> filter instanceof ResamplingFilter);
//        FilterExecutor.Builder builder;
//        if (containsFitFilter) {
//            builder = FilterExecutor.of(Image.of(applicationContext.imageHolder().getCurrentImage()));
//        } else {
//            builder = FilterExecutor.of(Image.of(applicationContext.imageHolder().getOriginalImage()));
//        }

        FilterExecutor.Builder builder = FilterExecutor.of(Image.of(image));


        for (Filter filter : filters) {
            builder = builder.with(filter);
        }
        return builder.progress(progressFilterListener)
                .process();
    }
}
