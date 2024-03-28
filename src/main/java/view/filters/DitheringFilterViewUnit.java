package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.EgorFloydDither;
import model.filter.boch.EgorOrderedDither;
import model.filter.darya.MyFloydDithering;
import model.filter.darya.MyOrderedDithering;
import model.filter.eric.EricOrderedDither;
import model.filter.eric.FloydSteinbergDSFilter;
import model.filter.leonid.FSDithering;
import model.filter.leonid.OrderedDithering;
import model.filter.mikhail.MikhailFloydDither;
import model.filter.mikhail.MikhailOrderedDither;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

//5 min Bochkarev

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

    public DitheringFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Floyd-Steinberg Dithering", "Apply Floyd-Steinberg dithering", "icons/ditherIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int redQuantizationRank = options.redRank().value();
        int greenQuantizationRank = options.greenRank().value();
        int blueQuantizationRank = options.blueRank().value();
        DitheringMethods ditheringMethods = options.ditheringMethods().value();
        DitheringPerson ditheringPerson = options.ditheringPerson().value();

        //switch does not work here because enum is not constant (?)
        if(ditheringPerson == DitheringPerson.LEONID)
        {
            if (ditheringMethods == DitheringMethods.FLOYD_STEINBERG) {
                FSDithering filter = new FSDithering(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
            else {
                OrderedDithering filter = new OrderedDithering(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
        }
        else if(ditheringPerson == DitheringPerson.DASHA)
        {
            int[] kv = {redQuantizationRank, greenQuantizationRank, blueQuantizationRank};
            if (ditheringMethods == DitheringMethods.FLOYD_STEINBERG) {
                MyFloydDithering filter = new MyFloydDithering(kv);
                applyFilters.accept(List.of(filter));
            }
            else {
                MyOrderedDithering filter = new MyOrderedDithering(kv);
                applyFilters.accept(List.of(filter));
            }
        }

        else if(ditheringPerson == DitheringPerson.MIHAIL)
        {
            if (ditheringMethods == DitheringMethods.FLOYD_STEINBERG) {
                MikhailFloydDither filter = new MikhailFloydDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
            else {
                MikhailOrderedDither filter = new MikhailOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
        }

        else if(ditheringPerson == DitheringPerson.ERIC)
        {
            if (ditheringMethods == DitheringMethods.FLOYD_STEINBERG) {
                FloydSteinbergDSFilter filter = new FloydSteinbergDSFilter(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
            else {
                EricOrderedDither filter = new EricOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
        }

        else if(ditheringPerson == DitheringPerson.EGOR)
        {
            if (ditheringMethods == DitheringMethods.FLOYD_STEINBERG) {
                EgorFloydDither filter = new EgorFloydDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
            else {
                EgorOrderedDither filter = new EgorOrderedDither(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);
                applyFilters.accept(List.of(filter));
            }
        }

    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.redRank(), options.greenRank(), options.blueRank(), options.ditheringMethods(), options.ditheringPerson());
    }
}
