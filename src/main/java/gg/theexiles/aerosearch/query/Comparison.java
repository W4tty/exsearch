// ? Project: Aero Search
// ? File: Comparison.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/query
// ? Description: Numeric comparison parser/evaluator.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.query;

public record Comparison(Op op, double value) {
    public enum Op { LT, LTE, EQ, GTE, GT }

    public boolean test(double candidate) {
        return switch (op) {
            case LT -> candidate < value;
            case LTE -> candidate <= value;
            case EQ -> Double.compare(candidate, value) == 0;
            case GTE -> candidate >= value;
            case GT -> candidate > value;
        };
    }

    public static Comparison parse(String raw) {
        String s = raw.trim();
        if (s.startsWith(">=")) return new Comparison(Op.GTE, Double.parseDouble(s.substring(2)));
        if (s.startsWith("<=")) return new Comparison(Op.LTE, Double.parseDouble(s.substring(2)));
        if (s.startsWith(">")) return new Comparison(Op.GT, Double.parseDouble(s.substring(1)));
        if (s.startsWith("<")) return new Comparison(Op.LT, Double.parseDouble(s.substring(1)));
        if (s.startsWith("=")) return new Comparison(Op.EQ, Double.parseDouble(s.substring(1)));
        return new Comparison(Op.EQ, Double.parseDouble(s));
    }
}
