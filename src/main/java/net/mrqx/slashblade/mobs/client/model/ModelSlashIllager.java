package net.mrqx.slashblade.mobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.client.model.ISlashBladeEntityModel;
import net.mrqx.sbr_core.client.utils.ModelUtils;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ModelSlashIllager<T extends AbstractIllager & ISlashBladeEntity> extends IllagerModel<T> implements ISlashBladeEntityModel {
    protected final ModelPart root;
    protected final Map<ModelPart, String> partMap;
    protected float bodyRotX = 0, bodyRotY = 0, bodyRotZ = 0;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart hat;
    protected final ModelPart arms;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    protected final ModelPart rightArm;
    protected final ModelPart leftArm;
    
    public ModelSlashIllager(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.body = this.root.getChild("body");
        this.arms = root.getChild("arms");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        
        this.partMap = new HashMap<>();
        partMap.put(this.head, "head");
        partMap.put(this.hat, "head");
        partMap.put(this.leftArm, "left arm");
        partMap.put(this.rightArm, "right arm");
        partMap.put(this.arms, "body");
        partMap.put(this.leftLeg, "left leg");
        partMap.put(this.rightLeg, "right leg");
        partMap.put(this.body, "torso");
    }
    
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelUtils.processAnimation(entity, this);
        
        boolean flag2 = entity.isAggressive();
        this.arms.visible = !flag2;
        this.leftArm.visible = flag2;
        this.rightArm.visible = flag2;
    }
    
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ModelUtils.processSlashModel(poseStack, this, poseStack1 -> {
            if (this.young) {
                ModelUtils.processYoungHumanoidModel(poseStack1,
                    poseStack2 -> this.headParts().forEach(modelPart ->
                        modelPart.render(poseStack2, buffer, packedLight, packedOverlay, red, green, blue, alpha)),
                    poseStack2 -> this.bodyParts().forEach(modelPart ->
                        modelPart.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha)));
            } else {
                this.headParts().forEach(modelPart ->
                    modelPart.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
                this.bodyParts().forEach(modelPart ->
                    modelPart.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
            }
        });
    }
    
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }
    
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.arms, this.rightLeg, this.leftLeg);
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
