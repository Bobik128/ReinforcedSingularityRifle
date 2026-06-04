package com.mod.rsrifle.entity;

import com.mod.rbh.entity.IBlackHole;
import com.mod.rbh.shaders.PostEffectRegistry;
import com.mod.rsrifle.CommonConfig;
import com.mod.rsrifle.RegisterDamageTypes;
import com.mod.rsrifle.items.SingularityRifle;
import com.mod.rsrifle.sound.RSRifleSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackHoleProjectile2 extends Projectile implements IBlackHole {
    private static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> EFFECT_SIZE =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> EFFECT_EXPONENT =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> STRETCH_STRENGTH =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Vector3f> STRETCH_DIR =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.VECTOR3);

    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> RAINBOW =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> EXPLODING_TIME =
            SynchedEntityData.defineId(BlackHoleProjectile2.class, EntityDataSerializers.INT);

    public static final int RENDER_DISTANCE = 120;
    public static final float DAMAGE_SIZE_MULTIPLIER = 10_000.0f / SingularityRifle.MAX_SIZE;
    protected static final float MAX_ITEM_REMOVE_PERCENT = 0.6f;

    public static final Logger LOGGER = LogUtils.getLogger();

    public int life = 0;
    public int lifetime = 1000;
    public final int maxExplodingTime = 3;
    public boolean exploding = false;

    private Vec3 lastDeltaDir = new Vec3(1.0, 0.0, 0.0);

    @OnlyIn(Dist.CLIENT)
    public PostEffectRegistry.HoleEffectInstance effectInstance;

    public BlackHoleProjectile2(Vec3 pos, Level level, float size, float effectSize) {
        this(RSRifleEntityTypes.BLACK_HOLE_PROJECTILE2.get(), level);
        this.setPos(pos);
        this.setSize(size);
        this.setEffectSize(effectSize);
    }

    public BlackHoleProjectile2(Vec3 pos, Level level, float size, float effectSize, boolean rainbow) {
        this(pos, level, size, effectSize);
        this.setRainbow(rainbow);
    }

    public BlackHoleProjectile2(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);

        if (this.level().isClientSide) {
            clientInit();
        }

        this.setNoGravity(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void clientInit() {
        this.effectInstance = PostEffectRegistry.HoleEffectInstance.createEffectInstance();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < RENDER_DISTANCE * RENDER_DISTANCE;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SIZE, 0.5f);
        builder.define(EFFECT_SIZE, 2.0f);
        builder.define(EFFECT_EXPONENT, 4.0f);
        builder.define(STRETCH_DIR, new Vector3f(1.0f, 0.0f, 0.0f));
        builder.define(STRETCH_STRENGTH, 0.0f);
        builder.define(COLOR, 0xFFFF00);
        builder.define(EXPLODING_TIME, -1);
        builder.define(RAINBOW, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putFloat("Size", this.getSize());
        tag.putFloat("EffectSize", this.getEffectSize());
        tag.putFloat("EffectExponent", this.getEffectExponent());
        tag.putFloat("StretchStrength", this.getStretchStrength());

        Vector3f stretchDir = this.getStretchDir();
        tag.putFloat("StretchDirX", stretchDir.x);
        tag.putFloat("StretchDirY", stretchDir.y);
        tag.putFloat("StretchDirZ", stretchDir.z);

        tag.putInt("Color", this.getColor());
        tag.putBoolean("Rainbow", this.shouldBeRainbow());
        tag.putInt("ExplodingTime", this.getExplodingTime());
        tag.putBoolean("Exploding", this.exploding);
        tag.putInt("Life", this.life);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Size")) {
            this.setSize(tag.getFloat("Size"));
        }

        if (tag.contains("EffectSize")) {
            this.setEffectSize(tag.getFloat("EffectSize"));
        }

        if (tag.contains("EffectExponent")) {
            this.setEffectExponent(tag.getFloat("EffectExponent"));
        }

        if (tag.contains("StretchStrength")) {
            this.setStretchStrength(tag.getFloat("StretchStrength"));
        }

        if (tag.contains("StretchDirX") && tag.contains("StretchDirY") && tag.contains("StretchDirZ")) {
            this.setStretchDir(new Vector3f(
                    tag.getFloat("StretchDirX"),
                    tag.getFloat("StretchDirY"),
                    tag.getFloat("StretchDirZ")
            ));
        }

        if (tag.contains("Color")) {
            this.setColor(tag.getInt("Color"));
        }

        if (tag.contains("Rainbow")) {
            this.setRainbow(tag.getBoolean("Rainbow"));
        }

        if (tag.contains("ExplodingTime")) {
            this.setExplodingTime(tag.getInt("ExplodingTime"));
        }

        if (tag.contains("Exploding")) {
            this.exploding = tag.getBoolean("Exploding");
        }

        if (tag.contains("Life")) {
            this.life = tag.getInt("Life");
        }
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return this.life > 1 && super.shouldRender(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.exploding) {
            this.setExplodingTime(this.getExplodingTime() + 1);

            if (this.getExplodingTime() > this.maxExplodingTime) {
                this.discard();
            }

            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 deltaMovement = this.getDeltaMovement();

        this.move(MoverType.SELF, deltaMovement);
        this.setDeltaMovement(deltaMovement);

        if (!this.level().isClientSide) {
            if (!this.lastDeltaDir.equals(deltaMovement) && deltaMovement.lengthSqr() > 0.000001) {
                this.setStretchDir(deltaMovement.toVector3f().normalize());
                this.setStretchStrength((float) deltaMovement.length() * 3.0f);
            }

            this.lastDeltaDir = deltaMovement;
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (!this.noPhysics) {
            this.onHit(hitResult);
            this.hasImpulse = true;
        }

        this.updateRotation();

        if (this.life == 0 && !this.isSilent()) {
            for (int i = 0; i < 2; i++) {
                this.level().playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        RSRifleSounds.RIFLE_SHOOT.get(),
                        SoundSource.AMBIENT,
                        6.0f,
                        1.1f
                );
            }
        }

        ++this.life;

        if (!this.level().isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity entity = result.getEntity();

        if (entity instanceof Player player) {
            this.removeItemsFromInvDepOnSize(
                    player,
                    (this.getSize() / SingularityRifle.MAX_SIZE) * MAX_ITEM_REMOVE_PERCENT
            );
        }

        entity.hurt(
                RegisterDamageTypes.causeHoleHitDamage(this),
                this.getEffectSize() * DAMAGE_SIZE_MULTIPLIER
        );

        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    private void removeItemsFromInvDepOnSize(Player player, float fraction) {
        if (player.level().isClientSide) {
            return;
        }

        class SlotRef {
            final List<ItemStack> list;
            final int index;

            SlotRef(List<ItemStack> list, int index) {
                this.list = list;
                this.index = index;
            }

            ItemStack get() {
                return this.list.get(this.index);
            }

            void set(ItemStack stack) {
                this.list.set(this.index, stack);
            }
        }

        List<SlotRef> allSlots = new ArrayList<>();
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            if (!inventory.items.get(i).isEmpty()) {
                allSlots.add(new SlotRef(inventory.items, i));
            }
        }

        for (int i = 0; i < inventory.armor.size(); i++) {
            if (!inventory.armor.get(i).isEmpty()) {
                allSlots.add(new SlotRef(inventory.armor, i));
            }
        }

        for (int i = 0; i < inventory.offhand.size(); i++) {
            if (!inventory.offhand.get(i).isEmpty()) {
                allSlots.add(new SlotRef(inventory.offhand, i));
            }
        }

        if (allSlots.isEmpty()) {
            return;
        }

        Collections.shuffle(allSlots);
        int count = Math.round(allSlots.size() * fraction);

        Level level = player.level();
        var random = player.getRandom();

        for (int i = 0; i < count; i++) {
            SlotRef slotRef = allSlots.get(i);
            ItemStack stack = slotRef.get();

            if (stack.isEmpty()) {
                continue;
            }

            if (random.nextBoolean()) {
                ItemStack dropStack = stack.copy();

                ItemEntity itemEntity = new ItemEntity(
                        level,
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        dropStack
                );

                double vx = (random.nextDouble() - 0.5) * 0.8;
                double vy = random.nextDouble() * 0.6 + 0.2;
                double vz = (random.nextDouble() - 0.5) * 0.8;

                itemEntity.setDeltaMovement(vx, vy, vz);
                itemEntity.setPickUpDelay(20);

                level.addFreshEntity(itemEntity);
            }

            slotRef.set(ItemStack.EMPTY);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos blockPos = result.getBlockPos();

        this.level()
                .getBlockState(blockPos)
                .entityInside(this.level(), blockPos, this);

        if (!this.level().isClientSide()) {
            this.explode();
        }

        super.onHitBlock(result);
    }

    private void explode() {
        this.level().broadcastEntityEvent(this, (byte) 17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());

        if (CommonConfig.destroyBlocks) {
            this.level().explode(
                    this,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    8.0f * this.getSize() / SingularityRifle.MAX_SIZE,
                    Level.ExplosionInteraction.TNT
            );
        } else {
            this.dealExplosionDamage();

            this.level().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.BLOCKS,
                    4.0f,
                    1.0f
            );
        }

        this.exploding = true;
    }

    private void dealExplosionDamage() {
        if (this.level().isClientSide) {
            return;
        }

        float radius = 5.0f;
        double radiusSq = radius * radius;
        Vec3 center = this.position();

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(radius)
        );

        for (LivingEntity target : targets) {
            double distSq = target.distanceToSqr(center);

            if (distSq > radiusSq) {
                continue;
            }

            double exposure = this.getExposure(center, target);

            if (exposure <= 0.0) {
                continue;
            }

            double dist = Math.sqrt(distSq);
            double distanceFactor = 1.0 - dist / radius;

            float damage = (float) ((distanceFactor * exposure) * radius * 2.0);

            Entity owner = this.getOwner();

            if (owner instanceof LivingEntity livingOwner) {
                target.hurt(
                        this.damageSources().mobProjectile(this, livingOwner),
                        damage
                );
            } else {
                target.hurt(
                        RegisterDamageTypes.causeHoleHitDamage(this),
                        damage
                );
            }
        }
    }

    private double getExposure(Vec3 explosionPos, Entity entity) {
        AABB box = entity.getBoundingBox();

        double stepX = 1.0 / (box.getXsize() * 2.0 + 1.0);
        double stepY = 1.0 / (box.getYsize() * 2.0 + 1.0);
        double stepZ = 1.0 / (box.getZsize() * 2.0 + 1.0);

        double visible = 0.0;
        double total = 0.0;

        for (double x = 0.0; x <= 1.0; x += stepX) {
            for (double y = 0.0; y <= 1.0; y += stepY) {
                for (double z = 0.0; z <= 1.0; z += stepZ) {
                    Vec3 sample = new Vec3(
                            Mth.lerp(x, box.minX, box.maxX),
                            Mth.lerp(y, box.minY, box.maxY),
                            Mth.lerp(z, box.minZ, box.maxZ)
                    );

                    BlockHitResult hit = this.level().clip(
                            new ClipContext(
                                    sample,
                                    explosionPos,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    this
                            )
                    );

                    if (hit.getType() == HitResult.Type.MISS) {
                        visible++;
                    }

                    total++;
                }
            }
        }

        return total <= 0.0 ? 0.0 : visible / total;
    }

    public void setSize(float value) {
        this.entityData.set(SIZE, value);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setEffectSize(float value) {
        this.entityData.set(EFFECT_SIZE, value);
    }

    public float getEffectSize() {
        return this.entityData.get(EFFECT_SIZE);
    }

    @Override
    public void setStretchStrength(float value) {
        this.entityData.set(STRETCH_STRENGTH, value);
    }

    @Override
    public float getStretchStrength() {
        return this.entityData.get(STRETCH_STRENGTH);
    }

    @Override
    public void setStretchDir(Vector3f value) {
        this.entityData.set(STRETCH_DIR, value);
    }

    @Override
    public Vector3f getStretchDir() {
        return this.entityData.get(STRETCH_DIR);
    }

    public void setEffectExponent(float value) {
        this.entityData.set(EFFECT_EXPONENT, value);
    }

    public float getEffectExponent() {
        return this.entityData.get(EFFECT_EXPONENT);
    }

    public void setColor(int value) {
        this.entityData.set(COLOR, value);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setExplodingTime(int value) {
        this.entityData.set(EXPLODING_TIME, value);
    }

    public int getExplodingTime() {
        return this.entityData.get(EXPLODING_TIME);
    }

    public void setRainbow(boolean value) {
        this.entityData.set(RAINBOW, value);
    }

    public boolean shouldBeRainbow() {
        return this.entityData.get(RAINBOW);
    }

    @Override
    public PostEffectRegistry.HoleEffectInstance getEffectInstance() {
        return this.effectInstance;
    }
}