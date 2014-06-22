package me.tmd.weeei;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.PushService;

import me.tmd.weeei.fragment.MainFragment;
import me.tmd.weeei.fragment.SigninFragment;

public class MainActivity extends Activity implements SigninFragment.SigninListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && currentUser.isAuthenticated()) {
            PushService.subscribe(getApplicationContext(), currentUser.getUsername(), MainActivity.class);
            changeMainFragment();
        } else {
            changeSigninFragment();
        }
    }

    private void changeSigninFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new SigninFragment());
        transaction.commit();
    }

    private void changeMainFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new MainFragment());
        transaction.commit();
    }

    @Override
    public void signinSuccess() {
        changeMainFragment();
    }

    @Override
    public void signinFail() {
        Toast.makeText(getApplicationContext(), "ｳｪｰｲ...", Toast.LENGTH_LONG).show();
    }
}
