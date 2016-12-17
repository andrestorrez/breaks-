package co.icoms.breaks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

import co.icoms.breaks.models.Request;
import co.icoms.breaks.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.icoms.breaks.RequestDetailActivity.DETAIL_FRAG;

/**
 * Created by escolarea on 12/12/16.
 */

public class RequestReplyFragment extends Fragment {

    CheckBox chkView;
    EditText commentsView;
    String API_NAMESPACE;
    Request mRequest;
    OkHttpClient mHttpClient;


    OnRequestUpdatedListener mCallback;

    public interface OnRequestUpdatedListener {
        void onRequestUpdated(Request request);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request_reply, container, false);

        Bundle args = getArguments();
        mRequest = DataHolder.getInstance().getRequests().get(args.getInt("request_position", 0));

        chkView = (CheckBox) rootView.findViewById(R.id.checkbox);
        commentsView = (EditText) rootView.findViewById(R.id.comments);

        chkView.setChecked(args.getBoolean("checkbox"));

        //getFragmentManager().

        API_NAMESPACE = getString(R.string.api_endpoint)+ User.current_user(getActivity()).getToken()+"/requests/";
        mHttpClient = new OkHttpClient();

        rootView.findViewById(R.id.send_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResponse();
            }
        });




        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnRequestUpdatedListener) getContext();
        }catch (ClassCastException e){
            throw new ClassCastException(getContext().toString()
                    +"must implement OnRequestUpdatedListener");
        }
    }

    private void attemptResponse(){
        String requestPrefix = "vacation_request";

        RequestBody body = new FormBody.Builder()
                .add(requestPrefix+"[comments]", commentsView.getText().toString())
                .add(requestPrefix+"[accepted]", chkView.isChecked() ? "1" : "0")
                .add(requestPrefix+"[rejected]", chkView.isChecked() ? "0" : "1")
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(API_NAMESPACE+"/update/"+mRequest.getId())
                .put(body)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            Handler h = new Handler();
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String json = response.body().string();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()){
                            Gson gson = new Gson();
                            Request request = gson.fromJson(json, Request.class);
                            mRequest.setApproved(request.isApproved());
                            mRequest.setRejected(request.isRejected());
                            mRequest.setComments(request.getComments());

                            //mCallback.onRequestUpdated(request);

                            getFragmentManager().popBackStack();
                        }else{

                        }
                    }
                });
            }
        });
    }
}
