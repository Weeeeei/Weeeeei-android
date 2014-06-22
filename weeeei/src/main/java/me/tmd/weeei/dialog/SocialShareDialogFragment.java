package me.tmd.weeei.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by takatoshi-maeda on 2014/06/22.
 */
public class SocialShareDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStage) {
        String[] items = {"Twitter", "Facebook", "SMS"};

        final String shareMessage = "Add my Weeeei username by tapping here: " + ParseUser.getCurrentUser().getUsername() + "(if you don't have Weeeei app get it here)";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        String twitterUrl = "http://twitter.com/share?text=" + shareMessage;
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                        startActivity(twitterIntent);
                        break;
                    case 1:
                        Intent facebookIntent = new Intent(Intent.ACTION_SEND);
                        facebookIntent.setType("text/plain");
                        facebookIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        boolean facebookAppFound = false;
                        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(facebookIntent, 0);
                        for (ResolveInfo info : matches) {
                            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                                facebookIntent.setPackage(info.activityInfo.packageName);
                                facebookAppFound = true;
                                break;
                            }
                        }
                        if (!facebookAppFound) {
                            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + shareMessage;
                            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                        }
                        startActivity(facebookIntent);
                        break;
                    case 2:
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("sms:"));
                        smsIntent.putExtra("sms_body", shareMessage);
                        startActivity(smsIntent);
                        break;
                }
            }
        });

        return builder.create();
    }
}
