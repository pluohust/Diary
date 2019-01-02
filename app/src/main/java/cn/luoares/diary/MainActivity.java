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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    private static int IMAGE_REQUEST_CODE =2;
    private final String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/diary/";
    private final int MaxSize = 8;
    public  static  final String Intent_key="MESSAGE_Main";
    EditText editText;
    GridView grid;
    int[] imageIds = new int[] {
            R.mipmap.image_add
    };
    List<Map<String, Object>> listItems;
    private SimpleAdapter simpleAdapter;     //适配器

    DiaryInformation todayDiary; //今日记录信息

    TextView titleText;
    Button saveAndSee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);

        applyWritePermission();
        initOpts();

        editText = (EditText) findViewById(R.id.NoteWord);

        titleText = (TextView) findViewById(R.id.textDate);
        titleText.setText(todayDiary.getLastTime());
        saveAndSee = (Button) findViewById(R.id.store);
        saveAndSee.setOnClickListener(new View.OnClickListener() { //将文件传输到显式activity
            @Override
            public void onClick(View v) {
                todayDiary.storeTxt(editText.getText().toString());
                todayDiary.saveToFile();
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                String text = todayDiary.getTxtFile();
                intent.putExtra(Intent_key,text);
                startActivityForResult(intent,0);
            }
        });

        listItems =
                new ArrayList<Map<String, Object>>();
        for (int i=0; i<imageIds.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }
        grid = (GridView) findViewById(R.id.grid_picture);
        simpleAdapter = new SimpleAdapter(this,
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
        grid.setAdapter(simpleAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((listItems.size() < MaxSize) && (position==0)){
                    showDialog();
                } else if((listItems.size() == MaxSize) && (position == 0) && todayDiary.isAdd()) {
                    todayDiary.setAdd(false);
                    listItems.remove(0);
                    showDialog();
                }
            }
        });
    }

    //初始化文件工作
    public void initOpts() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("错误")
                    .setMessage("无法存储文件")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create();
            alertDialog.show();
        }
        File file = new File(IMAGE_DIR);
        if(!file.exists()) {
            file.mkdir();
        }

        //获取今天日期
        Date nowData = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        todayDiary = new DiaryInformation();
        todayDiary.storeDate(simpleDateFormat.format(nowData));
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

    void CompressPicture(String inpath, String outpath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bm = BitmapFactory.decodeFile(inpath, options);
        File file = new File(outpath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void showDialog() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    if (new File(path).exists()) {
                        Log.d("images", "源文件存在" + path);
                    } else {
                        Log.d("images", "源文件不存在" + path);
                        return;
                    }
                    String inPath = path;
                    String outPath = todayDiary.getNextPictureFileName();
                    CompressPicture(inPath, outPath);
                    if(!(new File(outPath).exists())) {
                        Log.d("图片不存在: ", outPath);
                        return;
                    }
                    Bitmap addbmp=BitmapFactory.decodeFile(outPath);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("image", addbmp);
                    listItems.add(map);
                    simpleAdapter = new SimpleAdapter(this,
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
                    grid.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
