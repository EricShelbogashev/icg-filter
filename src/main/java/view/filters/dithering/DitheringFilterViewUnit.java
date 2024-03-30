package view.filters.dithering;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.EgorFloydDither;
import model.filter.boch.EgorOrderedDither;
import model.filter.darya.MyFloydDithering;
import model.filter.darya.MyOrderedDithering;
import model.filter.eric.EricFloydSteinbergDSFilter;
import model.filter.eric.EricOrderedDither;
import model.filter.leonid.FSDithering;
import model.filter.leonid.OrderedDithering;
import model.filter.mikhail.MikhailFloydDither;
import model.filter.mikhail.MikhailOrderedDither;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DitheringFilterViewUnit extends FilterViewUnit {

    private final DitheringSettings options = new DitheringSettings(
            OptionsFactory.settingInteger(
                    2,
                    "red quantization",
                    "red quantization rank",
                    2, 128
            ),
            OptionsFactory.settingInteger(
                    2,
                    "green quantization",
                    "green quantization rank",
                    2, 128
            ),
            OptionsFactory.settingInteger(
                    2,
                    "blue quantization",
                    "blue quantization rank",
                    2, 128
            ),
            OptionsFactory.settingEnum(
                    DitheringMethods.ORDERED,
                    "dithering method",
                    "choose dithering method",
                    DitheringMethods.class
            ),
            OptionsFactory.settingEnum(
                    DitheringPerson.LEONID,
                    "dithering person",
                    "choose dithering person",
                    DitheringPerson.class
            )
    );

    public DitheringFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Dithering", "Apply dithering", "icons/ditherIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int redQuantizationRank = options.redRank().value();
        int greenQuantizationRank = options.greenRank().value();
        int blueQuantizationRank = options.blueRank().value();
        DitheringMethods ditheringMethods = options.ditheringMethods().value();
        DitheringPerson ditheringPerson = options.ditheringPerson().value();

        switch (ditheringPerson) {
            case LEONID -> {
                switch (ditheringMethods) {
                    case FLOYD_STEINBERG -> {
                        FSDithering filter = new FSDithering(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                    case ORDERED -> {
                        OrderedDithering filter = new OrderedDithering(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                }
            }
            case DASHA -> {
                int[] kv = {redQuantizationRank, greenQuantizationRank, blueQuantizationRank};
                switch (ditheringMethods) {
                    case FLOYD_STEINBERG -> {
                        MyFloydDithering filter = new MyFloydDithering(kv);
                        return applyFilters(image, List.of(filter));
                    }
                    case ORDERED -> {
                        MyOrderedDithering filter = new MyOrderedDithering(kv);
                        return applyFilters(image, List.of(filter));
                    }
                }
            }
            case MIKHAIL -> {
                switch (ditheringMethods) {
                    case FLOYD_STEINBERG -> {
                        MikhailFloydDither filter = new MikhailFloydDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                    case ORDERED -> {
                        MikhailOrderedDither filter = new MikhailOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                }
            }
            case ERIC -> {
                switch (ditheringMethods) {
                    case FLOYD_STEINBERG -> {
                        EricFloydSteinbergDSFilter filter = new EricFloydSteinbergDSFilter(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                    case ORDERED -> {
                        EricOrderedDither filter = new EricOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                }
            }
            case EGOR -> {
                switch (ditheringMethods) {
                    case FLOYD_STEINBERG -> {
                        EgorFloydDither filter = new EgorFloydDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                    case ORDERED -> {
                        EgorOrderedDither filter = new EgorOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                        return applyFilters(image, List.of(filter));
                    }
                }
            }
        }
        throw new IllegalStateException("There are no dither settings.");
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.redRank(), options.greenRank(), options.blueRank(), options.ditheringMethods(), options.ditheringPerson());
    }
}
