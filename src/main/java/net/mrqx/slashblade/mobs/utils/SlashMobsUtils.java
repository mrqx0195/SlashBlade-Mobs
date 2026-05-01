package net.mrqx.slashblade.mobs.utils;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.mrqx.slashblade.mobs.entity.villager.SlashVillagerProfessionSettings;

import java.util.Map;

public class SlashMobsUtils {
    public static void restoreBladeData(ItemStack newBlade, ItemStack oldBlade) {
        newBlade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(newState ->
            oldBlade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(oldState -> {
                CompoundTag oldTag = oldState.serializeNBT();
                oldTag.putString("translationKey", newState.getTranslationKey());
                newState.getTexture().ifPresent((loc) -> oldTag.putString("TextureName", loc.toString()));
                newState.getModel().ifPresent((loc) -> oldTag.putString("ModelName", loc.toString()));
                newState.deserializeNBT(oldTag);
                
                newState.setNonEmpty();
                newState.setBaseAttackModifier(oldState.getBaseAttackModifier());
                newState.setMaxDamage(oldState.getMaxDamage());
                newState.setComboRoot(oldState.getComboRoot());
                newState.setSlashArtsKey(oldState.getSlashArtsKey());
                
                newState.getSpecialEffects().forEach(oldState::addSpecialEffect);
                
                SwordType.from(oldBlade).forEach((type) -> {
                    switch (type) {
                        case BEWITCHED:
                            newState.setDefaultBewitched(true);
                            break;
                        case BROKEN:
                            newBlade.setDamageValue(newBlade.getMaxDamage() - 1);
                            newState.setBroken(true);
                            break;
                        case SEALED:
                            newState.setSealed(true);
                            break;
                        default:
                    }
                });
                
                newState.setColorCode(oldState.getColorCode());
                newState.setEffectColorInverse(oldState.isEffectColorInverse());
                newState.setCarryType(oldState.getCarryType());
            })
        );
    }
    
    public static void setNewBladeEnchantments(ItemStack oldBlade, SlashVillagerProfessionSettings professionSettings, ItemStack newBlade) {
        Map<Enchantment, Integer> allEnchantments = oldBlade.getAllEnchantments();
        professionSettings.defaultBladeEnchantments.forEach(enchantmentIntegerEntry -> {
            Integer i = allEnchantments.get(enchantmentIntegerEntry.getFirst());
            if (i != null) {
                if (i < enchantmentIntegerEntry.getSecond()) {
                    allEnchantments.put(enchantmentIntegerEntry.getFirst(), enchantmentIntegerEntry.getSecond());
                }
            } else {
                allEnchantments.put(enchantmentIntegerEntry.getFirst(), enchantmentIntegerEntry.getSecond());
            }
        });
        EnchantmentHelper.setEnchantments(allEnchantments, newBlade);
    }
}
