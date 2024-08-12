package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private <E extends Entity> void captureArguments(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, @Share("renderedEntity")LocalRef<E> argRef) {
        argRef.set(entity);
    }

    @ModifyArg(method = "render",index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"))
    private <E extends Entity> float modifyShadowOpacity(float opacity, @Share("renderedEntity")LocalRef<E> argRef) {
        Entity entity = argRef.get();
        boolean bl = !entity.isInvisible();
        if(PersonalSpace.ENABLED && bl && entity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)){
            float opac = PersonalSpace.getOpacityForDistance(otherPerson);
            return opac / 255.0f;
        }
        return opacity;
    }
}
