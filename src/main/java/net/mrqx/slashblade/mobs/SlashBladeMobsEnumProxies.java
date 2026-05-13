package net.mrqx.slashblade.mobs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class SlashBladeMobsEnumProxies {
    public static final EnumProxy<Raid.RaiderType> SLASH_ILLAGER_RAIDER_TYPE_ENUM_PROXY = new EnumProxy<>(Raid.RaiderType.class,
        (Supplier<EntityType<? extends Raider>>) SlashMobsEntities.SLASH_ILLAGER::get, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
}
