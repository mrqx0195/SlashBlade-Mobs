package net.mrqx.slashblade.mobs.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePoolElement.class)
public class MixinSinglePoolElement {
    @Shadow
    @Final
    protected Either<ResourceLocation, StructureTemplate> template;
    
    @Inject(
        method = "place(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;Z)Z",
        at = @At("RETURN")
    )
    public void place(StructureTemplateManager structureTemplateManager, WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                      BlockPos offset, BlockPos pos, Rotation rotation, BoundingBox box, RandomSource random, LiquidSettings liquidSettings, boolean keepJigsaws, CallbackInfoReturnable<Boolean> cir) {
        this.template.left().ifPresent((resourceLocation) -> {
            if ("minecraft:village/common/iron_golem".equals(resourceLocation.toString())) {
                int maxCount = level.getRandom().nextInt(2) + 1;
                for (int count = 0; count < maxCount; ++count) {
                    EntitySlashVillager slashVillager = new EntitySlashVillager(SlashMobsEntities.SLASH_VILLAGER.get(), level.getLevel());
                    slashVillager.moveTo(offset, 0.0F, 0.0F);
                    slashVillager.finalizeSpawn(level, level.getCurrentDifficultyAt(offset), MobSpawnType.STRUCTURE, null);
                    level.addFreshEntityWithPassengers(slashVillager);
                }
            }
        });
    }
}
