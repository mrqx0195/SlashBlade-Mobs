package net.mrqx.slashblade.mobs.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleSlashGoal;
import net.mrqx.slashblade.mobs.entity.villager.SlashVillagerProfessionSettings;

import javax.annotation.Nullable;

public class VillagerSlashGoal<T extends PathfinderMob & ISlashBladeEntity & VillagerDataHolder> extends SimpleSlashGoal<T> {
    @Nullable
    public SlashVillagerProfessionSettings professionSettings;
    
    public VillagerSlashGoal(T mob, double speedModifier, int attackCooldown) {
        super(mob, speedModifier, attackCooldown, true);
        this.refreshProfessionSettings(mob.getVillagerData());
    }
    
    public void refreshProfessionSettings(VillagerData villagerData) {
        professionSettings = SlashVillagerProfessionSettings.getSettings(villagerData.getProfession(), villagerData.getLevel());
        if (professionSettings != null) {
            this.canRapidSlash = professionSettings.canRapidSlash;
            this.preferAirAttack = professionSettings.preferAirAttack;
            this.canVoidSlash = professionSettings.canVoidSlash;
            this.canDoSlashArts = professionSettings.canDoSlashArts;
            this.canDoJustSlashArts = professionSettings.canDoJustSlashArts;
            this.powerful = professionSettings.powerful;
        }
    }
}
