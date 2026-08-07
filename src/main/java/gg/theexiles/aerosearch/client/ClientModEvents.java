// ? Project: Aero Search
// ? File: ClientModEvents.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/client
// ? Description: MOD-bus-only client registration events.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.client;

import gg.theexiles.aerosearch.AeroSearch;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = AeroSearch.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        AeroSearchClient.registerKeys(event);
    }
}
