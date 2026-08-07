// ? Project: Aero Search
// ? File: QueryParser.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/query
// ? Description: JEI-style plus builder semantic query parser.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QueryParser {
    private static final List<String> NUMERIC = List.of("light", "hardness", "blast");
    private QueryParser() {}

    public static Query parse(String input) {
        Query q = new Query();
        for (String token : tokenize(input == null ? "" : input)) {
            boolean negative = token.startsWith("-") && token.length() > 1;
            String raw = negative ? token.substring(1) : token;
            String lower = raw.toLowerCase(Locale.ROOT);
            if (lower.startsWith("similar:")) { q.similarTo = raw.substring("similar:".length()); continue; }
            if (lower.startsWith("palette:")) {
                for (String id : raw.substring("palette:".length()).split(",")) if (!id.isBlank()) q.paletteSeeds.add(id.trim());
                continue;
            }
            Query.Term term;
            if (raw.startsWith("@")) term = new Query.Term(Query.Term.Type.MOD, "mod", raw.substring(1), null);
            else if (raw.startsWith("#")) term = new Query.Term(Query.Term.Type.TAG, "tag", raw.substring(1), null);
            else if (raw.startsWith("$")) term = new Query.Term(Query.Term.Type.TOOLTIP, "tooltip", raw.substring(1), null);
            else if (raw.startsWith("&")) term = new Query.Term(Query.Term.Type.ID, "id", raw.substring(1), null);
            else if (raw.contains(":")) {
                int split = raw.indexOf(':');
                String key = lower.substring(0, split);
                String value = raw.substring(split + 1);
                if (NUMERIC.contains(key)) {
                    try { term = new Query.Term(Query.Term.Type.NUMERIC, key, value, Comparison.parse(value)); }
                    catch (NumberFormatException ex) { term = new Query.Term(Query.Term.Type.TEXT, "", raw, null); }
                } else term = new Query.Term(Query.Term.Type.FILTER, key, value, null);
            } else term = new Query.Term(Query.Term.Type.TEXT, "", raw, null);
            (negative ? q.excluded : q.terms).add(term);
        }
        return q;
    }

    static List<String> tokenize(String input) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"') { quoted = !quoted; continue; }
            if (Character.isWhitespace(c) && !quoted) {
                if (!current.isEmpty()) { out.add(current.toString()); current.setLength(0); }
            } else current.append(c);
        }
        if (!current.isEmpty()) out.add(current.toString());
        return out;
    }
}
