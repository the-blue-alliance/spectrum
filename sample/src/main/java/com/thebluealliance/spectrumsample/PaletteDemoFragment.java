package com.thebluealliance.spectrumsample;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thebluealliance.spectrum.SpectrumPalette;

public class PaletteDemoFragment extends Fragment implements SpectrumPalette.OnColorSelectedListener{
    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_demo, container, false);
        SpectrumPalette spectrumPalette = (SpectrumPalette) v.findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);
        return v;
    }

    @Override public void onColorSelected(@ColorInt int color) {
        Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
    }
}
