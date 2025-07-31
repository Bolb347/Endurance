package blob.endurance.Item;

import blob.endurance.Endurance;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;

public class NbtHelper {
    private static void writePos(NbtCompound root, String key, BlockPos pos) {
        NbtCompound tag = new NbtCompound();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        root.put(key, tag);
    }

    public static void setPositions(ItemStack stack, BlockPos pos1, BlockPos pos2) {
        NbtCompound root = new NbtCompound();
        writePos(root, "Pos1", pos1);
        writePos(root, "Pos2", pos2);
        NbtComponent comp = NbtComponent.of(root);
        stack.set(DataComponentTypes.CUSTOM_DATA, comp);
    }

    public static BlockPos getPosition(ItemStack stack, String key) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return Endurance.INV_POS;
        NbtCompound root = comp.copyNbt();
        if (!root.contains(key)) return Endurance.INV_POS;
        NbtCompound tag = root.getCompound(key);
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
}
