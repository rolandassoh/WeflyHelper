package com.wedevgroup.weflyhelper.interfaces;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;

/**
 * Created by Obrina.KIMI on 1/29/2018.
 */

public interface MainAction {

    void onDisplayMainActivity(Activity activity);

    class MainActivityClass implements  MainAction{

        @Override
        public void onDisplayMainActivity(@NonNull Activity act) {
            Utils utils = new Utils();
            AppController.clearDestroyList();
            act.finish();
            utils.animActivityClose(act);

        }
    }
}
