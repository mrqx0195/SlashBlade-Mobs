package net.mrqx.slashblade.mobs.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.mrqx.slashblade.mobs.entity.ai.behavior.ShareGossipWithSlashVillager;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is based on the original work from guardvillagers by seymourimadeit.
 * <p>
 * Original source: <a href="https://github.com/seymourimadeit/guardvillagers/blob/1.20.1/src/main/java/tallestegg/guardvillagers/mixins/VillagerGoalPackagesMixin.java">seymourimadeit/guardvillagers/.../VillagerGoalPackagesMixin.java</a>
 * <p>
 * License: <a href="https://github.com/seymourimadeit/guardvillagers/blob/1.20.1/LICENSE.txt">MIT License</a>
 *
 * @author seymourimadeit
 */
@Mixin(VillagerGoalPackages.class)
public class MixinVillagerGoalPackages {
    @Inject(method = "getMeetPackage", cancellable = true, at = @At("RETURN"))
    private static void getMeetPackage(VillagerProfession profession, float speedModifier, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir) {
        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> villagerList = new ArrayList<>(cir.getReturnValue());
        villagerList.add(Pair.of(2, new GateBehavior<>(ImmutableMap.of(),
            ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
            GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE,
            ImmutableList.of(
                Pair.of(new ShareGossipWithSlashVillager(), 1),
                Pair.of(new TradeWithVillager(), 1)
            ))));
        cir.setReturnValue(ImmutableList.copyOf(villagerList));
    }
    
    @Inject(method = "getIdlePackage", cancellable = true, at = @At("RETURN"))
    private static void getIdlePackage(VillagerProfession profession, float speedModifier, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir) {
        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> villagerList = new ArrayList<>(cir.getReturnValue());
        villagerList.add(Pair.of(2, new RunOne<>(
            ImmutableList.of(
                Pair.of(InteractWith.of(SlashMobsEntities.SLASH_VILLAGER.get(), 8, MemoryModuleType.INTERACTION_TARGET, speedModifier, 2), 3),
                Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speedModifier, 2), 3),
                Pair.of(new DoNothing(30, 60), 1)
            ))));
        villagerList.add(Pair.of(2, new GateBehavior<>(ImmutableMap.of(),
            ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
            GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE,
            ImmutableList.of(
                Pair.of(new ShareGossipWithSlashVillager(), 1),
                Pair.of(new TradeWithVillager(), 1)
            ))));
        cir.setReturnValue(ImmutableList.copyOf(villagerList));
    }
}
