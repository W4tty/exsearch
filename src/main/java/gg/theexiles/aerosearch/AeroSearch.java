// ? Project: ExSearch
// ? File: AeroSearch.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch
// ? Description: NeoForge entrypoint and client bootstrap.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:27 EDT

package gg.theexiles.aerosearch;

import gg.theexiles.aerosearch.client.AeroSearchClient;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AeroSearch.MOD_ID)
public final class AeroSearch {
    public static final String MOD_ID = "exsearch";
    public static final Logger LOGGER = LoggerFactory.getLogger("ExSearch");

    public AeroSearch() {
        // ! Client-only mod. All Minecraft client references are isolated in the client package.
        AeroSearchClient.bootstrap();
    }
}
