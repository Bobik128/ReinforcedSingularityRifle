package com.mod.rsrifle.sound;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRifleSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ReinforcedSingularityRifle.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_BUZZ_STEREO =
            registerSoundEvent("electric_buzz");

    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_BUZZ_MONO =
            registerSoundEvent("electric_buzz_mono");

    public static final DeferredHolder<SoundEvent, SoundEvent> RIFLE_SHOOT =
            registerSoundEvent("rifle_shoot");

    public static final DeferredHolder<SoundEvent, SoundEvent> RIFLE_RELOAD =
            registerSoundEvent("reload_full");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                ReinforcedSingularityRifle.MODID,
                name
        );

        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(id)
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}