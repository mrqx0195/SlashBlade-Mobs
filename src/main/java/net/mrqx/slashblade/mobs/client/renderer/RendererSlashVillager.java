package net.mrqx.slashblade.mobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.client.ClientHandler;
import net.mrqx.slashblade.mobs.client.model.ModelSlashVillager;
import net.mrqx.slashblade.mobs.entity.villager.EntitySlashVillager;

public class RendererSlashVillager extends MobRenderer<EntitySlashVillager, ModelSlashVillager<EntitySlashVillager>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = SlashBladeMobs.prefix("textures/entity/slash_villager.png");
    
    public RendererSlashVillager(EntityRendererProvider.Context context) {
        super(context, new ModelSlashVillager<>(context.bakeLayer(ClientHandler.SLASH_VILLAGER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager"));
        this.addLayer(new LayerSlashEntityBlade<>(this));
    }
    
    @Override
    protected void setupRotations(EntitySlashVillager entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks);
        VanillaConvertedVmdAnimation currentAnimation = entityLiving.getCurrentAnimation();
        if (currentAnimation != null) {
            currentAnimation.setTickDelta(partialTicks);
        }
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntitySlashVillager entity) {
        return VILLAGER_BASE_SKIN;
    }
    
    @Override
    protected void scale(EntitySlashVillager livingEntity, PoseStack poseStack, float partialTickTime) {
        float f = 0.9375F;
        if (livingEntity.isBaby()) {
            f *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }
        
        poseStack.scale(f, f, f);
    }
}
