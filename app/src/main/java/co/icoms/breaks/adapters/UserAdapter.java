package co.icoms.breaks.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.icoms.breaks.R;
import co.icoms.breaks.models.User;

/**
 * Created by escolarea on 12/8/16.
 */

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context,List<User> users) {
        super(context, 0, users);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }

        User user = getItem(position);

        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        nameView.setText(user.getEmail());

        TextView dayLeftView = (TextView) convertView.findViewById(R.id.days_remaining);
        dayLeftView.setText(user.getDays_left()+"/"+user.getAvailable_days());

        return convertView;
    }
}
