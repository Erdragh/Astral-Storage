package com.github.erdragh.astralstorage.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class InvUtils {
    public static NbtElement toTag(SimpleInventory inv) {
        NbtList tag = new NbtList();

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                var stackTag = new NbtCompound();
                stackTag.putInt("slot", i);
                stackTag.put("stack", stack.writeNbt(new NbtCompound()));
                tag.add(stackTag);
            }
        }

        return tag;
    }

    public static void fromTag(NbtList tag, SimpleInventory inv) {
        inv.clear();

        tag.forEach(element -> {
            var stackTag = (NbtCompound) element;
            int slot = stackTag.getInt("slot");
            var stack = ItemStack.fromNbt(stackTag.getCompound("stack"));
            inv.setStack(slot, stack);
        });
    }
}
