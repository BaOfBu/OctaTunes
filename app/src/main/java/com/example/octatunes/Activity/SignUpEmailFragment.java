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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpEmailFragment extends Fragment {
    LoginActivity main; Context context; Bundle args;
    LinearLayout layout;
    Button btnNext; EditText edtEmail; ImageButton btnPrev;

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
        layout = (LinearLayout) inflater.inflate(R.layout.layout_signup_email,null);
        edtEmail = layout.findViewById(R.id.edtEmail);

        btnNext = layout.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                if(email.equals("")) {
                    Toast.makeText(context.getApplicationContext(), "Enter Email",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(!validateEmail(email)){
                    Toast.makeText(context.getApplicationContext(), "Invalid Email",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                args.putString("email", email);

                Fragment f = new SignUpPasswordFragment();
                f.setArguments(args);
                main.NavigateFragment(f);
            }
        });
        btnPrev = layout.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onBackPressed();
            }
        });
        return layout;
    }
    public boolean validateEmail(String email){
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
