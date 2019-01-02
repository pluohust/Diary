package cn.luoares.diary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    Button edit_storeTitleBt;
    TextView edit_txtTitleTV;
    Button edit_cancleTitleBt;
    ScrollView edit_scrollSV;
    EditText edit_notewordET;
    GridView edit_gridGV;

    private static int IMAGE_REQUEST_CODE =2;
    private final String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/diary/";
    private final int MaxSize = 8;
    private static final String Intent_key="MESSAGE";

    private int[] imageIds = new int[] {
            R.mipmap.image_add
    };
    private List<Map<String, Object>> listItems;
    private SimpleAdapter simpleAdapter;     //适配器
    private DiaryInformation todayDiary; //记录信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        applyWritePermission();

        Intent intent =getIntent();
        String filePath =intent.getStringExtra(ViewActivity.Intent_key);
        todayDiary = new DiaryInformation();
        todayDiary.readFromFile(filePath);


        edit_storeTitleBt = (Button) findViewById(R.id.edit_store);
        edit_txtTitleTV = (TextView) findViewById(R.id.edit_textDate);
        edit_cancleTitleBt = (Button) findViewById(R.id.edit_cancle);
        edit_scrollSV = (ScrollView) findViewById(R.id.edit_scrollview);
        edit_notewordET = (EditText) findViewById(R.id.edit_NoteWord);
        edit_gridGV = (GridView) findViewById(R.id.edit_grid_picture);

        edit_txtTitleTV.setText(todayDiary.getLastTime());
        edit_notewordET.setText(todayDiary.getTxt());
        listItems = new ArrayList<Map<String, Object>>();
        if(todayDiary.isAdd()) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", R.mipmap.image_add);
            listItems.add(listItem);
        }
        for(int i = 0; i < todayDiary.pictureFiles.size(); i++) {
            Bitmap addbmp=BitmapFactory.decodeFile(todayDiary.pictureFiles.get(i));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", addbmp);
            listItems.add(map);
        }
        SimpleAdapter simpleAdapter = simpleAdapter = new SimpleAdapter(this,
                listItems, R.layout.cell,
                new String[] { "image"}, new int[] { R.id.image1});
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
        edit_gridGV.setAdapter(simpleAdapter);
        edit_scrollSV.smoothScrollTo(0,0);
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
}
