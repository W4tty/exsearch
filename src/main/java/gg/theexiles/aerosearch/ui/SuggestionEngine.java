// ? Project: Aero Search
// ? File: SuggestionEngine.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/ui
// ? Description: Prefix-aware autocomplete and filter-value suggestions.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.ui;
import gg.theexiles.aerosearch.index.SearchIndex; import java.util.*; import java.util.stream.Stream;
public final class SuggestionEngine { private static final List<String> FILTERS=List.of("color:","material:","shape:","use:","style:","light:","hardness:","blast:","transparent:","waterloggable:","redstone:","fullblock:","slab:","stairs:","wall:","animated:","emissive:","source:","similar:","palette:"); private SuggestionEngine(){}
 public static List<String> suggest(String text,int max){String token=last(text).toLowerCase(Locale.ROOT);Stream<String>s;if(token.startsWith("@"))s=SearchIndex.documents().stream().flatMap(d->Stream.of("@"+d.namespace(),"@"+d.modName().replace(' ','_'))).distinct();else if(token.startsWith("#"))s=SearchIndex.documents().stream().flatMap(d->d.tags().stream()).map("#"::concat).distinct();else if(token.startsWith("color:"))s=Stream.of("color:black","color:white","color:gray","color:darkgray","color:red","color:orange","color:yellow","color:green","color:cyan","color:teal","color:blue","color:purple","color:magenta","color:pink","color:brown","color:brass","color:copper","color:steel");else if(token.startsWith("material:"))s=SearchIndex.documents().stream().flatMap(d->d.materials().stream()).distinct().map("material:"::concat);else if(token.startsWith("shape:"))s=SearchIndex.documents().stream().flatMap(d->d.shapes().stream()).distinct().map("shape:"::concat);else if(token.startsWith("use:"))s=SearchIndex.documents().stream().flatMap(d->d.uses().stream()).distinct().map("use:"::concat);else if(token.startsWith("style:"))s=SearchIndex.documents().stream().flatMap(d->d.styles().stream()).distinct().map("style:"::concat);else s=FILTERS.stream();return s.filter(x->x.toLowerCase(Locale.ROOT).startsWith(token)).sorted().limit(max).toList();}
 private static String last(String t){if(t==null)return"";int i=t.lastIndexOf(' ');return i<0?t:t.substring(i+1);}
}
