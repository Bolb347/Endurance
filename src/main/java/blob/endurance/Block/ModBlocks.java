package blob.endurance.Block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block THRUSTER_BLOCK;
    public static Block SEAT_BLOCK;

    public static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> group) {
        Identifier id = Identifier.of("endurance", name);

        // Register the block
        Registry.register(Registries.BLOCK, id, block);

        // Register the item form of the block
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));

        // Optionally: add to creative tab
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
            entries.add(block);
        });

        return block;
    }

    public static void register() {
        THRUSTER_BLOCK = registerBlock("thruster", new ThrusterBlock(AbstractBlock.Settings.create()), ItemGroups.BUILDING_BLOCKS);
        SEAT_BLOCK = registerBlock("seat", new ThrusterBlock(AbstractBlock.Settings.create()), ItemGroups.BUILDING_BLOCKS);
    }
}
