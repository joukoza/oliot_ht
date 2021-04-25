package com.example.ht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentSignInNone extends Fragment {

    private EditText editPassword;
    private EditText editUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_sign_in_none, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        editUserName = view.findViewById(R.id.editSignInUserName);
        editPassword = view.findViewById(R.id.editSignInPassword);

        editUserName.setText("");
        editPassword.setText("");
    }

    public String getUserName(){
        return editUserName.getText().toString();
    }

    public String getPassword(){
        return editPassword.getText().toString();
    }
}
