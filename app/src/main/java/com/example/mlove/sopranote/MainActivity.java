package com.example.mlove.sopranote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText numOne = (EditText) findViewById(R.id.numOne);
        EditText numTwo = (EditText) findViewById(R.id.numTwo);
        Button Addbtn = (Button) findViewById(R.id.Addbtn);
        TextView result = (TextView) findViewById(R.id.result);
        Addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num1 = Integer.parseInt(numOne.getText().toString());
                int num2 = Integer.parseInt(numTwo.getText().toString());
                int added = num1 + num2;
                result.setText(added + "");

            }
        });
    }
}
