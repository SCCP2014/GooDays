package org.misoton.goodays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.misoton.goodays.weather.Forecast;

import java.util.ArrayList;
import java.util.Locale;

public class SpotWeatherInfoFragment extends Fragment {

    HorizontalScrollView weathers_sv;
    Forecast forecast;
    LinearLayout weathers_ll;
    ArrayList<String> weatherList;
    ArrayList<String> dateList;

    public SpotWeatherInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spot_weather_info, container, false);
        Bundle bundle = getArguments();

        weatherList = bundle.getStringArrayList("weather");
        dateList = bundle.getStringArrayList("date");

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.spot_pager);
        viewPager.setAdapter(new SectionsPagerAdapter(this.getChildFragmentManager()));

        return v;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("weather", weatherList.get(position));
            bundle.putString("date", dateList.get(position));
            Fragment fragment = new WeatherFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return dateList.size();
        }
    }


}
