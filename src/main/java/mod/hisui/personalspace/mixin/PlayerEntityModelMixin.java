package mod.hisui.personalspace.mixin;

import mod.hisui.personalspace.PersonalSpace;
import mod.hisui.personalspace.impl.ModelPartCloakAccess;
import mod.hisui.personalspace.impl.PlayerEntityModelOwnerAccess;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> implements PlayerEntityModelOwnerAccess<T> {

    @Unique
    private T personalspace$owningEntity;

    @Shadow @Final private ModelPart cloak;

    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Override
    public void personalspace$setOwner(T owner) {
        this.personalspace$owningEntity = owner;
    }
    @Override
    public T personalspace$getOwner() {
        return this.personalspace$owningEntity;
    }

//    @Inject(method = "<init>",at = @At(value = "CONSTANT", args = "stringValue=cloak"))
//    private void setCloakFlag(ModelPart root, boolean thinArms, CallbackInfo ci){
//        if(this.cloak != null) {
//            ModelPartCloakAccess cloak = (ModelPartCloakAccess) (Object) this.cloak;
//            cloak.personalspace$setCloak(true);
//            cloak.personalspace$setOwner(personalspace$owningEntity);
//        }
//    }

    @Inject(method = "renderCape",at = @At("HEAD"))
    private void testGlobalCape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, CallbackInfo ci){
        PersonalSpace.RENDERING_CLOAK = true;
    }
    @Inject(method = "renderCape",at = @At("TAIL"))
    private void stoprendercape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, CallbackInfo ci){
        PersonalSpace.RENDERING_CLOAK = false;
    }
}

