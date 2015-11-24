package com.example.domocha.diaryproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView textDate;
    Button btnSave;
    EditText edtContents;
    String fileName;
    int year, month, day;
    String strSDPath;
    String strDiaryDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("[LOG]", "[LOG] - Start Application -");

        setContentView(R.layout.activity_main);
        setTitle("Diary");
        android.util.Log.d("[LOG]", "[LOG] - Set Content View -");

        textDate = (TextView) findViewById(R.id.TextDate);
        btnSave = (Button) findViewById(R.id.BtnSave);
        edtContents = (EditText) findViewById(R.id.EdtContents);
        android.util.Log.d("[LOG]", "[LOG] - Find View By ID -");

        createDir();

        year = CurrentDate.getInstance().getCurrentYear();
        month = CurrentDate.getInstance().getCurrentMonth();
        day = CurrentDate.getInstance().getCurrentDay();
        android.util.Log.d("[LOG]", "[LOG] - Get Current Date -");

        textDate.setText(getTextDateStr(year, month, day));
        fileName = getFileNameStr(year, month, day);
        edtContents.setText(readDiary(fileName));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream outFs = new FileOutputStream(strDiaryDirPath + "/" + fileName);
                    String str = edtContents.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + "이 저장됨", Toast.LENGTH_SHORT).show();
                    android.util.Log.d("[LOG]", "[LOG] - Save File : " + fileName + " -");
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "저장 실패", Toast.LENGTH_LONG).show();
                    android.util.Log.d("[LOG]", "[LOG] - <FAIL> Save File : " + fileName + " -");
                }
            }
        });


        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int tempYear = year;
                final int tempMonth = month;
                final int tempDay = day;

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        MainActivity.this.year = year;
                        month = monthOfYear + 1;
                        day = dayOfMonth;

                        textDate.setText(getTextDateStr(MainActivity.this.year, month, day));
                        fileName = getFileNameStr(MainActivity.this.year, month, day);
                        edtContents.setText(readDiary(fileName));
                        android.util.Log.d("[LOG]", "[LOG] - Select Date -");
                    }
                }, year, month - 1, day);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        year = tempYear;
                        month = tempMonth;
                        day = tempDay;
                        android.util.Log.d("[LOG]", "[LOG] - Cancel Date Picker -");
                    }
                });
                dialog.setTitle("날짜 선택");
                dialog.show();
                android.util.Log.d("[LOG]", "[LOG] - Create Date Picker -");
            }
        });
    }

    private void createDir() {
        try {
            strSDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            strDiaryDirPath = strSDPath + "/mydiary";
            File dir = new File(strDiaryDirPath);
            if(!dir.exists())
                dir.mkdir();
            android.util.Log.d("[LOG]", "[LOG] - Create Dir : " + strDiaryDirPath + " -");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "mydiary 디렉터리 생성 실패", Toast.LENGTH_LONG).show();
            android.util.Log.d("[LOG]", "[LOG] - <FAIL> Create Dir : " + strDiaryDirPath + " -");
        }
    }

    private String getFileNameStr(int year, int month, int day) {
        android.util.Log.d("[LOG]", "[LOG] - Get File Name String -");
        return Integer.toString(year) + "_" + Integer.toString(month) + "_" + Integer.toString(day) + ".txt";
    }

    private String getTextDateStr(int year, int month, int day) {
        android.util.Log.d("[LOG]", "[LOG] - Get Text Date String -");
        return Integer.toString(year) + "년" + Integer.toString(month) + "월" + Integer.toString(day) + "일";
    }

    private String readDiary(String fileName) {
        String diaryStr = null;
        FileInputStream inFs;
        try {
            inFs = new FileInputStream(strDiaryDirPath + "/" + fileName);
            byte[] txt = new byte[1024];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
            android.util.Log.d("[LOG]", "[LOG] - Read File : " + fileName + " -");
        } catch (IOException e) {
            edtContents.setHint("일기 없음");
            android.util.Log.d("[LOG]", "[LOG] - <FAIL> Read File : " + fileName + " -");
        }
        return diaryStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        android.util.Log.d("[LOG]", "[LOG] - Create Option Menu -");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuItemExit:
                textDate.setText(getTextDateStr(year, month, day));
                fileName = getFileNameStr(year, month, day);
                edtContents.setText(readDiary(fileName));
                android.util.Log.d("[LOG]", "[LOG] - Select Menu Item : Exit -");
                return true;

            case R.id.MenuItemDelete:
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("일기 삭제");
                dlg.setMessage(getTextDateStr(year, month, day) + " 일기를 삭제하시겠습니까?");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String fileChk = getFileNameStr(year, month, day);
                            File file = new File(strDiaryDirPath + "/" + fileChk);
                            if (file.exists()) {
                                file.delete();
                                edtContents.setText("");
                                Toast.makeText(getApplicationContext(), "파일 삭제 성공", Toast.LENGTH_SHORT).show();
                                android.util.Log.d("[LOG]", "[LOG] - Delete File : " + fileName + " -");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "파일 삭제 실패", Toast.LENGTH_LONG).show();
                            android.util.Log.d("[LOG]", "[LOG] - <FAIL> Delete File : " + fileName + " -");
                        }
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
                android.util.Log.d("[LOG]", "[LOG] - Select Menu Item : Delete -");
                return true;

            case R.id.TextSizeItemLarge:
                edtContents.setTextSize(25);
                android.util.Log.d("[LOG]", "[LOG] - Select Menu Item : TextSize -> Large -");
                return true;

            case R.id.TextSizeItemRegular:
                edtContents.setTextSize(15);
                android.util.Log.d("[LOG]", "[LOG] - Select Menu Item : TextSize -> Regular -");
                return true;

            case R.id.TextSizeItemSmall:
                edtContents.setTextSize(5);
                android.util.Log.d("[LOG]", "[LOG] - Select Menu Item : TextSize -> Small -");
                return true;
        }
        return false;
    }
}
