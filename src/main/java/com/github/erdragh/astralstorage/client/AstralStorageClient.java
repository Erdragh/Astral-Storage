package com.github.erdragh.astralstorage.client;

import com.github.erdragh.astralstorage.AstralStorage;
import com.github.erdragh.astralstorage.networking.C2SPackets;
import com.github.erdragh.astralstorage.screen.BackpackScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AstralStorageClient implements ClientModInitializer {

    public static KeyBinding openBackpack;

    @Override
    public void onInitializeClient() {
        openBackpack = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + AstralStorage.MODID + ".open_backpack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, KeyBinding.INVENTORY_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBackpack.wasPressed()) {
                ClientPlayNetworking.send(C2SPackets.OPEN_BACKPACK, PacketByteBufs.create());
            }
        });

        HandledScreens.register(AstralStorage.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);
    }
}
