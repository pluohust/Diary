package cn.luoares.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewActivity extends AppCompatActivity {
    TextView view_txt;
    GridView view_grid;
    Button view_titleButton;
    Button view_titleEdit;
    TextView view_titleTxt;
    ScrollView view_scroll;

    private int getNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ActivityCollector.addActivity(this);

        view_txt = (TextView) findViewById(R.id.view_NoteWord);
        view_grid = (GridView) findViewById(R.id.view_grid_picture);
        view_titleButton = (Button) findViewById(R.id.view_return);
        view_titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                startActivityForResult(intent,0);
            }
        });
        view_titleTxt = (TextView) findViewById(R.id.view_textDate);
        view_scroll = (ScrollView) findViewById(R.id.view_scrollview);
        view_titleEdit = (Button) findViewById(R.id.view_edit);
        view_titleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra(MainActivity.Intent_key_edit, getNumber);
                startActivityForResult(intent,0);
            }
        });

        Intent intent =getIntent();
        getNumber = intent.getIntExtra(MainActivity.Intent_key_view, -1);
        if (-1 == getNumber) {
            Toast.makeText(ViewActivity.this, "传输出现错误，显式第一个日记", Toast.LENGTH_SHORT).show();
            getNumber = 0;
        }
        ListInformation listInformation = MainActivity.dairyList.get(getNumber);

        view_txt.setText(listInformation.getTxt());
        view_titleTxt.setText(listInformation.getLastTime());
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        List<String> listImg = new ArrayList<>();
        for(int i = 0; i < listInformation.pictureFiles.size(); i++) {
            File fileImg = new File(listInformation.pictureFiles.get(i));
            if (fileImg.exists()) {
                Bitmap addbmp=BitmapFactory.decodeFile(listInformation.pictureFiles.get(i));
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("image", addbmp);
                listItems.add(map);
                listImg.add(listInformation.pictureFiles.get(i));
            }
        }
        listInformation.pictureFiles.clear();
        listInformation.pictureFiles.addAll(listImg);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                listItems, R.layout.viewcell,
                new String[] { "image"}, new int[] { R.id.view_image1});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        view_grid.setAdapter(simpleAdapter);
        view_scroll.smoothScrollTo(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            startActivityForResult(intent,0);
        }
        return false;
    }
}
