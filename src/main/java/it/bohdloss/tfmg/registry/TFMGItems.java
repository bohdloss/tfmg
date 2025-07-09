package it.bohdloss.tfmg.registry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import it.bohdloss.tfmg.TFMG;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.ItemEntry;
import it.bohdloss.tfmg.base.Spark;
import it.bohdloss.tfmg.base.SparkBase;
import it.bohdloss.tfmg.content.decoration.SteelVerticalGearboxItem;
import it.bohdloss.tfmg.content.electricity.debug.DebugCinderBlockItem;
import it.bohdloss.tfmg.content.electricity.utilities.fuse_block.FuseItem;
import it.bohdloss.tfmg.content.electricity.utilities.resistor.ResistorItem;
import it.bohdloss.tfmg.content.electricity.connection.SpoolItem;
import it.bohdloss.tfmg.content.electricity.utilities.transformer.ElectromagneticCoilItem;
import it.bohdloss.tfmg.content.items.CoalCokeItem;
import it.bohdloss.tfmg.content.items.ScrewdriverItem;
import it.bohdloss.tfmg.content.items.weapons.LeadAxeItem;
import it.bohdloss.tfmg.content.items.weapons.LeadSwordItem;
import it.bohdloss.tfmg.content.items.weapons.LitLithiumBladeItem;
import it.bohdloss.tfmg.content.items.weapons.LithiumBladeItem;
import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade;
import it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenadeItem;
import it.bohdloss.tfmg.content.machinery.oil_processing.OilHammerItem;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static it.bohdloss.tfmg.TFMG.REGISTRATE;
import static com.simibubi.create.AllTags.AllItemTags.CREATE_INGOTS;
import static it.bohdloss.tfmg.content.items.weapons.explosives.thermite_grenades.ThermiteGrenade.ChemicalColor.*;
import static it.bohdloss.tfmg.registry.TFMGTags.commonItemTag;

public class TFMGItems {
    public static void init() {}

    public static final ItemEntry<Item>
            STEEL_INGOT = taggedIngredient("steel_ingot", commonItemTag("ingots/steel"), CREATE_INGOTS.tag),
            CAST_IRON_INGOT = taggedIngredient("cast_iron_ingot", commonItemTag("ingots/cast_iron"), CREATE_INGOTS.tag),
            ALUMINUM_INGOT = taggedIngredient("aluminum_ingot", commonItemTag("ingots/aluminum"), CREATE_INGOTS.tag),
            PLASTIC_SHEET = taggedIngredient("plastic_sheet", commonItemTag("ingots/plastic"), CREATE_INGOTS.tag),
            HEAVY_PLATE = taggedIngredient("heavy_plate", commonItemTag("plates/steel")),
            ALUMINUM_SHEET = taggedIngredient("aluminum_sheet", commonItemTag("plates/aluminum")),
            NICKEL_SHEET = taggedIngredient("nickel_sheet", commonItemTag("plates/nickel")),
            CAST_IRON_SHEET = taggedIngredient("cast_iron_sheet", commonItemTag("plates/cast_iron")),
            LEAD_SHEET = taggedIngredient("lead_sheet", commonItemTag("plates/lead")),
            LEAD_INGOT = taggedIngredient("lead_ingot", commonItemTag("ingots/lead"), CREATE_INGOTS.tag),
            NICKEL_INGOT = taggedIngredient("nickel_ingot", commonItemTag("ingots/nickel"), CREATE_INGOTS.tag),
            CONSTANTAN_INGOT = taggedIngredient("constantan_ingot", commonItemTag("ingots/constantan"), CREATE_INGOTS.tag),
            LITHIUM_INGOT = taggedIngredient("lithium_ingot", commonItemTag("ingots/lithium"), CREATE_INGOTS.tag),
            ALUMINUM_NUGGET = taggedIngredient("aluminum_nugget", commonItemTag("nuggets/aluminum")),
            STEEL_NUGGET = taggedIngredient("steel_nugget", commonItemTag("nuggets/steel")),
            CAST_IRON_NUGGET = taggedIngredient("cast_iron_nugget", commonItemTag("nuggets/cast_iron")),
            CONSTANTAN_NUGGET = taggedIngredient("constantan_nugget", commonItemTag("nuggets/constantan")),
            LEAD_NUGGET = taggedIngredient("lead_nugget", commonItemTag("nuggets/lead")),
            NICKEL_NUGGET = taggedIngredient("nickel_nugget", commonItemTag("nuggets/nickel")),
            LITHIUM_NUGGET = taggedIngredient("lithium_nugget", commonItemTag("nuggets/lithium")),
            RAW_LEAD = taggedIngredient("raw_lead", commonItemTag("raw_materials/lead"), commonItemTag("raw_materials")),
            RAW_NICKEL = taggedIngredient("raw_nickel", commonItemTag("raw_materials/nickel"), commonItemTag("raw_materials")),
            RAW_LITHIUM = taggedIngredient("raw_lithium", commonItemTag("raw_materials/lithium"), commonItemTag("raw_materials")),
            SYNTHETIC_LEATHER = taggedIngredient("synthetic_leather", Tags.Items.LEATHERS, AllTags.commonItemTag("leather")),
            LIMESAND = taggedIngredient("limesand", TFMGTags.TFMGItemTags.FLUX.tag),
            SULFUR_DUST = taggedIngredient("sulfur_dust", commonItemTag("dusts/sulfur")),
            RUBBER_SHEET = taggedIngredient("rubber_sheet", commonItemTag("ingots/rubber")),
            SILICON_INGOT = taggedIngredient("silicon_ingot", commonItemTag("ingots/silicon"));

    public static final ItemEntry<Item>
            REBAR = REGISTRATE.item("rebar", Item::new)
            .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/steel")), RecipeCategory.BUILDING_BLOCKS, c, 4))
            .register(),
            SYNTHETIC_STRING = REGISTRATE.item("synthetic_string", Item::new)
                    .tag(Tags.Items.STRINGS, AllTags.commonItemTag("string"))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/rubber")), RecipeCategory.MISC, c, 4))
                    .register();

    public static final ItemEntry<Item>
            COPPER_WIRE = REGISTRATE.item("copper_wire", Item::new).tag(AllTags.commonItemTag("wires/copper"))
            .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/copper")), RecipeCategory.BUILDING_BLOCKS, c, 2)).register(),
            ALUMINUM_WIRE = REGISTRATE.item("aluminum_wire", Item::new).tag(AllTags.commonItemTag("wires/aluminum"))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/aluminum")), RecipeCategory.BUILDING_BLOCKS, c, 2)).register(),
            CONSTANTAN_WIRE = REGISTRATE.item("constantan_wire", Item::new).tag(AllTags.commonItemTag("wires/constantan"))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/constantan")), RecipeCategory.BUILDING_BLOCKS, c, 2)).register();

    public static final ItemEntry<Item>
            SPARK_PLUG = REGISTRATE.item("spark_plug", Item::new).register(),
            SLAG = REGISTRATE.item("slag", Item::new).register(),
            BITUMEN = REGISTRATE.item("bitumen", Item::new).register(),
            FIREPROOF_BRICK = REGISTRATE.item("fireproof_brick", Item::new).register(),
            FIRECLAY_BALL = REGISTRATE.item("fireclay_ball", Item::new).register(),
            SCREW = REGISTRATE.item("screw", Item::new)
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("ingots/steel")), RecipeCategory.BUILDING_BLOCKS, c, 4))
                    .lang("Screws")
                    .register(),
            THERMITE_POWDER = REGISTRATE.item("thermite_powder", Item::new).register(),
            STEEL_MECHANISM = REGISTRATE.item("steel_mechanism", Item::new).register(),
            NITRATE_DUST = REGISTRATE.item("nitrate_dust", Item::new).register(),
            CONCRETE_MIXTURE = REGISTRATE.item("concrete_mixture", Item::new).register(),
            ASPHALT_MIXTURE = REGISTRATE.item("asphalt_mixture", Item::new).register(),
            MAGNETIC_ALLOY_INGOT = REGISTRATE.item("magnetic_alloy_ingot", Item::new).register(),
            BAUXITE_POWDER = REGISTRATE.item("bauxite_powder", Item::new).register(),

    EMPTY_CIRCUIT_BOARD = REGISTRATE.item("empty_circuit_board", Item::new).register(),
            COATED_CIRCUIT_BOARD = REGISTRATE.item("coated_circuit_board", Item::new).register(),
            ETCHED_CIRCUIT_BOARD = REGISTRATE.item("etched_circuit_board", Item::new).register(),
            CIRCUIT_BOARD = REGISTRATE.item("circuit_board", Item::new).register(),
            TRANSISTOR = REGISTRATE.item("transistor_item", Item::new).lang("Transistor").register(),
            CAPACITOR = REGISTRATE.item("capacitor_item", Item::new).lang("Capacitor").register(),
            COPPER_SULFATE = REGISTRATE.item("copper_sulfate", Item::new).register(),
            LITHIUM_CHARGE = REGISTRATE.item("lithium_charge", Item::new).register(),
            TURBO = REGISTRATE.item("turbo", Item::new).register(),
            GOLDEN_TURBO = REGISTRATE.item("golden_turbo", Item::new).register(),
            CINDERBLOCK = REGISTRATE.item("cinderblock", Item::new)
                    .recipe((c, p) -> p.stonecutting(DataIngredient.items(TFMGBlocks.CONCRETE.block().asItem()), RecipeCategory.BUILDING_BLOCKS, c::get, 8))
                    .register(),
            CINDERFLOURBLOCK = REGISTRATE.item("cinderflourblock", Item::new).register(),
            NAPALM_POTATO = REGISTRATE.item("napalm_potato", Item::new).register(),
            MIXER_BLADE = REGISTRATE.item("mixer_blade", Item::new)
                    .properties(p->p.stacksTo(1))
                    .register(),
            CENTRIFUGE = REGISTRATE.item("centrifuge", Item::new)
                    .properties(p->p.stacksTo(1))
                    .register(),
            CRANKSHAFT = REGISTRATE.item("crankshaft", Item::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/crankshaft_model")))
                    .register(),
            P_SEMICONDUCTOR = REGISTRATE.item("p_semiconductor", Item::new)
                    .lang("P-Semiconductor").register(),
            N_SEMICONDUCTOR = REGISTRATE.item("n_semiconductor", Item::new)
                    .lang("N-Semiconductor").register(),
            UNFINISHED_ELECTROMAGNETIC_COIL = REGISTRATE.item("unfinished_electromagnetic_coil", Item::new).register(),
            COPPER_ELECTRODE = REGISTRATE.item("copper_electrode", Item::new)
                    .properties(p -> p.stacksTo(1))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("storage_blocks/copper")), RecipeCategory.BUILDING_BLOCKS, c::get, 1))
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/copper_electrode_model"))).register(),
            ZINC_ELECTRODE = REGISTRATE.item("zinc_electrode", Item::new)
                    .properties(p -> p.stacksTo(1))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("storage_blocks/zinc")), RecipeCategory.BUILDING_BLOCKS, c::get, 1))
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/zinc_electrode_model"))).register(),
            GRAPHITE_ELECTRODE = REGISTRATE.item("graphite_electrode", Item::new)
                    .properties(p -> p.stacksTo(1))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.tag(AllTags.commonItemTag("storage_blocks/coal_coke")), RecipeCategory.BUILDING_BLOCKS, c::get, 1))
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/graphite_electrode_model"))).register(),
            UNFIRED_INSULATOR = REGISTRATE.item("unfired_insulator", Item::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/unfired_insulator_model")))
                    .recipe((c, p) -> p.stonecutting(DataIngredient.items(Blocks.CLAY.asItem()), RecipeCategory.BUILDING_BLOCKS, c::get, 1))
                    .register(),
            UNFINISHED_INSULATOR = REGISTRATE.item("unfinished_insulator", Item::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/unfinished_insulator_model"))).register(),
            GLASS_INSULATOR_SEGMENT = REGISTRATE.item("glass_insulator_segment", Item::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/glass_insulator_segment_model"))).register();
//    public static final ItemEntry<TransmissionItem>
//            TRANSMISSION = REGISTRATE.item("transmission", TransmissionItem::new)
//            .properties(p -> p.stacksTo(1))
//            .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/transmission_model"))).register();
    public static final ItemEntry<Item>
            MAGNET = REGISTRATE.item("magnet", Item::new)
        .properties(p -> p.fireResistant())
        .register();

    public static final ItemEntry<ResistorItem>
            UNFINISHED_RESISTOR = REGISTRATE.item("unfinished_resistor", ResistorItem::new).register();
//    public static final ItemEntry<CylinderItem>
//            DIESEL_ENGINE_CYLINDER = REGISTRATE.item("diesel_engine_cylinder", CylinderItem::new).register(),
//            SIMPLE_ENGINE_CYLINDER = REGISTRATE.item("simple_engine_cylinder", CylinderItem::new).register(),
//            ENGINE_CYLINDER = REGISTRATE.item("engine_cylinder", CylinderItem::new).register(),
//            TURBINE_BLADE = REGISTRATE.item("turbine_blade", CylinderItem::new).register();
    public static final ItemEntry<SpoolItem>
            EMPTY_SPOOL = spoolItem("empty", 0x000000)
            .recipe((c, p) -> p.stonecutting(DataIngredient.items(TFMGBlocks.HARDENED_PLANKS.asItem()), RecipeCategory.BUILDING_BLOCKS, c, 1))
            .register(),
            COPPER_SPOOL = spoolItem("copper", 0xD8735A)
                    .register(),
            ALUMINUM_SPOOL = spoolItem("aluminum", 0xEDEFEF)
                    .register(),
            CONSTANTAN_SPOOL = spoolItem("constantan", 0xCFC2A8)
                    .register();

    public static final ItemEntry<ElectromagneticCoilItem> ELECTROMAGNETIC_COIL =
            REGISTRATE.item("electromagnetic_coil", ElectromagneticCoilItem::new)
                    .properties(p -> p.stacksTo(1))
                    .register();

    public static final ItemEntry<FuseItem> FUSE = REGISTRATE.item("fuse", FuseItem::new)
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<CoalCokeItem> COAL_COKE_DUST = REGISTRATE.item("coal_coke_dust", CoalCokeItem::new)
            .tag(TFMGTags.commonItemTag("dusts/coal_coke"), TFMGTags.TFMGItemTags.BLAST_FURNACE_FUEL.tag)
            .register();

    public static final ItemEntry<OilHammerItem> OIL_HAMMER = REGISTRATE.item("oil_hammer", OilHammerItem::new)
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

//    public static final ItemEntry<DepositItem> DEPOSIT_ITEM = REGISTRATE.item("deposit_item", DepositItem::new)
//            .properties(p -> p.stacksTo(1)
//                    .durability(1))
//            .register();
    public static final ItemEntry<ArmorItem>
            STEEL_HELMET = armor("steel_helmet", TFMGArmorMaterials.STEEL, ArmorItem.Type.HELMET),
            STEEL_CHESTPLATE = armor("steel_chestplate", TFMGArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE),
            STEEL_LEGGINGS = armor("steel_leggings", TFMGArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS),
            STEEL_BOOTS = armor("steel_boots", TFMGArmorMaterials.STEEL, ArmorItem.Type.BOOTS);

    public static final List<ItemEntry<?>>
            STEEL_TOOLS = toolset("steel", TFMGTiers.STEEL),
            ALUMINUM_TOOLS = toolset("aluminum", TFMGTiers.ALUMINUM);

    public static final List<ItemEntry<?>> LEAD_TOOLS = leadToolset();

    public static final ItemEntry<LithiumBladeItem> LITHIUM_BLADE =
            REGISTRATE.item("lithium_blade", p -> new LithiumBladeItem(TFMGTiers.STEEL, p))
                    .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.STEEL, 3, -2.4F)))
                    .model((ctx, prov) -> prov
                            .withExistingParent("lithium_blade", "minecraft:item/handheld")
                            .texture("layer0", "tfmg:item/lithium_blade"))
                    .register();
    public static final ItemEntry<LitLithiumBladeItem> LIT_LITHIUM_BLADE =
            REGISTRATE.item("lit_lithium_blade", p -> new LitLithiumBladeItem(TFMGTiers.STEEL, p))
                    .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.STEEL, 4, -2.4F)))
                    .model((ctx, prov) -> prov
                            .withExistingParent("lit_lithium_blade", "minecraft:item/handheld")
                            .texture("layer0", "tfmg:item/lithium_blade_lit"))
                    .lang("Lithium Blade")
                    .register();


//    public static final ItemEntry<AdvancedPotatoCannonItem> ADVANCED_POTATO_CANNON =
//            REGISTRATE.item("advanced_potato_cannon", AdvancedPotatoCannonItem::new)
//                    .model(AssetLookup.itemModelWithPartials())
//                    .register();
//
//    public static final ItemEntry<FlamethrowerItem> FLAMETHROWER =
//            REGISTRATE.item("flamethrower", FlamethrowerItem::new)
//                    .model(AssetLookup.itemModelWithPartials())
//                    .properties(p -> p.stacksTo(1))
//                    .register();
//
//
//    public static final Map<String, RegistryEntry<MultimeterItem>> MULTIMETERS = multimeters();
//
//    public static final ItemEntry<MultimeterItem> MULTIMETER = REGISTRATE.item("multimeter", MultimeterItem::new)
//            .register();
//
//    public static final ItemEntry<SequencedAssemblyItem>
//            UNFINISHED_POTENTIOMETER = sequencedIngredient("unfinished_potentiometer", "block/potentiometer/unfinished"),
//            UNFINISHED_ELECTRIC_MOTOR = sequencedIngredient("unfinished_electric_motor", "block/electric_motor/unfinished"),
//            UNFINISHED_GENERATOR = sequencedIngredient("unfinished_generator", "item/unfinished_generator_model"),
//            UNFINISHED_STEEL_MECHANISM = sequencedIngredient("unfinished_steel_mechanism"),
//            UNFINISHED_TRANSISTOR = sequencedIngredient("unfinished_transistor"),
//            UNFINISHED_CAPACITOR = sequencedIngredient("unfinished_capacitor"),
//            UNFINISHED_CIRCUIT_BOARD = sequencedIngredient("unfinished_circuit_board"),
//            UNPROCESSED_HEAVY_PLATE = sequencedIngredient("unprocessed_heavy_plate");
//
//
//    public static final ItemEntry<QuadPotatoCannonItem> QUAD_POTATO_CANNON =
//            REGISTRATE.item("quad_potato_cannon", QuadPotatoCannonItem::new)
//                    .model(AssetLookup.itemModelWithPartials())
//                    .register();
//
//    public static final ItemEntry<PipeBombItem>
//            PIPE_BOMB = REGISTRATE.item("pipe_bomb", PipeBombItem::new)
//            .register();
//
//    public static final ItemEntry<FluidContainingItem>
//            OIL_CAN = REGISTRATE.item("oil_can", p -> new FluidContainingItem(p, TFMGFluids.LUBRICATION_OIL))
//            .properties(p -> p.stacksTo(1))
//            .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource("item/oil_can_model")))
//            .register(),
//            COOLING_FLUID_BOTTLE = REGISTRATE.item("cooling_fluid_bottle", p -> new FluidContainingItem(p, TFMGFluids.COOLING_FLUID))
//                    .properties(p -> p.stacksTo(1))
//                    .register();
//    public static final ItemEntry<ElectriciansWrenchItem>
//            CONFIGURATION_WRENCH = REGISTRATE.item("electricians_wrench", ElectriciansWrenchItem::new)
//            .lang("Configuration Wrench")
//            .register();

    public static final ItemEntry<ThermiteGrenadeItem>
            THERMITE_GRENADE = thermiteGrenade("thermite_grenade", BASE, TFMGEntityTypes.THERMITE_GRENADE::get, TFMGEntityTypes.SPARK::get);
    public static final ItemEntry<ThermiteGrenadeItem>
            ZINC_GRENADE = thermiteGrenade("zinc_grenade", GREEN, TFMGEntityTypes.ZINC_GRENADE::get, TFMGEntityTypes.GREEN_SPARK::get);
    public static final ItemEntry<ThermiteGrenadeItem>
            COPPER_GRENADE = thermiteGrenade("copper_grenade", BLUE, TFMGEntityTypes.COPPER_GRENADE::get, TFMGEntityTypes.BLUE_SPARK::get);

    /// /////////////////////////

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name, String model) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .model((c, p) -> p.withExistingParent(c.getName(), TFMG.asResource(model)))
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    private static ItemEntry<ArmorItem> armor(String name, Holder<ArmorMaterial> material, ArmorItem.Type slot) {
        return REGISTRATE.item(name, p -> new ArmorItem(material, slot, p)).register();
    }

    private static List<ItemEntry<?>> toolset(String material, Tier tier) {

        List<ItemEntry<?>> list = new ArrayList<>();

        list.add(REGISTRATE.item(material + "_sword", p -> new SwordItem(tier, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(tier, 3, -2.4F)))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_sword", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_sword"))
                .register());
        list.add(REGISTRATE.item(material + "_pickaxe", p -> new PickaxeItem(tier, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(tier, 1, -2.8F)))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_pickaxe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_pickaxe"))
                .register());
        list.add(REGISTRATE.item(material + "_axe", p -> new AxeItem(tier, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(tier, 6.0F, -3.2F)))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_axe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_axe"))
                .register());
        list.add(REGISTRATE.item(material + "_shovel", p -> new ShovelItem(tier, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(tier, 1.5F, -3.0F)))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_shovel", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_shovel"))
                .register());
        list.add(REGISTRATE.item(material + "_hoe", p -> new HoeItem(tier,  p))
                .properties(p -> p.attributes(SwordItem.createAttributes(tier, 0, -3.0F)))
                .model((ctx, prov) -> prov
                        .withExistingParent(material + "_hoe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/" + material + "_hoe"))
                .register());


        return list;
    }

    public static final ItemEntry<SteelVerticalGearboxItem> STEEL_VERTICAL_GEARBOX =
            REGISTRATE.item("steel_vertical_gearbox", SteelVerticalGearboxItem::new)
                    .model(AssetLookup.customBlockItemModel("steel_gearbox", "item_vertical"))
                    .lang("Steel Vertical Gearbox")
                    .register();


    private static List<ItemEntry<?>> leadToolset() {
        List<ItemEntry<?>> list = new ArrayList<>();
        list.add(REGISTRATE.item("lead_sword", p -> new LeadSwordItem(TFMGTiers.LEAD, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.LEAD, 3, -2.4f)))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_sword", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_sword"))
                .register());
        list.add(REGISTRATE.item("lead_pickaxe", p -> new PickaxeItem(TFMGTiers.LEAD, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.LEAD, 1, -2.8f)))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_pickaxe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_pickaxe"))
                .register());
        list.add(REGISTRATE.item("lead_axe", p -> new LeadAxeItem(TFMGTiers.LEAD, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.LEAD, 6, -3.2f)))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_axe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_axe"))
                .register());
        list.add(REGISTRATE.item("lead_shovel", p -> new ShovelItem(TFMGTiers.LEAD, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.LEAD, 1.5f, -3.0f)))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_shovel", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_shovel"))
                .register());
        list.add(REGISTRATE.item("lead_hoe", p -> new HoeItem(TFMGTiers.LEAD, p))
                .properties(p -> p.attributes(SwordItem.createAttributes(TFMGTiers.LEAD, 0, -3.0f)))
                .model((ctx, prov) -> prov
                        .withExistingParent("lead_hoe", "minecraft:item/handheld")
                        .texture("layer0", "tfmg:item/lead_hoe"))
                .register());


        return list;
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

//    public static Map<String, RegistryEntry<MultimeterItem>> multimeters() {
//        Map<String, RegistryEntry<MultimeterItem>> map = new HashMap<>();
//
//        for (String color : COLORS) {
//
//            map.put(color, REGISTRATE.item(color + "_multimeter", MultimeterItem::new)
//                    .register());
//        }
//
//        return map;
//    }

    public static ItemBuilder<SpoolItem, CreateRegistrate> spoolItem(String name, int barColor) {
        return REGISTRATE.item(name + "_spool", p -> new SpoolItem(p, barColor))
                .tag(TFMGTags.TFMGItemTags.SPOOLS.tag)
                .properties(p -> p.stacksTo(1));

    }


    private static ItemEntry<ThermiteGrenadeItem> thermiteGrenade(String name, ThermiteGrenade.ChemicalColor color, Supplier<EntityType<? extends ThermiteGrenade>> entityType, Supplier<EntityType<? extends SparkBase>> sparkEntityType) {
        return REGISTRATE.item(name, p -> new ThermiteGrenadeItem(p, color, entityType, sparkEntityType))
                .register();
    }
}
