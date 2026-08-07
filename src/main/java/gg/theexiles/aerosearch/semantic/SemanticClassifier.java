// ? Project: Aero Search
// ? File: SemanticClassifier.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/semantic
// ? Description: Automatic material/shape/use/style inference for uncurated content.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.semantic;
import java.util.*;
public final class SemanticClassifier {
 private SemanticClassifier(){}
 public static Result classify(String id,String displayName,String className){
  String hay=(id+" "+displayName+" "+className).toLowerCase(Locale.ROOT);
  Set<String> m=new LinkedHashSet<>(),s=new LinkedHashSet<>(),u=new LinkedHashSet<>(),st=new LinkedHashSet<>(),k=new LinkedHashSet<>();
  add(hay,m,"metal","iron","steel","aluminum","aluminium","titanium","brass","copper","bronze","zinc","nickel","invar","electrum","lead"); add(hay,m,"glass","glass","window"); add(hay,m,"wood","plank","wood","log","timber"); add(hay,m,"stone","stone","slate","granite","andesite","diorite","deepslate","basalt","marble"); add(hay,m,"concrete","concrete","cement"); add(hay,m,"fabric","wool","carpet","canvas","fabric");
  for(String w:List.of("slab","stairs","wall","fence","pane","door","trapdoor","button","plate","pipe","tube","duct","cable","conduit","panel","tile","lamp","light","beam","pillar","column","railing","grate","catwalk")) if(hay.contains(w)) s.add(w);
  if(Collections.disjoint(s,Set.of("slab","stairs","wall","fence","pane","door","trapdoor","button","plate","pipe","tube","duct","cable","conduit"))) s.add("fullblock");
  if(any(hay,"lamp","light","lantern","glow","luminous")){u.add("light");st.add("scifi");} if(any(hay,"crate","barrel","chest","drawer","locker","vault","storage"))u.add("storage"); if(any(hay,"floor","tile","grate","catwalk","carpet"))u.add("floor"); if(any(hay,"wall","panel","casing","bricks","planks"))u.add("wall"); if(any(hay,"trim","border","frame","edge"))u.add("trim"); if(any(hay,"machine","casing","gear","engine","motor","generator"))u.add("machine");
  if(any(hay,"industrial","factory","machine","casing","grate","catwalk","pipe","duct"))st.add("industrial"); if(any(hay,"neon","cyber","holo","magenta"))st.add("cyberpunk"); if(any(hay,"space","sci","quantum","reactor","panel","hull","laboratory","lab"))st.add("scifi"); if(any(hay,"rust","weathered","oxidized","worn"))st.add("rusty"); if(any(hay,"smooth","polished","clean","white"))st.add("clean");
  return new Result(m,s,u,st,k);
 }
 private static void add(String h,Set<String>s,String v,String...n){if(any(h,n))s.add(v);} private static boolean any(String h,String...n){for(String x:n)if(h.contains(x))return true;return false;}
 public record Result(Set<String> materials,Set<String> shapes,Set<String> uses,Set<String> styles,Set<String> keywords){}
}
