package net.mrqx.slashblade.mobs.registy;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.entity.*;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashIllager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class SlashMobsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SlashBladeMobs.MODID);
    
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashZombie>> SLASH_ZOMBIE = ENTITIES.register("slash_zombie", () ->
        EntityType.Builder.of(EntitySlashZombie::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_zombie").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashHusk>> SLASH_HUSK = ENTITIES.register("slash_husk", () ->
        EntityType.Builder.of(EntitySlashHusk::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_husk").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashDrowned>> SLASH_DROWNED = ENTITIES.register("slash_drowned", () ->
        EntityType.Builder.of(EntitySlashDrowned::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_drowned").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashSkeleton>> SLASH_SKELETON = ENTITIES.register("slash_skeleton", () ->
        EntityType.Builder.of(EntitySlashSkeleton::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(8)
            .build(SlashBladeMobs.prefix("slash_skeleton").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashStray>> SLASH_STRAY = ENTITIES.register("slash_stray", () ->
        EntityType.Builder.of(EntitySlashStray::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(8).immuneTo(Blocks.POWDER_SNOW)
            .build(SlashBladeMobs.prefix("slash_stray").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashWitherSkeleton>> SLASH_WITHER_SKELETON = ENTITIES.register("slash_wither_skeleton", () ->
        EntityType.Builder.of(EntitySlashWitherSkeleton::new, MobCategory.MONSTER)
            .sized(0.7F, 2.4F).clientTrackingRange(8).fireImmune().immuneTo(Blocks.WITHER_ROSE)
            .build(SlashBladeMobs.prefix("slash_wither_skeleton").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashVillager>> SLASH_VILLAGER = ENTITIES.register("slash_villager", () ->
        EntityType.Builder.of(EntitySlashVillager::newInstance, MobCategory.MISC)
            .sized(0.6F, 1.95F).clientTrackingRange(10)
            .build(SlashBladeMobs.prefix("slash_villager").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySlashIllager>> SLASH_ILLAGER = ENTITIES.register("slash_illager", () ->
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
    public static void onRegisterSpawnPlacementsEvent(RegisterSpawnPlacementsEvent event) {
        event.register(SLASH_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_HUSK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashHusk::checkSlashHuskSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_DROWNED.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashDrowned::checkSlashDrownedSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_SKELETON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_STRAY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            EntitySlashStray::checkSlashStraySpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_WITHER_SKELETON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_VILLAGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SLASH_ILLAGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            PatrollingMonster::checkPatrollingMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}
