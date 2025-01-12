package com.drmangotea.tfmg.base;


import com.drmangotea.tfmg.base.spark.ElectricSparkParticle;
import com.drmangotea.tfmg.base.spark.Spark;
import com.drmangotea.tfmg.registry.TFMGEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TFMGUtils {


    public static void createFireExplosion(Level level, Entity entity, BlockPos pos, int sparkAmount, float radius) {

        if (level.isClientSide && entity != null) level.broadcastEntityEvent(entity, (byte) 3);

        for (int i = 0; i < sparkAmount; i++) {
            float x = Create.RANDOM.nextFloat(360);
            float y = Create.RANDOM.nextFloat(360);
            float z = Create.RANDOM.nextFloat(360);
            Spark spark = TFMGEntityTypes.SPARK.create(level);
            spark.moveTo(pos.getX(), pos.getY() + 1, pos.getZ());

            float f = -Mth.sin(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
            float f1 = -Mth.sin((x + z) * ((float) Math.PI / 180F));
            float f2 = Mth.cos(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
            spark.shoot(f, f1, f2, 0.3f, 1);
            level.addFreshEntity(spark);
        }
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, Level.ExplosionInteraction.BLOCK);
    }
    public static void blowUpTank(FluidTankBlockEntity tank, int power) {

        if (tank == null || tank.getControllerBE() == null) return;
        FluidTankBlockEntity be = tank.getControllerBE();

        for (int xOffset = 0; xOffset < be.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < be.getWidth(); zOffset++) {
                for (int yOffset = 0; yOffset < be.getHeight(); yOffset++) {

                    BlockPos pos = be.getBlockPos().offset(xOffset, yOffset, zOffset);

                    be.getLevel().destroyBlock(pos, false);
                }
            }
        }

        createFireExplosion(be.getLevel(), null, new BlockPos(be.getBlockPos().getX() + (be.getWidth() / 2), be.getBlockPos().getY() + (be.getHeight() / 2), be.getBlockPos().getZ() + (be.getWidth() / 2)), power * 15, (float) power);
    }

    public static String fromId(String key) {
        String s = key.replaceAll("_", " ");
        s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        s = StringUtils.normalizeSpace(s);
        return s;
    }

    public static String toHumanReadable(String key) {
        String s = key.replaceAll("_", " ");
        s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        s = StringUtils.normalizeSpace(s);
        return s;
    }

    public static void spawnElectricParticles(Level level, BlockPos pos) {
        if (level == null) return;


        RandomSource r = level.getRandom();


        for (int i = 0; i < r.nextInt(40); i++) {
            float x = Create.RANDOM.nextFloat(2) - 1;
            float y = Create.RANDOM.nextFloat(2) - 1;
            float z = Create.RANDOM.nextFloat(2) - 1;

            level.addParticle(new ElectricSparkParticle.Data(), pos.getX() + 0.5f + x, pos.getY() + 0.5f + y, pos.getZ() + 0.5f + z, x, y, z);


        }
    }
    public static float getDistance(BlockPos pos1, BlockPos pos2, boolean _2D) {


        float x = Math.abs(pos1.getX() - pos2.getX());
        float y = Math.abs(pos1.getY() - pos2.getY());
        float z = Math.abs(pos1.getZ() - pos2.getZ());


        float distance2D = (float) Math.sqrt(x * x + z * z);

        if (_2D) return distance2D;


        return (float) Math.sqrt(distance2D * distance2D + y * y);
    }

    public static void createStorageTooltip(BlockEntity be, List<Component> tooltip) {
        createFluidTooltip(be, tooltip);
        createItemTooltip(be, tooltip);
    }
    public static boolean createFluidTooltip(BlockEntity be, List<Component> tooltip) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");

        /////////
        LazyOptional<IFluidHandler> handler = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
        Optional<IFluidHandler> resolve = handler.resolve();
        if (!resolve.isPresent()) return false;

        IFluidHandler tank = resolve.get();
        if (tank.getTanks() == 0) return false;

        Lang.translate("goggles.fluid_storage").style(ChatFormatting.GRAY).forGoggles(tooltip);


        boolean isEmpty = true;
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack fluidStack = tank.getFluidInTank(i);
            if (fluidStack.isEmpty()) continue;
            Lang.fluidName(fluidStack).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            Lang.builder().add(Lang.number(fluidStack.getAmount()).add(mb).style(ChatFormatting.DARK_GREEN)).text(ChatFormatting.GRAY, " / ").add(Lang.number(tank.getTankCapacity(i)).add(mb).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
            isEmpty = false;
        }
        if (tank.getTanks() > 1) {
            if (isEmpty) tooltip.remove(tooltip.size() - 1);
            return true;
        }
        if (!isEmpty) return true;

        Lang.translate("gui.goggles.fluid_container.capacity").add(Lang.number(tank.getTankCapacity(0)).add(mb).style(ChatFormatting.DARK_GREEN)).style(ChatFormatting.DARK_GRAY).forGoggles(tooltip, 1);
        return true;
    }


    public static boolean createItemTooltip(BlockEntity be, List<Component> tooltip) {

        @NotNull LazyOptional<IItemHandler> handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER);
        Optional<IItemHandler> resolve = handler.resolve();
        if (!resolve.isPresent()) return false;
        IItemHandlerModifiable inventory = (IItemHandlerModifiable) resolve.get();
        if (inventory.getSlots() == 0) return false;
        Lang.translate("goggles.item_storage").style(ChatFormatting.GRAY).forGoggles(tooltip);
        boolean isEmpty = true;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);

            if (itemStack.isEmpty()) continue;
            Lang.itemName(itemStack).style(ChatFormatting.DARK_GREEN).add(Component.literal(" x " + itemStack.getCount())).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip, 1);
            isEmpty = false;
        }
        if (inventory.getSlots() > 1) {
            if (isEmpty) tooltip.remove(tooltip.size() - 1);
            return true;
        }
        if (!isEmpty) return true;

        Lang.translate("gui.goggles.item_storage_empty").style(ChatFormatting.DARK_GRAY).forGoggles(tooltip, 1);
        return true;
    }

    public static String formatUnits(double n, String unit) {
        if(n == 0)
            return Math.round(n) + unit;
        double var10000;
        if (n >= 1000000000) {
            var10000 = (double) Math.round((double) n / 1.0E8);
            return var10000 / 10.0 + "G" + unit;
        } else if (n >= 1000000) {
            var10000 = (double) Math.round((double) n / 100000.0);
            return var10000 / 10.0 + "M" + unit;
        } else if (n >= 1000) {
            var10000 = (double) Math.round((double) n / 100.0);
            return var10000 / 10.0 + "k" + unit;
        }
       // else if (n < 0.001) {
       //     var10000 = (double) Math.round((double) n * 10000000.0);
       //     return var10000 / 10.0 + "μ" + unit;
       // }
        else if (n < 1) {
            var10000 = (double) Math.round((double) n * 10000.0);
            return var10000 / 10.0 + "m" + unit;
        }
        else {
            return Math.round(n) + unit;
        }
    }
    public static void drainFilteredTank(SmartFluidTank tank, int amount){
        tank.setFluid(new FluidStack(tank.getFluid(),Math.max(tank.getFluidAmount()-amount,0)));
    }
    public static void fillFilteredTank(SmartFluidTank tank, FluidStack resource){
        if(tank.getFluid().getFluid().isSame(resource.getFluid())||tank.isEmpty())
            tank.setFluid(new FluidStack(resource.getFluid(),Math.min(tank.getFluidAmount()+resource.getAmount(),tank.getCapacity())));
    }
    public static Iterable<BlockPos> AABBtoBlockPos(AABB aabb) {
        return BlockPos.betweenClosed(new BlockPos((int) aabb.minX, (int) aabb.minY, (int) aabb.minZ), new BlockPos((int) aabb.maxX, (int) aabb.maxY, (int) aabb.maxZ));
    }
    public static SmartFluidTank createTank(int capacity, boolean extractionAllowed, Consumer<FluidStack> updateCallback) {
        return createTank(capacity, extractionAllowed, true, updateCallback, null);
    }
    public static SmartFluidTank createTank(int capacity, boolean extractionAllowed, boolean insertionAllowed, Consumer<FluidStack> updateCallback) {
        return createTank(capacity, extractionAllowed, insertionAllowed, updateCallback, null);
    }
    public static SmartFluidTank createTank(int capacity, boolean extractionAllowed, boolean insertionAllowed, Consumer<FluidStack> updateCallback, Fluid validFluid) {
        return new SmartFluidTank(capacity, updateCallback) {
            @Override
            public boolean isFluidValid(FluidStack stack) {

                if (validFluid == null) return true;

                return stack.getFluid().isSame(validFluid);
            }
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (!extractionAllowed) return FluidStack.EMPTY;
                return super.drain(resource, action);
            }
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (!extractionAllowed) return FluidStack.EMPTY;
                return super.drain(maxDrain, action);
            }
            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (!insertionAllowed) return 0;
                return super.fill(resource, action);
            }
        };
    }
}
