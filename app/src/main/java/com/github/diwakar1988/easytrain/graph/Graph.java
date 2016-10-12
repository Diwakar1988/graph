package com.github.diwakar1988.easytrain.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * Created by diwakar.mishra on 05/10/16.
 */

public class Graph {

    public static final int EDGE_WEIGHT=5;
    public static final int COST=1;

    private HashMap<String,StationVertex> stations=new HashMap<>();

    private static Graph instance;

    public static Graph getInstance(){
        if (instance==null){
            instance=new Graph();
        }
        return instance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }

    private Graph() {

    }
    public void clear(){
        stations.clear();
    }
    public StationVertex getStationVertex(String name){
        return stations.get(name);
    }
    public boolean containsStationVertex(String name){
        return stations.containsKey(name);
    }
    public void addStationVertex(StationVertex vertex){
        stations.put(vertex.getName(),vertex);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Iterator<StationVertex> iterator = stations.values().iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next().toString()).append('\n');
        }
        return sb.toString();
    }

    public int totalStations() {
        return stations.size();
    }
    public ArrayList<String> getAllStationNames(){
        ArrayList<String>keys=new ArrayList<>();
        keys.addAll(stations.keySet());
        return keys;
    }


    /***
     * Uses Dijkstra's Algorithm

     */
    public LinkedList<StationVertex> calculateShortestPath(StationVertex srcVertex, StationVertex destVertex){

        //heap + map data structure
        BinaryMinHeap<StationVertex> minHeap = new BinaryMinHeap<>();

        //stores shortest distance from root to every vertex
        Map<StationVertex,Integer> distance = new HashMap<>();

        //stores parent of every vertex in shortest distance
        Map<StationVertex, StationVertex> parent = new HashMap<>();

        //initialize all vertex with infinite distance from source vertex
        Collection<StationVertex> allVertices = stations.values();
        for(StationVertex vertex: allVertices){
            minHeap.add(Integer.MAX_VALUE, vertex);
        }

        //set distance of source vertex to 0
        minHeap.decrease(srcVertex, 0);

        //put it in map
        distance.put(srcVertex, 0);

        //source vertex parent is null
        parent.put(srcVertex, null);

        //iterate till heap is not empty
        while(!minHeap.empty()){
            //get the min value from heap node which has vertex and distance of that vertex from source vertex.
            BinaryMinHeap<StationVertex>.Node heapNode = minHeap.extractMinNode();
            StationVertex current = heapNode.key;

            //update shortest distance of current vertex from source vertex
            distance.put(current, heapNode.weight);

//            //terminate loop if destination vertex found from source
//            if (current.equals(destVertex)){
//                break;
//            }

            //iterate through all adjacent vertices of current vertex

            for(StationVertex adjacent : current.getAdjacentStations()){

                //if heap does not contain adjacent vertex means adjacent vertex already has shortest distance from source vertex
                if(!minHeap.containsData(adjacent)){
                    continue;
                }

                //add distance of current vertex to edge weight to get distance of adjacent vertex from source vertex
                //when it goes through current vertex
                int newDistance = distance.get(current) + EDGE_WEIGHT;

                //see if this above calculated distance is less than current distance stored for adjacent vertex from source vertex
                if(minHeap.getWeight(adjacent) > newDistance) {
                    minHeap.decrease(adjacent, newDistance);
                    parent.put(adjacent, current);
                }
            }
        }

        //create path from parent list
        LinkedList<StationVertex> path=new LinkedList<>();
        StationVertex adjVertex=destVertex;
        while (adjVertex!=null && !adjVertex.equals(srcVertex)){
            path.add(adjVertex);
            adjVertex = parent.get(adjVertex);
        }
        Collections.reverse(path);


        return path;
    }

}
