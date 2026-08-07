// ? Project: ExSearch
// ? File: HarmonyMode.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch
// ? Description: Supported color-theory harmony modes and forgiving aliases.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:55 EDT

package gg.theexiles.aerosearch.query;

import java.util.Locale;

public enum HarmonyMode {
    AUTO,
    MONOCHROMATIC,
    ANALOGOUS,
    COMPLEMENTARY,
    SPLIT_COMPLEMENTARY,
    TRIADIC,
    TETRADIC,
    NEUTRAL,
    ACCENT;

    public static HarmonyMode parse(String raw) {
        if (raw == null || raw.isBlank()) return AUTO;
        return switch (raw.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "")) {
            case "auto", "smart", "best" -> AUTO;
            case "mono", "monochrome", "monochromatic" -> MONOCHROMATIC;
            case "analog", "analogous" -> ANALOGOUS;
            case "complement", "complementary" -> COMPLEMENTARY;
            case "split", "splitcomplement", "splitcomplementary" -> SPLIT_COMPLEMENTARY;
            case "triad", "triadic" -> TRIADIC;
            case "tetrad", "tetradic", "rectangle" -> TETRADIC;
            case "neutral", "neutrals", "support" -> NEUTRAL;
            case "accent", "accents", "contrast" -> ACCENT;
            default -> AUTO;
        };
    }
}
