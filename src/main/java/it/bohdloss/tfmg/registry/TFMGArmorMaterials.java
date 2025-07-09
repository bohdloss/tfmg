package it.bohdloss.tfmg.registry;

import it.bohdloss.tfmg.TFMG;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

import static it.bohdloss.tfmg.TFMG.MOD_ID;
import static net.minecraft.world.item.ArmorItem.Type.*;

public class TFMGArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);

    public static final Holder<ArmorMaterial> STEEL = ARMOR_MATERIALS.register("steel", () -> new ArmorMaterial(
            Map.of(HELMET, 3, CHESTPLATE, 8, LEGGINGS, 6, BOOTS, 3),
            18,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.of(TFMGItems.STEEL_INGOT.get()),
            List.of(new ArmorMaterial.Layer(TFMG.asResource("steel"))),
            2.0f,
            0.1f
    ));

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
