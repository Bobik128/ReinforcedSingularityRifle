package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.items.RSRifleItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ReinforcedSingularityRifle.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(RSRifleItems.SINGULARITY_BATTERY);
        simpleItem(RSRifleItems.SINGULARITY_BATTERY_EMPTY);
        simpleItem(RSRifleItems.SINGULARITY_BATTERY_INCOMPLETE);

//        withExistingParent(ModItems.KEYCODE_LINK_ITEM.getId().getPath(),
//                ResourceLocation.fromNamespaceAndPath(ReinforcedBreakable.MOD_ID, "block/keycode_link_off"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, "item/" + item.getId().getPath()));
    }
}
