package me.tmd.weeei;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    EditText mEditUserName;

    Button mButtonGo;

    ListView mListViewNotifyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mButtonGo = (Button) findViewById(R.id.btn_go);
        mEditUserName = (EditText) findViewById(R.id.edit_user_name);
        mListViewNotifyUser = (ListView) findViewById(R.id.list_notify_users);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && currentUser.isAuthenticated()) {
            PushService.subscribe(getApplicationContext(), currentUser.getUsername(), MainActivity.class);
            changeMainView();
        } else {
            changeLoginView();
        }
    }

    private void changeLoginView() {
        findViewById(R.id.wrapper_login).setVisibility(View.VISIBLE);
        findViewById(R.id.wrapper_main).setVisibility(View.GONE);

        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException e) {
                        if (user != null) {
                            user.setUsername(mEditUserName.getText().toString());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    PushService.subscribe(getApplicationContext(), user.getUsername(), MainActivity.class);
                                }
                            });
                            changeMainView();
                        } else {
                            Toast.makeText(MainActivity.this, "ｳｪｰｲ...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void changeMainView() {
        findViewById(R.id.wrapper_login).setVisibility(View.GONE);
        findViewById(R.id.wrapper_main).setVisibility(View.VISIBLE);

        final ArrayList<String> items = restoreNotifyUsers();

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
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

        mListViewNotifyUser.setAdapter(new NotifyUserAdaper(getApplicationContext(), items));

        mListViewNotifyUser.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pushNotify(items.get(i));
            }
        });
    }

    private void addUserNameStore(ArrayList<String> items) {
        SharedPreferences pref = getSharedPreferences("NotifyUser", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", new Gson().toJson(items));
        editor.commit();
    }

    private ArrayList<String> restoreNotifyUsers() {
        String notifyUsersJSON = getSharedPreferences("NotifyUser", Activity.MODE_PRIVATE).getString("Data", "[]");
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
