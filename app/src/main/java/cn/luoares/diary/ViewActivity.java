package cn.luoares.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewActivity extends AppCompatActivity {
    DiaryInformation todayDiary;
    TextView view_txt;
    GridView view_grid;
    Button view_titleButton;
    Button view_titleEdit;
    TextView view_titleTxt;
    ScrollView view_scroll;

    public static final String Intent_key="MESSAGE_View";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        view_txt = (TextView) findViewById(R.id.view_NoteWord);
        view_grid = (GridView) findViewById(R.id.view_grid_picture);
        view_titleButton = (Button) findViewById(R.id.view_return);
        view_titleTxt = (TextView) findViewById(R.id.view_textDate);
        view_scroll = (ScrollView) findViewById(R.id.view_scrollview);
        view_titleEdit = (Button) findViewById(R.id.view_edit);
        view_titleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                String text = todayDiary.getTxtFile();
                intent.putExtra(Intent_key,text);
                startActivityForResult(intent,0);
            }
        });

        Intent intent =getIntent();
        String filePath =intent.getStringExtra(MainActivity.Intent_key);
        todayDiary = new DiaryInformation();
        todayDiary.readFromFile(filePath);

        view_txt.setText(todayDiary.getTxt());
        view_titleTxt.setText(todayDiary.getLastTime());
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < todayDiary.pictureFiles.size(); i++) {
            Bitmap addbmp=BitmapFactory.decodeFile(todayDiary.pictureFiles.get(i));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", addbmp);
            listItems.add(map);
        }
        SimpleAdapter simpleAdapter = simpleAdapter = new SimpleAdapter(this,
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
}
