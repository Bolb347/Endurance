package blob.endurance.Item;

import blob.endurance.Endurance;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Endurance.MOD_ID, name), item);
    }

    public static void register() {

    }

    public static final Item SHIP_DESIGNATOR = registerItem("ship_designator", new ShipDesignator(new Item.Settings().maxCount(1)));
}
