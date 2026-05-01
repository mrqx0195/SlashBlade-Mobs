package net.mrqx.slashblade.mobs.registy;

import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.mrqx.slashblade.mobs.SlashBladeMobs;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlashMobsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlashBladeMobs.MODID);
    
    public static final Supplier<Item> SLASH_ZOMBIE_SPAWN_EGG = ITEMS.register("slash_zombie_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_ZOMBIE, 0x4444FF, 0x799C65, (new Item.Properties())));
    public static final Supplier<Item> SLASH_HUSK_SPAWN_EGG = ITEMS.register("slash_husk_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_HUSK, 0x4444FF, 0xE6CC94, (new Item.Properties())));
    public static final Supplier<Item> SLASH_DROWNED_SPAWN_EGG = ITEMS.register("slash_drowned_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_DROWNED, 0x4444FF, 0x8FF1D7, (new Item.Properties())));
    public static final Supplier<Item> SLASH_SKELETON_SPAWN_EGG = ITEMS.register("slash_skeleton_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_SKELETON, 0x4444FF, 0x494949, (new Item.Properties())));
    public static final Supplier<Item> SLASH_STRAY_SPAWN_EGG = ITEMS.register("slash_stray_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_STRAY, 0x4444FF, 0xDDEAEA, (new Item.Properties())));
    public static final Supplier<Item> SLASH_WITHER_SKELETON_SPAWN_EGG = ITEMS.register("slash_wither_skeleton_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_WITHER_SKELETON, 0x4444FF, 0x474D4D, (new Item.Properties())));
    public static final Supplier<Item> SLASH_VILLAGER_SPAWN_EGG = ITEMS.register("slash_villager_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_VILLAGER, 0x4444FF, 0xBD8B72, (new Item.Properties())));
    public static final Supplier<Item> SLASH_ILLAGER_SPAWN_EGG = ITEMS.register("slash_illager_spawn_egg",
        () -> new ForgeSpawnEggItem(SlashMobsEntities.SLASH_ILLAGER, 0x4444FF, 0x959B9B, (new Item.Properties())));
    
    @SubscribeEvent
    public static void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab().equals(SlashBladeCreativeGroup.SLASHBLADE_GROUP.get())) {
            ITEMS.getEntries().forEach(event::accept);
        }
    }
}
