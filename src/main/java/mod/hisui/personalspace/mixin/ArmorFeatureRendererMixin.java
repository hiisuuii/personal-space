package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext context) {
        super(context);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void captureArguments(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci,
    @Share("renderedEntity") LocalRef<T> entityRef){
        entityRef.set(entity);
    }

    @ModifyArg(method = "renderArmor",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmorParts(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/model/BipedEntityModel;ILnet/minecraft/util/Identifier;)V"),index = 4)
    private int modifyArmorOpacity(int original, @Share("renderedEntity") LocalRef<T> entityRef){
        LivingEntity livingEntity = entityRef.get();
        boolean bl = !livingEntity.isInvisible();
        if(PersonalSpace.ENABLED && bl && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)){
            return (PersonalSpace.getOpacityForDistance(otherPerson) << 24) | (0x00FFFFFF & original);
        }
        return original;
    }
}
