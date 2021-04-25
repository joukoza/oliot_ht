package com.example.ht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentSignInUser extends Fragment {

    private TextView textUserName;
    private TextView textUserRank;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_sign_in_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        textUserName = view.findViewById(R.id.textSignInUserName);
        textUserRank = view.findViewById(R.id.textSignInUserRank);

        textUserName.setText(getArguments().getString("userName"));

        int userRank = getArguments().getInt("userRank");

        if (userRank == 1){
            textUserRank.setText("Normal user");
        }
        else if (userRank == 2){
            textUserRank.setText("Moderator user");
        }
        else{
            textUserRank.setText("Admin user");
        }
    }

}
