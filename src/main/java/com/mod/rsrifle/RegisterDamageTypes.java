package com.mod.rsrifle;

import com.mod.rsrifle.entity.BlackHoleProjectile2;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RegisterDamageTypes {
    public static final ResourceKey<DamageType> HOLE_HIT =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(
                            ReinforcedSingularityRifle.MODID,
                            "hole_hit"
                    )
            );

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterDamageTypes::gatherData);
    }

    public static class CustomEntityDamageSource extends DamageSource {
        public CustomEntityDamageSource(
                Holder<DamageType> damageType,
                @Nullable Entity directEntity
        ) {
            super(damageType, directEntity);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity livingEntity) {
            LivingEntity killCredit = livingEntity.getKillCredit();

            String baseKey = "death.attack." + this.getMsgId();
            int index = livingEntity.getRandom().nextInt(2);

            String victimOnlyKey = baseKey + "." + index;
            String attackerKey = baseKey + ".attacker_" + index;

            return killCredit != null
                    ? Component.translatable(
                    attackerKey,
                    livingEntity.getDisplayName(),
                    killCredit.getDisplayName()
            )
                    : Component.translatable(
                    victimOnlyKey,
                    livingEntity.getDisplayName()
            );
        }
    }

    public static class CustomIndirectEntityDamageSource extends DamageSource {
        public CustomIndirectEntityDamageSource(
                Holder<DamageType> damageType,
                @Nullable Entity directEntity,
                @Nullable Entity causingEntity
        ) {
            super(damageType, directEntity, causingEntity);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity livingEntity) {
            LivingEntity killCredit = livingEntity.getKillCredit();

            String baseKey = "death.attack." + this.getMsgId();
            int index = livingEntity.getRandom().nextInt(3);

            String victimOnlyKey = baseKey + "." + index;
            String attackerKey = baseKey + ".attacker_" + index;

            return killCredit != null
                    ? Component.translatable(
                    attackerKey,
                    livingEntity.getDisplayName(),
                    killCredit.getDisplayName()
            )
                    : Component.translatable(
                    victimOnlyKey,
                    livingEntity.getDisplayName()
            );
        }
    }

    private static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new ModDamageTypeTagsProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        ReinforcedSingularityRifle.MODID,
                        event.getExistingFileHelper()
                )
        );
    }

    public static CustomIndirectEntityDamageSource causeHoleHitDamage(@Nullable BlackHoleProjectile2 entity) {
        if (entity == null) {
            throw new IllegalArgumentException("BlackHoleProjectile2 entity cannot be null for hole hit damage source");
        }

        Holder<DamageType> holder = entity.level()
                .registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(HOLE_HIT);

        return new CustomIndirectEntityDamageSource(
                holder,
                entity,
                entity.getOwner()
        );
    }

    public static class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {
        public ModDamageTypeTagsProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> lookupProvider,
                String modId,
                @Nullable ExistingFileHelper existingFileHelper
        ) {
            super(output, lookupProvider, modId, existingFileHelper);
        }
    }
}