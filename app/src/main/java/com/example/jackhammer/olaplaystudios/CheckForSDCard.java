package com.example.jackhammer.olaplaystudios;

import android.os.Environment;

/**
 * Created by jackhammer on 19/12/17.
 */

public class CheckForSDCard {
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
