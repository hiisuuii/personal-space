package mod.hisui.personalspace.mixin.compat;

import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(CustomizableModelPart.class)
public class SkinLayersCompatMixin {

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
    at = @At(value = "INVOKE", target = "Ldev/tr7zw/skinlayers/render/CustomizableModelPart;render(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
    index = 8)
    private float adjustOpacityFor3D(float alpha){
        LivingEntity livingEntity = PersonalSpace.TEMP_OWNER;
        if(livingEntity != null) {
            boolean bl = !livingEntity.isInvisible();
            int intAlpha = (int) (alpha * 255);
            if (PersonalSpace.ENABLED && bl && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)) {
                PersonalSpace.LOGGER.info("Modifying 3D Skin Opacity");
                // TODO this doesnt work
                float value = ((PersonalSpace.getOpacityForDistance(otherPerson) << 24) | (0x00FFFFFF & intAlpha)) / 255.0f;
                return value;
            }
        }
        return alpha;
    }
}
