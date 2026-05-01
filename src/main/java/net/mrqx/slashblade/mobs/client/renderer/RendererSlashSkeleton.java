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
import net.mrqx.slashblade.mobs.client.model.ModelSlashHumanoidMobs;
import net.mrqx.slashblade.mobs.entity.EntitySlashSkeleton;

@OnlyIn(Dist.CLIENT)
public class RendererSlashSkeleton extends HumanoidMobRenderer<EntitySlashSkeleton, ModelSlashHumanoidMobs<EntitySlashSkeleton>> {
    private static final ResourceLocation SKELETON_LOCATION = ResourceLocation.parse("textures/entity/skeleton/skeleton.png");
    
    public RendererSlashSkeleton(EntityRendererProvider.Context context) {
        super(context, new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
        this.addLayer(new LayerSlashEntityArmor<>(this,
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
            context.getModelManager()));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashSkeleton entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashSkeleton entity) {
        return SKELETON_LOCATION;
    }
    
    @Override
    protected boolean isShaking(EntitySlashSkeleton entity) {
        return super.isShaking(entity);
    }
}
