package org.mobile.android.footysortit.main;

import android.content.ContentValues;

public interface DatabaseUpdateInterface {

    void updatePlayerStatus(ContentValues cv, long id);
    void getNumbersForPlayerStatus();
}
