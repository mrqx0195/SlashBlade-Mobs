package net.mrqx.slashblade.mobs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.slashblade.mobs.client.model.ModelSlashDrowned;
import net.mrqx.slashblade.mobs.entity.EntitySlashDrowned;

@OnlyIn(Dist.CLIENT)
public class LayerSlashDrownedOuter extends RenderLayer<EntitySlashDrowned, ModelSlashDrowned<EntitySlashDrowned>> {
    private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = ResourceLocation.parse("textures/entity/zombie/drowned_outer_layer.png");
    private final ModelSlashDrowned<EntitySlashDrowned> model;
    
    public LayerSlashDrownedOuter(RenderLayerParent<EntitySlashDrowned, ModelSlashDrowned<EntitySlashDrowned>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new ModelSlashDrowned<>(modelSet.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EntitySlashDrowned livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
    }
}