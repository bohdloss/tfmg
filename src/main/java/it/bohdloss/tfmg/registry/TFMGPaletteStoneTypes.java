package it.bohdloss.tfmg.registry;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.CreateLang;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.bohdloss.tfmg.TFMG;
import it.bohdloss.tfmg.base.palettes.TFMGPaletteBlockPattern;
import it.bohdloss.tfmg.base.palettes.TFMGPalettesVariantEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

import static it.bohdloss.tfmg.base.palettes.TFMGPaletteBlockPattern.STANDARD_RANGE;

public enum TFMGPaletteStoneTypes {
    BAUXITE(STANDARD_RANGE, r -> r.paletteStoneBlock("bauxite", () -> Blocks.DEEPSLATE, true, true)
            .properties(p -> p.destroyTime(1.25f))
            .register()),


    GALENA(STANDARD_RANGE, r -> r.paletteStoneBlock("galena", () -> Blocks.CALCITE, true, true)
            .properties(p -> p.destroyTime(1.25f))
            .register()),

    ;

    private Function<CreateRegistrate, NonNullSupplier<Block>> factory;
    private TFMGPalettesVariantEntry variants;

    public NonNullSupplier<Block> baseBlock;
    public TFMGPaletteBlockPattern[] variantTypes;
    public TagKey<Item> materialTag;

    private TFMGPaletteStoneTypes(TFMGPaletteBlockPattern[] variantTypes,
                                  Function<CreateRegistrate, NonNullSupplier<Block>> factory) {
        this.factory = factory;
        this.variantTypes = variantTypes;
    }

    public NonNullSupplier<Block> getBaseBlock() {
        return baseBlock;
    }

    public TFMGPalettesVariantEntry getVariants() {
        return variants;
    }

    public static void register(CreateRegistrate registrate) {
        for (TFMGPaletteStoneTypes paletteStoneVariants : values()) {
            NonNullSupplier<Block> baseBlock = paletteStoneVariants.factory.apply(registrate);
            paletteStoneVariants.baseBlock = baseBlock;
            String id = CreateLang.asId(paletteStoneVariants.name());
            paletteStoneVariants.materialTag =
                    AllTags.optionalTag(BuiltInRegistries.ITEM, TFMG.asResource("stone_types/" + id));
            paletteStoneVariants.variants = new TFMGPalettesVariantEntry(id, paletteStoneVariants);
        }
    }
}
