// ? Project: Aero Search
// ? File: Fuzzy.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/search
// ? Description: Allocation-light fuzzy text scoring with typo tolerance.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.search;

import java.util.Locale;

public final class Fuzzy {
    private Fuzzy() {}
    public static double score(String query, String candidate) {
        if (query == null || query.isBlank() || candidate == null) return 0.0;
        String q = normalize(query), c = normalize(candidate);
        if (c.equals(q)) return 1.0;
        if (c.startsWith(q)) return 0.94;
        if (c.contains(q)) return 0.88;
        int qi = 0, gaps = 0;
        for (int ci = 0; ci < c.length() && qi < q.length(); ci++) {
            if (c.charAt(ci) == q.charAt(qi)) qi++; else if (qi > 0) gaps++;
        }
        double subsequence = qi == q.length() ? Math.max(0.45, 0.78 - gaps * 0.015) : 0.0;
        String target = c.length() > q.length() + 8 ? c.substring(0, Math.min(c.length(), q.length() + 8)) : c;
        int distance = levenshtein(q, target);
        double edit = 1.0 - distance / (double)Math.max(q.length(), Math.max(1, target.length()));
        return Math.max(subsequence, Math.max(0.0, edit * 0.72));
    }
    private static String normalize(String s) { return s.toLowerCase(Locale.ROOT).replace('_',' ').replace('-',' ').trim(); }
    static int levenshtein(String a, String b) {
        int[] prev = new int[b.length()+1], curr = new int[b.length()+1];
        for (int j=0;j<=b.length();j++) prev[j]=j;
        for (int i=1;i<=a.length();i++) {
            curr[0]=i;
            for (int j=1;j<=b.length();j++) {
                int cost=a.charAt(i-1)==b.charAt(j-1)?0:1;
                curr[j]=Math.min(Math.min(curr[j-1]+1,prev[j]+1),prev[j-1]+cost);
            }
            int[] t=prev; prev=curr; curr=t;
        }
        return prev[b.length()];
    }
}
