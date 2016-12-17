package co.icoms.breaks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import co.icoms.breaks.models.Request;

public class RequestDetailActivity extends AppCompatActivity implements RequestReplyFragment.OnRequestUpdatedListener {

    public static final String DETAIL_FRAG = "detail.fragment";
    public static final String REPLY_FRAG = "reply.fragment";

    TextView nameView;
    TextView dateView;
    Request mRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RequestDetailFragment detail = new RequestDetailFragment();
        detail.setArguments(getIntent().getExtras());

        mRequest = DataHolder.getInstance().getRequests().get(getIntent().getIntExtra("request_position", 0));

        nameView = (TextView) findViewById(R.id.user_name);
        nameView.setText(mRequest.getUser().getEmail());

        dateView = (TextView) findViewById(R.id.date_range);
        dateView.setText(mRequest.getRange());

        getSupportFragmentManager().beginTransaction().add(R.id.fragment, detail, DETAIL_FRAG).commit();

    }

    @Override
    public void onRequestUpdated(Request request) {
        //DataHolder.getInstance().updateRequestAdapter();
    }

}
