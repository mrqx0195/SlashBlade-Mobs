package net.mrqx.slashblade.mobs.registy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.entity.*;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashIllager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlashMobsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SlashBladeMobs.MODID);
    
    public static final RegistryObject<EntityType<EntitySlashZombie>> SLASH_ZOMBIE = ENTITIES.register("slash_zombie", () ->
        EntityType.Builder.of(EntitySlashZombie::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_zombie").toString()));
    public static final RegistryObject<EntityType<EntitySlashHusk>> SLASH_HUSK = ENTITIES.register("slash_husk", () ->
        EntityType.Builder.of(EntitySlashHusk::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_husk").toString()));
    public static final RegistryObject<EntityType<EntitySlashDrowned>> SLASH_DROWNED = ENTITIES.register("slash_drowned", () ->
        EntityType.Builder.of(EntitySlashDrowned::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_drowned").toString()));
    public static final RegistryObject<EntityType<EntitySlashSkeleton>> SLASH_SKELETON = ENTITIES.register("slash_skeleton", () ->
        EntityType.Builder.of(EntitySlashSkeleton::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_skeleton").toString()));
    public static final RegistryObject<EntityType<EntitySlashStray>> SLASH_STRAY = ENTITIES.register("slash_stray", () ->
        EntityType.Builder.of(EntitySlashStray::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(8).immuneTo(Blocks.POWDER_SNOW)
            .build(SlashBladeMobs.prefix("slash_stray").toString()));
    public static final RegistryObject<EntityType<EntitySlashWitherSkeleton>> SLASH_WITHER_SKELETON = ENTITIES.register("slash_wither_skeleton", () ->
        EntityType.Builder.of(EntitySlashWitherSkeleton::new, MobCategory.MONSTER)
            .sized(0.7F, 2.4F).clientTrackingRange(8).fireImmune().immuneTo(Blocks.WITHER_ROSE)
            .build(SlashBladeMobs.prefix("slash_wither_skeleton").toString()));
    public static final RegistryObject<EntityType<EntitySlashVillager>> SLASH_VILLAGER = ENTITIES.register("slash_villager", () ->
        EntityType.Builder.of(EntitySlashVillager::newInstance, MobCategory.MISC)
            .sized(0.6F, 1.95F).clientTrackingRange(10)
            .build(SlashBladeMobs.prefix("slash_villager").toString()));
    public static final RegistryObject<EntityType<EntitySlashIllager>> SLASH_ILLAGER = ENTITIES.register("slash_illager", () ->
        EntityType.Builder.of(EntitySlashIllager::new, MobCategory.MONSTER)
            .canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_illager").toString()));
    
    @SubscribeEvent
    public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        event.put(SLASH_ZOMBIE.get(), EntitySlashZombie.createAttributes().build());
        event.put(SLASH_HUSK.get(), EntitySlashHusk.createAttributes().build());
        event.put(SLASH_DROWNED.get(), EntitySlashDrowned.createAttributes().build());
        event.put(SLASH_SKELETON.get(), EntitySlashSkeleton.createAttributes().build());
        event.put(SLASH_STRAY.get(), EntitySlashStray.createAttributes().build());
        event.put(SLASH_WITHER_SKELETON.get(), EntitySlashWitherSkeleton.createAttributes().build());
        event.put(SLASH_VILLAGER.get(), EntitySlashVillager.createAttributes().build());
        event.put(SLASH_ILLAGER.get(), EntitySlashIllager.createAttributes().build());
    }
    
    @SubscribeEvent
    public static void onSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
        event.register(SLASH_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_HUSK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashHusk::checkSlashHuskSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_DROWNED.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashDrowned::checkSlashDrownedSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_SKELETON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_STRAY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashStray::checkSlashStraySpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_WITHER_SKELETON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_VILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SLASH_ILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            PatrollingMonster::checkPatrollingMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
