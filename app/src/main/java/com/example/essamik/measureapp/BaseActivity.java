package com.example.essamik.measureapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.essamik.measureapp.altbeacon.AltBeaconRangingActivity;
import com.example.essamik.measureapp.estimote.EstimoteRangingActivity;
import com.example.essamik.measureapp.kontaktIO.KontaktIORangingActivity;

public abstract class BaseActivity extends AppCompatActivity implements BeaconListFragment.OnBeaconSelectedListener {
    protected BeaconAdapter mBeaconAdapter;
    protected Toolbar mToolBar;
    protected MeasureFragment mMeasureFragment;

    protected abstract void onRegionChange(MyBeacon beacon);
    protected abstract void startRangingAll();
    protected abstract String getLibraryName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mBeaconAdapter = new BeaconAdapter(this, mToolBar);

        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, BeaconListFragment.newInstance(mBeaconAdapter, this)).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            switch (item.getItemId()) {
                case R.id.action_switchto_altbeacon:  item.setVisible((!(this instanceof AltBeaconRangingActivity)));
                    break;
                case R.id.action_switchto_estimote: item.setVisible(!(this instanceof EstimoteRangingActivity));
                    break;
                case R.id.action_switchto_kontaktio:  item.setVisible(!(this instanceof  KontaktIORangingActivity));
                    break;
                default:
                    break;

            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switchto_altbeacon) {
                startActivity(new Intent(this, AltBeaconRangingActivity.class));
        } else if (item.getItemId() == R.id.action_switchto_estimote) {
            startActivity(new Intent(this, EstimoteRangingActivity.class));
        } else if (item.getItemId() == R.id.action_switchto_kontaktio) {
            startActivity(new Intent(this, KontaktIORangingActivity.class));
        }

        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconSelected(MyBeacon beacon) {
        //Change region and update detail view
        onRegionChange(beacon);
        mMeasureFragment = MeasureFragment.newInstance(this, beacon, getLibraryName());
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_layout, mMeasureFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMeasureFragment = null;
        startRangingAll();
    }
}
