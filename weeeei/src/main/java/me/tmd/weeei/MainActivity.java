package me.tmd.weeei;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

public class MainActivity extends Activity {

    EditText mEditUserName;

    Button mButtonGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mButtonGo = (Button) findViewById(R.id.btn_go);
        mEditUserName = (EditText) findViewById(R.id.edit_user_name);
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
    }

}
