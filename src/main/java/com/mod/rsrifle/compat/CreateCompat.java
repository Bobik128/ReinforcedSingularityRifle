package com.mod.rsrifle.compat;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

public class CreateCompat implements IConditionBuilder {
    public static final String CREATE_MODID = "create";

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded(CREATE_MODID);
    }
}