package com.mod.rsrifle.entity;


import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.entity.ItemEntity.SingularityRifleItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RSRifleEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ReinforcedSingularityRifle.MODID);

    public static final RegistryObject<EntityType<SingularityRifleItemEntity>> RIFLE_ITEM =
            ENTITY_TYPES.register("rifle_item", () -> EntityType.Builder.<SingularityRifleItemEntity>of(SingularityRifleItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(6)
                    .updateInterval(20)
                    .build("rifle_item"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

