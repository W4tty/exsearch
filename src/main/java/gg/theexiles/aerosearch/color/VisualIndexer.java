// ? Project: Aero Search
// ? File: VisualIndexer.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/color
// ? Description: Builds visual profiles from live baked block sprites with safe fallbacks.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.color;
import gg.theexiles.aerosearch.model.ColorProfile; import net.minecraft.client.Minecraft; import net.minecraft.client.renderer.block.BlockModelShaper; import net.minecraft.client.resources.model.BakedModel; import net.minecraft.client.renderer.texture.TextureAtlasSprite; import net.minecraft.world.level.block.Block; import java.util.*;
public final class VisualIndexer { private VisualIndexer(){}
 public static ColorProfile profile(Block block){ try{ Minecraft mc=Minecraft.getInstance(); BlockModelShaper shaper=mc.getBlockRenderer().getBlockModelShaper(); BakedModel model=shaper.getBlockModel(block.defaultBlockState()); TextureAtlasSprite sprite=model.getParticleIcon(); if(sprite==null)return fallback(block); int w=sprite.contents().width(),h=sprite.contents().height(),step=Math.max(1,Math.max(w,h)/16),sampled=0,transparent=0; Map<Integer,Integer> hist=new HashMap<>(); double sat=0,light=0; long fp=0xcbf29ce484222325L; for(int y=0;y<h;y+=step)for(int x=0;x<w;x+=step){int argb=sprite.contents().getPixelRGBA(0,x,y),a=argb>>>24&255; sampled++; if(a<32){transparent++;continue;} int r=argb>>>16&255,g=argb>>>8&255,b=argb&255,q=((r>>4)<<20)|((g>>4)<<12)|((b>>4)<<4);hist.merge(q,1,Integer::sum);float[] hsb=java.awt.Color.RGBtoHSB(r,g,b,null);sat+=hsb[1];light+=ColorMath.lab((r<<16)|(g<<8)|b)[0];fp^=q;fp*=0x100000001b3L;} if(hist.isEmpty())return fallback(block); List<Map.Entry<Integer,Integer>> c=new ArrayList<>(hist.entrySet());c.sort(Map.Entry.<Integer,Integer>comparingByValue(Comparator.reverseOrder()));int p=expand(c.get(0).getKey()),s=c.size()>1?expand(c.get(1).getKey()):p;return new ColorProfile(p,s,sampled==0?50:light/sampled,sampled==0?0:sat/sampled,sampled==0?0:transparent/(double)sampled,fp);}catch(Throwable ignored){return fallback(block);} }
 private static int expand(int q){int r=(q>>20&15)*17,g=(q>>12&15)*17,b=(q>>4&15)*17;return r<<16|g<<8|b;}
 private static ColorProfile fallback(Block b){try{int rgb=b.defaultMapColor().col&0xffffff;return new ColorProfile(rgb,rgb,ColorMath.lab(rgb)[0],0,0,rgb);}catch(Throwable ignored){return ColorProfile.unknown();}}
}
