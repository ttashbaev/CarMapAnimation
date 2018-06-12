package com.example.timur.carmapanimation.ui;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.timur.carmapanimation.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mLlBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Button btnStart, btnStop;
    EditText mEtGetLocat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtGetLocat = findViewById(R.id.etGetLocat);

        mLlBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mLlBottomSheet);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                Toast.makeText(this,"sdasdasdsadasd",Toast.LENGTH_SHORT).show();
                //
                break;
            case R.id.btnStop:
                Toast.makeText(this,"sdasdasdsadasd232232323",Toast.LENGTH_SHORT).show();
                //
                break;
        }

    }
}
