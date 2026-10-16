package de.nevergate.fahrzeuge.client;

import com.mojang.blaze3d.platform.NativeImage;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** Original pixel materials, generated deterministically on the render thread. */
public final class VehicleTextures {
    public static final ResourceLocation PAINT = id("paint");
    public static final ResourceLocation RUBBER = id("rubber");
    public static final ResourceLocation METAL = id("metal");
    public static final ResourceLocation FABRIC = id("fabric");
    public static final ResourceLocation LENS = id("lens");
    public static final ResourceLocation BADGE = id("badge");
    public static final ResourceLocation BADGE_MINT = id("badge_mint");
    private static boolean ready;
    private static ResourceLocation id(String name) {
        return new ResourceLocation(NgFahrzeuge.MOD_ID, "generated/" + name);
    }
    public static void initialize() {
        if (ready) return;
        register(PAINT, material(0)); register(RUBBER, material(1));
        register(METAL, material(2)); register(FABRIC, material(3));
        register(LENS, material(4)); register(BADGE, badge(false)); register(BADGE_MINT, badge(true));
        ready = true;
    }
    private static void register(ResourceLocation id, NativeImage image) {
        Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(image));
    }
    private static int rgba(int r, int g, int b) { return 0xff000000 | b << 16 | g << 8 | r; }
    private static NativeImage material(int kind) {
        NativeImage image = new NativeImage(128, 128, false);
        for (int y=0;y<128;y++) for (int x=0;x<128;x++) {
            int hash = (x/2*7349 ^ y/2*1931 ^ kind*8347) & 15;
            int value = 246 + hash/3;
            if (kind==0) value = 237 + hash/2 + ((x/8+y/8)%2)*4;
            if (kind==1) value = 215 + hash*2 - ((x%5==0 || y%7==0) ? 20 : 0);
            if (kind==2) value = 226 + hash + ((y%8==0) ? 10 : 0);
            if (kind==3) value = 215 + hash + ((x+y)%3==0 ? 12 : 0);
            if (kind==4) value = 205 + hash*2 + ((x%4==0 || y%4==0) ? 17 : 0);
            image.setPixelRGBA(x,y,rgba(value,value,value));
        }
        return image;
    }
    private static NativeImage badge(boolean mint) {
        NativeImage image = new NativeImage(32,16,false);
        for(int y=0;y<16;y++) for(int x=0;x<32;x++)
            image.setPixelRGBA(x,y,rgba(x==0||x==31||y==0||y==15?145:25,x==0||x==31||y==0||y==15?150:29,x==0||x==31||y==0||y==15?154:32));
        String[] n={"10001","11001","11001","10101","10011","10011","10001"};
        String[] g={"01110","10000","10000","10111","10001","10001","01110"};
        for(int letter=0;letter<2;letter++) for(int y=0;y<7;y++) for(int x=0;x<5;x++)
            if((letter==0?n:g)[y].charAt(x)=='1') for(int dx=0;dx<2;dx++) for(int dy=0;dy<2;dy++)
                image.setPixelRGBA(5+letter*12+x*2+dx,1+y*2+dy,mint?rgba(105,223,205):rgba(222,230,230));
        return image;
    }
    public static float[] paint(int dyeId) {
        // The approved cyan vehicle livery is mint; other dye choices retain their palette.
        if(dyeId==DyeColor.CYAN.getId()) return new float[]{0.40F,0.84F,0.78F};
        return DyeColor.byId(dyeId).getTextureDiffuseColors();
    }
    private VehicleTextures() {}
}
