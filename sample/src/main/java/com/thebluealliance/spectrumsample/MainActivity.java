package com.thebluealliance.spectrumsample;

import com.thebluealliance.spectrum.SpectrumDialog;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        //findViewById(R.id.open_dialog).setOnClickListener(this);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Fragment(), "Dialogs");
        adapter.addFrag(new PreferencesDemoFragment(), "Preferences");
        adapter.addFrag(new Fragment(), "Color Picker");
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(this);
        builder.setTitle("Select a color");
        builder.setColors(new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.BLACK});
        builder.setSelectedColor(Color.GREEN);
        builder.setDismissOnColorSelected(false);
        builder.setColorSelectedListener(new SpectrumDialog.ColorPickerListener() {
            @Override
            public void onColorPickerResult(int resultCode, @ColorInt int color) {
                if (resultCode == SpectrumDialog.ColorPickerListener.POSITIVE) {
                    Toast.makeText(MainActivity.this, "Color selected! " + color, Toast.LENGTH_SHORT).show();
                } else if (resultCode == SpectrumDialog.ColorPickerListener.NEGATIVE) {
                    Toast.makeText(MainActivity.this, "Color selection cancelled!", Toast.LENGTH_SHORT).show();
                }
            }
        }).build().show(getSupportFragmentManager(), "dialog");
    }
}
