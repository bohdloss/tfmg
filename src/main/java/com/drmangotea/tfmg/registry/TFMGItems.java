package com.drmangotea.tfmg.registry;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.base.TFMGArmorMaterials;
import com.drmangotea.tfmg.base.TFMGTiers;
import com.drmangotea.tfmg.content.decoration.gearbox.SteelVerticalGearboxItem;
import com.drmangotea.tfmg.content.electricity.debug.DebugCinderBlockItem;
import com.drmangotea.tfmg.content.electricity.electrians_wrench.ElectriciansWrenchItem;
import com.drmangotea.tfmg.content.electricity.utilities.transformer.ElectromagneticCoilItem;
import com.drmangotea.tfmg.content.items.CoalCokeItem;
import com.drmangotea.tfmg.content.items.ScrewdriverItem;
import com.drmangotea.tfmg.content.items.weapons.LeadAxeItem;
import com.drmangotea.tfmg.content.items.weapons.LeadSwordItem;
import com.drmangotea.tfmg.content.items.weapons.advanced_potato_cannon.AdvancedPotatoCannonItem;
import com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb.PipeBombItem;
import com.drmangotea.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade;
import com.drmangotea.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenadeItem;
import com.drmangotea.tfmg.content.items.weapons.flamethrover.FlamethrowerItem;
import com.drmangotea.tfmg.content.items.weapons.lithium_blade.LitLithiumBladeItem;
import com.drmangotea.tfmg.content.items.weapons.lithium_blade.LithiumBladeItem;
import com.drmangotea.tfmg.content.items.weapons.quad_potato_cannon.QuadPotatoCannonItem;
import com.drmangotea.tfmg.content.machinery.misc.winding_machine.SpoolItem;
import com.drmangotea.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base.DepositItem;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;

import static com.drmangotea.tfmg.TFMG.REGISTRATE;
import static com.drmangotea.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade.ChemicalColor.*;
import static com.simibubi.create.AllTags.AllItemTags.CREATE_INGOTS;
import static com.simibubi.create.AllTags.forgeItemTag;

public class TFMGItems {


    //materials


    public static final ItemEntry<Item>
            STEEL_INGOT = taggedIngredient("steel_ingot", forgeItemTag("ingots/steel"), CREATE_INGOTS.tag),
            CAST_IRON_INGOT = taggedIngredient("cast_iron_ingot", forgeItemTag("ingots/cast_iron"), CREATE_INGOTS.tag),
            ALUMINUM_INGOT = taggedIngredient("aluminum_ingot", forgeItemTag("ingots/aluminum"), CREATE_INGOTS.tag),

    PLASTIC_SHEET = taggedIngredient("plastic_sheet", forgeItemTag("ingots/plastic"), CREATE_INGOTS.tag),
            HEAVY_PLATE = taggedIngredient("heavy_plate", forgeItemTag("plates/steel")),
            ALUMINUM_SHEET = taggedIngredient("aluminum_sheet", forgeItemTag("plates/aluminum")),
            NICKEL_SHEET = taggedIngredient("nickel_sheet", forgeItemTag("plates/nickel")),
            CAST_IRON_SHEET = taggedIngredient("cast_iron_sheet", forgeItemTag("plates/cast_iron")),
            LEAD_SHEET = taggedIngredient("lead_sheet", forgeItemTag("plates/lead")),
            LEAD_INGOT = taggedIngredient("lead_ingot", forgeItemTag("ingots/lead"), CREATE_INGOTS.tag),
            NICKEL_INGOT = taggedIngredient("nickel_ingot", forgeItemTag("ingots/nickel"), CREATE_INGOTS.tag),
            CONSTANTAN_INGOT = taggedIngredient("constantan_ingot", forgeItemTag("ingots/constantan"), CREATE_INGOTS.tag),
            LITHIUM_INGOT = taggedIngredient("lithium_ingot", forgeItemTag("ingots/lithium"), CREATE_INGOTS.tag),
            ALUMINUM_NUGGET = taggedIngredient("aluminum_nugget", forgeItemTag("nuggets/aluminum")),
            STEEL_NUGGET = taggedIngredient("steel_nugget", forgeItemTag("nuggets/steel")),
            CAST_IRON_NUGGET = taggedIngredient("cast_iron_nugget", forgeItemTag("nuggets/cast_iron")),
            CONSTANTAN_NUGGET = taggedIngredient("constantan_nugget", forgeItemTag("nuggets/constantan")),
            LEAD_NUGGET = taggedIngredient("lead_nugget", forgeItemTag("nuggets/lead")),
            NICKEL_NUGGET = taggedIngredient("nickel_nugget", forgeItemTag("nuggets/nickel")),
            LITHIUM_NUGGET = taggedIngredient("lithium_nugget", forgeItemTag("nuggets/lithium")),
            RAW_LEAD = taggedIngredient("raw_lead", forgeItemTag("raw_materials/lead"), forgeItemTag("raw_materials")),
            RAW_NICKEL = taggedIngredient("raw_nickel", forgeItemTag("raw_materials/nickel"), forgeItemTag("raw_materials")),
            RAW_LITHIUM = taggedIngredient("raw_lithium", forgeItemTag("raw_materials/lithium"), forgeItemTag("raw_materials")),
            COPPER_WIRE = taggedIngredient("copper_wire", forgeItemTag("wires/copper")),
            ALUMINUM_WIRE = taggedIngredient("aluminum_wire", forgeItemTag("wires/aluminum")),
            SYNTHETIC_LEATHER = taggedIngredient("synthetic_leather", Tags.Items.LEATHER, AllTags.forgeItemTag("leather")),
            SYNTHETIC_STRING = taggedIngredient("synthetic_string", Tags.Items.STRING, AllTags.forgeItemTag("string")),
            LIMESAND = taggedIngredient("limesand", TFMGTags.TFMGItemTags.FLUX.tag),
            SULFUR_DUST = taggedIngredient("sulfur_dust", forgeItemTag("dusts/sulfur")),
            RUBBER_SHEET = taggedIngredient("rubber_sheet", forgeItemTag("ingots/rubber")),
            SILICON_INGOT = taggedIngredient("silicon_ingot", forgeItemTag("ingots/silicon"));

    public static final ItemEntry<Item>
            SPARK_PLUG = REGISTRATE.item("spark_plug", Item::new).register(),
            SLAG = REGISTRATE.item("slag", Item::new).register(),
            BITUMEN = REGISTRATE.item("bitumen", Item::new).register(),
            FIREPROOF_BRICK = REGISTRATE.item("fireproof_brick", Item::new).register(),
            FIRECLAY_BALL = REGISTRATE.item("fireclay_ball", Item::new).register(),
            SCREW = REGISTRATE.item("screw", Item::new).register(),
            ENGINE_CHAMBER = REGISTRATE.item("engine_chamber", Item::new).register(),
            TURBINE_BLADE = REGISTRATE.item("turbine_blade", Item::new).register(),
            THERMITE_POWDER = REGISTRATE.item("thermite_powder", Item::new).register(),
            STEEL_MECHANISM = REGISTRATE.item("steel_mechanism", Item::new).register(),
            NITRATE_DUST = REGISTRATE.item("nitrate_dust", Item::new).register(),
            CONCRETE_MIXTURE = REGISTRATE.item("concrete_mixture", Item::new).register(),
            MAGNETIC_ALLOY_INGOT = REGISTRATE.item("magnetic_alloy_ingot", Item::new).register(),
            MAGNET = REGISTRATE.item("magnet", Item::new).register(),
            EMPTY_CIRCUIT_BOARD = REGISTRATE.item("empty_circuit_board", Item::new).register(),
            COATED_CIRCUIT_BOARD = REGISTRATE.item("coated_circuit_board", Item::new).register(),
            ETCHED_CIRCUIT_BOARD = REGISTRATE.item("etched_circuit_board", Item::new).register(),
            CIRCUIT_BOARD = REGISTRATE.item("circuit_board", Item::new).register(),
            RESISTOR = REGISTRATE.item("resistor_item", Item::new).lang("Resistor").register(),
            TRANSISTOR = REGISTRATE.item("transistor_item", Item::new).lang("Transistor").register(),
            CAPACITOR = REGISTRATE.item("capacitor_item", Item::new).lang("Capacitor").register(),
            ZINC_SULFATE = REGISTRATE.item("zinc_sulfate", Item::new).register(),
            COPPER_SULFATE = REGISTRATE.item("copper_sulfate", Item::new).register(),
            LITHIUM_CHARGE = REGISTRATE.item("lithium_charge", Item::new).register(),
            FUSE = REGISTRATE.item("fuse", Item::new).register(),
            CINDERBLOCK = REGISTRATE.item("cinderblock", Item::new)
                    //  .recipe((c, p) -> p.stonecutting(DataIngredient.items(TFMGBlocks.CONCRETE.get()), RecipeCategory.BUILDING_BLOCKS, c::get, 4))
                    .register(),
            CINDERFLOURBLOCK = REGISTRATE.item("cinderflourblock", Item::new).register(),
            NAPALM_POTATO = REGISTRATE.item("napalm_potato", Item::new).register(),
            MIXER_BLADE = REGISTRATE.item("mixer_blade", Item::new).register(),
            CENTRIFUGE = REGISTRATE.item("centrifuge", Item::new).register(),
            MULTIMETER = REGISTRATE.item("multimeter", Item::new).register(),
            CRANKSHAFT = REGISTRATE.item("crankshaft", Item::new).register(),
            P_SEMICONDUCTOR = REGISTRATE.item("p_semiconductor", Item::new)
                    .lang("P-Semiconductor").register(),
            N_SEMICONDUCTOR = REGISTRATE.item("n_semiconductor", Item::new)
                    .lang("N-Semiconductor").register(),
            COPPER_ELECTRODE = REGISTRATE.item("copper_electrode", Item::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/copper_electrode_model"))).register(),

    ZINC_ELECTRODE = REGISTRATE.item("zinc_electrode", Item::new)
            .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/zinc_electrode_model"))).register(),

    GRAPHITE_ELECTRODE = REGISTRATE.item("graphite_electrode", Item::new)
            .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/graphite_electrode_model"))).register();


    public static final ItemEntry<SpoolItem>
            EMPTY_SPOOL = spoolItem("empty", null, 0x000000),
            COPPER_SPOOL = spoolItem("copper", TFMGPartialModels.COPPER_SPOOL, 0xD8735A),
            ALUMINUM_SPOOL = spoolItem("aluminum", TFMGPartialModels.ALUMINUM_SPOOL, 0xEDEFEF),
            CONSTANTAN_SPOOL = spoolItem("constantan", TFMGPartialModels.CONSTANTAN_SPOOL, 0xCFC2A8);

    public static final ItemEntry<ElectromagneticCoilItem> ELECTROMAGNETIC_COIL =
            REGISTRATE.item("electromagnetic_coil", ElectromagneticCoilItem::new).register();

    public static final ItemEntry<CoalCokeItem> COAL_COKE_DUST = REGISTRATE.item("coal_coke_dust", CoalCokeItem::new)
            .tag(forgeItemTag("dusts/coal_coke"), TFMGTags.TFMGItemTags.BLAST_FURNACE_FUEL.tag)
            .register();

    public static final ItemEntry<CoalCokeItem> COAL_COKE = REGISTRATE.item("coal_coke", CoalCokeItem::new)
            .register();

    public static final ItemEntry<DebugCinderBlockItem> DEBUG_CINDERBLOCK = REGISTRATE.item("debug_cinderblock", DebugCinderBlockItem::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();
    public static final ItemEntry<ScrewdriverItem> SCREWDRIVER = REGISTRATE.item("screwdriver", ScrewdriverItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256))
            .register();

    public static final ItemEntry<DepositItem> DEPOSIT_ITEM = REGISTRATE.item("deposit_item", DepositItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(1))
            .register();
    public static final ItemEntry<ArmorItem>
            STEEL_HELMET = armor("steel_helmet", TFMGArmorMaterials.STEEL, ArmorItem.Type.HELMET),
            STEEL_CHESTPLATE = armor("steel_chestplate", TFMGArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE),
            STEEL_LEGGINGS = armor("steel_leggings", TFMGArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS),
            STEEL_BOOTS = armor("steel_boots", TFMGArmorMaterials.STEEL, ArmorItem.Type.BOOTS);

    public static final ItemEntry<SwordItem>
            STEEL_SWORD = toolset("steel", TFMGTiers.STEEL),
            ALUMINUM_SWORD = toolset("aluminum", TFMGTiers.ALUMINUM);

    public static final ItemEntry<LeadSwordItem> LEAD_SWORD = leadToolset();

    public static final ItemEntry<LithiumBladeItem> LITHIUM_BLADE =
            REGISTRATE.item("lithium_blade", p -> new LithiumBladeItem(TFMGTiers.STEEL, 3, -2.4F, p))
                    .model((ctx, prov) -> prov
                            .withExistingParent("lithium_blade", "minecraft:item/handheld")
                            .texture("layer0", "tfmg:item/lithium_blade"))
                    .register();
    public static final ItemEntry<LitLithiumBladeItem> LIT_LITHIUM_BLADE =
            REGISTRATE.item("lit_lithium_blade", p -> new LitLithiumBladeItem(TFMGTiers.STEEL, 4, -2.4F, p))
                    .model((ctx, prov) -> prov
                            .withExistingParent("lit_lithium_blade", "minecraft:item/handheld")
                            .texture("layer0", "tfmg:item/lithium_blade_lit"))
                    .lang("Lithium Blade")
                    .register();


    public static final ItemEntry<AdvancedPotatoCannonItem> ADVANCED_POTATO_CANNON =
            REGISTRATE.item("advanced_potato_cannon", AdvancedPotatoCannonItem::new)
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static final ItemEntry<FlamethrowerItem> FLAMETHROWER =
            REGISTRATE.item("flamethrower", FlamethrowerItem::new)
                    .model(AssetLookup.itemModelWithPartials())
                    .properties(p -> p.stacksTo(1))
                    .register();


    public static final ItemEntry<QuadPotatoCannonItem> QUAD_POTATO_CANNON =
            REGISTRATE.item("quad_potato_cannon", QuadPotatoCannonItem::new)
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static final ItemEntry<PipeBombItem>
            PIPE_BOMB = REGISTRATE.item("pipe_bomb", PipeBombItem::new)
            .register();
    public static final ItemEntry<ElectriciansWrenchItem>
            ELECTRICIANS_WRENCH = REGISTRATE.item("electricians_wrench", ElectriciansWrenchItem::new)
            .lang("Electrician's Wrench")
            .register();

    public static final ItemEntry<ThermiteGrenadeItem>
            THERMITE_GRENADE = thermiteGrenade("thermite_grenade", BASE);
    public static final ItemEntry<ThermiteGrenadeItem>
            ZINC_GRENADE = thermiteGrenade("zinc_grenade", GREEN);
    public static final ItemEntry<ThermiteGrenadeItem>
            COPPER_GRENADE = thermiteGrenade("copper_grenade", BLUE);

////////////////////////////

    private static ItemEntry<ArmorItem> armor(String name, ArmorMaterial material, ArmorItem.Type slot) {
        return REGISTRATE.item(name, p -> new ArmorItem(material, slot, p)).register();
    }

    private static ItemEntry<SwordItem> toolset(String material, Tier tier) {

        REGISTRATE.item(material + "_axe", p -> new AxeItem(tier, 6.0F, -3.2F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_axe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_axe"))
                .register();
        REGISTRATE.item(material + "_hoe", p -> new HoeItem(tier, 0, -3.0F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_hoe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_hoe"))
                .register();
        REGISTRATE.item(material + "_shovel", p -> new ShovelItem(tier, 1.5F, -3.0F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_shovel", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_shovel"))
                .register();
        REGISTRATE.item(material + "_pickaxe", p -> new PickaxeItem(tier, 1, -2.8F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_pickaxe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_pickaxe"))
                .register();

        return REGISTRATE.item(material + "_sword", p -> new SwordItem(tier, 3, -2.4F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_sword", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_sword"))
                .register();


    }

    public static final ItemEntry<SteelVerticalGearboxItem> STEEL_VERTICAL_GEARBOX =
            REGISTRATE.item("steel_vertical_gearbox", SteelVerticalGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("steel_gearbox", "item_vertical"))
                    .lang("Steel Vertical Gearbox")
                    .register();


    private static ItemEntry<LeadSwordItem> leadToolset() {

        REGISTRATE.item("lead_axe", p -> new LeadAxeItem(TFMGTiers.LEAD, 6.0F, -3.2F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_axe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_axe"))
                .register();
        ;
        REGISTRATE.item("lead_hoe", p -> new HoeItem(TFMGTiers.LEAD, 0, -3.0F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_hoe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_hoe"))
                .register();
        REGISTRATE.item("lead_shovel", p -> new ShovelItem(TFMGTiers.LEAD, 1.5F, -3.0F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_shovel", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_shovel"))
                .register();
        REGISTRATE.item("lead_pickaxe", p -> new PickaxeItem(TFMGTiers.LEAD, 1, -2.8F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_pickaxe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_pickaxe"))
                .register();

        return REGISTRATE.item("lead_sword", p -> new LeadSwordItem(TFMGTiers.LEAD, 3, -2.4F, p))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_sword", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_sword"))
                .register();


    }


    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    public static ItemEntry<Item> item(String name) {
        return REGISTRATE.item(name, Item::new)
                .register();
    }


    public static ItemEntry<SequencedAssemblyItem> assemblyItem(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    public static ItemEntry<SpoolItem> spoolItem(String name, PartialModel model, int barColor) {
        return REGISTRATE.item(name + "_spool", p -> new SpoolItem(p, model, barColor))
                .properties(p -> p.stacksTo(1))
                .register();
    }


    private static ItemEntry<ThermiteGrenadeItem> thermiteGrenade(String name, ThermiteGrenade.ChemicalColor color) {
        return REGISTRATE.item(name, p -> new ThermiteGrenadeItem(p, color))
                .register();
    }

    public static void init() {
    }
}
