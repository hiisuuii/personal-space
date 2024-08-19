package mod.hisui.personalspace.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class WrappedVertexProvider implements VertexConsumerProvider {

    private final VertexConsumerProvider original;
    private OptionalInt color = OptionalInt.empty();
    private OptionalInt alpha = OptionalInt.empty();

    public WrappedVertexProvider(VertexConsumerProvider original) {
        this.original = original;
    }

    public VertexConsumerProvider getOriginal() { return original; }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        WrappedVertexConsumer buffer = new WrappedVertexConsumer(original.getBuffer(layer));
        color.ifPresent(buffer::rgb);
        alpha.ifPresent(buffer::alpha);
        return buffer;
    }

    public void rgb(int color){
        this.color = OptionalInt.of(color);
    }

    public void alpha(int alpha){
        this.alpha = OptionalInt.of(alpha);
    }
}
