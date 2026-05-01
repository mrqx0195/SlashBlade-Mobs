package net.mrqx.slashblade.mobs.registy;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mrqx.slashblade.mobs.SlashBladeMobs;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SlashMobsSpawnModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
    public static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
    
    public static final RegistryObject<Codec<CommonBiomeModifier>> SLASHBLADE_MOBS_BIOME_MODIFIERS = BIOME_MODIFIERS.register("slashblade_mobs_biomes_modifiers", CommonBiomeModifier::makeCodec);
    public static final RegistryObject<Codec<CommonStructureModifier>> SLASHBLADE_MOBS_STRUCTURE_MODIFIERS = STRUCTURE_MODIFIERS.register("slashblade_mobs_structure_modifiers", CommonStructureModifier::makeCodec);
    
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
        private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(SlashBladeMobs.prefix("slashblade_mobs_biomes_modifiers"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
        
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
        public Codec<? extends BiomeModifier> codec() {
            return SERIALIZER.get();
        }
        
        public static Codec<CommonBiomeModifier> makeCodec() {
            return Codec.unit(CommonBiomeModifier::new);
        }
    }
    
    public static class CommonStructureModifier implements StructureModifier {
        private static final RegistryObject<Codec<? extends StructureModifier>> SERIALIZER = RegistryObject.create(SlashBladeMobs.prefix("slashblade_mobs_structure_modifiers"), ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, SlashBladeMobs.MODID);
        
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
        public Codec<? extends StructureModifier> codec() {
            return SERIALIZER.get();
        }
        
        public static Codec<CommonStructureModifier> makeCodec() {
            return Codec.unit(CommonStructureModifier::new);
        }
    }
}
