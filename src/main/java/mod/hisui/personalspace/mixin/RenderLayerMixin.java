package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase {

    public RenderLayerMixin(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    @ModifyExpressionValue(method = "createArmorCutoutNoCull",
    at = @At(value = "FIELD",opcode = Opcodes.GETSTATIC,target = "Lnet/minecraft/client/render/RenderLayer;NO_TRANSPARENCY:Lnet/minecraft/client/render/RenderPhase$Transparency;"))
    private static RenderPhase.Transparency modifyTransparency(RenderPhase.Transparency original){

        return RenderPhase.TRANSLUCENT_TRANSPARENCY;
    }
}
