package net.mrqx.slashblade.mobs.mixin;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Villager.class)
public interface AccessorVillager {
    @Accessor("lastGossipTime")
    long getLastGossipTime();
    
    @Accessor("lastGossipTime")
    void setLastGossipTime(long lastGossipTime);
}
