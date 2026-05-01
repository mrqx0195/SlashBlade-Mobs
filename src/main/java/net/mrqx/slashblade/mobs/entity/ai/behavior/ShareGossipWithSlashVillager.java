package net.mrqx.slashblade.mobs.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;

import java.util.Set;

public class ShareGossipWithSlashVillager extends Behavior<Villager> {
    public ShareGossipWithSlashVillager() {
        super(ImmutableMap.of(
            MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT
        ));
    }
    
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Villager owner) {
        return BehaviorUtils.targetIsValid(owner.getBrain(), MemoryModuleType.INTERACTION_TARGET, SlashMobsEntities.SLASH_VILLAGER.get());
    }
    
    @Override
    protected boolean canStillUse(ServerLevel level, Villager entity, long gameTime) {
        return this.checkExtraStartConditions(level, entity);
    }
    
    @Override
    protected void start(ServerLevel level, Villager entity, long gameTime) {
        entity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).ifPresent(living -> {
            if (living instanceof EntitySlashVillager slashVillager) {
                BehaviorUtils.lockGazeAndWalkToEachOther(entity, slashVillager, 0.5F);
            }
        });
    }
    
    @Override
    protected void tick(ServerLevel level, Villager owner, long gameTime) {
        owner.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).ifPresent(living -> {
            if (living instanceof EntitySlashVillager slashVillager) {
                if (owner.distanceToSqr(slashVillager) < 5.0D) {
                    BehaviorUtils.lockGazeAndWalkToEachOther(owner, slashVillager, 0.5F);
                    slashVillager.gossip(owner, gameTime);
                }
                if (owner.hasExcessFood() && slashVillager.getOffhandItem().isEmpty()) {
                    throwHalfStack(owner, Villager.FOOD_POINTS.keySet(), slashVillager);
                }
            }
        });
    }
    
    @Override
    protected void stop(ServerLevel level, Villager entity, long gameTime) {
        entity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
    }
    
    /**
     * @see TradeWithVillager
     */
    private static void throwHalfStack(Villager villager, Set<Item> stack, LivingEntity entity) {
        SimpleContainer simplecontainer = villager.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        int i = 0;
        
        while (i < simplecontainer.getContainerSize()) {
            ItemStack itemStack1;
            Item item;
            int j;
            label28:
            {
                itemStack1 = simplecontainer.getItem(i);
                if (!itemStack1.isEmpty()) {
                    item = itemStack1.getItem();
                    if (stack.contains(item)) {
                        if (itemStack1.getCount() > itemStack1.getMaxStackSize() / 2) {
                            j = itemStack1.getCount() / 2;
                            break label28;
                        }
                        
                        if (itemStack1.getCount() > 24) {
                            j = itemStack1.getCount() - 24;
                            break label28;
                        }
                    }
                }
                
                ++i;
                continue;
            }
            
            itemStack1.shrink(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        
        if (!itemStack.isEmpty()) {
            entity.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
        }
    }
}