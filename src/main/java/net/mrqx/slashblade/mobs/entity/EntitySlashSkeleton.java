package net.mrqx.slashblade.mobs.entity;

import com.google.common.util.concurrent.AtomicDouble;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleSlashGoal;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;

import javax.annotation.Nullable;
import java.util.Set;

public class EntitySlashSkeleton extends Skeleton implements ISlashBladeEntity {
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;

    public EntitySlashSkeleton(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
        this.xpReward *= 2;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        addSlashSkeletonGoals(this);
    }

    public static <T extends PathfinderMob & ISlashBladeEntity> void addSlashSkeletonGoals(T slashSkeleton) {
        slashSkeleton.goalSelector.addGoal(2, new RestrictSunGoal(slashSkeleton));
        slashSkeleton.goalSelector.addGoal(3, new FleeSunGoal(slashSkeleton, 1.0D));
        slashSkeleton.goalSelector.addGoal(3, new AvoidEntityGoal<>(slashSkeleton, Wolf.class, 6.0F, 1.0D, 1.2D));
        slashSkeleton.goalSelector.addGoal(4, new SimpleSlashGoal<>(slashSkeleton, 1.1, 7, false,
                false, true, false, false, false, false));
        slashSkeleton.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(slashSkeleton, 1.0D));
        slashSkeleton.goalSelector.addGoal(6, new LookAtPlayerGoal(slashSkeleton, Player.class, 8.0F));
        slashSkeleton.goalSelector.addGoal(6, new RandomLookAroundGoal(slashSkeleton));
        slashSkeleton.targetSelector.addGoal(1, new HurtByTargetGoal(slashSkeleton));
        slashSkeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(slashSkeleton, Player.class, true));
        slashSkeleton.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(slashSkeleton, IronGolem.class, true));
        slashSkeleton.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(slashSkeleton, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        int i = random.nextInt(20);
        if (i < this.level().getDifficulty().getId()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_WHITE.get().getDefaultInstance());
        } else {
            if (i < this.level().getDifficulty().getId() * 4) {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_BAMBOO.get().getDefaultInstance());
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_WOOD.get().getDefaultInstance());
            }
        }
    }

    @Override
    public void reassessWeaponGoal() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (this.currentAnimation != null) {
                this.currentAnimation.tick();
                if (!this.currentAnimation.isActive() && this.currentAnimation.getCurrentTick() > 0) {
                    this.currentAnimation = null;
                }
            }
        }

        if (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemSlashBlade) {
            this.getItemInHand(InteractionHand.MAIN_HAND).inventoryTick(this.level(), this, 0, true);
        }
    }

    @Override
    protected void doFreezeConversion() {
        this.convertTo(SlashMobsEntities.SLASH_STRAY.get(), true);
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1048, this.blockPosition(), 0);
        }
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity entity) {
        AtomicDouble attackDistance = new AtomicDouble(super.getMeleeAttackRangeSqr(entity));
        this.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state ->
                attackDistance.set(TargetSelector.getResolvedReach(this)));
        return attackDistance.get() * attackDistance.get();
    }

    @Override
    public boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next) {
        return this.canUseCombo(next);
    }

    @Override
    public boolean canUseCombo(ResourceLocation combo) {
        return !combo.equals(ComboStateRegistry.COMBO_A1.getId()) &&
                !combo.equals(ComboStateRegistry.AERIAL_RAVE_B3.getId()) &&
                !combo.equals(ComboStateRegistry.AERIAL_CLEAVE.getId());
    }

    @Override
    public Set<Class<? extends Entity>> getAttackableEntities() {
        return Set.of(Player.class, IronGolem.class, Turtle.class, Wolf.class);
    }

    @Override
    public void setCurrentAnimation(@Nullable VanillaConvertedVmdAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
        if (this.currentAnimation != null) {
            this.currentAnimation.play();
        }
    }

    @Override
    public @Nullable VanillaConvertedVmdAnimation getCurrentAnimation() {
        return this.currentAnimation;
    }

    @Override
    public boolean useUpperSlashJump() {
        return true;
    }
}
