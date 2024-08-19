package mod.hisui.personalspace.mixin;

import mod.hisui.personalspace.util.WrappedVertexProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
	@Shadow protected abstract boolean isVisible(T entity);

	protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@ModifyArg(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V"),
	index = 1)
	private VertexConsumerProvider modifyVertexConsumerProvider(VertexConsumerProvider provider){
		return provider instanceof WrappedVertexProvider ? ((WrappedVertexProvider)provider).getOriginal() : provider;
	}
/*
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
	*/
}