package net.mrqx.slashblade.mobs.mixin;

import net.minecraft.world.entity.monster.Skeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Skeleton.class)
public interface AccessorSkeleton {
    @Accessor("conversionTime")
    void setConversionTime(int conversionTime);
}
