// ? Project: ExSearch
// ? File: SearchEngine.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch
// ? Description: Filter evaluation, fuzzy ranking, semantic search, similarity, palette, and color-theory harmony scoring.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:55 EDT

package gg.theexiles.aerosearch.search;

import gg.theexiles.aerosearch.color.ColorHarmony;
import gg.theexiles.aerosearch.color.ColorMath;
import gg.theexiles.aerosearch.index.SearchIndex;
import gg.theexiles.aerosearch.model.SearchDocument;
import gg.theexiles.aerosearch.query.Query;
import gg.theexiles.aerosearch.query.QueryParser;
import java.util.*;

public final class SearchEngine {
    public static final int RESULT_CAP = 5000;
    private SearchEngine() {}

    public static List<SearchDocument> search(String raw) {
        Query q = QueryParser.parse(raw);
        List<SearchDocument> docs = SearchIndex.documents();
        if (raw == null || raw.isBlank()) return docs;
        Map<String, SearchDocument> byId = new HashMap<>();
        for (SearchDocument d : docs) byId.put(d.id(), d);
        SearchDocument similaritySeed = q.similarTo == null ? null : byId.get(q.similarTo);
        SearchDocument harmonySeed = q.harmonySeed == null ? null : byId.get(q.harmonySeed);
        List<SearchDocument> paletteSeeds = q.paletteSeeds.stream().map(byId::get).filter(Objects::nonNull).toList();
        List<Scored> scored = new ArrayList<>();
        for (SearchDocument d : docs) {
            if (!matchesAll(d, q.terms) || matchesAny(d, q.excluded) || !roleMatches(d, q.harmonyRole)) continue;
            double score = baseScore(d, q.terms) * d.rankBoost();
            if (similaritySeed != null) score += similarityScore(similaritySeed, d) * 3.0;
            if (!paletteSeeds.isEmpty()) score += paletteScore(paletteSeeds, d) * 2.4;
            if (harmonySeed != null) score += ColorHarmony.score(harmonySeed, d, q.harmonyMode) * 4.0;
            score += LearningStore.boost(raw, d.id());
            scored.add(new Scored(d, score));
        }
        scored.sort(Comparator.comparingDouble(Scored::score).reversed().thenComparing(x -> x.doc().displayName(), String.CASE_INSENSITIVE_ORDER));
        return scored.stream().limit(RESULT_CAP).map(Scored::doc).toList();
    }

    private static boolean matchesAll(SearchDocument d, List<Query.Term> terms) { for (Query.Term t : terms) if (!matches(d,t)) return false; return true; }
    private static boolean matchesAny(SearchDocument d, List<Query.Term> terms) { for (Query.Term t : terms) if (matches(d,t)) return true; return false; }
    private static boolean matches(SearchDocument d, Query.Term t) {
        String v=t.value().toLowerCase(Locale.ROOT);
        return switch(t.type()) {
            case TEXT -> textScore(d,v)>=.45;
            case MOD -> contains(d.namespace(),v)||contains(d.modName(),v);
            case TAG -> d.tags().stream().anyMatch(x->contains(x,v));
            case TOOLTIP -> d.tooltips().stream().anyMatch(x->contains(x,v));
            case ID -> contains(d.id(),v);
            case NUMERIC -> switch(t.key()) { case "light" -> t.comparison().test(d.light()); case "hardness" -> t.comparison().test(d.hardness()); case "blast" -> t.comparison().test(d.blastResistance()); default -> false; };
            case FILTER -> filter(d,t.key(),v);
        };
    }
    private static boolean filter(SearchDocument d,String k,String v) {
        return switch(k) {
            case "mod" -> contains(d.namespace(),v)||contains(d.modName(),v);
            case "tag" -> d.tags().stream().anyMatch(x->contains(x,v));
            case "tooltip" -> d.tooltips().stream().anyMatch(x->contains(x,v));
            case "id" -> contains(d.id(),v);
            case "material" -> setContains(d.materials(),v);
            case "shape" -> setContains(d.shapes(),v);
            case "use" -> setContains(d.uses(),v);
            case "style" -> setContains(d.styles(),v);
            case "color" -> colorMatches(d,v);
            case "transparent" -> bool(v,d.transparent());
            case "waterloggable" -> bool(v,d.waterloggable());
            case "redstone" -> bool(v,d.redstone());
            case "fullblock" -> bool(v,d.fullBlock());
            case "slab" -> bool(v,d.slab());
            case "stairs" -> bool(v,d.stairs());
            case "wall" -> bool(v,d.wall());
            case "animated" -> bool(v,d.animated());
            case "emissive" -> bool(v,d.emissive());
            case "source" -> v.equals("vanilla")?d.namespace().equals("minecraft"):v.equals("modded")?!d.namespace().equals("minecraft"):contains(d.namespace(),v);
            default -> textScore(d,k+" "+v)>=.52;
        };
    }
    private static boolean roleMatches(SearchDocument d,String role) {
        if(role==null||role.isBlank()||role.equals("any")) return true;
        return switch(role) {
            case "accent" -> d.emissive()||d.uses().contains("trim")||d.color().saturation()>=.45;
            case "light","lighting" -> d.emissive()||d.light()>0||d.uses().contains("light");
            case "wall","floor","trim","storage","machine" -> d.uses().contains(role);
            case "panel" -> d.shapes().contains("panel")||d.uses().contains("wall");
            case "glass","window" -> d.materials().contains("glass")||d.transparent();
            default -> d.uses().contains(role)||d.shapes().contains(role)||d.materials().contains(role);
        };
    }
    private static boolean colorMatches(SearchDocument d,String v) { try { int target=ColorMath.parseColor(v); return target>=0&&Math.min(ColorMath.distance(target,d.color().primaryRgb()),ColorMath.distance(target,d.color().secondaryRgb()))<=28; } catch(RuntimeException e){ return false; } }
    private static double baseScore(SearchDocument d,List<Query.Term> terms) { if(terms.isEmpty()) return 1; double s=0; for(Query.Term t:terms) s+=t.type()==Query.Term.Type.TEXT?textScore(d,t.value()):1; return s/terms.size(); }
    private static double textScore(SearchDocument d,String q) { double b=Math.max(Fuzzy.score(q,d.displayName()),Fuzzy.score(q,d.path())); b=Math.max(b,Fuzzy.score(q,d.id())*.95); b=Math.max(b,Fuzzy.score(q,d.modName())*.72); for(String x:d.keywords())b=Math.max(b,Fuzzy.score(q,x)*.92); for(String x:d.materials())b=Math.max(b,Fuzzy.score(q,x)*.86); for(String x:d.shapes())b=Math.max(b,Fuzzy.score(q,x)*.86); for(String x:d.uses())b=Math.max(b,Fuzzy.score(q,x)*.86); for(String x:d.styles())b=Math.max(b,Fuzzy.score(q,x)*.86); return b; }
    private static double similarityScore(SearchDocument a,SearchDocument b) { if(a.id().equals(b.id()))return-100; double c=1-Math.min(1,Math.min(ColorMath.distance(a.color().primaryRgb(),b.color().primaryRgb()),ColorMath.distance(a.color().primaryRgb(),b.color().secondaryRgb()))/70); double sem=jaccard(union(a.materials(),a.shapes(),a.uses(),a.styles()),union(b.materials(),b.shapes(),b.uses(),b.styles())); double p=(a.fullBlock()==b.fullBlock()?.2:0)+(a.transparent()==b.transparent()?.1:0)+(a.emissive()==b.emissive()?.1:0); return c*.58+sem*.32+p; }
    private static double paletteScore(List<SearchDocument> seeds,SearchDocument c) { if(seeds.stream().anyMatch(x->x.id().equals(c.id())))return-100; double total=0; for(SearchDocument s:seeds){ double dist=ColorMath.distance(s.color().primaryRgb(),c.color().primaryRgb()), color=dist<18?.8:dist>45&&dist<85?1:.45; total+=color*.45+jaccard(s.styles(),c.styles())*.25+jaccard(s.uses(),c.uses())*.15+jaccard(s.materials(),c.materials())*.15; } return total/Math.max(1,seeds.size()); }
    private static boolean setContains(Set<String>s,String q){return s.stream().anyMatch(x->contains(x,q));}
    private static boolean contains(String v,String q){return v!=null&&v.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT));}
    private static boolean bool(String r,boolean v){return switch(r){case "true","yes","1","on"->v;case "false","no","0","off"->!v;default->false;};}
    @SafeVarargs private static Set<String> union(Set<String>...sets){Set<String>o=new HashSet<>();for(Set<String>s:sets)o.addAll(s);return o;}
    private static double jaccard(Set<String>a,Set<String>b){if(a.isEmpty()&&b.isEmpty())return 0;Set<String>i=new HashSet<>(a);i.retainAll(b);Set<String>u=new HashSet<>(a);u.addAll(b);return i.size()/(double)Math.max(1,u.size());}
    private record Scored(SearchDocument doc,double score){}
}
