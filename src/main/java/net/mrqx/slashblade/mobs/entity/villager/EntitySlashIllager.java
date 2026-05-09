package net.mrqx.slashblade.mobs.entity.villager;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import mods.flammpfeil.slashblade.RegistryEvents;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.utils.JustSlashArtManager;
import net.mrqx.sbr_core.utils.SlashBladeMovementUtils;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.compat.SlashBladeMobCompat;
import net.mrqx.slashblade.mobs.entity.ai.goal.VillagerMirageBladeGoal;
import net.mrqx.slashblade.mobs.entity.ai.goal.VillagerSlashGoal;
import net.mrqx.slashblade.mobs.registy.SlashMobsVillagerProfessions;
import net.mrqx.slashblade.mobs.utils.SlashMobsUtils;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class EntitySlashIllager extends AbstractIllager implements ISlashBladeEntity, VillagerDataHolder, RangedAttackMob {
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(EntitySlashIllager.class, EntityDataSerializers.VILLAGER_DATA);
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;
    private int villagerXp;
    @Nullable
    public GoalSelector extraGoalSelector;
    @Nullable
    protected VillagerSlashGoal<EntitySlashIllager> slashGoal;
    @Nullable
    protected VillagerMirageBladeGoal<EntitySlashIllager> mirageBladeGoal;
    @Nullable
    private final SlashVillagerFakePlayer fakePlayer;
    private final SlashVillagerFakeBladeStand bladeStand;
    
    public EntitySlashIllager(EntityType<? extends EntitySlashIllager> entityType, Level level) {
        super(entityType, level);
        this.xpReward *= 2;
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        if (level instanceof ServerLevel serverLevel) {
            fakePlayer = new SlashVillagerFakePlayer(this, serverLevel);
        } else {
            fakePlayer = null;
        }
        bladeStand = new SlashVillagerFakeBladeStand(RegistryEvents.BladeStand, level);
        this.xpReward *= 2;
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.slashGoal = new VillagerSlashGoal<>(this, 1, 4);
        this.mirageBladeGoal = new VillagerMirageBladeGoal<>(this, 1);
        this.goalSelector.addGoal(3, this.slashGoal);
        this.extraGoalSelector = new GoalSelector(this.level().getProfilerSupplier());
        this.extraGoalSelector.addGoal(0, this.mirageBladeGoal);
        this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.ARMOR, 5.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.SWEEPING_DAMAGE_RATIO);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 1));
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(SlashBladeMobs.LOGGER::error)
            .ifPresent(tag -> compound.put("VillagerData", tag));
        compound.putInt("Xp", this.villagerXp);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compound.get("VillagerData")));
            dataresult.resultOrPartial(SlashBladeMobs.LOGGER::error).ifPresent(this::setVillagerData);
        }
        if (compound.contains("Xp", 3)) {
            this.villagerXp = compound.getInt("Xp");
        }
        if (this.slashGoal != null) {
            this.slashGoal.refreshProfessionSettings(this.getVillagerData());
        }
        if (this.mirageBladeGoal != null) {
            this.mirageBladeGoal.refreshProfessionSettings(this.getVillagerData());
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PILLAGER_HURT;
    }
    
    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(level, difficulty, reason, spawnData);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomsource = level.getRandom();
        VillagerProfession profession;
        switch (randomsource.nextInt(3)) {
            case 1 -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get();
            case 2 -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get();
            default -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get();
        }
        this.setVillagerData(this.getVillagerData().setProfession(profession).setLevel(randomsource.nextInt(5) + 1));
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        return spawngroupdata;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        if (this.getCurrentRaid() == null) {
            ItemStack stack = new ItemStack(SlashBladeItems.SLASHBLADE.get());
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchantments.set(this.registryAccess().holderOrThrow(Enchantments.SWEEPING_EDGE), 1);
            EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
            this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }
        this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 100.0F;
        this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 100.0F;
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        this.setItemSlot(EquipmentSlot.MAINHAND, EntitySlashVillager.getDefaultBladeForVillagerLevel(bladeRegistry, this.getVillagerData().getLevel(), this.registryAccess()));
        this.refreshBlade();
    }
    
    public void refreshBlade() {
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        ItemStack newBlade = EntitySlashVillager.getDefaultBladeForVillagerLevel(bladeRegistry, this.getVillagerData().getLevel(), this.registryAccess());
        ItemStack oldBlade = this.getMainHandItem();
        SlashMobsUtils.restoreBladeData(newBlade, oldBlade);
        SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
        if (professionSettings != null) {
            SlashMobsUtils.setNewBladeEnchantments(oldBlade, professionSettings, newBlade, this.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, newBlade);
        this.onPickupProudSoul(SlashBladeItems.PROUDSOUL_TRAPEZOHEDRON.get().getDefaultInstance());
        if (this.slashGoal != null) {
            this.slashGoal.refreshProfessionSettings(this.getVillagerData());
        }
        if (this.mirageBladeGoal != null) {
            this.mirageBladeGoal.refreshProfessionSettings(this.getVillagerData());
        }
    }
    
    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unusedFalse) {
        ItemStack stack = new ItemStack(SlashBladeItems.SLASHBLADE.get());
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        RegistryAccess registryAccess = level.registryAccess();
        if (this.raid != null && wave > this.raid.getNumGroups(Difficulty.NORMAL)) {
            enchantments.set(registryAccess.holderOrThrow(Enchantments.SHARPNESS), 2);
        }
        if (this.random.nextFloat() < 0.1F) {
            enchantments.set(registryAccess.holderOrThrow(Enchantments.SWEEPING_EDGE), 1);
        }
        EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        this.setVillagerData(this.getVillagerData().setLevel(Math.min(wave - 2, 5)));
        this.refreshBlade();
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
    
    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity.getType().is(EntityTypeTags.ILLAGER_FRIENDS)) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
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
        
        AttributeInstance armorAttribute = this.getAttribute(Attributes.ARMOR);
        if (armorAttribute != null) {
            armorAttribute.removeModifier(EntitySlashVillager.SLASH_VILLAGER_ARMOR_MODIFIER);
            armorAttribute.addPermanentModifier(new AttributeModifier(EntitySlashVillager.SLASH_VILLAGER_ARMOR_MODIFIER,
                this.getVillagerData().getLevel() * 3, AttributeModifier.Operation.ADD_VALUE));
        }
        
        if (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemSlashBlade) {
            this.getItemInHand(InteractionHand.MAIN_HAND).inventoryTick(this.level(), this, 0, true);
        }
        
        
        long cooldown = JustSlashArtManager.getJustCooldown(this);
        if (cooldown > 0) {
            SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
            if (professionSettings != null) {
                cooldown -= professionSettings.powerful ? 2 : 1;
            }
            JustSlashArtManager.setJustCooldown(this, cooldown);
            if (cooldown <= 0) {
                JustSlashArtManager.resetJustCount(this);
            }
        }
    }
    
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.isAggressive()) {
            BladeStateAccess.of(this.getMainHandItem()).ifPresent(state -> state.setDamage(state.getDamage() - 1));
        } else if (this.getTarget() != null && this.getTarget().isAlive()) {
            SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
            if (professionSettings != null) {
                SlashBladeMovementUtils.tickSlashBladeTrick(this, this.getTarget(),
                    professionSettings.canAirTrick, professionSettings.canTrickDown, professionSettings.canTrickDodge, professionSettings.powerful);
            }
        }
        MinecraftServer server = this.level().getServer();
        if (server != null) {
            if (this.extraGoalSelector != null) {
                int i = server.getTickCount() + this.getId();
                if (i % 2 != 0 && this.tickCount > 1) {
                    this.level().getProfiler().push("extraGoalSelector");
                    this.extraGoalSelector.tickRunningGoals(false);
                    this.level().getProfiler().pop();
                } else {
                    this.level().getProfiler().push("extraGoalSelector");
                    this.extraGoalSelector.tick();
                    this.level().getProfiler().pop();
                }
            }
        }
    }
    
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(SlashBladeItemTags.PROUD_SOULS) ||
            stack.is(SlashBladeItemTags.CAN_CHANGE_SA) ||
            stack.is(SlashBladeItemTags.CAN_CHANGE_SE) ||
            stack.is(SlashBladeItemTags.CAN_COPY_SA) ||
            stack.is(SlashBladeItemTags.CAN_COPY_SE);
    }
    
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.wantsToPickUp(player.getItemInHand(hand))) {
            this.onPickupProudSoul(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        this.onPickupProudSoul(itemEntity.getItem());
    }
    
    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity entity) {
        ItemStack stack = this.getMainHandItem();
        if (!stack.isEmpty()) {
            if (BladeStateAccess.of(stack).isPresent()) {
                IConcentrationRank.ConcentrationRanks rankBonus = this.getData(CapabilityConcentrationRank.RANK_POINT).getRank(this.level().getGameTime());
                int souls = (int) Math.floor(entity.getExperienceReward(level, this) * (1 + rankBonus.level * 0.1));
                BladeStateAccess.of(stack).ifPresent((state) -> {
                    SlashBladeEvent.AddProudSoulEvent soulEvent = new SlashBladeEvent.AddProudSoulEvent(stack, state, Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls));
                    NeoForge.EVENT_BUS.post(soulEvent);
                    int newCount = soulEvent.getNewCount();
                    state.setProudSoulCount(state.getProudSoulCount() + newCount);
                    if (SwordType.from(stack).contains(SwordType.SOULEATER)) {
                        int damage = Math.max(1, newCount / 4);
                        stack.setDamageValue(Math.max(stack.getDamageValue() - damage, 0));
                    }
                    this.rewardXp(newCount);
                });
            }
        }
        return true;
    }
    
    public void onPickupProudSoul(ItemStack item) {
        ItemStack blade = this.getMainHandItem();
        if (this.level() instanceof ServerLevel && this.fakePlayer != null) {
            ItemStack copy = item.copy();
            AnvilUpdateEvent updateEvent = new AnvilUpdateEvent(blade, copy, blade.getHoverName().toString(),
                blade.getOrDefault(DataComponents.REPAIR_COST, 0) + (copy.isEmpty() ? 0 : copy.getOrDefault(DataComponents.REPAIR_COST, 0)), this.fakePlayer);
            if (!NeoForge.EVENT_BUS.post(updateEvent).isCanceled() && !updateEvent.getOutput().isEmpty()) {
                ItemStack output = updateEvent.getOutput();
                copy.setCount(copy.getCount() - updateEvent.getMaterialCost());
                int proudSoulCount = Math.max(BladeStateAccess.of(output).map(ISlashBladeState::getProudSoulCount).orElse(0)
                    - BladeStateAccess.of(blade).map(ISlashBladeState::getProudSoulCount).orElse(0), 0);
                this.setItemInHand(InteractionHand.MAIN_HAND, output);
                AnvilRepairEvent repairEvent = new AnvilRepairEvent(this.fakePlayer, blade, copy, output);
                NeoForge.EVENT_BUS.post(repairEvent);
                this.rewardXp(proudSoulCount / 100);
            }
            
            ItemStack blade1 = this.getMainHandItem();
            ItemStack copy1 = item.copy();
            this.fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, copy1);
            this.bladeStand.setItem(blade1.copy());
            BladeStateAccess.of(blade1).ifPresent(state -> {
                SlashBladeEvent.BladeStandAttackEvent attackEvent = new SlashBladeEvent.BladeStandAttackEvent(blade1, state, this.bladeStand,
                    this.level().damageSources().playerAttack(this.fakePlayer));
                NeoForge.EVENT_BUS.post(attackEvent);
                this.setItemInHand(InteractionHand.MAIN_HAND, attackEvent.getBlade());
            });
            copy1 = this.fakePlayer.getMainHandItem();
            item.setCount(Math.min(copy.getCount(), copy1.getCount()));
            this.fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }
    
    public void rewardXp(int villagerXp) {
        int i = 3 + this.random.nextInt(4);
        this.villagerXp += villagerXp;
        if (VillagerData.canLevelUp(this.getVillagerData().getLevel()) && this.villagerXp >= VillagerData.getMaxXpPerLevel(this.getVillagerData().getLevel())) {
            this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
            this.refreshBlade();
            i += 5;
        }
        this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
    }
    
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return BladeStateAccess.of(this.getMainHandItem()).map(state -> {
            double reach = TargetSelector.getResolvedReach(this);
            return this.distanceTo(entity) < reach * reach;
        }).orElse(false);
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
    public boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next) {
        SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
        PropertyDispatch.QuadFunction<LivingEntity, LivingEntity, ResourceLocation, ResourceLocation, Boolean> canProgressComboFunction = null;
        if (professionSettings != null) {
            canProgressComboFunction = professionSettings.canProgressComboFunction;
        }
        if (canProgressComboFunction != null) {
            return canProgressComboFunction.apply(this, target, current, next);
        } else {
            return this.canUseCombo(next);
        }
    }
    
    @Override
    public boolean canUseCombo(ResourceLocation combo) {
        SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
        BiFunction<LivingEntity, ResourceLocation, Boolean> canUseComboFunction = null;
        if (professionSettings != null) {
            canUseComboFunction = professionSettings.canUseComboFunction;
        }
        if (canUseComboFunction != null) {
            return canUseComboFunction.apply(this, combo);
        } else {
            return true;
        }
    }
    
    @Override
    public Set<Class<? extends Entity>> getAttackableEntities() {
        return Set.of(LivingEntity.class);
    }
    
    @Override
    public List<Entity> processTargetList(Level world, LivingEntity attacker, AABB aabb, double reach, List<Entity> originalTargetList) {
        List<Entity> targetList = ISlashBladeEntity.super.processTargetList(world, attacker, aabb, reach, originalTargetList);
        
        targetList.removeIf(entity -> SlashBladeMobCompat.Factories.getSlashIllagerIgnores().stream().anyMatch(clazz -> clazz.isInstance(entity)));
        
        BladeStateAccess.of(attacker.getMainHandItem()).ifPresent(state -> {
            Entity target = state.getTargetEntity(world);
            if (target != null) {
                targetList.add(target);
            }
        });
        if (attacker instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                targetList.add(target);
            }
        }
        return targetList;
    }
    
    @Override
    public boolean useUpperSlashJump() {
        SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
        if (professionSettings != null) {
            return professionSettings.canUseUpperSlashJump;
        } else {
            return ISlashBladeEntity.super.useUpperSlashJump();
        }
    }
    
    @Nullable
    public SlashVillagerProfessionSettings getSlashVillagerProfessionSettings() {
        return SlashVillagerProfessionSettings.getSettings(this.getVillagerData().getProfession(), this.getVillagerData().getLevel());
    }
    
    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }
    
    
    @Override
    public void setVillagerData(VillagerData data) {
        this.entityData.set(DATA_VILLAGER_DATA, data);
    }
    
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
    }
    
    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        return IllagerArmPose.NEUTRAL;
    }
    
    public class SlashVillagerFakePlayer extends FakePlayer {
        public SlashVillagerFakePlayer(EntitySlashIllager slashVillager, ServerLevel level) {
            super(level, new GameProfile(UUID.randomUUID(), slashVillager.stringUUID));
        }
        
        @Override
        public void tick() {
            super.tick();
            this.moveTo(EntitySlashIllager.this.getX(), EntitySlashIllager.this.getY(), EntitySlashIllager.this.getZ(), EntitySlashIllager.this.getYRot(), EntitySlashIllager.this.getXRot());
        }
    }
    
    public class SlashVillagerFakeBladeStand extends BladeStandEntity {
        public SlashVillagerFakeBladeStand(EntityType<? extends BladeStandEntity> entityType, Level level) {
            super(entityType, level);
        }
        
        @Override
        public void tick() {
            super.tick();
            this.moveTo(EntitySlashIllager.this.getX(), EntitySlashIllager.this.getY(), EntitySlashIllager.this.getZ(), EntitySlashIllager.this.getYRot(), EntitySlashIllager.this.getXRot());
        }
    }
}
