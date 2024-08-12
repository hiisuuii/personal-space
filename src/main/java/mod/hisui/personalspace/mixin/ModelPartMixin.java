package mod.hisui.personalspace.mixin;

import mod.hisui.personalspace.PersonalSpace;
import mod.hisui.personalspace.impl.ModelPartCloakAccess;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartCloakAccess {

    @Unique
    private boolean personalspace$isCloak = false;
    @Unique
    private LivingEntity personalspace$owningEntity;

    @Override
    public void personalspace$setCloak(boolean isCloak){
        this.personalspace$isCloak = isCloak;
    }

    @Override
    public void personalspace$setOwner(LivingEntity entity){
        this.personalspace$owningEntity = entity;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"),
    index = 4)
    private int modifyOpacity(int original){
        if(PersonalSpace.RENDERING_CLOAK && PersonalSpace.TEMP_OWNER != null){
            LivingEntity livingEntity = PersonalSpace.TEMP_OWNER;
            boolean bl = !livingEntity.isInvisible();
            if(PersonalSpace.ENABLED && bl && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)){
                return (PersonalSpace.getOpacityForDistance(otherPerson) << 24) | (0x00FFFFFF & original);
            }
        }
        return original;
    }

}
