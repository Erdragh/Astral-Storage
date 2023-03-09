package com.github.erdragh.astralstorage.screen;

import com.github.erdragh.astralstorage.AstralStorage;
import com.github.erdragh.astralstorage.item.BackpackItem;
import com.github.erdragh.astralstorage.util.BackpackInfo;
import com.github.erdragh.astralstorage.util.Dimension;
import com.github.erdragh.astralstorage.util.InvUtils;
import com.github.erdragh.astralstorage.util.Point;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {

    private final ItemStack backpackStack;
    private final int padding = 8;
    private final int titleSpace = 10;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readItemStack());
    }

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack backpackStack) {
        super(AstralStorage.BACKPACK_SCREEN_HANDLER, syncId);

        this.backpackStack = backpackStack;

        if (backpackStack.getItem() instanceof BackpackItem) {
            setupContainer(playerInventory);
        } else {
            PlayerEntity player = playerInventory.player;
            this.close(player);
        }
    }

    private void setupContainer(PlayerInventory playerInventory) {
        Dimension dim = getDimension();
        BackpackInfo info = getItem().getInfo();

        int rows = info.rows();
        int cols = info.cols();

        NbtList tag = backpackStack.getOrCreateNbt().getList("inventory", NbtType.COMPOUND);
        BackpackInventory inv = new BackpackInventory(9) {
            @Override
            public void markDirty() {
                backpackStack.getOrCreateNbt().put("inventory", InvUtils.toTag(this));
                super.markDirty();
            }
        };

        InvUtils.fromTag(tag, inv);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Point backpackSlotPosition = getBackpackSlotPosition(dim, x, y);
                addSlot(new BackpackLockedSlot(inv, y * cols + x, backpackSlotPosition.x() + 1, backpackSlotPosition.y()));
            }
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dim, x, y);
                addSlot(new BackpackLockedSlot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x() + 1, playerInvSlotPosition.y() + 1));
            }
        }

        for (int x = 0; x < 9; ++x) {
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dim, x, 3);
            this.addSlot(new BackpackLockedSlot(playerInventory, x, playerInvSlotPosition.x() + 1, playerInvSlotPosition.y() + 1));
        }
    }

    public BackpackItem getItem() {
        return (BackpackItem) backpackStack.getItem();
    }

    public Dimension getDimension() {
        BackpackInfo info = getItem().getInfo();
        return new Dimension(padding * 2 + Math.max(info.cols(), 9) * 18, padding * 2 + titleSpace * 2 + 8 + (info.rows() + 4) * 18);
    }

    public Point getBackpackSlotPosition(Dimension dim, int x, int y) {
        BackpackInfo info = getItem().getInfo();
        return new Point(dim.width() / 2 - info.cols() * 9 + x * 18, padding + titleSpace + y * 18);
    }

    public Point getPlayerInvSlotPosition(Dimension dim, int x, int y)  {
        BackpackInfo info = getItem().getInfo();
        return new Point(dim.width() / 2 - 9 * 9 + x * 18, dim.height() - padding - 4 * 18 - 3 + y * 18 + (y == 3 ? 4 : 0));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return backpackStack.getItem() instanceof BackpackItem;
    }

    public ItemStack getBackpackStack() {
        return backpackStack;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        var stack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            var toInsert = slot.getStack();
            stack = toInsert.copy();
            BackpackInfo info = getItem().getInfo();
            if (index < info.rows() * info.cols()) {
                if (!this.insertItem(toInsert, info.rows() * info.cols(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(toInsert, 0, info.rows() * info.cols(), false)) {
                return ItemStack.EMPTY;
            }

            if (toInsert.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return stack;
    }

    class BackpackInventory extends SimpleInventory {
        public BackpackInventory(int slots) {
            super(slots);
        }
    }

    private class BackpackLockedSlot extends Slot {
        public BackpackLockedSlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementAllowed(getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stackMovementAllowed(stack);
        }

        private boolean stackMovementAllowed(ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem) && stack != backpackStack;
        }
    }
}
