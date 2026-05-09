package net.mrqx.slashblade.mobs.registy;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SlashMobsVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, SlashBladeMobs.MODID);
    
    public static final Supplier<VillagerProfession> SLASHBLADE_SAMURAI_A = VILLAGER_PROFESSIONS.register("slashblade_samurai_a",
        () -> new VillagerProfession("slashblade_samurai_a", PoiType.NONE, PoiType.NONE, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final Supplier<VillagerProfession> SLASHBLADE_SAMURAI_B = VILLAGER_PROFESSIONS.register("slashblade_samurai_b",
        () -> new VillagerProfession("slashblade_samurai_b", PoiType.NONE, PoiType.NONE, ImmutableSet.of(), ImmutableSet.of(), null));
    public static final Supplier<VillagerProfession> SLASHBLADE_SAMURAI_C = VILLAGER_PROFESSIONS.register("slashblade_samurai_c",
        () -> new VillagerProfession("slashblade_samurai_c", PoiType.NONE, PoiType.NONE, ImmutableSet.of(), ImmutableSet.of(), null));
}
