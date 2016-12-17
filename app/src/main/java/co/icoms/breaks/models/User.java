package co.icoms.breaks.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import co.icoms.breaks.R;

/**
 * Created by escolarea on 12/4/16.
 */

public class User implements Serializable {
    private String email;
    private String token;
    private String reset_password_token;
    private ArrayList<Team> teams;
    private int role;
    private int available_days;
    private int days_left;


    public User(String email, String token, ArrayList<Team> teams) {
        super();
        this.email = email;
        this.token = token;
        this.teams = teams;
    }

    public String getEmail() {
        return email;
    }


    public String getToken() {
        return token;
    }


    public ArrayList<Team> getTeams() {
        return teams;
    }


    public String getReset_password_token() {
        return reset_password_token;
    }

    public boolean isAdmin(){
        return role == 0;
    }

    public boolean isUser(){
        return role == 1;
    }

    public int getAvailable_days() {
        return available_days;
    }

    public void setAvailable_days(int available_days) {
        this.available_days = available_days;
    }

    public int getDays_left() {
        return days_left;
    }

    public void setDays_left(int days_left) {
        this.days_left = days_left;
    }

    public static String roleValue(String role){
        if (role.equals("Admin")){
            return "0";
        }else{
            return "1";
        }
    }

    //Methods to handle a user
    public static void logIn(Activity activity, User user){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity); //activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(activity.getString(R.string.user_token_label), json);
        editor.commit();
    }

    public static void logIn(Activity activity, String json){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity); //activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.user_token_label), json);
        editor.commit();
    }

    public static User current_user(Activity activity){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity); //activity.getPreferences(Context.MODE_PRIVATE);
        String json = sharedPref.getString(activity.getString(R.string.user_token_label), null);
        Gson gson = new Gson();
        if (json!=null){
            return gson.fromJson(json, User.class);
        }
        return null;
    }

    public static void logOut(Activity activity){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity); //activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.user_token_label), null);
        editor.commit();
    }

}
