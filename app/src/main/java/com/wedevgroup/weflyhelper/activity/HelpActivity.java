package com.wedevgroup.weflyhelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
}
