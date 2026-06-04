package com.mod.rsrifle.entity.ItemEntity;

import com.mod.rsrifle.entity.RSRifleEntityTypes;
import com.mod.rsrifle.sound.LoopingSound;
import com.mod.rsrifle.sound.RSRifleSounds;
import com.mod.rsrifle.utils.FirearmMode;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SingularityRifleItemEntity extends ItemEntity {
    private static final int LIFETIME = 6000;

    @OnlyIn(Dist.CLIENT)
    private LoopingSound soundInstance;

    public SingularityRifleItemEntity(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SingularityRifleItemEntity(
            Level level,
            double posX,
            double posY,
            double posZ,
            ItemStack itemStack,
            double deltaX,
            double deltaY,
            double deltaZ
    ) {
        this(RSRifleEntityTypes.RIFLE_ITEM.get(), level);

        this.setPos(posX, posY, posZ);
        this.setDeltaMovement(deltaX, deltaY, deltaZ);
        this.setItem(itemStack);

        this.lifespan = itemStack.isEmpty()
                ? LIFETIME
                : itemStack.getEntityLifespan(level);
    }

    public SingularityRifleItemEntity(ItemEntity other) {
        super(RSRifleEntityTypes.RIFLE_ITEM.get(), other.level());

        this.setItem(other.getItem().copy());
        this.copyPosition(other);
        this.lifespan = other.lifespan;
        this.setDeltaMovement(other.getDeltaMovement());
        this.setDefaultPickUpDelay();
    }

    public SingularityRifleItemEntity(
            Level level,
            double posX,
            double posY,
            double posZ,
            ItemStack itemStack
    ) {
        this(
                level,
                posX,
                posY,
                posZ,
                itemStack,
                level.random.nextDouble() * 0.2D - 0.1D,
                0.2D,
                level.random.nextDouble() * 0.2D - 0.1D
        );
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick() {
        float volume = FirearmMode.getVolume(this.getItem());

        if (this.soundInstance == null || this.soundInstance.isStopped()) {
            this.soundInstance = new LoopingSound(
                    RSRifleSounds.ELECTRIC_BUZZ_MONO.get(),
                    SoundSource.NEUTRAL,
                    this,
                    volume + 0.01f
            );

            Minecraft.getInstance().getSoundManager().play(this.soundInstance);
        }

        this.soundInstance.setVolume(volume);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.clientTick();
        }
    }
}