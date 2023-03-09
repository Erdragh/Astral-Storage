package com.github.erdragh.astralstorage;

import com.github.erdragh.astralstorage.item.BackpackItem;
import com.github.erdragh.astralstorage.networking.C2SPackets;
import com.github.erdragh.astralstorage.screen.BackpackScreenHandler;
import com.github.erdragh.astralstorage.util.BackpackInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AstralStorage implements ModInitializer {

    public static final String MODID = "astralstorage";
    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    public static final Item BACKPACK_ITEM = new BackpackItem(new FabricItemSettings().group(ItemGroup.MISC), new BackpackInfo(9, 1));

    static {
        BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "backpack_screen"), BackpackScreenHandler::new);
    }

    @Override
    public void onInitialize() {
        C2SPackets.register();

        Registry.register(Registry.ITEM, new Identifier(MODID, "backpack"), BACKPACK_ITEM);
    }
}
