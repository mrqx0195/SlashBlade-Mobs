package net.mrqx.slashblade.mobs.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleMirageBladeGoal;
import net.mrqx.slashblade.mobs.entity.villager.SlashVillagerProfessionSettings;

import javax.annotation.Nullable;

public class VillagerMirageBladeGoal<T extends PathfinderMob & ISlashBladeEntity & VillagerDataHolder & RangedAttackMob> extends SimpleMirageBladeGoal<T> {
    @Nullable
    public SlashVillagerProfessionSettings professionSettings;

    public VillagerMirageBladeGoal(T rangedAttackMob, double speedModifier) {
        super(rangedAttackMob, speedModifier, false, false, false, false, false);
        this.refreshProfessionSettings(entity.getVillagerData());
    }

    public void refreshProfessionSettings(VillagerData villagerData) {
        professionSettings = SlashVillagerProfessionSettings.getSettings(villagerData.getProfession(), villagerData.getLevel());
        if (professionSettings != null) {
            this.canUseBaseSummonedSword = professionSettings.canUseBaseSummonedSword;
            this.canUseSpiralSword = professionSettings.canUseSpiralSword;
            this.canUseStormSword = professionSettings.canUseStormSword;
            this.canUseBlisteringSword = professionSettings.canUseBlisteringSword;
            this.canUseHeavyRainSword = professionSettings.canUseHeavyRainSword;
        }
    }

    @Override
    public int getBaseSummonedSwordCooldown() {
        return 20 * 2 / entity.getVillagerData().getLevel();
    }

    @Override
    public int getSpiralSwordCooldown() {
        return 200 * 2 / entity.getVillagerData().getLevel();
    }

    @Override
    public int getStormSwordCooldown() {
        return 200 * 2 / entity.getVillagerData().getLevel();
    }

    @Override
    public int getBlisteringSwordCooldown() {
        return 400 * 2 / entity.getVillagerData().getLevel();
    }

    @Override
    public int getHeavyRainSwordCooldown() {
        return 600 * 2 / entity.getVillagerData().getLevel();
    }
}
