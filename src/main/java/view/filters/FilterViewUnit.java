package view.filters;

import core.filter.Filter;
import core.options.Setting;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public abstract class FilterViewUnit {
    protected final String filterName;

    protected final String tipText;

    protected final String iconPath;
    protected final Consumer<List<Filter>> applyFilters;

    protected FilterViewUnit(String filterName,
                             String tipText,
                             String iconPath,
                             Consumer<List<Filter>> applyFilters) {
        this.filterName = filterName;
        this.tipText = tipText;
        this.iconPath = iconPath;
        this.applyFilters = applyFilters;
    }

    abstract public void applyFilter(BufferedImage image);

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
}
