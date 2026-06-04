package com.mod.rsrifle.datagen;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ReinforcedSingularityRifle.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        /*
         * No blocks currently.
         *
         * Example later:
         * blockWithItem(RSRifleBlocks.SOME_BLOCK);
         */
    }

    private void facingBlock(DeferredHolder<Block, ? extends Block> blockHolder) {
        Block block = blockHolder.get();
        facingBlock(block, cubeAll(block));
    }

    private void facingBlock(DeferredHolder<Block, ? extends Block> blockHolder, ModelFile modelFile) {
        facingBlock(blockHolder.get(), modelFile);
    }

    private void facingBlock(Block block, ModelFile modelFile) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.FACING);

            int xRot = switch (facing) {
                case UP -> 0;
                case DOWN -> 180;
                case NORTH, SOUTH, EAST, WEST -> 90;
            };

            int yRot = switch (facing) {
                case SOUTH -> 180;
                case EAST -> 90;
                case WEST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });
    }

    private void blockWithItem(DeferredHolder<Block, ? extends Block> blockHolder) {
        Block block = blockHolder.get();
        simpleBlockWithItem(block, cubeAll(block));
    }

    private String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}