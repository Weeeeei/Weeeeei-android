package me.tmd.weeei.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParsePush;
import com.parse.ParseUser;

import java.util.ArrayList;

import me.tmd.weeei.adapter.NotifyUserAdaper;
import me.tmd.weeei.R;
import me.tmd.weeei.dialog.SocialShareDialogFragment;

/**
 * Created by takatoshi-maeda on 2014/06/23.
 */
public class MainFragment extends Fragment {

    ListView mListViewNotifyUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mListViewNotifyUser = (ListView)view.findViewById(R.id.list_notify_users);

        setupNotifyUserListView();

        return view;
    }

    private void setupNotifyUserListView() {
        final ArrayList<String> items = restoreNotifyUsers();
        final NotifyUserAdaper adapter = new NotifyUserAdaper(getActivity().getApplicationContext(), items);

        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
        View header = inflater.inflate(R.layout.list_row_notify_user_header, null);
        header.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new SocialShareDialogFragment();
                dialog.show(getFragmentManager(), "Share");
            }
        });

        View footer = inflater.inflate(R.layout.list_row_notify_user_footer, null);
        final EditText editBox = (EditText) footer.findViewById(R.id.edit_notify_user);
        final Button editButton = ((Button) footer.findViewById(R.id.btn_edit));
        editBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    items.add(textView.getText().toString());
                    adapter.notifyDataSetChanged();
                    addUserNameStore(items);
                    editBox.setVisibility(View.GONE);
                    editBox.setText("");
                    editButton.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBox.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
            }
        });

        if (mListViewNotifyUser.getHeaderViewsCount() == 0) {
            mListViewNotifyUser.addHeaderView(header);
        }

        if (mListViewNotifyUser.getFooterViewsCount() == 0) {
            mListViewNotifyUser.addFooterView(footer);
        }

        mListViewNotifyUser.setAdapter(adapter);

        mListViewNotifyUser.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pushNotify(items.get(i - 1));
            }
        });
    }

    private void addUserNameStore(ArrayList<String> items) {
        SharedPreferences pref = getActivity().getSharedPreferences("NotifyUser", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", new Gson().toJson(items));
        editor.commit();
    }

    private ArrayList<String> restoreNotifyUsers() {
        String notifyUsersJSON = getActivity().getSharedPreferences("NotifyUser", Activity.MODE_PRIVATE).getString("Data", "[]");
        return new Gson().fromJson(notifyUsersJSON, new TypeToken<ArrayList<String>>() {}.getType());
    }

    private void pushNotify(String notifyUserName) {
        ParsePush push = new ParsePush();
        push.setChannel(notifyUserName);
        push.setMessage(
                new StringBuilder().
                        append(ParseUser.getCurrentUser().getUsername()).
                        append(": ｳｪｰｲ").toString()
        );
        push.sendInBackground();
    }
}
