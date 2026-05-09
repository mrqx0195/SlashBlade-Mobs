package net.mrqx.slashblade.mobs.registy;

import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import net.minecraft.world.item.Item;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
@EventBusSubscriber
public class SlashMobsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlashBladeMobs.MODID);
    
    public static final DeferredItem<Item> SLASH_ZOMBIE_SPAWN_EGG = ITEMS.register("slash_zombie_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_ZOMBIE, 0x4444FF, 0x799C65, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_HUSK_SPAWN_EGG = ITEMS.register("slash_husk_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_HUSK, 0x4444FF, 0xE6CC94, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_DROWNED_SPAWN_EGG = ITEMS.register("slash_drowned_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_DROWNED, 0x4444FF, 0x8FF1D7, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_SKELETON_SPAWN_EGG = ITEMS.register("slash_skeleton_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_SKELETON, 0x4444FF, 0x494949, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_STRAY_SPAWN_EGG = ITEMS.register("slash_stray_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_STRAY, 0x4444FF, 0xDDEAEA, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_WITHER_SKELETON_SPAWN_EGG = ITEMS.register("slash_wither_skeleton_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_WITHER_SKELETON, 0x4444FF, 0x474D4D, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_VILLAGER_SPAWN_EGG = ITEMS.register("slash_villager_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_VILLAGER, 0x4444FF, 0xBD8B72, (new Item.Properties())));
    public static final DeferredItem<Item> SLASH_ILLAGER_SPAWN_EGG = ITEMS.register("slash_illager_spawn_egg",
        () -> new DeferredSpawnEggItem(SlashMobsEntities.SLASH_ILLAGER, 0x4444FF, 0x959B9B, (new Item.Properties())));
    
    @SubscribeEvent
    public static void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab().equals(SlashBladeCreativeGroup.SLASHBLADE_GROUP.get())) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
