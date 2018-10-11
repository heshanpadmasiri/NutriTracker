package com.app.nutritracker.nutritracker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;
import java.util.Random;

import static com.firebase.ui.auth.ui.phone.CheckPhoneNumberFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {

    LineChart lineChartCalories;
    LineChart lineChartFootSteps;

    public LineDataSet lineDataSet2;
    public LineDataSet lineDataSet3;

    private FitnessOptions mFitnessOptions;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(this.getContext(),mFitnessOptions);
        WeeklyRecordAccessThread weeklyCaloryConsumptionRecordThread = new WeeklyRecordAccessThread(gsa,this.getContext(),this,DataType.TYPE_CALORIES_EXPENDED);
        weeklyCaloryConsumptionRecordThread.run();

        WeeklyRecordAccessThread weeklyStepCountThread = new WeeklyRecordAccessThread(gsa,this.getContext(),this,DataType.TYPE_STEP_COUNT_CUMULATIVE);
        weeklyStepCountThread.run();
    }

    public synchronized void updateWeeklyRecord(ArrayList<Float> weeklyCaloryCounts, DataType dataType){
        // TODO: use this to update graph
        if (dataType == DataType.TYPE_CALORIES_EXPENDED){

        } else if (dataType == DataType.TYPE_STEP_COUNT_CUMULATIVE){

        }

        ////lineDataSet2.removeLast();
       // for(int i=0;i<=weeklyCaloryCounts.size();i++){
       //     lineDataSet2.addEntry(new Entry(weeklyCaloryCounts.get(i),i));
       // }
        //lineChartCalories.notifyDataSetChanged(); // let the chart know it's data changed
        //lineChartCalories.invalidate();

        Log.i(TAG,"tt");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view  = inflater.inflate(R.layout.fragment_graph, container, false);
        lineChartCalories = view.findViewById(R.id.lineChartCalories);
        lineChartFootSteps = view.findViewById(R.id.lineChartFootSteps);

        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXESsin = new ArrayList<>();
        ArrayList<Entry> yAXESfootSteps = new ArrayList<>();
        Random random = new Random(40);

        double x = 0 ;
        int numDataPoints = 50;
        for(int i=0;i<numDataPoints;i++){
            // float sinFunction = Float.parseFloat(String.valueOf(Math.sin(x)));

            x = x + 0.1;
            yAXESsin.add(new Entry(random.nextFloat(),i));
            yAXESfootSteps.add(new Entry(random.nextFloat(),i));
            xAXES.add(i, String.valueOf(x));
        }
        String[] xaxes = new String[xAXES.size()];
        for(int i=0; i<xAXES.size();i++){
            xaxes[i] = xAXES.get(i);
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        ArrayList<ILineDataSet> lineDataSets1 = new ArrayList<>();



        lineDataSet2 = new LineDataSet(yAXESsin,"Your daily step count until now");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setValueTextSize(0);
        lineDataSet2.setCircleColorHole(Color.BLACK);
        lineDataSet2.setCircleColor(Color.BLACK);
        lineDataSet2.setColor(Color.BLUE);

        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFillAlpha(255);
        lineDataSet2.setFillColor(Color.BLUE);


        lineDataSet3 = new LineDataSet(yAXESfootSteps,"Your daily step count until now");
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setValueTextSize(0);
        lineDataSet3.setCircleColorHole(Color.BLACK);
        lineDataSet3.setCircleColor(Color.BLACK);
        lineDataSet3.setColor(Color.parseColor("#FF4500"));

        lineDataSet3.setDrawFilled(true);
        lineDataSet3.setFillAlpha(255);
        lineDataSet3.setFillColor(Color.parseColor("#FF4500"));




        lineDataSets.add(lineDataSet2);
        lineDataSets1.add(lineDataSet3);

        lineChartCalories.setData(new LineData(xaxes,lineDataSets));
        lineChartFootSteps.setData(new LineData(xaxes,lineDataSets1));

        lineChartCalories.setVisibleXRangeMaximum(65f);
        lineChartFootSteps.setVisibleXRangeMaximum(65f);

        Legend legend = lineChartCalories.getLegend();
        Legend legend1 = lineChartFootSteps.getLegend();
        legend.setEnabled(false);
        legend1.setEnabled(false);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
            //mListener.onFragmentInteractionHome(Uri.parse("doWhatYouWant"));
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
