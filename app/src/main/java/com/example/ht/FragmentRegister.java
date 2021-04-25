package com.example.ht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentRegister extends Fragment {

    EditText editRegisterPassword;
    EditText editRegisterUserName;
    EditText editRegisterEmail;
    EditText editRegisterRank;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        editRegisterUserName = view.findViewById(R.id.editRegisterUserName);
        editRegisterPassword = view.findViewById(R.id.editRegisterPassword);
        editRegisterEmail = view.findViewById(R.id.editRegisterEmail);
        editRegisterRank = view.findViewById(R.id.editRegisterRank);
    }

    public String getUserName(){
        return editRegisterUserName.getText().toString();
    }

    public String getPassword(){
        return editRegisterPassword.getText().toString();
    }

    public String getEmail(){
        return editRegisterEmail.getText().toString();
    }

    public int getRank(){
        int rank;
        try{
            rank = Integer.parseInt(editRegisterRank.getText().toString());
        } catch (NumberFormatException e){
            rank = 1;
        }
        System.out.println(rank);
        return rank;
    }
}
