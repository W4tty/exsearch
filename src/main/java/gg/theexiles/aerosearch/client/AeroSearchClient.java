// ? Project: Aero Search
// ? File: AeroSearchClient.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/client
// ? Description: Client event wiring, keybinds, resource reload rebuild and ranking store startup.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.client;

import com.mojang.blaze3d.platform.InputConstants;
import gg.theexiles.aerosearch.index.SearchIndex;
import gg.theexiles.aerosearch.search.LearningStore;
import gg.theexiles.aerosearch.ui.SearchSession;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class AeroSearchClient {
    private static KeyMapping rebuild;
    private static KeyMapping help;

    private AeroSearchClient() {}

    public static void bootstrap() {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        LearningStore.load();
        NeoForge.EVENT_BUS.register(AeroSearchClient.class);
    }

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        SearchIndex.rebuildAsync();
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (rebuild != null && rebuild.consumeClick()) {
            if (mc.player != null) mc.player.displayClientMessage(Component.translatable("aerosearch.index.rebuilding"), true);
            SearchIndex.rebuildAsync().thenAccept(count -> mc.execute(() -> {
                if (mc.player != null) mc.player.displayClientMessage(Component.translatable("aerosearch.index.ready", count), true);
            }));
        }
        if (help != null && help.consumeClick()) SearchSession.toggleHelp();
    }

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        rebuild = new KeyMapping("key.aerosearch.rebuild", InputConstants.Type.KEYSYM, InputConstants.KEY_F6, "key.categories.aerosearch");
        help = new KeyMapping("key.aerosearch.help", InputConstants.Type.KEYSYM, InputConstants.KEY_F7, "key.categories.aerosearch");
        event.register(rebuild);
        event.register(help);
    }
}
