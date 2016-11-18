package com.sudicode.fb2gh.github;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * GitHub label.
 */
public class GHLabel {

    /**
     * Hex code of the default label color.
     */
    private static final String DEFAULT_LABEL_COLOR = "ffffff";

    private final String name;
    private final String hexColor;

    /**
     * Create a label with the default label color.
     *
     * @param name The name of the label.
     */
    public GHLabel(final String name) {
        this(name, DEFAULT_LABEL_COLOR);
    }

    /**
     * Create a label with a specific label color.
     *
     * @param name     The name of the label.
     * @param hexColor A 6 character hex code, without the leading #, identifying the color.
     */
    public GHLabel(final String name, final String hexColor) {
        this.name = name;
        this.hexColor = hexColor;
    }

    /**
     * @return The name of this label.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The hex color of this label (without the leading #).
     */
    public String getHexColor() {
        return hexColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GHLabel ghLabel = (GHLabel) o;

        return new EqualsBuilder()
                .append(name.toLowerCase(), ghLabel.name.toLowerCase())
                .append(hexColor, ghLabel.hexColor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name.toLowerCase())
                .append(hexColor)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("hexColor", hexColor)
                .toString();
    }

}
