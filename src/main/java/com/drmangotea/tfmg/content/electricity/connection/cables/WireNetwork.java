package com.drmangotea.tfmg.content.electricity.connection.cables;

import com.drmangotea.tfmg.content.electricity.base.IElectric;
import org.apache.http.impl.conn.Wire;

import java.util.ArrayList;
import java.util.List;

public class WireNetwork{

    public long id;

    public List<IHaveCables> connectors = new ArrayList<>();

    public int voltage;

    public float resistance;


    public WireNetwork(long id){
        this.id = id;
    }

    public void add(IHaveCables be) {
        List<Long> posList = new ArrayList<>();

        connectors.forEach(member -> posList.add(member.getId()));

        if (posList.contains(be.getId()))
            return;
        connectors.add(be);

    }

    public void updateCables(){


        for(IHaveCables cable : connectors){
            if(cable instanceof IElectric be){

            }
        }
    }
}
