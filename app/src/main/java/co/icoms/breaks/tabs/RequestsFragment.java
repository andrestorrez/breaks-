package co.icoms.breaks.tabs;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import co.icoms.breaks.DataHolder;
import co.icoms.breaks.HomeActivity;
import co.icoms.breaks.R;
import co.icoms.breaks.adapters.MyBreaksAdapter;
import co.icoms.breaks.adapters.RequestAdapter;
import co.icoms.breaks.models.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by escolarea on 12/5/16.
 */

public class RequestsFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static String API_NAMESPACE;
    private SwipeRefreshLayout swipeRefresfV;
    private ListView requestListV;
    private RequestAdapter mAdapter;
    private ArrayList<Request> items;
    private OkHttpClient mHttpClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_requests, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        mHttpClient = new OkHttpClient();
        HomeActivity parentActivity = (HomeActivity) getActivity();
        API_NAMESPACE = getString(R.string.api_endpoint)+parentActivity.getCurrent_user().getToken()+"/requests/";



        requestListV = (ListView) rootView.findViewById(R.id.requests_list);
        items = new ArrayList<>();
        DataHolder.getInstance().setRequests(items);
        //DataHolder.getInstance().setRequestAdapter(mAdapter);
        mAdapter = new RequestAdapter(getContext(), items);
        requestListV.setAdapter(mAdapter);
        swipeRefresfV = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefresfV.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBreaks();
            }
        });

        loadBreaks();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void loadBreaks(){
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(API_NAMESPACE+"index")
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            Handler h = new Handler(Looper.getMainLooper());
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresfV.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                final boolean status = response.isSuccessful();


                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status){
                            Gson gson = new Gson();
                            Type cType = new TypeToken<ArrayList<Request>>(){}.getType();
                            ArrayList<co.icoms.breaks.models.Request> requests = gson.fromJson(json, cType);
                            mAdapter.clear();
                            mAdapter.addAll(requests);
                        }
                        swipeRefresfV.setRefreshing(false);
                    }
                });
            }
        });
    }
}
