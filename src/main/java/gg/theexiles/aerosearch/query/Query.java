// ? Project: ExSearch
// ? File: Query.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch
// ? Description: Parsed query model including similarity, palette, and color-theory harmony modes.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:55 EDT

package gg.theexiles.aerosearch.query;

import java.util.ArrayList;
import java.util.List;

public final class Query {
    public final List<Term> terms = new ArrayList<>();
    public final List<Term> excluded = new ArrayList<>();
    public String similarTo;
    public final List<String> paletteSeeds = new ArrayList<>();
    public String harmonySeed;
    public HarmonyMode harmonyMode = HarmonyMode.AUTO;
    public String harmonyRole;

    public record Term(Type type, String key, String value, Comparison comparison) {
        public enum Type { TEXT, MOD, TAG, TOOLTIP, ID, FILTER, NUMERIC }
    }
}
