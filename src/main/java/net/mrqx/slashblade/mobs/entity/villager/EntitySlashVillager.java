package net.mrqx.slashblade.mobs.entity.villager;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.utils.JustSlashArtManager;
import net.mrqx.sbr_core.utils.SlashBladeMovementUtils;
import net.mrqx.slashblade.mobs.SlashBladeMobs;
import net.mrqx.slashblade.mobs.compat.SlashBladeMobCompat;
import net.mrqx.slashblade.mobs.entity.ai.goal.SlashVillagerPickupItemGoal;
import net.mrqx.slashblade.mobs.entity.ai.goal.VillagerMirageBladeGoal;
import net.mrqx.slashblade.mobs.entity.ai.goal.VillagerSlashGoal;
import net.mrqx.slashblade.mobs.mixin.AccessorVillager;
import net.mrqx.slashblade.mobs.registy.SlashMobsVillagerProfessions;
import net.mrqx.slashblade.mobs.utils.SlashMobsUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class EntitySlashVillager extends AbstractVillager implements ISlashBladeEntity, NeutralMob, RangedAttackMob, VillagerDataHolder {
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(EntitySlashVillager.class, EntityDataSerializers.VILLAGER_DATA);
    public static final UUID SLASH_VILLAGER_ARMOR_MODIFIER = UUID.fromString("172608B9-4A42-4493-B1DB-215889A10B05");
    @Nullable
    public VanillaConvertedVmdAnimation currentAnimation;
    private int villagerXp;
    @Nullable
    public GoalSelector extraGoalSelector;
    private final GossipContainer gossips = new GossipContainer();
    public long lastGossipTime;
    public long lastGossipDecayTime;
    private static final UniformInt ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private int updateLevelTimer;
    private boolean increaseProfessionLevelOnUpdate;
    @Nullable
    private UUID persistentAngerTarget;
    @Nullable
    private final SlashVillagerFakePlayer fakePlayer;
    private final SlashVillagerFakeBladeStand bladeStand;
    @Nullable
    protected VillagerSlashGoal<EntitySlashVillager> slashGoal;
    @Nullable
    protected VillagerMirageBladeGoal<EntitySlashVillager> mirageBladeGoal;

    public EntitySlashVillager(EntityType<? extends AbstractVillager> entityType, Level level) {
        this(entityType, level, VillagerType.PLAINS);
    }

    public EntitySlashVillager(EntityType<? extends AbstractVillager> entityType, Level level, VillagerType villagerType) {
        super(entityType, level);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        if (level instanceof ServerLevel serverLevel) {
            fakePlayer = new SlashVillagerFakePlayer(this, serverLevel);
        } else {
            fakePlayer = null;
        }
        this.setVillagerData(this.getVillagerData().setType(villagerType));
        bladeStand = new SlashVillagerFakeBladeStand(SlashBlade.RegistryEvents.BladeStand, level);
        this.xpReward *= 2;
    }

    public static EntitySlashVillager newInstance(EntityType<? extends AbstractVillager> entityType, Level level) {
        return new EntitySlashVillager(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        this.slashGoal = new VillagerSlashGoal<>(this, 1, 4);
        this.mirageBladeGoal = new VillagerMirageBladeGoal<>(this, 1);
        this.goalSelector.addGoal(3, this.slashGoal);
        this.extraGoalSelector = new GoalSelector(this.level().getProfilerSupplier());
        this.extraGoalSelector.addGoal(0, this.mirageBladeGoal);
        this.goalSelector.addGoal(4, new MoveBackToVillageGoal(this, 0.5D, false));
        this.goalSelector.addGoal(5, new GolemRandomStrollInVillageGoal(this, 0.5D));
        this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.5D, false, 4, () -> false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AbstractVillager.class, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new SlashVillagerPickupItemGoal(this));
        this.goalSelector.addGoal(10, new FloatGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && ((EntitySlashVillager.this.getTarget() != null && (EntitySlashVillager.this.getY() - EntitySlashVillager.this.getTarget().getY()) >= 5)
                        || EntitySlashVillager.this.getTarget() == null
                        || EntitySlashVillager.this.getAirSupply() <= 100);
            }
        });

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, SlashBladeMobCompat.Factories.getSlashVillagerIgnores()
                .toArray(new Class<?>[0])).setAlertOthers());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Mob.class, 5, true, true, mob -> mob instanceof Enemy));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void tick() {
        this.maybeDecayGossip();
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
            armorAttribute.removeModifier(SLASH_VILLAGER_ARMOR_MODIFIER);
            armorAttribute.addPermanentModifier(new AttributeModifier(SLASH_VILLAGER_ARMOR_MODIFIER,
                    "SlashVillager Armor Modifier", this.getVillagerData().getLevel() * 3, AttributeModifier.Operation.ADDITION));
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
            if (this.updateLevelTimer > 0) {
                --this.updateLevelTimer;
                if (this.updateLevelTimer <= 0) {
                    if (this.increaseProfessionLevelOnUpdate) {
                        this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
                        this.refreshBlade();
                        this.increaseProfessionLevelOnUpdate = false;
                    }

                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                }
            }
            this.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> state.setDamage(state.getDamage() - 1));
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
            if (stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
                IConcentrationRank.ConcentrationRanks rankBonus = this.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                        .map((rp) -> rp.getRank(this.level().getGameTime()))
                        .orElse(IConcentrationRank.ConcentrationRanks.NONE);
                int souls = (int) Math.floor(entity.getExperienceReward() * (1 + rankBonus.level * 0.1));
                stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                    SlashBladeEvent.AddProudSoulEvent soulEvent = new SlashBladeEvent.AddProudSoulEvent(stack, state, Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls));
                    MinecraftForge.EVENT_BUS.post(soulEvent);
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
                    blade.getBaseRepairCost() + (copy.isEmpty() ? 0 : copy.getBaseRepairCost()), this.fakePlayer);
            if (!MinecraftForge.EVENT_BUS.post(updateEvent) && !updateEvent.getOutput().isEmpty()) {
                ItemStack output = updateEvent.getOutput();
                copy.setCount(copy.getCount() - updateEvent.getMaterialCost());
                int proudSoulCount = Math.max(output.getCapability(ItemSlashBlade.BLADESTATE).map(ISlashBladeState::getProudSoulCount).orElse(0)
                        - blade.getCapability(ItemSlashBlade.BLADESTATE).map(ISlashBladeState::getProudSoulCount).orElse(0), 0);
                this.setItemInHand(InteractionHand.MAIN_HAND, output);
                AnvilRepairEvent repairEvent = new AnvilRepairEvent(this.fakePlayer, blade, copy, output);
                MinecraftForge.EVENT_BUS.post(repairEvent);
                this.rewardXp(proudSoulCount / 100);
            }

            ItemStack blade1 = this.getMainHandItem();
            ItemStack copy1 = item.copy();
            this.fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, copy1);
            this.bladeStand.setItem(blade1.copy());
            blade1.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                SlashBladeEvent.BladeStandAttackEvent attackEvent = new SlashBladeEvent.BladeStandAttackEvent(blade1, state, this.bladeStand,
                        this.level().damageSources().playerAttack(this.fakePlayer));
                MinecraftForge.EVENT_BUS.post(attackEvent);
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
        if (this.shouldIncreaseLevel()) {
            this.updateLevelTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
        }
        this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
    }

    private boolean shouldIncreaseLevel() {
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.villagerXp >= VillagerData.getMaxXpPerLevel(i);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get(), 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(SlashBladeMobs.LOGGER::error)
                .ifPresent(tag -> compound.put("VillagerData", tag));
        compound.putInt("Xp", this.villagerXp);
        compound.putLong("LastGossipTime", this.lastGossipTime);
        compound.putLong("LastGossipDecay", this.lastGossipDecayTime);
        compound.put("Gossips", this.gossips.store(NbtOps.INSTANCE));
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
        this.lastGossipDecayTime = compound.getLong("LastGossipDecay");
        this.lastGossipTime = compound.getLong("LastGossipTime");
        ListTag listtag = compound.getList("Gossips", 10);
        this.gossips.update(new Dynamic<>(NbtOps.INSTANCE, listtag));
        if (this.slashGoal != null) {
            this.slashGoal.refreshProfessionSettings(this.getVillagerData());
        }
        if (this.mirageBladeGoal != null) {
            this.mirageBladeGoal.refreshProfessionSettings(this.getVillagerData());
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        VillagerProfession profession;
        RandomSource randomSource = worldIn.getRandom();
        switch (randomSource.nextInt(3)) {
            case 1 -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_B.get();
            case 2 -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_C.get();
            default -> profession = SlashMobsVillagerProfessions.SLASHBLADE_SAMURAI_A.get();
        }
        this.setVillagerData(this.getVillagerData().setProfession(profession));
        this.setPersistenceRequired();
        this.populateDefaultEquipmentSlots(randomSource, difficultyIn);
        this.populateDefaultEquipmentEnchantments(randomSource, difficultyIn);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance instance) {
        super.populateDefaultEquipmentSlots(source, instance);
        this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 100.0F;
        this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 100.0F;
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        this.setItemSlot(EquipmentSlot.MAINHAND, getDefaultBladeForVillagerLevel(bladeRegistry, this.getVillagerData().getLevel()));
        this.refreshBlade();
    }

    public void refreshBlade() {
        Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(this.level());
        ItemStack newBlade = EntitySlashVillager.getDefaultBladeForVillagerLevel(bladeRegistry, this.getVillagerData().getLevel());
        ItemStack oldBlade = this.getMainHandItem();
        SlashMobsUtils.restoreBladeData(newBlade, oldBlade);
        SlashVillagerProfessionSettings professionSettings = this.getSlashVillagerProfessionSettings();
        if (professionSettings != null) {
            SlashMobsUtils.setNewBladeEnchantments(oldBlade, professionSettings, newBlade);
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
        VillagerData villagerdata = this.getVillagerData();
        if (!villagerdata.getProfession().equals(data.getProfession())) {
            this.offers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, data);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.villagerXp += offer.getXp();
        if (this.shouldIncreaseLevel()) {
            this.updateLevelTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }

        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    @Override
    protected void updateTrades() {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else if (id == 13) {
            this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if (id == 14) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else if (id == 42) {
            this.addParticlesAroundSelf(ParticleTypes.SPLASH);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isTrading() {
        return false;
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void maybeDecayGossip() {
        long i = level().getGameTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = i;
        } else if (i >= this.lastGossipDecayTime + 24000L) {
            this.gossips.decay();
            this.lastGossipDecayTime = i;
        }
    }

    public void gossip(Villager villager, long gameTime) {
        if (villager instanceof AccessorVillager accessorVillager) {
            boolean b1 = gameTime < this.lastGossipTime || gameTime >= this.lastGossipTime + 1200L;
            boolean b2 = gameTime < accessorVillager.getLastGossipTime() || gameTime >= accessorVillager.getLastGossipTime() + 1200L;
            if (b1 && b2) {
                this.gossips.transferFrom(villager.getGossips(), this.random, 10);
                this.lastGossipTime = gameTime;
                accessorVillager.setLastGossipTime(gameTime);
            }
        }
    }

    public static ItemStack getDefaultBladeForVillagerLevel(Registry<SlashBladeDefinition> bladeRegistry, int level) {
        return switch (level) {
            case 1 ->
                    Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.RODAI_WOODEN.location())).getBlade();
            case 2 ->
                    Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.RODAI_STONE.location())).getBlade();
            case 3 ->
                    Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.RODAI_IRON.location())).getBlade();
            case 4 ->
                    Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.RODAI_GOLDEN.location())).getBlade();
            default ->
                    Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.RODAI_DIAMOND.location())).getBlade();
        };
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity entity) {
        AtomicDouble attackDistance = new AtomicDouble(super.getMeleeAttackRangeSqr(entity));
        this.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state ->
                attackDistance.set(TargetSelector.getResolvedReach(this)));
        return attackDistance.get() * attackDistance.get();
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

        targetList.removeIf(entity -> !(entity instanceof Mob && entity instanceof Enemy));
        targetList.removeIf(entity -> SlashBladeMobCompat.Factories.getSlashVillagerIgnores().stream().anyMatch(clazz -> clazz.isInstance(entity)));

        attacker.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
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

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID arg0) {
        this.persistentAngerTarget = arg0;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int arg0) {
        this.remainingPersistentAngerTime = arg0;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME.sample(random));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        if (level.getDifficulty() != Difficulty.PEACEFUL && ForgeEventFactory.canLivingConvert(this, EntityType.WITCH, (timer) -> {
        })) {
            Witch witch = EntityType.WITCH.create(level);
            if (witch == null) {
                return;
            }
            witch.copyPosition(this);
            witch.finalizeSpawn(level, level.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, null, null);
            witch.setNoAi(this.isNoAi());
            witch.setCustomName(this.getCustomName());
            witch.setCustomNameVisible(this.isCustomNameVisible());
            witch.setPersistenceRequired();
            level.addFreshEntityWithPassengers(witch);
            this.discard();
        } else {
            super.thunderHit(level, lightning);
        }
    }

    public class SlashVillagerFakePlayer extends FakePlayer {
        public SlashVillagerFakePlayer(EntitySlashVillager slashVillager, ServerLevel level) {
            super(level, new GameProfile(UUID.randomUUID(), slashVillager.stringUUID));
        }

        @Override
        public void tick() {
            super.tick();
            this.moveTo(EntitySlashVillager.this.getX(), EntitySlashVillager.this.getY(), EntitySlashVillager.this.getZ(), EntitySlashVillager.this.getYRot(), EntitySlashVillager.this.getXRot());
        }
    }

    public class SlashVillagerFakeBladeStand extends BladeStandEntity {
        public SlashVillagerFakeBladeStand(EntityType<? extends BladeStandEntity> entityType, Level level) {
            super(entityType, level);
        }

        @Override
        public void tick() {
            super.tick();
            this.moveTo(EntitySlashVillager.this.getX(), EntitySlashVillager.this.getY(), EntitySlashVillager.this.getZ(), EntitySlashVillager.this.getYRot(), EntitySlashVillager.this.getXRot());
        }
    }
}
