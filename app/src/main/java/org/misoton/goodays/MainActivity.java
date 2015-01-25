package org.misoton.goodays;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddressHistoryManager.init(this);

        TextView test_tb = (TextView) this.findViewById(R.id.main_test_tb);

        long unixtime = System.currentTimeMillis() / 1000;

        Log.d("Main", "" + unixtime);

        long avavvavav = 1422151200;

        Date date = new Date(avavvavav * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日H時m分s秒");
        String datest = format.format(date);
        Toast.makeText(this, datest + " " + unixtime, Toast.LENGTH_LONG).show();

        test_tb.setText("" + unixtime + "\n" + 1422151200);

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
