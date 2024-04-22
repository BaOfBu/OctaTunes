package com.example.octatunes.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.R;

public class SignUpPasswordFragment extends Fragment {
    LoginActivity main; Context context; Bundle args;
    LinearLayout layout;
    Button btnNext; EditText edtPass; ImageButton btnPrev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            context = getActivity();
            main = (LoginActivity) (getActivity());
            args = getArguments();
        }
        catch (IllegalStateException e){
            throw new IllegalStateException("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState ){
        layout = (LinearLayout) inflater.inflate(R.layout.layout_signup_password,null);
        edtPass = layout.findViewById(R.id.edtPass);
        btnNext = layout.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edtPass.getText().toString();
                if(pass.equals("")) {
                    Toast.makeText(context.getApplicationContext(), "Enter Password",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass.length() < 10){
                    Toast.makeText(context.getApplicationContext(), "Invalid Password",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                args.putString("password", pass);

                Fragment f = new SignUpBirthFragment();
                f.setArguments(args);
                main.NavigateFragment(f);
            }
        });
        btnPrev = layout.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edtPass.getText().toString();
                args.putString("password", pass);
                main.onBackPressed();
            }
        });
        return layout;
    }
}