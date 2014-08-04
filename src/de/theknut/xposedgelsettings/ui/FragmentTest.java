package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.theknut.xposedgelsettings.R;

@SuppressLint("WorldReadableFiles")
public class FragmentTest extends FragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.test, container, false);


        ImageView clock = (ImageView) rootView.findViewById(R.id.clock);
        clock.setBackgroundResource(R.drawable.clock);

        String nextAlarm = Settings.System.getString(mContext.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);

        float radius = 0.0f;
        //float x = (float)(radius * Math.Cos(angleInDegrees * Math.PI / 180F))

        return rootView;
    }
}