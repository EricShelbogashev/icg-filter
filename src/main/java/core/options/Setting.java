package core.options;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Setting<T> {
    protected final String label;
    protected final String description;
    protected final List<Function<T, String>> restrictions;
    protected final Class<T> type;
    protected final String id;
    protected T value;

    public Setting(T defaultValue, String label, String description, List<Function<T, String>> restrictions, Class<T> type, String id) {
        this.label = label;
        this.description = description;
        this.restrictions = Collections.unmodifiableList(restrictions);
        this.value = defaultValue;
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public final String label() {
        return label;
    }

    public final String description() {
        return description;
    }

    @SuppressWarnings("unchecked")
    public final <U> U value() {
        return (U) value;
    }

    public final void value(Object value) {
        if (!validate(value).isEmpty()) {
            throw new IllegalArgumentException(
                    "The object '%s' you are trying to pass through is not permitted due to given restrictions '%s'".formatted(
                            value, restrictions
                    )
            );
        }
        this.value = type.cast(value);
    }

    public final Collection<String> validate(Object value) {
        if (!type.isInstance(value)) {
            return List.of("The object '%s' you are trying to pass through are not type '%s'".formatted(value, type.getName()));
        }

        final T cast = type.cast(value);
        return restrictions.stream()
                .map(it -> it.apply(cast))
                .filter(Objects::nonNull)
                .toList();
    }

    public Collection<String> putSafe(Object value) {
        final var validated = validate(value);
        if (!validated.isEmpty()) {
            return validated;
        }
        this.value = type.cast(value);
        return List.of();
    }

    public abstract JComponent createComponent();
}