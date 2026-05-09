package net.mrqx.slashblade.mobs.registy;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.neoforged.neoforge.common.world.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SlashMobsSpawnModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
    public static final DeferredRegister<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
    
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<CommonBiomeModifier>> SLASHBLADE_MOBS_BIOME_MODIFIERS = BIOME_MODIFIERS.register("slashblade_mobs_biomes_modifiers", CommonBiomeModifier::makeCodec);
    public static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<CommonStructureModifier>> SLASHBLADE_MOBS_STRUCTURE_MODIFIERS = STRUCTURE_MODIFIERS.register("slashblade_mobs_structure_modifiers", CommonStructureModifier::makeCodec);
    
    private static List<MobSpawnSettings.SpawnerData> modifySpawnerData(List<MobSpawnSettings.SpawnerData> spawnerDataList) {
        List<MobSpawnSettings.SpawnerData> tempList = new ArrayList<>();
        spawnerDataList.forEach(spawner -> {
            if (spawner.type.equals(EntityType.ZOMBIE)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_ZOMBIE.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.HUSK)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_HUSK.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.DROWNED)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_DROWNED.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.SKELETON)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_SKELETON.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.STRAY)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_STRAY.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.WITHER_SKELETON)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_WITHER_SKELETON.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
            if (spawner.type.equals(EntityType.PILLAGER)) {
                tempList.add(new MobSpawnSettings.SpawnerData(SlashMobsEntities.SLASH_ILLAGER.get(),
                    Weight.of((int) Math.max(spawner.getWeight().asInt() * 0.1, 1)), spawner.minCount, spawner.maxCount));
            }
        });
        return tempList;
    }
    
    public static class CommonBiomeModifier implements BiomeModifier {
        private static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<? extends BiomeModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SlashBladeMobs.prefix("slashblade_mobs_biomes_modifiers"));
        
        public CommonBiomeModifier() {
        }
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                List<MobSpawnSettings.SpawnerData> spawnerDataList = builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER);
                List<MobSpawnSettings.SpawnerData> spawnerData = modifySpawnerData(spawnerDataList);
                spawnerDataList.addAll(spawnerData);
            }
        }
        
        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return SERIALIZER.get();
        }
        
        public static MapCodec<CommonBiomeModifier> makeCodec() {
            return MapCodec.unit(CommonBiomeModifier::new);
        }
    }
    
    public static class CommonStructureModifier implements StructureModifier {
        private static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<? extends StructureModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, SlashBladeMobs.prefix("slashblade_mobs_structure_modifiers"));
        
        public CommonStructureModifier() {
        }
        
        @Override
        public void modify(Holder<Structure> biome, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (phase == Phase.ADD) {
                StructureSettingsBuilder.StructureSpawnOverrideBuilder spawnOverrides = builder.getStructureSettings().getSpawnOverrides(MobCategory.MONSTER);
                if (spawnOverrides != null) {
                    List<MobSpawnSettings.SpawnerData> spawnerDataList = spawnOverrides.getSpawns();
                    List<MobSpawnSettings.SpawnerData> spawnerData = modifySpawnerData(spawnerDataList);
                    spawnerData.forEach(spawnOverrides::addSpawn);
                }
            }
        }
        
        @Override
        public MapCodec<? extends StructureModifier> codec() {
            return SERIALIZER.get();
        }
        
        public static MapCodec<CommonStructureModifier> makeCodec() {
            return MapCodec.unit(CommonStructureModifier::new);
        }
    }
}
