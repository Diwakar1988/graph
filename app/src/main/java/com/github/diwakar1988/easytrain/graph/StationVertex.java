package com.github.diwakar1988.easytrain.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by diwakar.mishra on 05/10/16.
 */

public class StationVertex {
    private LinkedList<TrainLine> trainLines =new LinkedList<>();
    private String name;
    private LinkedList<StationVertex> adjacentStations=new LinkedList<>();

    protected StationVertex(TrainLine trainLine, String name){
        this.trainLines.add(trainLine);
        this.name=name;
    }
    public void addAdjacent(StationVertex station){
        if (!adjacentStations.contains(station)){
            adjacentStations.add(station);
        }
    }

    public LinkedList<TrainLine> getTrainLines() {
        return trainLines;
    }
    public void addTrainLine(TrainLine line){
        if (trainLines.contains(line)){
            return;
        }
        trainLines.add(line);
    }

    public String getName() {
        return name;
    }

    public LinkedList<StationVertex> getAdjacentStations() {
        return adjacentStations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==this){
            return true;
        }
        if (!(obj instanceof StationVertex)){
            return false;
        }
        return this.getName().equals(((StationVertex)obj).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        for (int i = 0; i < adjacentStations.size(); i++) {
            sb.append("-->");
            sb.append(adjacentStations.get(i).getName());
        }
        return sb.toString();
    }

    public boolean onSameLine(StationVertex vertex) {
        LinkedList<TrainLine> trainLines1 = vertex.getTrainLines();
        LinkedList<TrainLine> trainLines2 = this.getTrainLines();

        for (int i = 0; i < trainLines1.size(); i++) {
            if (trainLines2.contains(trainLines1.get(i))){
                return true;
            }
        }
        return false;
    }
}
