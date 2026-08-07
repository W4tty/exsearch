// ? Project: Aero Search
// ? File: LearningStore.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/search
// ? Description: Local-only adaptive ranking persisted in config.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 15:04 EDT

package gg.theexiles.aerosearch.search;
import com.google.gson.*; import com.google.gson.reflect.TypeToken; import gg.theexiles.aerosearch.AeroSearch; import net.neoforged.fml.loading.FMLPaths; import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*;
public final class LearningStore {
 private static final Gson GSON=new Gson(); private static final Map<String,Map<String,Integer>> COUNTS=new HashMap<>(); private static final Path FILE=FMLPaths.CONFIGDIR.get().resolve("aerosearch/learning.json"); private LearningStore(){}
 public static synchronized void load(){COUNTS.clear();try{if(!Files.exists(FILE))return;try(Reader r=Files.newBufferedReader(FILE,StandardCharsets.UTF_8)){Map<String,Map<String,Integer>> m=GSON.fromJson(r,new TypeToken<Map<String,Map<String,Integer>>>(){}.getType());if(m!=null)COUNTS.putAll(m);}}catch(Exception e){AeroSearch.LOGGER.warn("Could not load local ranking data",e);}}
 public static synchronized void record(String q,String id){if(q==null||q.isBlank()||id==null)return;COUNTS.computeIfAbsent(norm(q),x->new HashMap<>()).merge(id,1,Integer::sum);save();}
 public static synchronized double boost(String q,String id){if(q==null)return 0;int n=COUNTS.getOrDefault(norm(q),Map.of()).getOrDefault(id,0);return Math.min(.35,Math.log1p(n)*.08);}
 private static String norm(String q){return q.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+"," ");}
 private static void save(){try{Files.createDirectories(FILE.getParent());Files.writeString(FILE,GSON.toJson(COUNTS),StandardCharsets.UTF_8);}catch(Exception e){AeroSearch.LOGGER.warn("Could not save local ranking data",e);}}
}
