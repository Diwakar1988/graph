package com.github.diwakar1988.easytrain;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.diwakar1988.easytrain.graph.Graph;
import com.github.diwakar1988.easytrain.graph.GraphController;
import com.github.diwakar1988.easytrain.graph.StationVertex;
import com.github.diwakar1988.easytrain.util.BackgroundExecutor;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private AutoCompleteTextView mSourceTextView;
    private AutoCompleteTextView mDestinationTextView;
    private ArrayAdapter mStationAdaptor;
    private TextView mResultsTextView;
    private ArrayList<String> mStations=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSourceTextView= (AutoCompleteTextView) findViewById(R.id.act_source);
        mDestinationTextView= (AutoCompleteTextView) findViewById(R.id.act_destination);
        mResultsTextView= (TextView) findViewById(R.id.tv_search_results);

        findViewById(R.id.btn_search).setOnClickListener(this);

        loadDefaultData();

    }

    private void loadDefaultData() {

        showProgress("Please wait...");
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {
                GraphController.initialize(MainActivity.this.getResources().openRawResource(R.raw.metro_stations));
                mStations = Graph.getInstance().getAllStationNames();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStationAdaptor= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, mStations);

                        mSourceTextView.setAdapter(mStationAdaptor);
                        mSourceTextView.setThreshold(1);

                        mDestinationTextView. setAdapter(mStationAdaptor);
                        mDestinationTextView.setThreshold(1);
                        hideProgress();
                    }
                });
            }
        });
    }

    private ProgressDialog progress;

    protected void showProgress(String message) {
        if (progress == null) {
            progress = ProgressDialog.show(this, "",
                    message, true);
        } else if (progress.isShowing()) {
            progress.setMessage(message);
        } else {
            progress.setMessage(message);
            progress.show();
        }

    }

    protected void hideProgress() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BackgroundExecutor.getInstance().stop();
    }

    @Override
    public void onClick(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        final StationVertex src = Graph.getInstance().getStationVertex(mSourceTextView.getText().toString());
        final StationVertex dest = Graph.getInstance().getStationVertex(mDestinationTextView.getText().toString());

        if (src==null){
            mSourceTextView.requestFocus();
            Toast.makeText(this, "Please type & select source station from list.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dest==null){
            mDestinationTextView.requestFocus();
            Toast.makeText(this, "Please type & select destination station from list.", Toast.LENGTH_SHORT).show();
            return;
        }

        //clear last result text
        mResultsTextView.setText("");

        showProgress("Searching...");
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {
                startPathSearch(src,dest);
            }
        });


    }

    private void startPathSearch(StationVertex src, StationVertex dest) {
        LinkedList<StationVertex> path = Graph.getInstance().calculateShortestPath(src, dest);
        ArrayList<StationVertex> lineChange=lookupLineChanges(src,path);

        final String result = getFormattedResult(src,path,lineChange);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                showSearchResult(result);
            }
        });
    }

    private String getFormattedResult(StationVertex src, LinkedList<StationVertex> path, ArrayList<StationVertex> lineChange) {
        StringBuilder sb = new StringBuilder();

        sb.append("Time it would take – ").append(Graph.EDGE_WEIGHT).append(" * ").append(path.size()).append(" = ").append((Graph.EDGE_WEIGHT*path.size())).append(" minutes.\n");

        int cost=Graph.COST*path.size();
        sb.append("\nCost – ").append(Graph.COST).append(" * ").append(path.size());
        if (lineChange.size()>0){
            cost+=lineChange.size();
            sb.append(" + ").append (lineChange.size()).append(" (line change)");
        }
        sb.append(" = ").append(cost);
        sb.append("$\n");

        if (path.size()>0){
            sb.append("\nPath\n");
            if (lineChange.size()==0){
                //user travelling on same line
                sb.append("Take ").append(path.getFirst().getTrainLines().getFirst().getName()).append(" at ").append(src.getName()).append(" to reach at ").append(path.getLast().getName()).append('.');
            }else{
                sb.append("Take ").append(path.getFirst().getTrainLines().getFirst().getName()).append(" at ").append(src.getName());
                for (int i = 0; i < lineChange.size(); i++) {
                    sb.append(" move towards ").append(lineChange.get(i).getName());
                    sb.append(", at ").append(lineChange.get(i).getName()).append(" change to ");
                    StationVertex next = path.get(path.indexOf(lineChange.get(i))+1);
                    sb.append(next.getTrainLines().getFirst().getName()).append(" and ");
                }
                sb.append("move towards ").append(path.getLast().getName()).append('.');
            }
        }else{
            sb.append("\nPath\n");
            sb.append(src.getName());
        }

        sb.append("\n\nDirection\n");

        sb.append(src.getName());
        for (int i = 0; i < path.size(); i++) {
            sb.append("-->");
            sb.append(path.get(i).getName());
        }
        return sb.toString();
    }

    private ArrayList<StationVertex> lookupLineChanges(StationVertex startVertex,LinkedList<StationVertex> path) {

        ArrayList<StationVertex> list=new ArrayList<>();
        StationVertex nextVertex=null;
        StationVertex preVertex=null;

        for (int i = 0; i < path.size(); i++) {
            nextVertex = path.get(i);
            if(!nextVertex.onSameLine(startVertex)){
                //if start and next vertex isn't on same line then consider it as a line change and store it
                preVertex=path.get(i-1);
                list.add(preVertex);
                startVertex=preVertex;
            }
        }
        return list;
    }

    private void showSearchResult(String result){
        mResultsTextView.setText(result);
    }


}
