package net.mrqx.slashblade.mobs.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.entity.ai.goal.SimpleSlashGoal;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class EntitySlashDrowned extends Drowned implements ISlashBladeEntity {
    boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;
    
    public EntitySlashDrowned(EntityType<? extends Drowned> entityType, Level level) {
        super(entityType, level);
        this.xpReward *= 2;
        this.moveControl = new DrownedMoveControl(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }
    
    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new DrownedGoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new SimpleSlashGoal<>(this, 1.1, 7, false)
            .setAfterSlashConsumer(goal -> {
                    if (goal.getLastComboStateLocation() != null
                        && goal.getLastComboStateLocation().equals(ComboStateRegistry.AERIAL_CLEAVE.getId())) {
                        goal.resetAttackCooldown();
                    }
                }
            ));
        this.goalSelector.addGoal(5, new DrownedGoToBeachGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new DrownedSwimUpGoal(this, 1.0D, this.level().getSeaLevel()));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Drowned.class).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::okTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Axolotl.class, true, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0)
            .add(Attributes.MOVEMENT_SPEED, 0.23)
            .add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.ARMOR, 2.0)
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(Attributes.SWEEPING_DAMAGE_RATIO);
    }
    
    public static boolean checkSlashDrownedSpawnRules(EntityType<EntitySlashDrowned> ignoredDrowned, ServerLevelAccessor serverLevel, MobSpawnType mobSpawnType, BlockPos pos, RandomSource random) {
        if (!serverLevel.getFluidState(pos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            Holder<Biome> holder = serverLevel.getBiome(pos);
            boolean flag = serverLevel.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(serverLevel, pos, random) && (mobSpawnType == MobSpawnType.SPAWNER || serverLevel.getFluidState(pos).is(FluidTags.WATER));
            if (holder.is(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
                return random.nextInt(15) == 0 && flag;
            } else {
                return random.nextInt(40) == 0 && isDeepEnoughToSpawn(serverLevel, pos) && flag;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private static boolean isDeepEnoughToSpawn(LevelAccessor level, BlockPos pos) {
        return pos.getY() < level.getSeaLevel() - 5;
    }
    
    @Override
    public boolean canBreakDoors() {
        return false;
    }
    
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(level, difficulty, reason, spawnData);
        if (this.getNavigation() instanceof GroundPathNavigation groundPathNavigation) {
            groundPathNavigation.setCanOpenDoors(true);
        }
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && level.getRandom().nextFloat() < NAUTILUS_SHELL_CHANCE) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        }
        return spawngroupdata;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        int i = random.nextInt(20);
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        if (i < this.level().getDifficulty().getId()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.AGITO_RUST.location())).getBlade(this.registryAccess()));
        } else {
            if (i < this.level().getDifficulty().getId() * 4) {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_BAMBOO.get().getDefaultInstance());
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, SlashBladeItems.SLASHBLADE_WOOD.get().getDefaultInstance());
            }
        }
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
    public boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next) {
        return this.canUseCombo(next);
    }
    
    @Override
    public boolean canUseCombo(ResourceLocation combo) {
        return !combo.equals(ComboStateRegistry.COMBO_A3.getId()) &&
            !combo.equals(ComboStateRegistry.COMBO_B1.getId()) &&
            !combo.equals(ComboStateRegistry.UPPERSLASH.getId());
    }
    
    @Override
    public Set<Class<? extends Entity>> getAttackableEntities() {
        return Set.of(Player.class, AbstractVillager.class, IronGolem.class, Axolotl.class, Turtle.class);
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
    protected boolean canReplaceCurrentItem(ItemStack candidate, ItemStack existing) {
        return false;
    }
    
    boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else {
            LivingEntity livingentity = this.getTarget();
            return livingentity != null && livingentity.isInWater();
        }
    }
    
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
        
    }
    
    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
        
    }
    
    @Override
    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            double d0 = this.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            return d0 < 4.0D;
        }
        
        return false;
    }
    
    @Override
    public void setSearchingForLand(boolean searchingForLand) {
        this.searchingForLand = searchingForLand;
    }
    
    protected boolean isCloseToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            double d0 = this.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            return d0 < 4.0D;
        }
        
        return false;
    }
    
    static class DrownedGoToBeachGoal extends MoveToBlockGoal {
        private final EntitySlashDrowned drowned;
        
        public DrownedGoToBeachGoal(EntitySlashDrowned drowned, double speedModifier) {
            super(drowned, speedModifier, 8, 2);
            this.drowned = drowned;
        }
        
        @Override
        public boolean canUse() {
            return super.canUse() && !this.drowned.level().isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double) (this.drowned.level().getSeaLevel() - 3);
        }
        
        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockPos blockpos = pos.above();
            return level.isEmptyBlock(blockpos) && level.isEmptyBlock(blockpos.above()) && level.getBlockState(pos).entityCanStandOn(level, pos, this.drowned);
        }
        
        @Override
        public void start() {
            this.drowned.setSearchingForLand(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.start();
        }
        
    }
    
    static class DrownedGoToWaterGoal extends Goal {
        private final PathfinderMob mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;
        
        public DrownedGoToWaterGoal(PathfinderMob mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.level = mob.level();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            if (!this.level.isDay()) {
                return false;
            } else if (this.mob.isInWater()) {
                return false;
            } else {
                Vec3 vec3 = this.getWaterPos();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z;
                    return true;
                }
            }
        }
        
        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }
        
        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }
        
        @Nullable
        private Vec3 getWaterPos() {
            RandomSource randomsource = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();
            
            for (int i = 0; i < 10; ++i) {
                BlockPos blockPos = blockpos.offset(randomsource.nextInt(20) - 10, 2 - randomsource.nextInt(8), randomsource.nextInt(20) - 10);
                if (this.level.getBlockState(blockPos).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockPos);
                }
            }
            
            return null;
        }
    }
    
    static class DrownedMoveControl extends MoveControl {
        private final EntitySlashDrowned drowned;
        
        public DrownedMoveControl(EntitySlashDrowned drowned) {
            super(drowned);
            this.drowned = drowned;
        }
        
        @Override
        public void tick() {
            LivingEntity livingentity = this.drowned.getTarget();
            if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
                if (livingentity != null) {
                    if (livingentity.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
                        this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                    }
                }
                
                if (this.operation != MoveControl.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
                    this.drowned.setSpeed(0.0F);
                    return;
                }
                
                double d0 = this.wantedX - this.drowned.getX();
                double d1 = this.wantedY - this.drowned.getY();
                double d2 = this.wantedZ - this.drowned.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.drowned.setYRot(this.rotlerp(this.drowned.getYRot(), f, 90.0F));
                this.drowned.yBodyRot = this.drowned.getYRot();
                float f1 = (float) (this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.drowned.getSpeed(), f1);
                this.drowned.setSpeed(f2);
                this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
            } else {
                if (!this.drowned.onGround()) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                
                super.tick();
            }
            
        }
    }
    
    static class DrownedSwimUpGoal extends Goal {
        private final EntitySlashDrowned drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;
        
        public DrownedSwimUpGoal(EntitySlashDrowned drowned, double speedModifier, int seaLevel) {
            this.drowned = drowned;
            this.speedModifier = speedModifier;
            this.seaLevel = seaLevel;
        }
        
        @Override
        public boolean canUse() {
            return !this.drowned.level().isDay() && this.drowned.isInWater() && this.drowned.getY() < (double) (this.seaLevel - 2);
        }
        
        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }
        
        @Override
        public void tick() {
            boolean b = this.drowned.getNavigation().isDone() || this.drowned.isCloseToNextPos();
            if (this.drowned.getY() < (double) (this.seaLevel - 1) && b) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), this.seaLevel - 1, this.drowned.getZ()), (float) Math.PI / 2F);
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }
                
                this.drowned.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }
            
        }
        
        @Override
        public void start() {
            this.drowned.setSearchingForLand(true);
            this.stuck = false;
        }
        
        @Override
        public void stop() {
            this.drowned.setSearchingForLand(false);
        }
    }
}
