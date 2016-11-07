package com.sudicode.fb2gh.github;

/**
 * GitHub label.
 */
public final class GHLabel {

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

}
