package it.bohdloss.tfmg.base.palettes;


import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.bohdloss.tfmg.registry.TFMGCreativeTabs;
import it.bohdloss.tfmg.registry.TFMGPaletteStoneTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static it.bohdloss.tfmg.TFMG.REGISTRATE;

@SuppressWarnings("'onRegister(com.tterrag.registrate.util.nullness.NonNullConsumer<? super capture<? extends net.minecraft.world.level.block.Block>>)' in 'com.tterrag.registrate.builders.Builder' cannot be applied to '(com.tterrag.registrate.util.nullness.NonNullConsumer<capture<? super capture<? extends net.minecraft.world.level.block.Block>>>)'")
public class TFMGPalettesVariantEntry {

    public final ImmutableList<BlockEntry<? extends Block>> registeredBlocks;
    public final ImmutableList<BlockEntry<? extends Block>> registeredPartials;

    static {
        REGISTRATE.setCreativeTab(TFMGCreativeTabs.TFMG_DECORATION);
    }

    @SuppressWarnings("'onRegister(com.tterrag.registrate.util.nullness.NonNullConsumer<? super capture<? extends net.minecraft.world.level.block.Block>>)' in 'com.tterrag.registrate.builders.Builder' cannot be applied to '(com.tterrag.registrate.util.nullness.NonNullConsumer<capture<? super capture<? extends net.minecraft.world.level.block.Block>>>)'")
    public TFMGPalettesVariantEntry(String name, TFMGPaletteStoneTypes paletteStoneVariants) {
        ImmutableList.Builder<BlockEntry<? extends Block>> registeredBlocks = ImmutableList.builder();
        ImmutableList.Builder<BlockEntry<? extends Block>> registeredPartials = ImmutableList.builder();
        NonNullSupplier<Block> baseBlock = paletteStoneVariants.baseBlock;

        for (TFMGPaletteBlockPattern pattern : paletteStoneVariants.variantTypes) {
            BlockBuilder<? extends Block, CreateRegistrate> builder =
                    REGISTRATE.block(pattern.createName(name), pattern.getBlockFactory())
                            .initialProperties(baseBlock)
                            .transform(pickaxeOnly())
                            .blockstate(pattern.getBlockStateGenerator()
                                    .apply(pattern)
                                    .apply(name)::accept);

            ItemBuilder<BlockItem, ? extends BlockBuilder<? extends Block, CreateRegistrate>> itemBuilder =
                    builder.item();

            TagKey<Block>[] blockTags = pattern.getBlockTags();
            if (blockTags != null)
                builder.tag(blockTags);
            TagKey<Item>[] itemTags = pattern.getItemTags();
            if (itemTags != null)
                itemBuilder.tag(itemTags);

            itemBuilder.tag(paletteStoneVariants.materialTag);

            if (pattern.isTranslucent())
                builder.addLayer(() -> RenderType::translucent);
            pattern.createCTBehaviour(name)
                    .ifPresent(b -> builder.onRegister(connectedTextures(b)));

            builder.recipe((c, p) -> {
                p.stonecutting(DataIngredient.tag(paletteStoneVariants.materialTag), RecipeCategory.BUILDING_BLOCKS, c);
                pattern.addRecipes(baseBlock, c, p);
            });

            itemBuilder.register();
            BlockEntry<? extends Block> block = builder.register();
            registeredBlocks.add(block);

            for (TFMGPaletteBlockPartial<? extends Block> partialBlock : pattern.getPartials())
                registeredPartials.add(partialBlock.create(name, pattern, block, paletteStoneVariants)
                        .register());
        }

        REGISTRATE.addDataGenerator(ProviderType.RECIPE,
         p -> p.stonecutting(DataIngredient.tag(paletteStoneVariants.materialTag), RecipeCategory.BUILDING_BLOCKS,
                 baseBlock));
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, p -> p.addTag(paletteStoneVariants.materialTag)
                .add(baseBlock.get()
                        .asItem()));

        this.registeredBlocks = registeredBlocks.build();
        this.registeredPartials = registeredPartials.build();
    }

}
