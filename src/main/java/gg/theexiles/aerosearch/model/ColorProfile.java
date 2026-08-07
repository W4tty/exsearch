package gg.theexiles.aerosearch.model;

public record ColorProfile(int primaryRgb,int secondaryRgb,double lightness,double saturation,double transparency,long fingerprint){
    public static ColorProfile unknown(){return new ColorProfile(0x808080,0x808080,50.0,0.0,0.0,0L);}
}
