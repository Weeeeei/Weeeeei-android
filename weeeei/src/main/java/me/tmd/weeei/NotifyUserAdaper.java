package me.tmd.weeei;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by takatoshi-maeda on 2014/06/21.
 */
public class NotifyUserAdaper extends ArrayAdapter<String> {

    public NotifyUserAdaper(Context context, List<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_notify_user, null);
        }

        String item = getItem(position);

        ((TextView)convertView.findViewById(R.id.text_notify_user)).setText(item);

        return convertView;
    }
}
