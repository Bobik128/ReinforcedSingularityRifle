package com.mod.rsrifle.compat;

import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fml.ModList;

public class CreateCompat implements IConditionBuilder {
    public static final String CREATE_MODID = "create";

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded(CREATE_MODID);
    }
}
