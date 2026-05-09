package net.mrqx.slashblade.mobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityArmor;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;
import net.mrqx.slashblade.mobs.client.model.ModelSlashHumanoidMobs;
import net.mrqx.slashblade.mobs.entity.EntitySlashHusk;

public class RendererSlashHusk extends HumanoidMobRenderer<EntitySlashHusk, ModelSlashHumanoidMobs<EntitySlashHusk>> {
    private static final ResourceLocation HUSK_LOCATION = ResourceLocation.parse("textures/entity/zombie/husk.png");
    
    public RendererSlashHusk(EntityRendererProvider.Context context) {
        super(context, new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.HUSK)), 0.5F);
        this.addLayer(new LayerSlashEntityArmor<>(this,
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR)),
            context.getModelManager()));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashHusk entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks, scale);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }
    
    @Override
    protected void scale(EntitySlashHusk livingEntity, PoseStack poseStack, float partialTickTime) {
        float f = 1.0625F;
        poseStack.scale(f, f, f);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashHusk entity) {
        return HUSK_LOCATION;
    }
    
    @Override
    protected boolean isShaking(EntitySlashHusk entity) {
        return super.isShaking(entity) || entity.isUnderWaterConverting();
    }
}
