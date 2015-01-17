package org.misoton.goodays;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button map = (Button) this.findViewById(R.id.main_intent_map_bt);
        map.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_intent_map_bt:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
            default:
        }
    }
}
