package net.mrqx.slashblade.mobs.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.raid.Raid;
import net.mrqx.slashblade.mobs.SlashBladeMobsEnumProxies;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public abstract class MixinRaid {
    @Inject(method = "getPotentialBonusSpawns(Lnet/minecraft/world/entity/raid/Raid$RaiderType;Lnet/minecraft/util/RandomSource;ILnet/minecraft/world/DifficultyInstance;Z)I", at = @At("HEAD"), cancellable = true)
    private void injectGetPotentialBonusSpawns(Raid.RaiderType raiderType, RandomSource random, int wave,
                                               DifficultyInstance difficulty, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> cir) {
        if (raiderType.equals(SlashBladeMobsEnumProxies.SLASH_ILLAGER_RAIDER_TYPE_ENUM_PROXY.getValue()) && wave >= 3) {
            if (difficulty.getDifficulty() == Difficulty.EASY) {
                cir.setReturnValue(random.nextInt(2));
            } else if (difficulty.getDifficulty() == Difficulty.NORMAL) {
                cir.setReturnValue(1);
            } else {
                cir.setReturnValue(2);
            }
        }
    }
}
