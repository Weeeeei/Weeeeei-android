package me.tmd.weeei.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import me.tmd.weeei.MainActivity;
import me.tmd.weeei.R;

/**
 * Created by takatoshi-maeda on 2014/06/23.
 */
public class SigninFragment extends Fragment {

    EditText mEditUserName;

    Button mButtonGo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        mEditUserName = (EditText) view.findViewById(R.id.edit_user_name);
        mButtonGo = (Button)view.findViewById(R.id.btn_go);

        setupSigninButton();

        return view;
    }

    private void setupSigninButton() {
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
                                    PushService.subscribe(getActivity().getApplicationContext(), user.getUsername(), MainActivity.class);
                                    ((SigninListner)getActivity()).signinSuccess();
                                }
                            });
                        } else {
                            ((SigninListner)getActivity()).signinFail();
                        }
                    }
                });
            }
        });
    }

    public static interface SigninListner {
        public void signinSuccess();
        public void signinFail();
    }
}
