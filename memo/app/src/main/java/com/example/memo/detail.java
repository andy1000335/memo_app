package com.example.memo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

@RequiresApi(api = Build.VERSION_CODES.N)
public class detail extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnLongClickListener, DialogInterface.OnClickListener {

    EditText etTitle;
    EditText etText;
    TextView tvRemind;
    Calendar c = Calendar.getInstance();
    String deadline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra("標題");
        String content = intent.getStringExtra("備忘");
        deadline = intent.getStringExtra("死線");

        etTitle = findViewById(R.id.et_title);
        etText = findViewById(R.id.et_text);
        tvRemind = findViewById(R.id.tv_remind);

        etTitle.setText(title);
        etText.setText(content);
        tvRemind.setText(deadline);

        tvRemind.setOnLongClickListener(this);
    }

    public void onCancel(android.view.View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onSave(android.view.View v) {
        Intent intent2 = new Intent();
        intent2.putExtra("標題", etTitle.getText().toString());
        intent2.putExtra("備忘", etText.getText().toString());
        intent2.putExtra("死線", deadline);

        setResult(RESULT_OK, intent2);
        finish();
    }

    public void onRemind(android.view.View v) {
        new DatePickerDialog(this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        deadline = (i+"/"+(i1+1)+"/"+i2);
        new TimePickerDialog(this, this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        tvRemind.setText(deadline);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        deadline += ("\n"+i+":"+i1);
        tvRemind.setText(deadline);
    }

    @Override
    public boolean onLongClick(View view) {
        new AlertDialog.Builder(this)
                .setMessage("是否要清除時間?")
                .setNegativeButton("否", this)
                .setPositiveButton("是", this)
                .show();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            deadline = "";
            tvRemind.setText("");
        }
        else if (i == DialogInterface.BUTTON_NEGATIVE){

        }
    }
}
