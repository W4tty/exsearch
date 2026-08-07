// ? Project: Aero Search
// ? File: SearchIndex.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/index
// ? Description: Builds and atomically publishes runtime item/block search documents.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.index;

import gg.theexiles.aerosearch.AeroSearch;
import gg.theexiles.aerosearch.color.VisualIndexer;
import gg.theexiles.aerosearch.metadata.MetadataEntry;
import gg.theexiles.aerosearch.metadata.MetadataRepository;
import gg.theexiles.aerosearch.model.ColorProfile;
import gg.theexiles.aerosearch.model.SearchDocument;
import gg.theexiles.aerosearch.semantic.SemanticClassifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.fml.ModList;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public final class SearchIndex {
 private static final AtomicReference<List<SearchDocument>> DOCS=new AtomicReference<>(List.of()); private static final MetadataRepository METADATA=new MetadataRepository(); private static volatile boolean rebuilding; private SearchIndex(){}
 public static List<SearchDocument> documents(){return DOCS.get();} public static boolean isRebuilding(){return rebuilding;}
 public static CompletableFuture<Integer> rebuildAsync(){ if(rebuilding)return CompletableFuture.completedFuture(DOCS.get().size()); rebuilding=true; CompletableFuture<Integer> f=new CompletableFuture<>(); Minecraft.getInstance().execute(()->{try{METADATA.reload();List<SearchDocument> n=build();DOCS.set(List.copyOf(n));AeroSearch.LOGGER.info("Indexed {} searchable entries",n.size());f.complete(n.size());}catch(Throwable t){AeroSearch.LOGGER.error("Aero Search index rebuild failed",t);f.completeExceptionally(t);}finally{rebuilding=false;}}); return f; }
 private static List<SearchDocument> build(){ List<SearchDocument> docs=new ArrayList<>(); for(Item item:BuiltInRegistries.ITEM){ ResourceLocation id=BuiltInRegistries.ITEM.getKey(item); if(id==null)continue; ItemStack stack=item.getDefaultInstance(); if(stack.isEmpty())continue; String rid=id.toString(); MetadataEntry meta=METADATA.get(rid); if(meta.hidden)continue; String display=stack.getHoverName().getString(); String modName=ModList.get().getModContainerById(id.getNamespace()).map(c->c.getModInfo().getDisplayName()).orElse(id.getNamespace()); Set<String> tags=new LinkedHashSet<>(); stack.getTags().map(TagKey::location).map(ResourceLocation::toString).forEach(tags::add); Set<String> tips=new LinkedHashSet<>(); try{for(var line:stack.getTooltipLines(Item.TooltipContext.EMPTY,Minecraft.getInstance().player,TooltipFlag.NORMAL))tips.add(line.getString().toLowerCase(Locale.ROOT));}catch(Throwable ignored){} boolean isBlock=BuiltInRegistries.BLOCK.containsKey(id); Block block=isBlock?BuiltInRegistries.BLOCK.get(id):null; var inferred=SemanticClassifier.classify(rid,display,block!=null?block.getClass().getSimpleName():item.getClass().getSimpleName()); Set<String> materials=merged(inferred.materials(),meta.materials),shapes=merged(inferred.shapes(),meta.shapes),uses=merged(inferred.uses(),meta.uses),styles=merged(inferred.styles(),meta.styles),keywords=merged(inferred.keywords(),meta.keywords);keywords.addAll(meta.aliases); boolean water=block!=null&&block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED),slab=block instanceof SlabBlock||shapes.contains("slab"),stairs=block instanceof StairBlock||shapes.contains("stairs"),wall=block instanceof WallBlock||shapes.contains("wall"),full=block!=null&&block.defaultBlockState().isCollisionShapeFullBlock(null,null); int light=block==null?0:block.defaultBlockState().getLightEmission(); boolean emissive=light>0||keywords.stream().anyMatch(k->k.contains("emiss")),transparent=block!=null&&!block.defaultBlockState().canOcclude(),redstone=rid.contains("redstone")||rid.contains("repeater")||rid.contains("comparator")||rid.contains("sensor")||rid.contains("switch")||rid.contains("lever"); double hardness=0,blast=0; if(block!=null){try{hardness=block.defaultDestroyTime();}catch(Throwable ignored){}try{blast=block.getExplosionResistance();}catch(Throwable ignored){}} ColorProfile color=block!=null?VisualIndexer.profile(block):ColorProfile.unknown(); docs.add(new SearchDocument(rid,id.getNamespace(),id.getPath(),display,modName,tags,tips,materials,shapes,uses,styles,keywords,isBlock,transparent,water,redstone,full,slab,stairs,wall,false,emissive,light,hardness,blast,color,meta.rankBoost,stack.copy())); } return docs; }
 private static Set<String> merged(Set<String>a,Set<String>b){Set<String>o=new LinkedHashSet<>(a);o.addAll(b);return o;}
}
