package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.items.RSRifleItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ReinforcedSingularityRifle.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(RSRifleItems.SINGULARITY_BATTERY);
        simpleItem(RSRifleItems.SINGULARITY_BATTERY_EMPTY);

        if (RSRifleItems.SINGULARITY_BATTERY_INCOMPLETE != null) {
            simpleItem(RSRifleItems.SINGULARITY_BATTERY_INCOMPLETE);
        }

        /*
        Example later:

        withExistingParent(
                RSRifleItems.SOME_ITEM.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, "block/some_block")
        );
        */
    }

    private ItemModelBuilder simpleItem(DeferredItem<? extends Item> item) {
        String path = item.getId().getPath();

        return withExistingParent(
                path,
                ResourceLocation.parse("item/generated")
        ).texture(
                "layer0",
                ResourceLocation.fromNamespaceAndPath(
                        ReinforcedSingularityRifle.MODID,
                        "item/" + path
                )
        );
    }
}