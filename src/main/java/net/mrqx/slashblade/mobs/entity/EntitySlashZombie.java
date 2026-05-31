package net.mrqx.slashblade.mobs.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleSlashGoal;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class EntitySlashZombie extends Zombie implements ISlashBladeEntity {
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;
    
    public EntitySlashZombie(EntityType<? extends EntitySlashZombie> entityType, Level level) {
        super(entityType, level);
        this.xpReward *= 2;
    }
    
    @Override
    protected void addBehaviourGoals() {
        addSlashZombieBehaviours(this);
    }
    
    public static <T extends Zombie & ISlashBladeEntity> void addSlashZombieBehaviours(T entitySlashZombie) {
        entitySlashZombie.goalSelector.addGoal(2, new SimpleSlashGoal<>(entitySlashZombie, 1.1, 7, false));
        entitySlashZombie.goalSelector.addGoal(6, new MoveThroughVillageGoal(entitySlashZombie, 1.0, true, 4, entitySlashZombie::canBreakDoors));
        entitySlashZombie.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(entitySlashZombie, 1.0));
        entitySlashZombie.targetSelector.addGoal(1, new HurtByTargetGoal(entitySlashZombie).setAlertOthers(ZombifiedPiglin.class));
        entitySlashZombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(entitySlashZombie, Player.class, true));
        entitySlashZombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(entitySlashZombie, AbstractVillager.class, false));
        entitySlashZombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(entitySlashZombie, IronGolem.class, true));
        entitySlashZombie.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(entitySlashZombie, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.ARMOR, 2.0)
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
            .add(Attributes.SWEEPING_DAMAGE_RATIO);
    }
    
    @Override
    public boolean canBreakDoors() {
        return false;
    }
    
    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(level, difficulty, reason, spawnData);
        if (this.getNavigation() instanceof GroundPathNavigation groundPathNavigation) {
            groundPathNavigation.setCanOpenDoors(true);
        }
        return spawngroupdata;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        int i = random.nextInt(20);
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        if (i < this.level().getDifficulty().getId()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.SABIGATANA.location())).getBlade(this.registryAccess()));
        } else {
            if (i < this.level().getDifficulty().getId() * 4) {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_BAMBOO.get().getDefaultInstance());
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_WOOD.get().getDefaultInstance());
            }
        }
    }
    
    @Override
    protected void doUnderWaterConversion() {
        if (!EventHooks.canLivingConvert(this, EntityType.DROWNED, (timer) -> this.conversionTime = timer)) {
            return;
        }
        this.convertToZombieType(SlashMobsEntities.SLASH_DROWNED.get());
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1040, this.blockPosition(), 0);
        }
    }
    
    @Override
    protected boolean canReplaceCurrentItem(ItemStack candidate, ItemStack existing) {
        return false;
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
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return BladeStateAccess.of(this.getMainHandItem()).map(state -> {
            double reach = TargetSelector.getResolvedReach(this);
            return this.distanceTo(entity) < reach * reach;
        }).orElse(false);
    }
    
    @Override
    public boolean canProgressCombo(@Nullable LivingEntity target, ResourceLocation current, ResourceLocation next) {
        return this.canUseCombo(next);
    }
    
    @Override
    public boolean canUseCombo(ResourceLocation combo) {
        return !combo.equals(ComboStateRegistry.COMBO_C.getId()) &&
            !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
            !combo.equals(ComboStateRegistry.UPPERSLASH.getId()) &&
            !combo.equals(ComboStateRegistry.AERIAL_CLEAVE.getId());
    }
    
    @Override
    public Set<Class<? extends Entity>> getAttackableEntities() {
        return Set.of(Player.class, AbstractVillager.class, IronGolem.class, Turtle.class);
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
}
