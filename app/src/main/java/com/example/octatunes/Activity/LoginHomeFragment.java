package com.example.octatunes.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.R;

public class LoginHomeFragment extends Fragment {
    LoginActivity main; Context context;
    RelativeLayout layout;
    Button btnEmail, btnPhone, btnGoogle, btnFacebook, btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            context = getActivity();
            main = (LoginActivity) (getActivity());
        }
        catch (IllegalStateException e){
            throw new IllegalStateException("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState ){
        layout = (RelativeLayout) inflater.inflate(R.layout.layout_login_home,null);
        btnEmail = layout.findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new SignUpEmailFragment();
                f.setArguments(new Bundle());
                main.NavigateFragment(f);
            }
        });
        btnPhone = layout.findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new SignUpPhoneFragment();
                f.setArguments(new Bundle());
                main.NavigateFragment(f);
            }
        });
        btnLogin = layout.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new LoginFormFragment();
                main.NavigateFragment(f);
            }
        });
        return layout;
    }
}
