package net.mrqx.slashblade.mobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityArmor;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;
import net.mrqx.slashblade.mobs.client.layer.LayerSlashStrayClothing;
import net.mrqx.slashblade.mobs.client.model.ModelSlashHumanoidMobs;
import net.mrqx.slashblade.mobs.entity.EntitySlashStray;

@OnlyIn(Dist.CLIENT)
public class RendererSlashStray extends HumanoidMobRenderer<EntitySlashStray, ModelSlashHumanoidMobs<EntitySlashStray>> {
    private static final ResourceLocation STRAY_SKELETON_LOCATION = ResourceLocation.parse("textures/entity/skeleton/stray.png");
    
    public RendererSlashStray(EntityRendererProvider.Context context) {
        super(context, new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.STRAY)), 0.5F);
        this.addLayer(new LayerSlashEntityArmor<>(this,
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.STRAY_INNER_ARMOR)),
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.STRAY_OUTER_ARMOR)),
            context.getModelManager()));
        this.addLayer(new LayerSlashStrayClothing<>(this, context.getModelSet()));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashStray entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashStray entity) {
        return STRAY_SKELETON_LOCATION;
    }
    
    @Override
    protected boolean isShaking(EntitySlashStray entity) {
        return super.isShaking(entity);
    }
}
