package co.icoms.breaks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import co.icoms.breaks.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

public class SignUpActivity extends AppCompatActivity {

    private EditText mTeamNameV;
    private EditText mEmailV;
    private EditText mPasswordV;
    private EditText mPasswordConfirmV;
    private Spinner mCountriesV;
    private View mSignUpForm;
    private View mProgressView;
    private OkHttpClient mHttpClient;
    private String API_NAMESPACE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mTeamNameV = (EditText) findViewById(R.id.team_name);
        mEmailV = (EditText) findViewById(R.id.email);
        mPasswordV = (EditText) findViewById(R.id.password);
        mPasswordConfirmV = (EditText) findViewById(R.id.password_confirm);
        mCountriesV = (Spinner) findViewById(R.id.countries);
        mSignUpForm = findViewById(R.id.sign_up_form);
        mProgressView = findViewById(R.id.loading_bar);

        API_NAMESPACE = getResources().getString(R.string.api_endpoint)+"registrations/";
        mHttpClient = new OkHttpClient();

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<CountryObject> countries = new ArrayList();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                if (loc.getDisplayCountry().equals( "Honduras"))
                    countries.add(new CountryObject(loc.getDisplayCountry(), loc.getCountry(), loc.getLanguage()));
            }
        }
        Comparator<CountryObject> comparator = new Comparator<CountryObject>(){

            @Override
            public int compare(CountryObject o1, CountryObject o2) {
                return CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
            }
        };
        Collections.sort(countries, comparator);
        Locale current = BreaksUtils.getCurrentLocale(this);

        Comparator<CountryObject> searchComparator= new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };
        int i = Collections.binarySearch(countries,
                new CountryObject(current.getDisplayCountry(), current.getCountry(), current.getLanguage()),
                searchComparator);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries);
        mCountriesV.setAdapter(adapter);
        mCountriesV.setSelection(i);


        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }


    private void attemptSignUp(){
        mTeamNameV.setError(null);
        mEmailV.setError(null);
        mPasswordV.setError(null);
        mPasswordConfirmV.setError(null);

        boolean cancel = false;
        View focusView = null;

        String team_name = mTeamNameV.getText().toString();
        CountryObject country_obj = (CountryObject) mCountriesV.getSelectedItem();
        String country = country_obj.getName();
        String country_iso = country_obj.getIso();
        String country_lang = country_obj.getLang();
        String email = mEmailV.getText().toString();
        String password = mPasswordV.getText().toString();
        String password_confirm = mPasswordConfirmV.getText().toString();

        if (!isPasswordValid(password)){
            mPasswordV.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordV;
            cancel = true;
        }

        if (!password.equals(password_confirm)){
            mPasswordConfirmV.setError(getString(R.string.error_confirm_password));
            focusView = mPasswordConfirmV;
            cancel = true;
        }

        if (!isEmailValid(email)){
            mEmailV.setError(getString(R.string.error_invalid_email));
            focusView = mEmailV;
            cancel = true;
        }

        if (!BreaksUtils.mayRequestNetwork(this) || cancel){
            focusView.requestFocus();
        }else{
            showProgress(true);
            try {
                String userPrefix = "user";
                String teamPrefix = "team";
                String countryPrefix = "country";
                RequestBody formBody = new FormBody.Builder()
                        .add(userPrefix+"[email]", email)
                        .add(userPrefix+"[password]", password)
                        .add(teamPrefix+"[name]", team_name)
                        .add(countryPrefix+"[name]", country)
                        .add(countryPrefix+"[iso]",country_iso)
                        .add(countryPrefix+"[lang]", country_lang)
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
                .url(API_NAMESPACE+"sign_up")
                .post(formBody)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()){
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                        }
                    });
                }else{
                    final String json =  response.body().string();
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                User.logIn(SignUpActivity.this, json);
                                Intent main = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(main);
                                finish();

                        }
                    });
                }

            }
        });
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

            mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
