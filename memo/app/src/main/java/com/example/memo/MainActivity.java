package com.example.memo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {

    SimpleCursorAdapter adapter;
    ListView lvList;
    SQLiteDatabase dataBase;
    Cursor cursor;
    String DB_NAME = "myDataBase";
    String TB_NAME = "myTable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        String creatTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title String, content String, contentPrint String, titlePrint String, deadline String)";
        dataBase.execSQL(creatTable);
        cursor = dataBase.rawQuery("SELECT * FROM " + TB_NAME, null);

        lvList = findViewById(R.id.lv_list);
        adapter = new SimpleCursorAdapter(this, R.layout.item, cursor,  new String[] {"titlePrint", "contentPrint", "deadline"}, new int[] {R.id.tv_title, R.id.tv_content, R.id.tv_deadline});
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(this);
        lvList.setOnItemLongClickListener(this);

    }

    public void onAdd(android.view.View v) {
        if ((cursor.getCount() != 0)&&(cursor.getString(cursor.getColumnIndex("title")).equals(""))&&(cursor.getString(cursor.getColumnIndex("content")).equals("")))
            Toast.makeText(this, "已新增空白清單", Toast.LENGTH_SHORT).show();
        else {
            ContentValues cv = new ContentValues(3);
            cv.put("title", "");
            cv.put("content", "");
            cv.put("deadline", "");
            dataBase.insert(TB_NAME, null, cv);
            cursor = dataBase.rawQuery("SELECT * FROM " + TB_NAME, null);
            adapter.changeCursor(cursor);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, detail.class);
        intent.putExtra("標題", cursor.getString(cursor.getColumnIndex("title")));
        intent.putExtra("備忘", cursor.getString(cursor.getColumnIndex("content")));
        intent.putExtra("死線", cursor.getString(cursor.getColumnIndex("deadline")));

        startActivityForResult(intent, i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        new AlertDialog.Builder(this)
                .setMessage("確定要刪除此備忘錄嗎?")
                .setNegativeButton("取消", this)
                .setPositiveButton("確認", this)
                .show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent it) {
        if (resultCode == RESULT_OK) {
            String content = it.getStringExtra("備忘");
            String title = it.getStringExtra("標題");

            int countLine = 0;
            double count = 0;

            String msgTitle = "";
            for (int n=0; n<title.length(); n++) {
                msgTitle += title.charAt(n);
                if ((31<(int)title.charAt(n))&&((int)title.charAt(n)<127))
                    count+=0.7;
                else
                    count++;
                if (count>12) {
                    msgTitle += "...";
                    break;
                }
            }

            String msg = "";
            count = 0;
            for (int n=0; n<content.length(); n++) {
                msg += content.charAt(n);
                if ((31<(int)content.charAt(n))&&((int)content.charAt(n)<127))
                    count+=0.5;
                else
                    count++;
                if ((int)content.charAt(n) == 10)
                    countLine++;
                if ((countLine==3)||(count>67)) {
                    msg += "...";
                    break;
                }
            }

            ContentValues cv = new ContentValues(6);
            cv.put("_id", (requestCode+1));
            cv.put("title", it.getStringExtra("標題"));
            cv.put("titlePrint", msgTitle);
            cv.put("content", it.getStringExtra("備忘"));
            cv.put("contentPrint", msg);
            cv.put("deadline", it.getStringExtra("死線"));
            dataBase.update(TB_NAME, cv, "_id="+cursor.getInt(0), null);
            cursor = dataBase.rawQuery("SELECT * FROM " + TB_NAME, null);
            adapter.changeCursor(cursor);

        }

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            dataBase.delete(TB_NAME, "_id="+cursor.getInt(0), null);
            cursor = dataBase.rawQuery("SELECT * FROM " + TB_NAME, null);
            adapter.changeCursor(cursor);

        }
        else if (i == DialogInterface.BUTTON_NEGATIVE){

        }
    }
}
