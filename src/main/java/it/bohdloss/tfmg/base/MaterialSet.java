package it.bohdloss.tfmg.base;

import com.tterrag.registrate.util.entry.BlockEntry;

public record MaterialSet(BlockEntry<?> block, BlockEntry<?> slab, BlockEntry<?> stairs, BlockEntry<?> wall) {}