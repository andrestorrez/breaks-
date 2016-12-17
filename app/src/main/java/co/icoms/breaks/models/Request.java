package co.icoms.breaks.models;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

import co.icoms.breaks.R;

/**
 * Created by escolarea on 12/11/16.
 */

public class Request implements Serializable {

    private int id;
    private Date start_date;
    private Date end_date;
    private String message;
    private String comments;
    private boolean approved;
    private boolean rejected;
    private User user;
    private User admin;
    private String range;


    public int getId() {
        return id;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public String getMessage() {
        return message;
    }

    public String getComments() {
        return comments;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isRejected() {
        return rejected;
    }

    public User getUser() {
        return user;
    }

    public User getAdmin() {
        return admin;
    }

    public String getRange() {
        return range;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public static String getRequestStatus(Context context, Request request){
        if (!request.isApproved() && !request.isRejected()){
            return context.getString(R.string.request_not_approved);
        }else if(request.isRejected()){
            return context.getString(R.string.request_rejected);
        }else{
            return context.getString(R.string.request_approved);
        }
    }
}
