package net.mrqx.slashblade.mobs.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.client.model.ModelSlashVillager;
import net.mrqx.slashblade.mobs.client.renderer.*;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {
    public static final ModelLayerLocation SLASH_VILLAGER = new ModelLayerLocation(SlashBladeMobs.prefix("slash_villager"), "main");
    
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SlashMobsEntities.SLASH_ZOMBIE.get(), RendererSlashZombie::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_HUSK.get(), RendererSlashHusk::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_DROWNED.get(), RendererSlashDrowned::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_SKELETON.get(), RendererSlashSkeleton::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_STRAY.get(), RendererSlashStray::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_WITHER_SKELETON.get(), RendererSlashWitherSkeleton::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_VILLAGER.get(), RendererSlashVillager::new);
        event.registerEntityRenderer(SlashMobsEntities.SLASH_ILLAGER.get(), RendererSlashIllager::new);
    }
    
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SLASH_VILLAGER, () -> LayerDefinition.create(ModelSlashVillager.createMesh(), 64, 64));
    }
}
