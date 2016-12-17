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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.icoms.breaks.models.User;
import co.icoms.breaks.tabs.PeopleFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPeopleActivity extends AppCompatActivity {

    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener mDateListener;

    private EditText mEmailV;
    private EditText mStartWorkingDateV;
    private Spinner mRoleV;

    private String API_NAMESPACE;

    private OkHttpClient mHttpClient;

    private View mAddMemberFormView;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        Spinner rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolesSpinner.setAdapter(adapter);

        API_NAMESPACE = getString(R.string.api_endpoint)+User.current_user(this).getToken()+"/users/";
        mHttpClient = new OkHttpClient();

        mAddMemberFormView = findViewById(R.id.container_add_people);
        mProgressView = findViewById(R.id.request_progress);



        mCalendar = Calendar.getInstance();
        mStartWorkingDateV = (EditText) findViewById(R.id.start_working_date);
        mEmailV = (EditText) findViewById(R.id.email);
        mRoleV = (Spinner) findViewById(R.id.roles_spinner);

        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        mStartWorkingDateV.setKeyListener(null);
        mStartWorkingDateV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showPicker();
                }
            }
        });
        mStartWorkingDateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker();
            }
        });

        findViewById(R.id.action_add_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddPeople();
            }
        });
    }

    private void attemptAddPeople(){
        mEmailV.setError(null);
        mStartWorkingDateV.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailV.getText().toString();
        String date = mStartWorkingDateV.getText().toString();
        String role = mRoleV.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        if (date.isEmpty()){
            cancel = true;
            focusView = mStartWorkingDateV;
            mStartWorkingDateV.setError(getString(R.string.error_blank_field));
        }

        if (!isEmailValid(email)){
            cancel = true;
            focusView = mEmailV;
            mEmailV.setError(getString(R.string.error_invalid_email));
        }



        if (cancel){
            focusView.requestFocus();
        }else{
            showProgress(true);
            try {
                String userPrefix = "user";
                RequestBody formBody = new FormBody.Builder()
                        .add(userPrefix+"[email]", email)
                        .add(userPrefix+"[start_working_date]", date)
                        .add(userPrefix+"[role]", User.roleValue(role))
                        .build();
                makeRequest(formBody);
            } catch (Exception e) {
                e.printStackTrace();
                showProgress(false);
            }
        }
    }

    private void makeRequest(RequestBody formBody){
        Request request = new Request.Builder()
                .url(API_NAMESPACE+"add_member")
                .post(formBody)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AddPeopleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    AddPeopleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                        }
                    });
                }else{
                    final String json =  response.body().string();
                    AddPeopleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //User.logIn(SignUpActivity.this, json);
                            //Intent main = new Intent(SignUpActivity.this, HomeActivity.class);
                            //startActivity(main);
                            Gson gson = new Gson();
                            User user = gson.fromJson(json, User.class);
                            Intent intent = new Intent();
                            intent.putExtra("user", user);
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                    });
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") || !email.equals("");
    }



    private void updateLabel(){
        String format = "dd/MM/yyyy";
        SimpleDateFormat date = new SimpleDateFormat(format, Locale.US);
        mStartWorkingDateV.setText(date.format(mCalendar.getTime()));
    }

    private void showPicker(){
       DatePickerDialog dialog = new DatePickerDialog(AddPeopleActivity.this, mDateListener, mCalendar.get(Calendar.YEAR),
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

            mAddMemberFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddMemberFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddMemberFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAddMemberFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
