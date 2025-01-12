package com.drmangotea.tfmg.content.engines.regular_engine;

public class PistonPosition {

    private final float xOffset;
    private final float yOffset;
    private final float rotation;

    public PistonPosition(float x, float y, float rotation){
        this.xOffset = x;
        this.yOffset = y;
        this.rotation = rotation;
    }
    public float getXOffset(){
        return xOffset;
    }
    public float getYOffset(){
        return yOffset;
    }
    public float getRotation(){
        return rotation;
    }
}
