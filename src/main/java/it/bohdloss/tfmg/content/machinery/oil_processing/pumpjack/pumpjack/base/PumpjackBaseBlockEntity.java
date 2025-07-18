package it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.base;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.bohdloss.tfmg.TFMGUtils;
import it.bohdloss.tfmg.base.TFMGFluidBehavior;
import it.bohdloss.tfmg.config.TFMGConfigs;
import it.bohdloss.tfmg.content.machinery.oil_processing.IDeposit;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.crank.PumpjackCrankBlockEntity;
import it.bohdloss.tfmg.content.machinery.oil_processing.pumpjack.pumpjack.hammer.PumpjackBlockEntity;
import it.bohdloss.tfmg.registry.TFMGBlockEntities;
import it.bohdloss.tfmg.registry.TFMGTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

@EventBusSubscriber
public class PumpjackBaseBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public BlockPos pumpjackPosition;
    public TFMGFluidBehavior tank;
    protected BlockPos deposit;
    protected int depositCheckTimer = 0;

    public PumpjackBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new TFMGFluidBehavior(TFMGFluidBehavior.TYPE, "Fluid", this, 8000)
                .allowExtraction(true)
                .allowInsertion(false)
                .syncCapacity(false)
                .withCallback(this::notifyUpdate);

        behaviours.add(tank);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("goggles.pumpjack_info")
                .forGoggles(tooltip);
        if (deposit == null) {
            CreateLang.translate("goggles.zero")
                    .style(ChatFormatting.DARK_RED)
                    .forGoggles(tooltip, 1);
        }

        TFMGUtils.createFluidTooltip(this, tooltip, Direction.DOWN);
        return true;
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TFMGBlockEntities.PUMPJACK_BASE.get(),
                (be, ctx) -> be.tank.getCapability()
        );
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) {
            return;
        }

        if(pumpjackPosition != null && level.getBlockEntity(pumpjackPosition) instanceof PumpjackBlockEntity pumpjack
            && pumpjack.crankPosition != null && level.getBlockEntity(pumpjack.crankPosition) instanceof PumpjackCrankBlockEntity crank
        ) {
            depositCheckTimer++;
            if (depositCheckTimer > 50) {
                depositCheckTimer = 0;
                findDeposit();
            }

            // We don't want the height modifier we want the derivative aka "upwards" angular speed
            float heightModifier = (float) (pumpjack.crankRadius * Math.cos(Math.toRadians(crank.angle)));
            // Same max speed calculation as PumpjackCrankBlockEntity but without the division by 6
            // TODO Maybe remove upwards limit (60) if unbalanced
            float speed_amogus = Math.min(Math.abs(crank.getMachineInputSpeed()), (float) 60);
            int miningRate = (int) Math.max(0, -speed_amogus * (heightModifier));
            process(miningRate);
        }
    }

    public int maxDepositDepth() {
        return TFMGConfigs.common().machines.pumpjackMaxSearchDepth.get();
    }

    protected void findDeposit() {
        BlockPos.MutableBlockPos checkedPos = getBlockPos().mutable();
        for(int i = 0; i < maxDepositDepth(); i++) {
            checkedPos.setY(checkedPos.getY() - 1);
            if (level.getBlockEntity(checkedPos) instanceof IDeposit) {
                deposit = checkedPos;
                return;
            }
            if (!(level.getBlockState(checkedPos).is(TFMGTags.TFMGBlockTags.INDUSTRIAL_PIPE.tag))) {
                deposit = null;
                return;
            }
        }
        deposit = null;
    }

    protected void process(int miningRate) {
        if (deposit == null || !(level.getBlockEntity(deposit) instanceof IDeposit depositBe)) {
            return;
        }

        var handler = tank.getHandler();
        int mineAmount = Math.min(miningRate, handler.getSpace());
        FluidStack fluidSimulated = depositBe.getReservoir().drain(mineAmount, IFluidHandler.FluidAction.SIMULATE);

        if(handler.getFluid().isEmpty() || handler.getFluid().getFluid().isSame(fluidSimulated.getFluid())) {
            FluidStack fluidPumped = depositBe.getReservoir().drain(mineAmount, IFluidHandler.FluidAction.EXECUTE);
            handler.fill(fluidPumped, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if(!clientPacket) {
            tag.putInt("DepositCheckTimer", depositCheckTimer);
        }
        if(deposit != null) {
            tag.put("Deposit", NbtUtils.writeBlockPos(deposit));
        }

        if(pumpjackPosition != null) {
            tag.put("Pumpjack", NbtUtils.writeBlockPos(pumpjackPosition));
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if(!clientPacket) {
            depositCheckTimer = tag.getInt("DepositCheckTimer");
        }
        deposit = null;
        if(tag.contains("Deposit")) {
            deposit = NbtUtils.readBlockPos(tag, "Deposit").get();
        }

        pumpjackPosition = null;
        if(tag.contains("Pumpjack")) {
            pumpjackPosition = NbtUtils.readBlockPos(tag, "Pumpjack").get();
        }
    }
}
