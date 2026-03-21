package net.mrqx.slashblade.mobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;
import net.mrqx.slashblade.mobs.client.model.ModelSlashIllager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashIllager;

@OnlyIn(Dist.CLIENT)
public class RendererSlashIllager extends IllagerRenderer<EntitySlashIllager> {
    private static final ResourceLocation VINDICATOR = ResourceLocation.parse("textures/entity/illager/vindicator.png");

    public RendererSlashIllager(EntityRendererProvider.Context context) {
        super(context, new ModelSlashIllager<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }

    @Override
    protected void setupRotations(EntitySlashIllager entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySlashIllager entity) {
        return VINDICATOR;
    }
}
