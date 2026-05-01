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
import net.mrqx.slashblade.mobs.entity.EntitySlashZombie;

@OnlyIn(Dist.CLIENT)
public class RendererSlashZombie extends HumanoidMobRenderer<EntitySlashZombie, ModelSlashHumanoidMobs<EntitySlashZombie>> {
    private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.parse("textures/entity/zombie/zombie.png");
    
    public RendererSlashZombie(EntityRendererProvider.Context context) {
        super(context, new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new LayerSlashEntityArmor<>(this,
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
            new ModelSlashHumanoidMobs<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
            context.getModelManager()));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashZombie entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashZombie entity) {
        return ZOMBIE_LOCATION;
    }
    
    @Override
    protected boolean isShaking(EntitySlashZombie entity) {
        return super.isShaking(entity) || entity.isUnderWaterConverting();
    }
}
