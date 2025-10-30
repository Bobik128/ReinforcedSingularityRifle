package com.mod.rsrifle.sound;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RSRifleSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ReinforcedSingularityRifle.MODID);

    public static final RegistryObject<SoundEvent> ELECTRIC_BUZZ_STEREO = registerSoundEvent("electric_buzz");
    public static final RegistryObject<SoundEvent> ELECTRIC_BUZZ_MONO = registerSoundEvent("electric_buzz_mono");
    public static final RegistryObject<SoundEvent> RIFLE_SHOOT = registerSoundEvent("rifle_shoot");
    public static final RegistryObject<SoundEvent> RIFLE_RELOAD = registerSoundEvent("reload_full");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
