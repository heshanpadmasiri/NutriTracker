package com.app.nutritracker.nutritracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.firebase.ui.auth.ui.phone.CheckPhoneNumberFragment.TAG;
import static java.text.DateFormat.getDateInstance;

class HistoryAccessThread implements Runnable {

    GoogleSignInAccount gsa;
    Context context;

    HistoryAccessThread(GoogleSignInAccount googleSignInAccount, Context context){
        gsa = googleSignInAccount;
        this.context = context;
    }
    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        final Task<DataReadResponse> response = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)).readData(readRequest);
        response.addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                List<DataSet> dataSets = response.getResult().getDataSets();
            }
        });
        response.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "History access failed due to :" + e.toString());
            }
        });
    }
}
