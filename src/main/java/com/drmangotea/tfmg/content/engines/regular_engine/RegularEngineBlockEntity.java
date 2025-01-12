package com.drmangotea.tfmg.content.engines.regular_engine;

import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.content.engines.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.EngineBlock;
import com.jozufozu.flywheel.backend.instancing.Engine;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

import static com.drmangotea.tfmg.content.engines.EngineProperties.*;

public class RegularEngineBlockEntity extends AbstractEngineBlockEntity {


    public EngineType type = EngineType.I;

    public RegularEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);


    }

    @Override
    public int effectiveSpeed() {
        return type.effectiveSpeed;
    }

    @Override
    public float efficiencyModifier() {
        return type.effeciencyModifier;
    }

    @Override
    public float speedModifier() {
        return type.speedModifier;
    }

    @Override
    public float torqueModifier() {
        return type.torqueModifier;
    }

    @Override
    public String engineId() {
        return type.name;
    }
    enum EngineType{
        I("engine_i",pistonsI(),12,1,1,1),
        V("engine_v",pistonsV(),14,1.4f,1.3f,0.8f),
        W("engine_w",pistonsW(),16,1.8f,1.3f,0.5f),
        U("engine_u",pistonsU(),12,1,1.5f,0.9f),
        BOXER("engine_boxer",pistonsBoxer(),11,1,0.8f,1.2f)
        ;
        public final int effectiveSpeed;
        public final float speedModifier;
        public final float torqueModifier;
        public final float effeciencyModifier;
        public final List<PistonPosition> positions;
        public final List<Fluid> fluidBlacklist;
        public final String name;

        EngineType(String name, List<PistonPosition> positions, int effectiveSpeed, float speedModifier,
                   float torqueModifier, float efficiencyModifier){
             this(name,positions, effectiveSpeed, speedModifier, torqueModifier, efficiencyModifier, new ArrayList<>());
        }

        EngineType(String name, List<PistonPosition> positions, int effectiveSpeed, float speedModifier,
                   float torqueModifier, float efficiencyModifier , List<Fluid> fluidBlacklist){
            this.name = name;
            this.positions = positions;
            this.effectiveSpeed = effectiveSpeed;
            this.speedModifier = speedModifier;
            this.torqueModifier = torqueModifier;
            this.effeciencyModifier = efficiencyModifier;
            this.fluidBlacklist = fluidBlacklist;

        }
        public static List<PistonPosition> pistonsI(){
            List<PistonPosition> pistonPositions = new ArrayList<>();

            return pistonPositions;
        }

    }

}
