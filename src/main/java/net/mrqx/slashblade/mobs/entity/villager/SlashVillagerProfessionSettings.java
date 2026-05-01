package net.mrqx.slashblade.mobs.entity.villager;

import com.mojang.datafixers.util.Pair;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.mrqx.slashblade.mobs.registy.SlashMobsVillagerProfessions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class SlashVillagerProfessionSettings {
    public final boolean canRapidSlash;
    public final boolean preferAirAttack;
    public final boolean canUseUpperSlashJump;
    public final boolean canVoidSlash;
    public final boolean canDoSlashArts;
    public final boolean canDoJustSlashArts;
    public final boolean powerful;
    public final boolean canUseBaseSummonedSword;
    public final boolean canUseSpiralSword;
    public final boolean canUseStormSword;
    public final boolean canUseBlisteringSword;
    public final boolean canUseHeavyRainSword;
    public final boolean canAirTrick;
    public final boolean canTrickDown;
    public final boolean canTrickDodge;
    public final List<Pair<Enchantment, Integer>> defaultBladeEnchantments;
    @Nullable
    public final BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction;
    @Nullable
    public final PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction;
    
    public SlashVillagerProfessionSettings(boolean canRapidSlash, boolean preferAirAttack, boolean canUseUpperSlashJump, boolean canVoidSlash,
                                           boolean canDoSlashArts, boolean canDoJustSlashArts, boolean powerful,
                                           boolean canUseBaseSummonedSword, boolean canUseSpiralSword, boolean canUseStormSword,
                                           boolean canUseBlisteringSword, boolean canUseHeavyRainSword,
                                           boolean canAirTrick, boolean canTrickDown, boolean canTrickDodge,
                                           List<Pair<Enchantment, Integer>> defaultBladeEnchantments,
                                           @Nullable BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction,
                                           @Nullable PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction) {
        this.canRapidSlash = canRapidSlash;
        this.preferAirAttack = preferAirAttack;
        this.canUseUpperSlashJump = canUseUpperSlashJump;
        this.canVoidSlash = canVoidSlash;
        this.canDoSlashArts = canDoSlashArts;
        this.canDoJustSlashArts = canDoJustSlashArts;
        this.powerful = powerful;
        this.canUseBaseSummonedSword = canUseBaseSummonedSword;
        this.canUseSpiralSword = canUseSpiralSword;
        this.canUseStormSword = canUseStormSword;
        this.canUseBlisteringSword = canUseBlisteringSword;
        this.canUseHeavyRainSword = canUseHeavyRainSword;
        this.canAirTrick = canAirTrick;
        this.canTrickDown = canTrickDown;
        this.canTrickDodge = canTrickDodge;
        this.defaultBladeEnchantments = defaultBladeEnchantments;
        this.canUseComboFunction = canUseComboFunction;
        this.canProgressComboFunction = canProgressComboFunction;
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings) {
        return withOverrides(overrideSettings, this.defaultBladeEnchantments, this.canUseComboFunction, this.canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         List<Pair<Enchantment, Integer>> defaultBladeEnchantments) {
        return withOverrides(overrideSettings, defaultBladeEnchantments, this.canUseComboFunction, this.canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         @Nullable BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction) {
        return withOverrides(overrideSettings, this.defaultBladeEnchantments, canUseComboFunction, this.canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         List<Pair<Enchantment, Integer>> defaultBladeEnchantments,
                                                         @Nullable BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction) {
        return withOverrides(overrideSettings, defaultBladeEnchantments, canUseComboFunction, this.canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         @Nullable BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction,
                                                         @Nullable PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction) {
        return withOverrides(overrideSettings, this.defaultBladeEnchantments, canUseComboFunction, canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         @Nullable PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction) {
        return withOverrides(overrideSettings, this.defaultBladeEnchantments, this.canUseComboFunction, canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         List<Pair<Enchantment, Integer>> defaultBladeEnchantments,
                                                         @Nullable PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction) {
        return withOverrides(overrideSettings, defaultBladeEnchantments, this.canUseComboFunction, canProgressComboFunction);
    }
    
    public SlashVillagerProfessionSettings withOverrides(Map<String, Boolean> overrideSettings,
                                                         List<Pair<Enchantment, Integer>> defaultBladeEnchantments,
                                                         @Nullable BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction,
                                                         @Nullable PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction) {
        return new SlashVillagerProfessionSettings(
            this.canRapidSlash || overrideSettings.getOrDefault("canRapidSlash", false),
            this.preferAirAttack || overrideSettings.getOrDefault("preferAirAttack", false),
            this.canUseUpperSlashJump || overrideSettings.getOrDefault("canUseUpperSlashJump", false),
            this.canVoidSlash || overrideSettings.getOrDefault("canVoidSlash", false),
            this.canDoSlashArts || overrideSettings.getOrDefault("canDoSlashArts", false),
            this.canDoJustSlashArts || overrideSettings.getOrDefault("canDoJustSlashArts", false),
            this.powerful || overrideSettings.getOrDefault("powerful", false),
            this.canUseBaseSummonedSword || overrideSettings.getOrDefault("canUseBaseSummonedSword", false),
            this.canUseSpiralSword || overrideSettings.getOrDefault("canUseSpiralSword", false),
            this.canUseStormSword || overrideSettings.getOrDefault("canUseStormSword", false),
            this.canUseBlisteringSword || overrideSettings.getOrDefault("canUseBlisteringSword", false),
            this.canUseHeavyRainSword || overrideSettings.getOrDefault("canUseHeavyRainSword", false),
            this.canAirTrick || overrideSettings.getOrDefault("canAirTrick", false),
            this.canTrickDown || overrideSettings.getOrDefault("canTrickDown", false),
            this.canTrickDodge || overrideSettings.getOrDefault("canTrickDodge", false),
            defaultBladeEnchantments,
            canUseComboFunction,
            canProgressComboFunction
        );
    }
    
    protected static final Map<VillagerProfession, Map<Integer, SlashVillagerProfessionSettings>> SLASHBLADE_SAMURAI_SETTINGS = new HashMap<>();
    
    public static final SlashVillagerProfessionSettings EMPTY = new SlashVillagerProfessionSettings(
        false, false, false, false,
        false, false, false,
        false, false, false, false, false,
        false, false, false,
        List.of(),
        null, null
    );
    
    public static final SlashVillagerProfessionSettings A_1 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 1,
        EMPTY.withOverrides(Map.of(),
            (villager, combo) -> !combo.equals(ComboStateRegistry.COMBO_C.getId()) &&
                !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
                !combo.equals(ComboStateRegistry.UPPERSLASH.getId()))
    );
    
    public static final SlashVillagerProfessionSettings A_2 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 2,
        A_1.withOverrides(Map.of("powerful", true))
    );
    
    public static final SlashVillagerProfessionSettings A_3 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 3,
        A_2.withOverrides(Map.of(
            "canRapidSlash", true,
            "canUseBaseSummonedSword", true
        ), List.of(new Pair<>(Enchantments.POWER_ARROWS, 1)))
    );
    
    public static final SlashVillagerProfessionSettings A_4 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 4,
        A_3.withOverrides(Map.of(
            "canDoSlashArts", true,
            "canUseSpiralSword", true
        ))
    );
    
    public static final SlashVillagerProfessionSettings A_5 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 5,
        A_4.withOverrides(Map.of(
            "canVoidSlash", true,
            "canDoJustSlashArts", true,
            "canTrickDodge", true
        ))
    );
    
    public static final SlashVillagerProfessionSettings B_1 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get(), 1,
        EMPTY.withOverrides(Map.of(),
            (villager, combo) -> {
                if (!combo.equals(ComboStateRegistry.COMBO_C.getId()) &&
                    !combo.equals(ComboStateRegistry.COMBO_A4.getId()) &&
                    !combo.equals(ComboStateRegistry.COMBO_A4_EX.getId()) &&
                    !combo.equals(ComboStateRegistry.UPPERSLASH.getId())) {
                    String comboString = combo.toString();
                    if (comboString.startsWith("slashblade:combo_b")
                        && !comboString.endsWith("_end")) {
                        try {
                            return villager.level().random.nextInt(7) + 1 >= Integer.parseInt(comboString.substring(comboString.length() - 1));
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            })
    );
    
    public static final SlashVillagerProfessionSettings B_2 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get(), 2,
        B_1.withOverrides(Map.of(
                "canUseBaseSummonedSword", true
            ), List.of(new Pair<>(Enchantments.POWER_ARROWS, 3)),
            (villager, combo) -> {
                if (!combo.equals(ComboStateRegistry.COMBO_C.getId()) &&
                    !combo.equals(ComboStateRegistry.COMBO_A4.getId()) &&
                    !combo.equals(ComboStateRegistry.COMBO_A4_EX.getId()) &&
                    !combo.equals(ComboStateRegistry.UPPERSLASH.getId())) {
                    String comboString = combo.toString();
                    if (comboString.startsWith("slashblade:combo_b")
                        && !comboString.endsWith("_end")) {
                        try {
                            return villager.level().random.nextInt(3, 7) + 1 >= Integer.parseInt(comboString.substring(comboString.length() - 1));
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            })
    );
    
    public static final SlashVillagerProfessionSettings B_3 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get(), 3,
        B_2.withOverrides(Map.of(
            "canUseSpiralSword", true,
            "canUseStormSword", true
        ), (villager, combo) -> !combo.equals(ComboStateRegistry.COMBO_C.getId()) &&
            !combo.equals(ComboStateRegistry.COMBO_A4.getId()) &&
            !combo.equals(ComboStateRegistry.COMBO_A4_EX.getId()) &&
            !combo.equals(ComboStateRegistry.UPPERSLASH.getId()))
    );
    
    public static final SlashVillagerProfessionSettings B_4 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get(), 4,
        B_3.withOverrides(Map.of(
            "canUseBlisteringSword", true,
            "canUseHeavyRainSword", true
        ))
    );
    
    public static final SlashVillagerProfessionSettings B_5 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get(), 5,
        B_4.withOverrides(Map.of(
            "powerful", true,
            "canAirTrick", true
        ), List.of(new Pair<>(Enchantments.POWER_ARROWS, 5)))
    );
    
    public static final SlashVillagerProfessionSettings C_1 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get(), 1,
        EMPTY.withOverrides(Map.of(),
            (villager, combo) -> !combo.equals(ComboStateRegistry.COMBO_A3.getId()) &&
                !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
                !combo.equals(ComboStateRegistry.UPPERSLASH.getId()))
    );
    
    public static final SlashVillagerProfessionSettings C_2 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get(), 2,
        C_1.withOverrides(Map.of(
            "preferAirAttack", true,
            "canUseUpperSlashJump", true
        ), (villager, combo) -> !combo.equals(ComboStateRegistry.COMBO_A3.getId()) &&
            !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
            !combo.equals(ComboStateRegistry.AERIAL_RAVE_B3.getId()))
    );
    
    public static final SlashVillagerProfessionSettings C_3 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get(), 3,
        C_2.withOverrides(Map.of(
                "canUseBaseSummonedSword", true
            ), List.of(new Pair<>(Enchantments.POWER_ARROWS, 1)),
            (villager, combo) -> !combo.equals(ComboStateRegistry.COMBO_A3.getId()) &&
                !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
                !combo.equals(ComboStateRegistry.AERIAL_RAVE_A3.getId()))
    );
    
    public static final SlashVillagerProfessionSettings C_4 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get(), 4,
        C_3.withOverrides(Map.of(
            "canDoSlashArts", true,
            "canUseStormSword", true
        ))
    );
    
    public static final SlashVillagerProfessionSettings C_5 = registerSettings(SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get(), 5,
        C_4.withOverrides(Map.of(
            "canDoJustSlashArts", true,
            "powerful", true,
            "canTrickDown", true
        ))
    );
    
    public static SlashVillagerProfessionSettings registerSettings(VillagerProfession profession, int level, SlashVillagerProfessionSettings settings) {
        SLASHBLADE_SAMURAI_SETTINGS.computeIfAbsent(profession, k -> new HashMap<>(5)).put(level, settings);
        return settings;
    }
    
    @Nullable
    public static SlashVillagerProfessionSettings getSettings(VillagerProfession profession, int level) {
        Map<Integer, SlashVillagerProfessionSettings> levelMap = SLASHBLADE_SAMURAI_SETTINGS.get(profession);
        return levelMap != null ? levelMap.get(level) : null;
    }
}