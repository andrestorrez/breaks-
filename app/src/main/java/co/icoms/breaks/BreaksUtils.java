package co.icoms.breaks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.Locale;

import static android.Manifest.permission.INTERNET;

/**
 * Created by escolarea on 12/4/16.
 */

public class BreaksUtils {
    private static final int REQUEST_INTERNET = 0;

    public static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static boolean mayRequestNetwork(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity.checkSelfPermission(INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        activity.requestPermissions(new String[]{INTERNET}, REQUEST_INTERNET);

        return false;
    }
}
