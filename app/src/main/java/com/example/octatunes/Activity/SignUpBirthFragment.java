package com.example.octatunes.Activity;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpBirthFragment extends Fragment {
    LoginActivity main; Context context; Bundle args;
    LinearLayout layout; DatePicker datePicker;
    Button btnNext; ImageButton btnPrev;

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
        layout = (LinearLayout) inflater.inflate(R.layout.layout_signup_birth,null);
        datePicker = layout.findViewById(R.id.datePicker);

        Calendar today = Calendar.getInstance();
        datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                null
        );

        btnNext = layout.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long birthdayInMillis = getBirth();
                args.putLong("birth", birthdayInMillis);

                Fragment f = new SignUpNameFragment();
                f.setArguments(args);
                main.NavigateFragment(f);
            }
        });
        btnPrev = layout.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long birthdayInMillis = getBirth();
                args.putLong("birth", birthdayInMillis);

                main.onBackPressed();
            }
        });
        return layout;
    }
    public long getBirth(){
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);
        return selectedDate.getTimeInMillis();
    }
}
