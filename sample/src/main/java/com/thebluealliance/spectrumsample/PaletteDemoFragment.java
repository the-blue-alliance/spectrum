package com.thebluealliance.spectrumsample;

import com.thebluealliance.spectrum.SpectrumPalette;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PaletteDemoFragment extends Fragment implements SpectrumPalette.OnColorSelectedListener{
    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_demo, container, false);
        SpectrumPalette spectrumPalette = (SpectrumPalette) v.findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);
        return v;
    }

    @Override public void onColorSelected(@ColorInt int color) {
        Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
    }
}
