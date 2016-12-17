package co.icoms.breaks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.icoms.breaks.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestActivity extends AppCompatActivity {

    private Calendar mCalendar;
    private EditText mStartDateV;
    private EditText mEndDateV;
    private EditText mMessage;

    private DatePickerDialog.OnDateSetListener mDateListener1;
    private DatePickerDialog.OnDateSetListener mDateListener2;

    private View mRequestFormView;
    private View mProgressView;
    private OkHttpClient mHttpClient;

    private String API_NAMESPACE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mHttpClient = new OkHttpClient();
        API_NAMESPACE = getString(R.string.api_endpoint)+ User.current_user(this).getToken()+"/requests/";

        mRequestFormView = findViewById(R.id.container_request);
        mProgressView = findViewById(R.id.request_progress);

        mCalendar = Calendar.getInstance();

        mStartDateV = (EditText) findViewById(R.id.start_date);
        mEndDateV = (EditText) findViewById(R.id.end_date);
        mMessage = (EditText) findViewById(R.id.message);
        mStartDateV.setKeyListener(null);
        mEndDateV.setKeyListener(null);

        mDateListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(mStartDateV);
            }
        };

        mDateListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(mEndDateV);
            }
        };

        mStartDateV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showPicker(mDateListener1);
                }
            }
        });
        mStartDateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(mDateListener1);
            }
        });

        mEndDateV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showPicker(mDateListener2);
            }
        });
        mEndDateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(mDateListener2);
            }
        });

        findViewById(R.id.send_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSendRequest();
            }
        });
    }

    public void attempSendRequest(){
        mStartDateV.setError(null);
        mEndDateV.setError(null);

        String start_date = mStartDateV.getText().toString();
        String end_date = mEndDateV.getText().toString();
        String message = mMessage.getText().toString();

        boolean cancel= false;
        View focusView = null;

        if (end_date.isEmpty()){
            cancel = true;
            mEndDateV.setError(getString(R.string.error_blank_field));
            focusView = mEndDateV;
        }

        if (start_date.isEmpty()){
            cancel = true;
            mStartDateV.setError(getString(R.string.error_blank_field));
            focusView = mStartDateV;
        }

        if (cancel){
            focusView.requestFocus();
        }else{
            showProgress(true);
            try {
                String requestPrefix = "vacation_request";
                RequestBody formBody = new FormBody.Builder()
                        .add(requestPrefix+"[start_date]", start_date)
                        .add(requestPrefix+"[end_date]", end_date)
                        .add(requestPrefix+"[message]", message)
                        .build();
                makeRequest(formBody);
                showProgress(true);
            } catch (Exception e) {
                e.printStackTrace();
                showProgress(false);
            }
        }
    }

    private void makeRequest(RequestBody formBody){
        final Request request= new Request.Builder()
                .url(API_NAMESPACE+"create")
                .post(formBody)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                RequestActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                final boolean status = response.isSuccessful();
                RequestActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status){
                            Gson gson = new Gson();
                            co.icoms.breaks.models.Request request = gson.fromJson(json, co.icoms.breaks.models.Request.class);
                            Intent resp = new Intent();
                            resp.putExtra("request", request);
                            setResult(RESULT_OK, resp);
                            finish();
                        }
                        showProgress(false);
                    }
                });
            }
        });

    }

    private void updateLabel(EditText dateView){
        String format = "dd/MM/yyyy";
        SimpleDateFormat date = new SimpleDateFormat(format, Locale.US);
        dateView.setText(date.format(mCalendar.getTime()));
    }


    private void showPicker(DatePickerDialog.OnDateSetListener mDateListener){
        DatePickerDialog dialog = new DatePickerDialog(RequestActivity.this, mDateListener, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        DatePicker picker = dialog.getDatePicker();

        picker.setSpinnersShown(true);
        picker.setCalendarViewShown(false);
        dialog.show();

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRequestFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
