package net.mrqx.slashblade.mobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.client.model.ISlashBladeEntityModel;
import net.mrqx.sbr_core.client.utils.ModelUtils;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ModelSlashVillager<T extends AbstractVillager & ISlashBladeEntity> extends VillagerModel<T> implements ISlashBladeEntityModel {
    protected final ModelPart root;
    protected final Map<ModelPart, String> partMap;
    protected float bodyRotX = 0, bodyRotY = 0, bodyRotZ = 0;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ModelPart arms;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;

    public ModelSlashVillager(ModelPart root) {
        super(root);
        this.root = root;
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.leftArm = this.root.getChild("left_arm");
        this.rightArm = this.root.getChild("right_arm");
        this.arms = this.root.getChild("arms");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");

        this.partMap = new HashMap<>();
        partMap.put(this.head, "head");
        partMap.put(this.leftArm, "left arm");
        partMap.put(this.rightArm, "right arm");
        partMap.put(this.arms, "body");
        partMap.put(this.leftLeg, "left leg");
        partMap.put(this.rightLeg, "right leg");
        partMap.put(this.body, "torso");
    }

    public static MeshDefinition createMesh() {
        MeshDefinition meshdefinition = VillagerModel.createBodyModel();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        return meshdefinition;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        boolean flag = entity.getFallFlyingTicks() > 4;
        this.rightArm.z = 0.0F;
        this.rightArm.x = -5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        if (this.riding) {
            this.rightArm.xRot += (-(float) Math.PI / 5F);
            this.leftArm.xRot += (-(float) Math.PI / 5F);
        }
        this.rightArm.yRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        if (entity.isCrouching()) {
            this.body.xRot = 0.5F;
            this.rightArm.xRot += 0.4F;
            this.leftArm.xRot += 0.4F;
            this.rightLeg.z = 4.0F;
            this.leftLeg.z = 4.0F;
            this.rightLeg.y = 12.2F;
            this.leftLeg.y = 12.2F;
            this.head.y = 4.2F;
            this.body.y = 3.2F;
            this.leftArm.y = 5.2F;
            this.rightArm.y = 5.2F;
        } else {
            this.body.xRot = 0.0F;
            this.rightLeg.z = 0.0F;
            this.leftLeg.z = 0.0F;
            this.rightLeg.y = 12.0F;
            this.leftLeg.y = 12.0F;
            this.head.y = 0.0F;
            this.body.y = 0.0F;
            this.leftArm.y = 2.0F;
            this.rightArm.y = 2.0F;
        }

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
