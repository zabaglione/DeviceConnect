
package com.nttdocomo.android.dconnect.deviceplugin.hue.activity;

import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.nttdocomo.android.dconnect.deviceplugin.hue.activity.fragment.HueFragment01;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class HueMainActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
        getActionBar().setTitle("CLOSE");
        
        // FragmentManagerを生成.
        FragmentManager manager = getSupportFragmentManager();

        // FirstFragmentの生成.
        HueFragment01 mFirstFragment = new HueFragment01();

        // FragmentTransactionを生成.
        FragmentTransaction transaction = manager.beginTransaction();

        // Fragmentの追加.
        transaction.add(R.id.fragment_frame, mFirstFragment, "fragment002");
        transaction.commit();
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
