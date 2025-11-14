package com.mod.rsrifle;

import com.mod.rsrifle.entity.BlackHoleProjectile2;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = ReinforcedSingularityRifle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterDamageTypes {
    public static final ResourceKey<DamageType> HOLE_HIT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("rbrifle", "hole_hit"));

    static class CustomEntityDamageSource extends DamageSource {
        public CustomEntityDamageSource(Holder<DamageType> damageTypeIn, @Nullable Entity damageSourceEntityIn) {
            super(damageTypeIn, damageSourceEntityIn);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    static class CustomIndirectEntityDamageSource extends DamageSource {

        public CustomIndirectEntityDamageSource(Holder<DamageType> damageTypeIn, @Nullable Entity source, @Nullable Entity indirectEntityIn) {
            super(damageTypeIn, source, indirectEntityIn);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                // Tell generator to run only when server data are generating
                event.includeServer(),
                (DataProvider.Factory<ModDamageTypeTagsProvider>) output -> new ModDamageTypeTagsProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        ReinforcedSingularityRifle.MODID,
                        event.getExistingFileHelper()
                )
        );
    }

    public static CustomIndirectEntityDamageSource causeHoleHitDamage(@Nullable BlackHoleProjectile2 entity) {
        Holder<DamageType> holder = entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(HOLE_HIT).get();
        return new CustomIndirectEntityDamageSource(holder, entity, entity.getOwner());
    }

    public static class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {

        public ModDamageTypeTagsProvider(PackOutput p_270719_, CompletableFuture<HolderLookup.Provider> p_270256_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_270719_, p_270256_, modId, existingFileHelper);
        }
    }
}
