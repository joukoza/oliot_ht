package com.example.ht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentStatsUser extends Fragment {

    ListView listView;
    ArrayList<String> entries;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_stats_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        entries = getArguments().getStringArrayList("entries");

        ArrayAdapter<String> entryAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, entries);

        listView = (ListView) view.findViewById(R.id.listEntry);
        listView.setAdapter(entryAdapter);

    }
}
