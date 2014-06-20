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
import com.parse.ParseUser;

public class MainActivity extends Activity {

    EditText mEditUserName;

    Button mButtonGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonGo = (Button)findViewById(R.id.btn_go);
        mEditUserName = (EditText)findViewById(R.id.edit_user_name);
        if (savedInstanceState == null) {
            if (ParseUser.getCurrentUser() == null) {
                changeLoginView();
            } else {
                changeMainView();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ParseUser.getCurrentUser() == null) {
            mButtonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseAnonymousUtils.logIn(new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                user.setUsername(mEditUserName.getText().toString());
                                changeMainView();
                            } else {
                                Toast.makeText(MainActivity.this, "ｳｪｰｲ...", Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            });
        }
    }

    private void changeLoginView() {
        findViewById(R.id.wrapper_login).setVisibility(View.VISIBLE);
    }

    private void changeMainView() {
        findViewById(R.id.wrapper_login).setVisibility(View.GONE);
    }

}
