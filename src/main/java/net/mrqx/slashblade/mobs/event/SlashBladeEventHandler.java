package net.mrqx.slashblade.mobs.event;

import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.events.StunEvent;
import net.mrqx.sbr_core.utils.JustSlashArtManager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashIllager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;
import net.mrqx.slashblade.mobs.entity.villager.SlashVillagerProfessionSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;

import java.util.List;

@EventBusSubscriber
public class SlashBladeEventHandler {
    @SubscribeEvent
    public static void onStunEvent(StunEvent event) {
        if (event.getEntity() instanceof EntitySlashVillager slashVillager) {
            if (slashVillager.getVillagerData().getLevel() >= 5) {
                event.setCanceled(true);
            } else {
                event.setDuration(event.getDuration() / slashVillager.getVillagerData().getLevel());
            }
        }
        if (event.getEntity() instanceof EntitySlashIllager slashIllager) {
            if (slashIllager.getVillagerData().getLevel() >= 5) {
                event.setCanceled(true);
            } else {
                event.setDuration(event.getDuration() / slashIllager.getVillagerData().getLevel());
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttackEvent(EntityInvulnerabilityCheckEvent event) {
        ISlashBladeEntity slashBladeEntity = null;
        if (event.getSource().getEntity() instanceof ISlashBladeEntity) {
            slashBladeEntity = (ISlashBladeEntity) event.getSource().getEntity();
        }
        if (event.getSource().getDirectEntity() instanceof ISlashBladeEntity) {
            slashBladeEntity = (ISlashBladeEntity) event.getSource().getDirectEntity();
        }
        
        if (slashBladeEntity instanceof EntitySlashVillager slashVillager) {
            if (!slashVillager.processTargetList(slashVillager.level(), slashVillager, slashVillager.getBoundingBox(), 0, List.of(event.getEntity()))
                .contains(event.getEntity())) {
                event.setInvulnerable(true);
            }
        }
        if (slashBladeEntity instanceof EntitySlashIllager slashIllager) {
            if (!slashIllager.processTargetList(slashIllager.level(), slashIllager, slashIllager.getBoundingBox(), 0, List.of(event.getEntity()))
                .contains(event.getEntity())) {
                event.setInvulnerable(true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onChargeActionEvent(SlashBladeEvent.PerformSlashArtEvent event) {
        if (event.getEntityLiving() instanceof EntitySlashVillager slashVillager) {
            onChargeAction(event, slashVillager);
        }
        if (event.getEntityLiving() instanceof EntitySlashIllager slashIllager) {
            onChargeAction(event, slashIllager);
        }
    }
    
    @SubscribeEvent
    public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
        if (event.getUser() instanceof EntitySlashVillager slashVillager) {
            SlashVillagerProfessionSettings professionSettings = slashVillager.getSlashVillagerProfessionSettings();
            if (professionSettings != null && professionSettings.powerful) {
                event.setPowered(true);
            }
        }
        if (event.getUser() instanceof EntitySlashIllager slashIllager) {
            SlashVillagerProfessionSettings professionSettings = slashIllager.getSlashVillagerProfessionSettings();
            if (professionSettings != null && professionSettings.powerful) {
                event.setPowered(true);
            }
        }
    }
    
    private static void onChargeAction(SlashBladeEvent.PerformSlashArtEvent event, EntitySlashVillager slashVillager) {
        SlashVillagerProfessionSettings professionSettings = slashVillager.getSlashVillagerProfessionSettings();
        if (professionSettings != null) {
            if (!professionSettings.canDoSlashArts) {
                event.setCanceled(true);
                return;
            }
            int count = JustSlashArtManager.addJustCount(slashVillager);
            int maxCount = professionSettings.canDoJustSlashArts ? 3 : 1;
            if (count > maxCount) {
                JustSlashArtManager.setJustCooldown(slashVillager, 240);
                event.setCanceled(true);
            }
            if (event.getType() == SlashArts.ArtsType.Jackpot) {
                AdvancementHelper.grantedIf(slashVillager.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED).value(), slashVillager);
            }
        }
    }
    
    private static void onChargeAction(SlashBladeEvent.PerformSlashArtEvent event, EntitySlashIllager slashIllager) {
        SlashVillagerProfessionSettings professionSettings = slashIllager.getSlashVillagerProfessionSettings();
        if (professionSettings != null) {
            if (!professionSettings.canDoSlashArts) {
                event.setCanceled(true);
                return;
            }
            int count = JustSlashArtManager.addJustCount(slashIllager);
            int maxCount = professionSettings.canDoJustSlashArts ? 3 : 1;
            if (count > maxCount) {
                JustSlashArtManager.setJustCooldown(slashIllager, 240);
                event.setCanceled(true);
            }
            if (event.getType() == SlashArts.ArtsType.Jackpot) {
                AdvancementHelper.grantedIf(slashIllager.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED).value(), slashIllager);
            }
        }
    }
}
