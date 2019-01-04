package cn.luoares.diary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    public static List<ListInformation> dairyList;
    private Button cancle;
    private Button create;

    public static final String Intent_key_edit = "MESSAGE_Edit";
    public static final String Intent_key_view = "MESSAGE_View";
    public static final String dairyDir = Environment.getExternalStorageDirectory() + "/diary/";
    public static final String dairyFile =
            Environment.getExternalStorageDirectory() + "/diary/diary.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyWritePermission();
        ActivityCollector.addActivity(this);

        if(null == dairyList) {
            initDairy();
        }
        DiaryAdapter diaryAdapter = new DiaryAdapter(MainActivity.this, R.layout.initlist, dairyList);
        listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(diaryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra(MainActivity.Intent_key_view, position);
                startActivityForResult(intent,0);
            }
        });
        diaryAdapter.notifyDataSetChanged();
        cancle = (Button) findViewById(R.id.main_out);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
            }
        });
        create = (Button) findViewById(R.id.main_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                int number = -1;
                intent.putExtra(Intent_key_edit,number);
                startActivityForResult(intent,0);
            }
        });
    }

    public void applyWritePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权 DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void initDairy() {
        applyWritePermission();
        File file = new File(dairyDir);
        if (!file.exists()) {
            file.mkdir();
        }
        File writeName = new File(dairyFile);
        try {
            if(!writeName.exists())
                writeName.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        dairyList = new ArrayList<>();
        readDiary();
    }

    public void readDiary() {
        try {
            FileReader reader = new FileReader(dairyFile);
            BufferedReader br = new BufferedReader(reader);
            while (true) {
                String firstline = br.readLine();
                if (null == firstline)
                    break;
                ListInformation listInformation = new ListInformation();
                listInformation.setDayNumber(firstline);
                Log.d("dayNumber: ", firstline);
                listInformation.setDate(br.readLine());
                Log.d("date: ", listInformation.getDate());
                listInformation.setImg1(br.readLine().trim());
                Log.d("img1: ", listInformation.getImg1());
                listInformation.setImg2(br.readLine().trim());
                Log.d("img2: ", listInformation.getImg2());
                listInformation.setImg3(br.readLine().trim());
                Log.d("img3: ", listInformation.getImg3());
                listInformation.setOther(br.readLine());
                Log.d("other: ", listInformation.getOther());
                listInformation.setDayDate(br.readLine());
                Log.d("other: ", listInformation.getDayDate());
                listInformation.setAdd(Boolean.parseBoolean(br.readLine()));
                Log.d("add: ", listInformation.getAdd());
                listInformation.setLastTime(br.readLine());
                Log.d("lastTime: ", listInformation.getLastTime());
                int numofPictures = Integer.parseInt(br.readLine());
                for (int i = 0; i < numofPictures; i++) {
                    listInformation.pictureFiles.add(br.readLine());
                    Log.d("img: ", listInformation.pictureFiles.get(i));
                }
                listInformation.setNextNumerofPicture(Integer.parseInt(br.readLine()));
                Log.d("nextNumberofPicture: ", listInformation.getNextNumerofPicture());
                listInformation.setRandomNumber(Integer.parseInt(br.readLine()));
                Log.d("randomNumber: ", listInformation.getRandomNumber());
                String eachline;
                String txt = "";
                while(!(eachline = br.readLine()).equals("**@@end---8lkhljcvbn88^^^^$$**")) {
                    txt = txt + eachline + "\n";
                }
                txt = txt.trim();
                listInformation.setTxt(txt);
                Log.d("txt: ", listInformation.getTxt());
                dairyList.add(listInformation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
