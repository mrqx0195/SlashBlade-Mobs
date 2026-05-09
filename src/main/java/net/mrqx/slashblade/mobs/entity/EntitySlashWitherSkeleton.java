package net.mrqx.slashblade.mobs.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleSlashGoal;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class EntitySlashWitherSkeleton extends WitherSkeleton implements ISlashBladeEntity {
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;
    
    public EntitySlashWitherSkeleton(EntityType<? extends WitherSkeleton> entityType, Level level) {
        super(entityType, level);
        this.xpReward *= 2;
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ENTITY_INTERACTION_RANGE, 5.0)
            .add(Attributes.SWEEPING_DAMAGE_RATIO);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new SimpleSlashGoal<>(this, 1.1, 7, false,
            true, true, true, false, false, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        int i = random.nextInt(20);
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        if (i < this.level().getDifficulty().getId()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.SANGE.location())).getBlade(this.registryAccess()));
        } else {
            if (i < this.level().getDifficulty().getId() * 4) {
                this.setItemSlot(EquipmentSlot.MAINHAND, Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.SABIGATANA.location())).getBlade(this.registryAccess()));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.SABIGATANA_BROKEN.location())).getBlade(this.registryAccess()));
            }
        }
    }
    
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(level, difficulty, reason, spawnData);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(2.0);
        return spawnGroupData;
    }
    
    @Override
    protected void populateDefaultEquipmentEnchantments(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty) {
        this.enchantSpawnedWeapon(level, random, difficulty);
        
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            if (equipmentslot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                this.enchantSpawnedArmor(level, random, equipmentslot, difficulty);
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
        
        CompoundTag data = this.getPersistentData();
        int voidSlashCounter = data.getInt(SlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY);
        if (voidSlashCounter > 0) {
            data.putInt(SlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY, voidSlashCounter - 1);
        }
    }
    
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return BladeStateAccess.of(this.getMainHandItem()).map(state -> {
            double reach = TargetSelector.getResolvedReach(this);
            return this.distanceTo(entity) < reach * reach;
        }).orElse(false);
    }
    
    @Override
    public void hitEffect(LivingEntity enemy) {
        enemy.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
    }
    
    @Override
    public boolean canProgressCombo(@Nullable LivingEntity target, ResourceLocation current, ResourceLocation next) {
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
        return Set.of(Player.class, IronGolem.class, Turtle.class, AbstractPiglin.class, Wolf.class);
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
