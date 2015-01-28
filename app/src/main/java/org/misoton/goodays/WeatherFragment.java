package org.misoton.goodays;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);

        Bundle bundle = this.getArguments();

        TextView date_tv = (TextView) v.findViewById(R.id.weather_date_tv);
        date_tv.setText(bundle.getString("date"));

        TextView weather_tv = (TextView) v.findViewById(R.id.weather_weather_tv);
        weather_tv.setText(bundle.getString("weather"));

        return v;
    }


}
