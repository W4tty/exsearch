// ? Project: Aero Search
// ? File: SearchDocument.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/model
// ? Description: Immutable searchable representation of an installed item/block.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.model;

import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record SearchDocument(
        String id,
        String namespace,
        String path,
        String displayName,
        String modName,
        Set<String> tags,
        Set<String> tooltips,
        Set<String> materials,
        Set<String> shapes,
        Set<String> uses,
        Set<String> styles,
        Set<String> keywords,
        boolean block,
        boolean transparent,
        boolean waterloggable,
        boolean redstone,
        boolean fullBlock,
        boolean slab,
        boolean stairs,
        boolean wall,
        boolean animated,
        boolean emissive,
        int light,
        double hardness,
        double blastResistance,
        ColorProfile color,
        double rankBoost,
        ItemStack stack
) {}
