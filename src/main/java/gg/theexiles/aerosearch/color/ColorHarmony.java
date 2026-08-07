// ? Project: ExSearch
// ? File: ColorHarmony.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch
// ? Description: Color-theory ranking for monochromatic, analogous, complementary, split, triadic, tetradic, neutral, and accent palettes.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:55 EDT

package gg.theexiles.aerosearch.color;

import gg.theexiles.aerosearch.model.SearchDocument;
import gg.theexiles.aerosearch.query.HarmonyMode;

public final class ColorHarmony {
    private ColorHarmony() {}

    public static double score(SearchDocument seed, SearchDocument candidate, HarmonyMode requestedMode) {
        if (seed.id().equals(candidate.id())) return -100.0;

        double[] seedHsv = ColorMath.hsv(seed.color().primaryRgb());
        double[] candidateHsv = ColorMath.hsv(candidate.color().primaryRgb());
        HarmonyMode mode = resolveMode(requestedMode, seedHsv[1]);

        double hueScore = hueScore(seedHsv[0], candidateHsv[0], mode);
        double saturationScore = saturationScore(seedHsv[1], candidateHsv[1], mode);
        double lightnessScore = lightnessScore(seed.color().lightness(), candidate.color().lightness(), mode);
        double textureFit = semanticTextureFit(seed, candidate);

        // ! Hue relationship is primary, but perceptual usability prevents technically-correct ugly matches.
        return hueScore * .58 + saturationScore * .12 + lightnessScore * .16 + textureFit * .14;
    }

    public static HarmonyMode resolveMode(HarmonyMode requested, double seedSaturation) {
        if (requested != HarmonyMode.AUTO) return requested;
        // ? Low-saturation blocks have unreliable hue; neutrals/supporting contrast are more useful.
        return seedSaturation < .16 ? HarmonyMode.NEUTRAL : HarmonyMode.COMPLEMENTARY;
    }

    private static double hueScore(double seedHue, double candidateHue, HarmonyMode mode) {
        return switch (mode) {
            case MONOCHROMATIC -> ColorMath.closeness(candidateHue, seedHue, 24);
            case ANALOGOUS -> best(candidateHue, 34, seedHue - 30, seedHue + 30, seedHue);
            case COMPLEMENTARY -> best(candidateHue, 30, seedHue + 180);
            case SPLIT_COMPLEMENTARY -> best(candidateHue, 30, seedHue + 150, seedHue + 210);
            case TRIADIC -> best(candidateHue, 30, seedHue + 120, seedHue + 240);
            case TETRADIC -> best(candidateHue, 28, seedHue + 60, seedHue + 180, seedHue + 240);
            case NEUTRAL -> 1.0;
            case ACCENT -> best(candidateHue, 42, seedHue + 180, seedHue + 120, seedHue + 240);
            case AUTO -> 0.0;
        };
    }

    private static double saturationScore(double seed, double candidate, HarmonyMode mode) {
        return switch (mode) {
            case NEUTRAL -> clamp01(1.0 - candidate * 1.25);
            case ACCENT -> clamp01(candidate * 1.15);
            case MONOCHROMATIC, ANALOGOUS -> clamp01(1.0 - Math.abs(seed - candidate));
            default -> clamp01(.55 + candidate * .45);
        };
    }

    private static double lightnessScore(double seed, double candidate, HarmonyMode mode) {
        double delta = Math.abs(seed - candidate);
        return switch (mode) {
            case MONOCHROMATIC -> clamp01(.45 + Math.min(delta, 42) / 52.0);
            case NEUTRAL -> clamp01(1.0 - Math.abs(delta - 22.0) / 55.0);
            case ACCENT, COMPLEMENTARY, SPLIT_COMPLEMENTARY, TRIADIC, TETRADIC -> clamp01(.5 + Math.min(delta, 38) / 76.0);
            case ANALOGOUS -> clamp01(1.0 - delta / 70.0);
            case AUTO -> .5;
        };
    }

    private static double semanticTextureFit(SearchDocument seed, SearchDocument candidate) {
        double fit = .35;
        if (seed.styles().stream().anyMatch(candidate.styles()::contains)) fit += .25;
        if (seed.materials().stream().anyMatch(candidate.materials()::contains)) fit += .12;
        if (seed.fullBlock() == candidate.fullBlock()) fit += .12;
        if (seed.emissive() != candidate.emissive()) fit += .08;
        if (seed.transparent() == candidate.transparent()) fit += .08;
        return clamp01(fit);
    }

    private static double best(double actual, double tolerance, double... targets) {
        double best = 0;
        for (double target : targets) {
            best = Math.max(best, ColorMath.closeness(actual, ColorMath.normalizeHue(target), tolerance));
        }
        return best;
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
