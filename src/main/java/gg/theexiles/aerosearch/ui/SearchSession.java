// ? Project: Aero Search
// ? File: SearchSession.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/ui
// ? Description: In-memory history, palette seeds and help state for Creative Search UI.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.ui;
import java.util.*;
public final class SearchSession { private static final ArrayDeque<String> HISTORY=new ArrayDeque<>(); private static final Set<String> PALETTE=new LinkedHashSet<>(); private static boolean help=true; private SearchSession(){} public static void remember(String q){if(q==null||q.isBlank())return;HISTORY.remove(q);HISTORY.addFirst(q);while(HISTORY.size()>50)HISTORY.removeLast();} public static List<String> history(){return List.copyOf(HISTORY);} public static Set<String> palette(){return Set.copyOf(PALETTE);} public static void togglePalette(String id){if(!PALETTE.remove(id))PALETTE.add(id);} public static void clearPalette(){PALETTE.clear();} public static boolean help(){return help;} public static void toggleHelp(){help=!help;} }
