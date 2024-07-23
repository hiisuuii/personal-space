package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Shadow public abstract double getSquaredDistanceToCamera(Entity entity);

    @WrapOperation(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"))
    private void wrapRenderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, Operation<Void> original){
        if(PersonalSpace.ENABLED && entity instanceof OtherClientPlayerEntity otherPerson) {
            double distance = this.getSquaredDistanceToCamera(otherPerson);
            double minDistance = PersonalSpace.MIN_DISTANCE;
            if (distance <= minDistance) {
                return;
            }
        }
        original.call(matrices, vertexConsumers, entity, opacity, tickDelta, world, radius);
    }
}
