package com.android.example.aysenur.flickrapiproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.widget.SearchView;


public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment" ;

    private static final int COLUMN_NUM = 3;
    private static final int ITEM_PER_PAGE = 100;

    private RequestQueue mRq;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private GalleryAdapter mAdapter;
    private boolean mLoading = false;
    private boolean mHasMore = true;

    public GalleryFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "----------onCreate----------");

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mRq = Volley.newRequestQueue(getActivity());


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItem = mLayoutManager.getItemCount();
                Log.d(TAG, "--------onScrolled-----totalItem is " + totalItem);

                int lastItemPos = mLayoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "--------onScrolled-----lastItemPos is " + lastItemPos);

                if (mHasMore && !mLoading && totalItem -1 != lastItemPos) {
                    startLoading();
                }
            }
        });


        mLayoutManager = new GridLayoutManager(getActivity(), COLUMN_NUM);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GalleryAdapter(getActivity(), new ArrayList<GalleryItem>());
        mRecyclerView.setAdapter(mAdapter);



        mCustomSwipeRefreshLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mCustomSwipeRefreshLayout.setOnRefreshListener(
                new CustomSwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );



        startLoading();
        return view;
    }

    public void refresh() {
        mAdapter.clear();
        startLoading();
    }

    private void startLoading() {
        Log.d(TAG, "startLoading");
        mLoading = true;

        int totalItem = mLayoutManager.getItemCount();
        final int page = totalItem / ITEM_PER_PAGE + 1;


        String url = UrlManager.getInstance().getItemUrl(page);


        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse " + response);

                List<GalleryItem> result = new ArrayList<>();

                try {
                    JSONObject photos = response.getJSONObject("photos");

                    if (photos.getInt("pages") == page) {
                        mHasMore = false;
                    }

                    JSONArray photoArr = photos.getJSONArray("photo");
                    for ( int i = 0; i < photoArr.length(); i++) {
                        JSONObject itemObj = photoArr.getJSONObject(i);
                        GalleryItem item = new GalleryItem(
                                itemObj.getString("id"),
                                itemObj.getString("secret"),
                                itemObj.getString("server"),
                                itemObj.getString("farm")
                        );

                        result.add(item);
                    }
                } catch (JSONException e) {

                }
                mAdapter.addAll(result);
                mAdapter.notifyDataSetChanged();
                mLoading = false;
                mCustomSwipeRefreshLayout.refreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
