package me.tmd.weeei;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Created by takatoshi-maeda on 2014/06/21.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        Parse.initialize(getApplicationContext(),
                getApplicationContext().getString(R.string.parse_application_id),
                getApplicationContext().getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(getApplicationContext(), MainActivity.class);
    }

}
