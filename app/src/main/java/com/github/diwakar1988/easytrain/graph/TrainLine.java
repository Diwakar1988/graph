package com.github.diwakar1988.easytrain.graph;

import java.util.LinkedList;

/**
 * Created by diwakar.mishra on 05/10/16.
 */

public class TrainLine {
    private String name;
    private LinkedList<StationVertex> stations=new LinkedList<>();

    protected TrainLine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StationVertex addStation(String name){
        StationVertex vertex=new StationVertex(this,name);
        stations.add(vertex);
        return  vertex;
    }
    @Override
    public String toString() {
        return getName();
    }

    public LinkedList<StationVertex> getStations() {
        return stations;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==this){
            return true;
        }if (!(obj instanceof TrainLine)){
            return false;
        }

        return ((TrainLine)obj).getName().equals(this.getName());
    }
}
