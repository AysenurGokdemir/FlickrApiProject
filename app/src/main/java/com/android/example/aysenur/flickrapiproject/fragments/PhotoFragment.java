package com.android.example.aysenur.flickrapiproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.aysenur.flickrapiproject.model.GalleryItem;
import com.android.example.aysenur.flickrapiproject.R;
import com.android.example.aysenur.flickrapiproject.helper.UrlManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;


public class PhotoFragment extends Fragment {
    // tag for logcat
    public static final String TAG = PhotoFragment.class.getSimpleName();

    private ProgressBar mProgressBar;
    private TextView mDescText;
    private ImageView mPhoto;

    private GalleryItem mItem;
    private RequestQueue mRq;


    private boolean mLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,
                container,
                false);

        mItem = (GalleryItem) getActivity().getIntent().getSerializableExtra("item");


        mRq = Volley.newRequestQueue(getActivity());

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mDescText = (TextView) view.findViewById(R.id.desc_text);

        mPhoto = (ImageView) view.findViewById(R.id.photo);
        Glide.with(this).load(mItem.getUrl()).thumbnail(0.5f).into(mPhoto);




        // load photo
        startLoading();
        return view;
    }



    // function for loading single photo description by Volley
    private void startLoading() {
        mLoading = true;
        mProgressBar.setVisibility(View.VISIBLE);
        String url =  UrlManager.getInstance().getPhotoInfoUrl(mItem.getId());
        JsonObjectRequest request = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject photo = response.getJSONObject("photo");
                            JSONObject descObj = photo.getJSONObject("description");
                            String desc = descObj.getString("_content");
                            mDescText.setText(desc);
                        } catch (JSONException e) {
                            if(e != null) {
                                Toast.makeText(getActivity(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                    }
                }
        );

        request.setTag(TAG);
        mRq.add(request);
    }



    private void stopLoading() {
        if (mRq != null) {
            mRq.cancelAll(TAG);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoading();
    }
}
