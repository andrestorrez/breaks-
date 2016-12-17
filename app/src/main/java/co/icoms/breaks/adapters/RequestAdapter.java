package co.icoms.breaks.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import co.icoms.breaks.R;
import co.icoms.breaks.RequestDetailActivity;
import co.icoms.breaks.models.Request;

/**
 * Created by escolarea on 12/12/16.
 */

public class RequestAdapter extends ArrayAdapter<Request> {

    public RequestAdapter(Context context, ArrayList<Request> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_list_item, parent, false);
        }

        final Request request = getItem(position);

        TextView nameView = (TextView) convertView.findViewById(R.id.user_name);
        nameView.setText(request.getUser().getEmail());

        TextView rangeV = (TextView) convertView.findViewById(R.id.range_date);
        rangeV.setText(request.getRange());

        TextView statusV = (TextView) convertView.findViewById(R.id.status);
        statusV.setText(Request.getRequestStatus(getContext(),request));

        Button buttonV = (Button) convertView.findViewById(R.id.action_reply);
        buttonV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent request_detail = new Intent(getContext(), RequestDetailActivity.class);
                request_detail.putExtra("request_position", position);
                getContext().startActivity(request_detail);
            }
        });


        return convertView;
    }

}
