package com.github.erdragh.astralstorage.item;

import com.github.erdragh.astralstorage.AstralStorage;
import com.github.erdragh.astralstorage.screen.BackpackScreenHandler;
import com.github.erdragh.astralstorage.util.BackpackInfo;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class BackpackItem extends TrinketItem implements ExtendedScreenHandlerFactory {

    protected BackpackInfo info;

    public BackpackItem(Settings settings, BackpackInfo info) {
        super(settings);
        this.info = info;
    }

    public BackpackInfo getInfo() {
        return info;
    }

    public void openScreen(PlayerEntity player, ItemStack backpackItemStack) {
        if (player.world != null && !player.world.isClient) {
            player.openHandledScreen(this);
        }
    }
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        var trinketComponent = TrinketsApi.getTrinketComponent(player);
        if (trinketComponent.isPresent()) {
            var comp = trinketComponent.get();
            comp.getEquipped(AstralStorage.BACKPACK_ITEM).forEach(slotPair -> {
                buf.writeItemStack(slotPair.getRight());
            });
        }
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("gui." + AstralStorage.MODID + ".backpack_screen");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        var trinketComponent = TrinketsApi.getTrinketComponent(player);
        if (trinketComponent.isPresent()) {
            var comp = trinketComponent.get();
            for (var pair : comp.getEquipped(AstralStorage.BACKPACK_ITEM)) {
                return new BackpackScreenHandler(syncId, inv, pair.getRight());
            }
        }
        return null;
    }
}
