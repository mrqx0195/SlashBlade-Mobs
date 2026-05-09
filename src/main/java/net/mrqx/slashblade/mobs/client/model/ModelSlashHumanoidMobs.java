package net.mrqx.slashblade.mobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.mrqx.sbr_core.client.model.ISlashBladeEntityModel;
import net.mrqx.sbr_core.client.utils.ModelUtils;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

import java.util.HashMap;
import java.util.Map;

public class ModelSlashHumanoidMobs<T extends Mob & ISlashBladeEntity> extends HumanoidModel<T> implements ISlashBladeEntityModel {
    protected final ModelPart root;
    protected final Map<ModelPart, String> partMap;
    protected float bodyRotX = 0, bodyRotY = 0, bodyRotZ = 0;
    
    public ModelSlashHumanoidMobs(ModelPart root) {
        super(root);
        this.root = root;
        this.partMap = new HashMap<>();
        partMap.put(this.head, "head");
        partMap.put(this.hat, "head");
        partMap.put(this.leftArm, "left arm");
        partMap.put(this.rightArm, "right arm");
        partMap.put(this.leftLeg, "left leg");
        partMap.put(this.rightLeg, "right leg");
        partMap.put(this.body, "torso");
    }
    
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelUtils.processAnimation(entity, this);
    }
    
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        ModelUtils.processSlashModel(poseStack, this, poseStack1 -> {
            if (this.young) {
                ModelUtils.processYoungHumanoidModel(poseStack1,
                    poseStack2 -> this.headParts().forEach(modelPart ->
                        modelPart.render(poseStack2, buffer, packedLight, packedOverlay, color)),
                    poseStack2 -> this.bodyParts().forEach(modelPart ->
                        modelPart.render(poseStack, buffer, packedLight, packedOverlay, color)));
            } else {
                this.headParts().forEach(modelPart ->
                    modelPart.render(poseStack, buffer, packedLight, packedOverlay, color));
                this.bodyParts().forEach(modelPart ->
                    modelPart.render(poseStack, buffer, packedLight, packedOverlay, color));
            }
        });
    }
    
    @Override
    protected void setupAttackAnimation(T livingEntity, float ageInTicks) {
    }
    
    @Override
    public ModelPart getBody() {
        return this.body;
    }
    
    @Override
    public float getBodyRotX() {
        return bodyRotX;
    }
    
    @Override
    public float getBodyRotY() {
        return bodyRotY;
    }
    
    @Override
    public float getBodyRotZ() {
        return bodyRotZ;
    }
    
    @Override
    public void setBodyRotX(float bodyRotX) {
        this.bodyRotX = bodyRotX;
    }
    
    @Override
    public void setBodyRotY(float bodyRotY) {
        this.bodyRotY = bodyRotY;
    }
    
    @Override
    public void setBodyRotZ(float bodyRotZ) {
        this.bodyRotZ = bodyRotZ;
    }
    
    @Override
    public Map<ModelPart, String> getPartMap() {
        return partMap;
    }
}