package mod.hisui.personalspace.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.hisui.personalspace.PersonalSpace;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
	@Shadow protected abstract boolean isVisible(T entity);

	protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At("HEAD"), cancellable = true)
	private void cancelRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
		if(PersonalSpace.ENABLED && this.isVisible(livingEntity) && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)){
			double distance = this.dispatcher.camera.getPos().squaredDistanceTo(otherPerson.getPos().offset(Direction.UP, 0.5));
			if(distance <= PersonalSpace.MIN_DISTANCE && PersonalSpace.MIN_OPACITY == 0) {
				ci.cancel();
			}
		}
	}

	@ModifyExpressionValue(slice =
	@Slice(from = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z")),
			at = @At(value = "CONSTANT", args = "intValue=-1", ordinal = 0),
			method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	private int modifyPlayerOpacity(int original, T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		boolean bl = this.isVisible(livingEntity);
		if(PersonalSpace.ENABLED && bl && livingEntity instanceof OtherClientPlayerEntity otherPerson && !PersonalSpace.isIgnored(otherPerson)){
			return (PersonalSpace.getOpacityForDistance(otherPerson) << 24) | (0x00FFFFFF & original);
		}

		return original;
	}
}