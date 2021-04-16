package com.example.finalprojectmoore.ui.graphsdisplay;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalprojectmoore.CustomRequest;
import com.example.finalprojectmoore.MainActivity;
import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SetInformation;
import com.example.finalprojectmoore.SpinnerAdapter;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphsFragment extends Fragment {

    private static final String url = "http://10.0.0.133:5000/get_set";

    private GraphsViewModel graphsViewModel;
    private View root;
    private TextView textView;
    private LinearLayout main_graphs;

    private GraphView max_bench , max_deadlift, max_squat;
    private GraphView[] graphArray = new GraphView[3];
    private Format data_format = new SimpleDateFormat("MMM-d");

    private ArrayList<SetInformation> data_bench = new ArrayList<>(), data_deadlift = new ArrayList<>(), data_squat = new ArrayList<>();
    private ArrayList<ArrayList<SetInformation>> data_max = new ArrayList<>();

    /*
    private int[] bench_data = {5, 226, 7, 227, 13, 230};
    private int[] deadlift_data = {5, 444, 7, 245, 11, 123};
    private int[] squat_data = {3, 265, 4, 300, 8, 467};

    private int[][] max_data = {bench_data, deadlift_data, squat_data};
    */

    private Spinner exercise_spin;
    private SpinnerAdapter spinnerAdapter;
    private final String[] exercise_names = {"Choice", "Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs = {R.drawable.dropdown, R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};
    private final String[] exercise_name_less = {"Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs_less = {R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.graphs_fragment, container, false);

        setToolbarIcon();

        // Add arraylist data to main data
        data_max.add(data_bench);
        data_max.add(data_deadlift);
        data_max.add(data_squat);

        textView = root.findViewById(R.id.text_graphs);
        textView.setVisibility(View.GONE);  // GONE, but can use if wanted later

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Creating the JSON object to POST to flask
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", ((MainActivity) getActivity()).user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Array", String.valueOf(response));
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject full_set = response.getJSONObject(i);

                                int exercise_id = full_set.getInt("exercise_id");
                                String time_added = full_set.getString("timestamp");
                                int one_rep_max = full_set.getInt("1_rep_max");

                                SetInformation set = new SetInformation(
                                       exercise_id,
                                       time_added,
                                       one_rep_max
                                );
                                data_max.get(exercise_id).add(set);  // Add to correct data set
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        createLineSeries();  // Create the series for each graph
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");  // Need this to talk to Flask
                return params;
            }
        };
        requestQueue.add(customRequest);   //Add request to the queue
    }

    // Sets all graphs visibility to GONE and labels vertical axis
    private void setMaxGraphsTraits() {
        for (GraphView graphView : graphArray) {
            graphView.setVisibility(View.GONE);  // No data on create, hide graph
            GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
            gridLabel.setVerticalAxisTitle("1 Rep Max");
            formatHorizontalAxisLabel(graphView);
            // TODO: Change graph view (some points are not showing)
            //graphView.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
            //graphView.getViewport().setScrollable(true);  // activate horizontal scrolling
            //graphView.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
            //graphView.getViewport().setScrollableY(true);  // activate vertical scrolling
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

    // Adds corresponding data to graphs
    private void createLineSeries() {
        for (int g = 0; g < data_max.size(); g++) {
            ArrayList<SetInformation> curr_data = data_max.get(g);  // Current exercise

            // If there is data for that exercise
            if (curr_data.size() > 0) {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                PointsGraphSeries<DataPoint> points = new PointsGraphSeries<>();

                // Create a new data point for each set
                for (int i = 0; i < curr_data.size(); i++) {
                    long date = curr_data.get(i).getDateMillis();
                    int one_rep = curr_data.get(i).getOneRepMax();
                    DataPoint set_point = new DataPoint(date, one_rep);
                    series.appendData(set_point, true, curr_data.size());
                    points.appendData(set_point, true, curr_data.size());
                }
                graphArray[g].addSeries(series);  // Add the series to that set
                graphArray[g].addSeries(points);  // Add the series to that set
            }
        }
    }

    // Initialize the spinner items
    private void newSpinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs_less, exercise_name_less);
        exercise_spin.setAdapter(spinnerAdapter);
    }

    ////////////////////////////////////////////////
    ///////////////// MAIN METHODS /////////////////
    ////////////////////////////////////////////////
    // Properly sets the graph array and the visibility
    private void onCreateGraph() {
        // Add graphs to graph array
        graphArray[0] = max_bench;
        graphArray[1] = max_deadlift;
        graphArray[2] = max_squat;

        getSetsRequests();
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

    // Sets correct image in the toolbar
    private void setToolbarIcon() {
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem icon = menu.findItem(R.id.icon_set);
        icon.setIcon(R.drawable.menu_graphs);
        Drawable drawable = icon.getIcon();
        drawable.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
    }
}