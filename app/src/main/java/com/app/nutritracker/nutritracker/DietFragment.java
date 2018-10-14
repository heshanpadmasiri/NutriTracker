package com.app.nutritracker.nutritracker;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.ui.phone.CheckPhoneNumberFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DietFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DietFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private FirebaseFunctions mFunctions;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;

    public DietFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DietFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DietFragment newInstance(String param1, String param2) {
        DietFragment fragment = new DietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        try{
            getRecommandations();
        } catch (SocketTimeoutException ex ){
            Log.e(TAG,ex.toString());
        }
        this.view = view;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void getRecommandations() throws SocketTimeoutException{
        FirebaseUser user = AuthenticationService.getUser();
        Map<String,Object> data = new HashMap<>();
        data.put("uid",user.getUid());
        mFunctions.getHttpsCallable("getRecommandations")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        HashMap<String, ArrayList<String>> data = (HashMap<String, ArrayList<String>>) task.getResult().getData();
                        ArrayList<String> recommandations = data.get("recommandations");
                        updateFragement(recommandations);
                    }
                });
    }

    private void updateRecommandation(int which, String imageURL, String description){
        TextView txtDescription = null;
        ImageView image = null;
        switch (which){
            case 0:
                txtDescription = view.findViewById(R.id.desc_1);
                image = view.findViewById(R.id.image_1);
                break;
            case 1:
                txtDescription = view.findViewById(R.id.desc_2);
                image = view.findViewById(R.id.image_2);
                break;
            case 2:
                txtDescription = view.findViewById(R.id.desc_3);
                image = view.findViewById(R.id.image_3);
                break;
        }
        if (description != null && image != null){
            txtDescription.setText(description);
        }
    }


    private void updateFragement(ArrayList<String> recommandations){
        for (int i = 0; i < 3; i++) {
            String id = recommandations.get(i);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final int finalI = i;
            db.collection("recommended").document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try{

                                Map<String,Object> data = task.getResult().getData();
                                String description = (String) data.get("desc");
                                String imageUrl = (String) data.get("URL");
                                updateRecommandation(finalI,imageUrl,description);
                            } catch (NullPointerException ex){
                                Log.e(TAG,ex.toString());
                            }

                        }
                    });
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
