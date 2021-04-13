package com.example.finalprojectmoore.ui.graphsdisplay;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SpinnerAdapter;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class GraphsFragment extends Fragment {

    private GraphsViewModel graphsViewModel;
    private View root;
    private TextView textView;
    private LinearLayout main_graphs;

    private GraphView max_bench, max_deadlift, max_squat;
    private GraphView[] graphArray = new GraphView[3];
    private Format data_format = new SimpleDateFormat("MMM-d");

    private int[] bench_data = {5, 226, 7, 227, 13, 230};
    private int[] deadlift_data = {5, 444, 7, 245, 11, 123};
    private int[] sqaut_data = {3, 265, 4, 300, 8, 467};

    private int[][] max_data = {bench_data, deadlift_data, sqaut_data};

    private Spinner exercise_spin;
    private SpinnerAdapter spinnerAdapter;
    private final String[] exercise_names = {"Choice", "Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs = {R.drawable.dropdown, R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};
    private final String[] exercise_name_less = {"Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs_less = {R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.graphs_fragment, container, false);

        textView = root.findViewById(R.id.text_graphs);
        textView.setVisibility(View.GONE);  // TODO: Should tell about reps

        main_graphs = root.findViewById(R.id.main_graphs);

        max_bench = root.findViewById(R.id.max_bench);
        max_deadlift = root.findViewById(R.id.max_deadlift);
        max_squat = root.findViewById(R.id.max_squat);

        onCreateGraph();

        setGraphsViewModel();

        // Find and set spinner
        exercise_spin = root.findViewById(R.id.exercise_spinner);
        spinnerSetup();
        spinnerChange();

        return root;
    }

    ////////////////////////////////////////////////
    //////////////// HELPER METHODS ////////////////
    ////////////////////////////////////////////////
    private void getSetsRequests() {
        // Post user id
        // The result will need to be returned:
        //   Exercises with exercise id
        //   OR
        //   Three arrays with each exercise in one

        // The data will be stored in an object set information
        // The class will store the calculated max weight and millis

        // Might need to add a boolean column called valid reps
        //   For the formula reps need to be <= 10
        //   0 would be use data, 1 would be do not use data
    }

    // Sets all graphs visibility to GONE and labels vertical axis
    private void setMaxGraphsTraits() {
        for (GraphView graphView : graphArray) {
            graphView.setVisibility(View.GONE);  // No data on create, hide graph
            GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
            gridLabel.setVerticalAxisTitle("1 Rep Max");
            formatHorizontalAxisLabel(graphView);
        }
    }

    // Changes format of horizontal axis labels
    private void formatHorizontalAxisLabel(GraphView graphView) {
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return data_format.format(new Date((long) value));
                }
                return super.formatLabel(value, isValueX);
            }
        });
    }

    // TODO: Rework with the volley request
    // Adds corresponding data to graphs
    private void createLineSeries() {
        for (int g = 0; g < graphArray.length; g++) {
            int[] curr_data = max_data[g];
            DataPoint[] values = new DataPoint[curr_data.length / 2];

            int values_spot = 0;
            for (int i = 0; i < curr_data.length; i++) {
                int weight = curr_data[i];
                int reps = curr_data[i+1];
                DataPoint set = new DataPoint(weight, reps);
                values[values_spot] = set;
                i++;
                values_spot++;
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
            graphArray[g].addSeries(series);
        }
    }

    // Initialize the spinner items
    private void newSpinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs_less, exercise_name_less);
        exercise_spin.setAdapter(spinnerAdapter);
    }

    // Tester to have dates on the x-axis
    /*
    private void test() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1618275897164L, 125),
                new DataPoint(86400000+1618275897164L, 75),
                new DataPoint(172800000+1618275897164L, 234),
                new DataPoint(259200000+1618275897164L, 45),
                new DataPoint(345600000+1618275897164L, 463),
                new DataPoint(432000000+1618275897164L, 88)
        });
        Log.d("FATE", String.valueOf(new Date().getTime()));
        graphArray[0].addSeries(series);
    } */

    ////////////////////////////////////////////////
    ///////////////// MAIN METHODS /////////////////
    ////////////////////////////////////////////////
    // Properly sets the graph array and the visibility
    private void onCreateGraph() {
        // Add graphs to graph array
        graphArray[0] = max_bench;
        graphArray[1] = max_deadlift;
        graphArray[2] = max_squat;

        // TODO: Use volley data to fill the data for the graphs
        //test();
        createLineSeries();
        setMaxGraphsTraits();
    }

    // Initialize the spinner items
    private void spinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs, exercise_names);
        exercise_spin.setAdapter(spinnerAdapter);
    }

    // Removes the entered weight if an exercise is selected
    private void spinnerChange() {
        exercise_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // The spinner still contains the choice and is has not been changed from default
                if (position != 0 && spinnerAdapter.getNames()[0].equals("Choice")) {
                    newSpinnerSetup();  // Remove the base option from the spinner
                    exercise_spin.setSelection(position-1);

                    graphArray[position-1].setVisibility(View.VISIBLE);  // Set correct graph visible

                    main_graphs.requestFocus();

                } else if (!spinnerAdapter.getNames()[0].equals("Choice")) {
                    setMaxGraphsTraits();
                    graphArray[position].setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // Setup for viewmodel
    private void setGraphsViewModel() {
        graphsViewModel = new ViewModelProvider(this).get(GraphsViewModel.class);
        graphsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }
}