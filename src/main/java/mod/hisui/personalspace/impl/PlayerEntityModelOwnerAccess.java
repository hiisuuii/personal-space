package mod.hisui.personalspace.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface PlayerEntityModelOwnerAccess<T extends LivingEntity> {
    void personalspace$setOwner(T owner);
    T personalspace$getOwner();
}
