package com.example.ht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDayNone extends Fragment {

    TextView textDay;
    TextView textDate;
    TextView textOmnivore;
    TextView textVegan;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_day_none, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        textDay = view.findViewById(R.id.textDayTitle);
        textDate = view.findViewById(R.id.textDayNoneDate);
        textOmnivore = view.findViewById(R.id.textDayOmnivore);
        textVegan = view.findViewById(R.id.textDayVegan);
        textDay.setText(getArguments().getString("dayName"));
        textDate.setText(getArguments().getString("dayDate"));
        textOmnivore.append(getArguments().getString("foodInfoOmnivore"));
        textVegan.append(getArguments().getString("foodInfoVegan"));
    }
}
