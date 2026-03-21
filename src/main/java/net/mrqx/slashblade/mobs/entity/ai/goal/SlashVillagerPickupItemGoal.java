package net.mrqx.slashblade.mobs.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;

import javax.annotation.Nullable;
import java.util.List;

public class SlashVillagerPickupItemGoal extends Goal {
    private final EntitySlashVillager slashVillager;
    @Nullable
    private ItemEntity targetItem;

    public SlashVillagerPickupItemGoal(EntitySlashVillager slashVillager) {
        this.slashVillager = slashVillager;
    }

    @Override
    public boolean canUse() {
        List<ItemEntity> items = this.slashVillager.level().getEntitiesOfClass(ItemEntity.class, this.slashVillager.getBoundingBox().inflate(16.0D),
                itemEntity -> this.slashVillager.wantsToPickUp(itemEntity.getItem()));
        if (items.isEmpty()) {
            return false;
        }
        this.targetItem = items.get(0);
        return true;
    }

    @Override
    public void start() {
        if (this.targetItem != null) {
            this.slashVillager.getNavigation().moveTo(this.targetItem, 0.7D);
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
    }

    @Override
    public void tick() {
        if (this.targetItem != null && this.slashVillager.getNavigation().isDone()) {
            List<ItemEntity> items = this.slashVillager.level().getEntitiesOfClass(ItemEntity.class, this.slashVillager.getBoundingBox().inflate(1.5),
                    itemEntity -> this.slashVillager.wantsToPickUp(itemEntity.getItem()));
            if (items.isEmpty()) {
                this.slashVillager.getNavigation().moveTo(this.targetItem, 0.7D);
            } else {
                items.forEach(this.slashVillager::pickUpItem);
                this.stop();
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetItem != null && !this.targetItem.isRemoved();
    }
}