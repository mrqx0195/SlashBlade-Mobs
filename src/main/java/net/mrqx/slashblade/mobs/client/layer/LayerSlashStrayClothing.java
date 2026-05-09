package net.mrqx.slashblade.mobs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.slashblade.mobs.client.model.ModelSlashHumanoidMobs;

public class LayerSlashStrayClothing<T extends Mob & ISlashBladeEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation STRAY_CLOTHES_LOCATION = ResourceLocation.parse("textures/entity/skeleton/stray_overlay.png");
    private final ModelSlashHumanoidMobs<T> layerModel;
    
    public LayerSlashStrayClothing(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new ModelSlashHumanoidMobs<>(modelSet.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
    }
    
    @Override
    public void render(
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        T livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        coloredCutoutModelCopyLayerRender(
            this.getParentModel(),
            this.layerModel,
            STRAY_CLOTHES_LOCATION,
            poseStack,
            bufferSource,
            packedLight,
            livingEntity,
            limbSwing,
            limbSwingAmount,
            ageInTicks,
            netHeadYaw,
            headPitch,
            partialTick,
            -1
        );
    }
}
