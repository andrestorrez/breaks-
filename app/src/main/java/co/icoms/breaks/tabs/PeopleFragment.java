package co.icoms.breaks.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import co.icoms.breaks.AddPeopleActivity;
import co.icoms.breaks.HomeActivity;
import co.icoms.breaks.R;
import co.icoms.breaks.adapters.UserAdapter;
import co.icoms.breaks.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by escolarea on 12/5/16.
 */

public class PeopleFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int ADD_USER_REQUEST = 1;
    private static String API_NAMESPACE;

    private SwipeRefreshLayout swipeContainer;
    private ListView mPeopleView;
    private UserAdapter mAdapter;
    private OkHttpClient mHttpClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_people, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        rootView.findViewById(R.id.add_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_people = new Intent(getContext(), AddPeopleActivity.class);
                startActivityForResult(add_people, ADD_USER_REQUEST);
            }
        });

        mHttpClient = new OkHttpClient();
        HomeActivity parentActivity = (HomeActivity) getActivity();
        API_NAMESPACE = getString(R.string.api_endpoint)+parentActivity.getCurrent_user().getToken()+"/users/";

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTeamUsers();
            }
        });
        mPeopleView = (ListView) rootView.findViewById(R.id.people_list);
        ArrayList<User> users = new ArrayList();
        mAdapter = new UserAdapter(getContext(), users);
        mPeopleView.setAdapter(mAdapter);


        loadTeamUsers();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_USER_REQUEST){
            if (resultCode == RESULT_OK){
                System.out.println("Result OK");
                mAdapter.add((User) data.getSerializableExtra("user"));
                mAdapter.notifyDataSetChanged();

            }
            System.out.println("Result ---");
        }
    }

    private void loadTeamUsers(){
        Request request = new Request.Builder()
                .url(API_NAMESPACE+"index")
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            Handler h = new Handler(Looper.getMainLooper());
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (response.isSuccessful()){

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type cType = new TypeToken<ArrayList<User>>(){}.getType();
                            ArrayList<User> users = gson.fromJson(json, cType);
                            mAdapter.clear();
                            mAdapter.addAll(users);
                            mAdapter.notifyDataSetChanged();
                            swipeContainer.setRefreshing(false);
                        }
                    });

                }else {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }

            }
        });
    }
}
