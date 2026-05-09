package net.mrqx.slashblade.mobs.utils;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.mrqx.slashblade.mobs.entity.villager.SlashVillagerProfessionSettings;

public class SlashMobsUtils {
    public static void restoreBladeData(ItemStack newBlade, ItemStack oldBlade) {
        BladeStateAccess.of(newBlade).ifPresent(newState ->
            BladeStateAccess.of(oldBlade).ifPresent(oldState -> {
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
    
    public static void setNewBladeEnchantments(ItemStack oldBlade, SlashVillagerProfessionSettings professionSettings, ItemStack newBlade, HolderLookup.RegistryLookup<Enchantment> lookup) {
        ItemEnchantments.Mutable allEnchantments = new ItemEnchantments.Mutable(oldBlade.getAllEnchantments(lookup));
        professionSettings.defaultBladeEnchantments.forEach(enchantmentIntegerEntry -> {
            Holder.Reference<Enchantment> holder = lookup.getOrThrow(enchantmentIntegerEntry.getFirst());
            int i = allEnchantments.getLevel(holder);
            if (i != 0) {
                if (i < enchantmentIntegerEntry.getSecond()) {
                    allEnchantments.set(holder, enchantmentIntegerEntry.getSecond());
                }
            } else {
                allEnchantments.set(holder, enchantmentIntegerEntry.getSecond());
            }
        });
        EnchantmentHelper.setEnchantments(newBlade, allEnchantments.toImmutable());
    }
}
