package net.mrqx.slashblade.mobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityArmor;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;
import net.mrqx.slashblade.mobs.client.layer.LayerSlashDrownedOuter;
import net.mrqx.slashblade.mobs.client.model.ModelSlashDrowned;
import net.mrqx.slashblade.mobs.entity.EntitySlashDrowned;

@OnlyIn(Dist.CLIENT)
public class RendererSlashDrowned extends HumanoidMobRenderer<EntitySlashDrowned, ModelSlashDrowned<EntitySlashDrowned>> {
    private static final ResourceLocation DROWNED_LOCATION = ResourceLocation.parse("textures/entity/zombie/drowned.png");
    
    public RendererSlashDrowned(EntityRendererProvider.Context context) {
        super(context, new ModelSlashDrowned<>(context.bakeLayer(ModelLayers.DROWNED)), 0.5F);
        this.addLayer(new LayerSlashEntityArmor<>(this,
            new ModelSlashDrowned<>(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
            new ModelSlashDrowned<>(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)),
            context.getModelManager()));
        this.addLayer(new LayerSlashDrownedOuter(this, context.getModelSet()));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashDrowned entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
        float f = entityLiving.getSwimAmount(partialTicks);
        if (f > 0.0F) {
            float f1 = -10.0F - entityLiving.getXRot();
            float f2 = Mth.lerp(f, 0.0F, f1);
            poseStack.rotateAround(Axis.XP.rotationDegrees(f2), 0.0F, entityLiving.getBbHeight() / 2.0F, 0.0F);
        }
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashDrowned entity) {
        return DROWNED_LOCATION;
    }
}
