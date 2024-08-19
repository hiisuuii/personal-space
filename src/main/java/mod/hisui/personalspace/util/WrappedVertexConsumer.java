package mod.hisui.personalspace.util;

import net.minecraft.client.render.VertexConsumer;

import java.util.OptionalInt;

public class WrappedVertexConsumer implements VertexConsumer {

    private final VertexConsumer original;

    private OptionalInt color = OptionalInt.empty();
    private OptionalInt alpha = OptionalInt.empty();

    public WrappedVertexConsumer(VertexConsumer original){
        this.original = original;
    }

    public void rgb(int color) {
        this.color = OptionalInt.of(color);
    }

    public void alpha(int alpha) {
        this.alpha = OptionalInt.of(alpha);
    }

    public void rgba(int rgba) {
        this.color = OptionalInt.of(rgba & 0x00FFFFFF);
        this.alpha = OptionalInt.of((rgba >> 24) & 0xFF);
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        return original.vertex(x,y,z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        if(color.isPresent())
        {
            int col = color.getAsInt();
            red = mixColors(red, (col & 0xFF0000) >> 16);
            green = mixColors(green, (col & 0xFF00) >> 8);
            blue = mixColors(blue, (col & 0xFF));
        }

        if(this.alpha.isPresent())
            alpha = mixColors(alpha, this.alpha.getAsInt());

        return original.color(red, green, blue, alpha);
    }

    private static int mixColors(int colA, int colB)
    {
        float a = (float)colA / 255F;
        float b = (float)colB / 255F;
        return (int)(255 * (a * b));
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return original.texture(u,v);
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return original.overlay(u,v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return original.light(u,v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return original.normal(x,y,z);
    }


}
