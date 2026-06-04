package com.mod.rsrifle.entity;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.entity.ItemEntity.SingularityRifleItemEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRifleEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(
                    BuiltInRegistries.ENTITY_TYPE,
                    ReinforcedSingularityRifle.MODID
            );

    public static final DeferredHolder<EntityType<?>, EntityType<BlackHoleProjectile2>> BLACK_HOLE_PROJECTILE2 =
            ENTITY_TYPES.register(
                    "black_hole_projectile2",
                    () -> EntityType.Builder
                            .<BlackHoleProjectile2>of(BlackHoleProjectile2::new, MobCategory.MISC)
                            .sized(0.2f, 0.2f)
                            .build("black_hole_projectile2")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<SingularityRifleItemEntity>> RIFLE_ITEM =
            ENTITY_TYPES.register(
                    "rifle_item",
                    () -> EntityType.Builder
                            .<SingularityRifleItemEntity>of(SingularityRifleItemEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(6)
                            .updateInterval(20)
                            .build("rifle_item")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}