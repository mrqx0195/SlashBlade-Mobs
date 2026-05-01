package net.mrqx.slashblade.mobs;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.mrqx.slashblade.mobs.registy.SlashMobsItems;
import net.mrqx.slashblade.mobs.registy.SlashMobsSpawnModifiers;
import net.mrqx.slashblade.mobs.registy.SlashMobsVillagerProfessions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(SlashBladeMobs.MODID)
@Mod.EventBusSubscriber(modid = SlashBladeMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlashBladeMobs {
    public static final String MODID = "slashblade_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    public static Raid.RaiderType SLASH_ILLAGER_RAIDER_TYPE;
    
    public static ResourceLocation prefix(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
    
    public SlashBladeMobs(FMLJavaModLoadingContext modLoadingContext) {
        IEventBus modEventBus = modLoadingContext.getModEventBus();
        SlashMobsEntities.ENTITIES.register(modEventBus);
        SlashMobsItems.ITEMS.register(modEventBus);
        SlashMobsSpawnModifiers.BIOME_MODIFIERS.register(modEventBus);
        SlashMobsSpawnModifiers.STRUCTURE_MODIFIERS.register(modEventBus);
        SlashMobsVillagerProfessions.VILLAGER_PROFESSIONS.register(modEventBus);
    }
    
    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        SLASH_ILLAGER_RAIDER_TYPE = Raid.RaiderType.create("slash_illager", SlashMobsEntities.SLASH_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});
    }
}
