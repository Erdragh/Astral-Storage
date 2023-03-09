package com.github.erdragh.astralstorage.networking;

import com.github.erdragh.astralstorage.AstralStorage;
import com.github.erdragh.astralstorage.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class C2SPackets {

    public static final Identifier OPEN_BACKPACK = new Identifier(AstralStorage.MODID, "open_backpack");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK, ((server, player, handler, buf, responseSender) -> {
            var trinketComponent = TrinketsApi.getTrinketComponent(player);
            System.out.println(trinketComponent);
            if (trinketComponent.isPresent()) {
                var comp = trinketComponent.get();
                comp.getEquipped(AstralStorage.BACKPACK_ITEM).forEach(slotPair -> {
                    ((BackpackItem)AstralStorage.BACKPACK_ITEM).openScreen(player, slotPair.getRight());
                });
            }
        }));
    }

}
