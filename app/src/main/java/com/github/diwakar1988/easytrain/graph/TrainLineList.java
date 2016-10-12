package com.github.diwakar1988.easytrain.graph;

import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 05/10/16.
 */

public class TrainLineList {
    private ArrayList<TrainLine> trainLines =new ArrayList<>();
    private static TrainLineList instance;
    public static TrainLineList getInstance(){
        if (instance==null){
            instance=new TrainLineList();
        }
        return instance;
    }

    private TrainLineList() {
    }
    public TrainLine addTrainLine(String name){
        TrainLine trainLine =new TrainLine(name);
        trainLines.add(trainLine);
        return trainLine;
    }

    public ArrayList<TrainLine> getTrainLines() {
        return trainLines;
    }
    public int totalStations(){
        int count=0;
        for (int i = 0; i < trainLines.size(); i++) {
            count+=trainLines.get(i).getStations().size();
        }
        return count;
    }
    public void clear(){
        trainLines.clear();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }
}
