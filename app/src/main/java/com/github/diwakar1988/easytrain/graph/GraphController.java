package com.github.diwakar1988.easytrain.graph;

import android.util.Log;

import com.github.diwakar1988.easytrain.util.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by diwakar.mishra on 06/10/16.
 */

public class GraphController {
    private static final String TAG = GraphController.class.getSimpleName();

    private static GraphController instance;

    public static synchronized void initialize(InputStream csvResource){

        loadStations(csvResource);
        createGraph();
//        Log.d(TAG, "***** Graph TOTAL STATIONS= "+Graph.getInstance().totalStations());
//        Log.d(TAG, (Graph.getInstance().toString()));


    }

    private GraphController() {

    }

    private static void createGraph() {
        Graph.getInstance().clear();
        ArrayList<TrainLine> lines = TrainLineList.getInstance().getTrainLines();


        for (int i = 0; i < lines.size(); i++) {
            TrainLine line = lines.get(i);
            LinkedList<StationVertex> stations = line.getStations();

            if (stations.size()>1){
                StationVertex v=stations.getFirst();
                v.addAdjacent(stations.get(1));
                addVertexInGraph(v);

                for (int j=1;j<(stations.size()-1);j++){
                    v = stations.get(j);

                    v.addAdjacent(stations.get(j-1));
                    v.addAdjacent(stations.get(j+1));
                    addVertexInGraph(v);
                }

                v=stations.getLast();
                v.addAdjacent(stations.get(stations.size()-2));
                addVertexInGraph(v);

            }

        }
    }

    private static void addVertexInGraph(StationVertex newVertex) {
        Graph graph = Graph.getInstance();
        StationVertex oldVertex=graph.getStationVertex(newVertex.getName());

        if (oldVertex==null){
            //station not exist in graph
            graph.addStationVertex(newVertex);
        }else{

            LinkedList<StationVertex> stations = newVertex.getAdjacentStations();
            for (int i = 0; i < stations.size(); i++) {
                oldVertex.addAdjacent(stations.get(i));

            }
            //add lines
            LinkedList<TrainLine> lines = newVertex.getTrainLines();
            for (int i = 0; i < lines.size(); i++) {
                oldVertex.addTrainLine(lines.get(i));
            }
        }
    }

    private static  void loadStations(InputStream csvResource) {
        TrainLineList.getInstance().clear();

        CSVReader reader = new CSVReader(new InputStreamReader(csvResource));

        String[] values;
        try {
            while ((values = reader.readNext()) != null) {

                TrainLine trainLine = TrainLineList.getInstance().addTrainLine(values[0].trim());
                for (int i = 1; i < values.length; i++) {
                    trainLine.addStation(values[i].trim());
                }
//                Log.d(TAG, (trainLine.getStations().toString()));
            }
//            Log.d(TAG, "***** TrainLineList TOTAL STATIONS= "+TrainLineList.getInstance().totalStations());
        } catch (IOException e) {
            Log.d(TAG, "***** ERROR= "+e);
        }
    }

}
