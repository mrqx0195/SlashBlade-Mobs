package net.mrqx.slashblade.mobs;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.mrqx.slashblade.mobs.registy.SlashMobsItems;
import net.mrqx.slashblade.mobs.registy.SlashMobsSpawnModifiers;
import net.mrqx.slashblade.mobs.registy.SlashMobsVillagerProfessions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SlashBladeMobs.MODID)
public class SlashBladeMobs {
    public static final String MODID = "slashblade_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static ResourceLocation prefix(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
    
    public SlashBladeMobs(IEventBus modEventBus, ModContainer container) {
        SlashMobsEntities.ENTITIES.register(modEventBus);
        SlashMobsItems.ITEMS.register(modEventBus);
        SlashMobsSpawnModifiers.BIOME_MODIFIERS.register(modEventBus);
        SlashMobsSpawnModifiers.STRUCTURE_MODIFIERS.register(modEventBus);
        SlashMobsVillagerProfessions.VILLAGER_PROFESSIONS.register(modEventBus);
    }
}
