package com.drmangotea.tfmg.content.engines.base;

import com.drmangotea.tfmg.content.decoration.pipes.TFMGPipes;
import com.drmangotea.tfmg.content.engines.regular_engine.PistonPosition;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllItems;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class EngineProperties {
    public static List<PistonPosition> pistonsI(){
        List<PistonPosition> pistonPositions = new ArrayList<>();

        pistonPositions.add(new PistonPosition(0,0.2f,0,0));
        pistonPositions.add(new PistonPosition(0.5f,0.2f,0,0));

        return pistonPositions;
    }
    public static List<PistonPosition> pistonsV(){
        List<PistonPosition> pistonPositions = new ArrayList<>();
        pistonPositions.add(new PistonPosition(0,0.1f,-0.35f,45));
        pistonPositions.add(new PistonPosition(0.5f,0.1f,-0.35f,45));
        pistonPositions.add(new PistonPosition(0,0.1f,0.35f,-45));
        pistonPositions.add(new PistonPosition(0.5f,0.1f,0.35f,-45));
        return pistonPositions;
    }
    public static List<PistonPosition> pistonsW(){
        List<PistonPosition> pistonPositions = new ArrayList<>();
        pistonPositions.add(new PistonPosition(0,0.15f,-0.25f,45));
        pistonPositions.add(new PistonPosition(0.5f,0.15f,-0.25f,45));
        pistonPositions.add(new PistonPosition(0,0.15f,0.25f,-45));
        pistonPositions.add(new PistonPosition(0.5f,0.15f,0.25f,-45));

        pistonPositions.add(new PistonPosition(0,-0.1f,-0.35f,77.5f));
        pistonPositions.add(new PistonPosition(0.5f,-0.1f,-0.35f,77.5f));
        pistonPositions.add(new PistonPosition(0,-0.1f,0.35f,-77.5f));
        pistonPositions.add(new PistonPosition(0.5f,-0.1f,0.35f,-77.5f));
        return pistonPositions;
    }
    public static List<PistonPosition> pistonsU(){
        List<PistonPosition> pistonPositions = new ArrayList<>();
        pistonPositions.add(new PistonPosition(0,0.2f,-0.25f,0));
        pistonPositions.add(new PistonPosition(0.5f,0.2f,-0.25f,0));
        pistonPositions.add(new PistonPosition(0,0.2f,0.25f,0));
        pistonPositions.add(new PistonPosition(0.5f,0.2f,0.25f,0));
        return pistonPositions;
    }
    public static List<PistonPosition> pistonsBoxer(){
        List<PistonPosition> pistonPositions = new ArrayList<>();
        pistonPositions.add(new PistonPosition(0,0.05f,-0.3f,90));
        pistonPositions.add(new PistonPosition(0.5f,0.05f,-0.3f,90));
        pistonPositions.add(new PistonPosition(0,0.05f,0.3f,-90));
        pistonPositions.add(new PistonPosition(0.5f,0.05f,0.3f,-90));
        return pistonPositions;
    }
    //

    //public static List<ItemStack> componentsW(){
    //    List<ItemStack> components = commonRegularComponents();
    //    components.add(1,TFMGItems.CRANKSHAFT.asStack());
//
    //    return components;
    //}
    //public static List<ItemStack> componentsU(){
    //    List<ItemStack> components = new ArrayList<>();
//
    //    return components;
    //}
    public static List<Ingredient> commonRegularComponents(){
        List<Ingredient> components = new ArrayList<>();

        components.add(Ingredient.of(TFMGItems.CRANKSHAFT.asStack()));
        components.add(Ingredient.of(TFMGBlocks.STEEL_COGWHEEL.asStack()));
        components.add(Ingredient.of(TFMGBlocks.LARGE_STEEL_COGWHEEL.asStack()));
        components.add(Ingredient.of(AllItems.BELT_CONNECTOR.asStack()));
        components.add(Ingredient.of(TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(0).asStack()));
        components.add(Ingredient.of(TFMGPipes.TFMG_PIPES.get(TFMGPipes.PipeMaterial.STEEL).get(0).asStack()));
        components.add(Ingredient.of(TFMGItems.STEEL_MECHANISM.asStack()));

        return components;
    }


}
