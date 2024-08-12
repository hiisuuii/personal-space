package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
    at = @At("HEAD"))
    private void captureArgs(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci, @Share("renderedEntity")LocalRef<LivingEntity> argRef){
        argRef.set(entity);
    }

    @ModifyArg(method = "renderBakedItemQuads",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"),
    index = 5)
    private float modifyOpacity(float opacity, @Share("renderedEntity")LocalRef<LivingEntity> argRef){
//        LivingEntity livingEntity = argRef.get();
//        if(livingEntity != null) {
//            boolean bl = !livingEntity.isInvisible();
//            if (PersonalSpace.ENABLED && bl && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)) {
//                return PersonalSpace.getOpacityForDistance(otherPerson) / 255.0f;
//            }
//        }
        return 0.5f;
    }
}
