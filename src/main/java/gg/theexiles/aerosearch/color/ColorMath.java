// ? Project: Aero Search
// ? File: ColorMath.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/color
// ? Description: Named/hex parsing and CIE Lab perceptual color distance.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.color;

import java.util.Locale;
import java.util.Map;

public final class ColorMath {
    private static final Map<String, Integer> NAMED = Map.ofEntries(
            Map.entry("black", 0x111111), Map.entry("white", 0xF2F2F2),
            Map.entry("gray", 0x7F7F7F), Map.entry("grey", 0x7F7F7F),
            Map.entry("lightgray", 0xB7B7B7), Map.entry("darkgray", 0x343434),
            Map.entry("red", 0xD52B2B), Map.entry("orange", 0xE87A22),
            Map.entry("yellow", 0xE5D64A), Map.entry("lime", 0x74D844),
            Map.entry("green", 0x3B8E48), Map.entry("cyan", 0x27C8D8),
            Map.entry("teal", 0x218C91), Map.entry("blue", 0x356BD8),
            Map.entry("purple", 0x7D4BC6), Map.entry("magenta", 0xD14BBA),
            Map.entry("pink", 0xE58FAE), Map.entry("brown", 0x78543A),
            Map.entry("brass", 0xB89A42), Map.entry("copper", 0xB66C43),
            Map.entry("steel", 0x88939A)
    );

    private ColorMath() {}

    public static int parseColor(String raw) {
        String s = raw.trim().toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        if (s.startsWith("#")) return Integer.parseInt(s.substring(1), 16) & 0xFFFFFF;
        return NAMED.getOrDefault(s, -1);
    }

    public static double distance(int rgbA, int rgbB) {
        double[] a = lab(rgbA);
        double[] b = lab(rgbB);
        double dl = a[0] - b[0], da = a[1] - b[1], db = a[2] - b[2];
        return Math.sqrt(dl * dl + da * da + db * db);
    }

    public static double[] lab(int rgb) {
        double r = ((rgb >> 16) & 255) / 255.0;
        double g = ((rgb >> 8) & 255) / 255.0;
        double b = (rgb & 255) / 255.0;
        r = pivotRgb(r); g = pivotRgb(g); b = pivotRgb(b);
        double x = (r * .4124 + g * .3576 + b * .1805) / .95047;
        double y = (r * .2126 + g * .7152 + b * .0722);
        double z = (r * .0193 + g * .1192 + b * .9505) / 1.08883;
        x = pivotXyz(x); y = pivotXyz(y); z = pivotXyz(z);
        return new double[]{116 * y - 16, 500 * (x - y), 200 * (y - z)};
    }

    private static double pivotRgb(double n) {
        return n > .04045 ? Math.pow((n + .055) / 1.055, 2.4) : n / 12.92;
    }

    private static double pivotXyz(double n) {
        return n > .008856 ? Math.cbrt(n) : (7.787 * n) + 16.0 / 116.0;
    }
}
