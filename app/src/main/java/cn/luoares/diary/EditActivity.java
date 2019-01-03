package cn.luoares.diary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static cn.luoares.diary.MainActivity.dairyFile;

public class EditActivity extends AppCompatActivity {
    Button edit_storeTitleBt;
    TextView edit_txtTitleTV;
    Button edit_cancleTitleBt;
    ScrollView edit_scrollSV;
    EditText edit_notewordET;
    GridView edit_gridGV;

    private static int IMAGE_REQUEST_CODE =2;
    private final String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/diary/";
    public final int MaxSize = 8;

    private int[] imageIds = new int[] {
            R.mipmap.image_add
    };
    private List<Map<String, Object>> listItems;
    private SimpleAdapter simpleAdapter;     //适配器
    private ListInformation listInformation; //记录信息

    private int getNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActivityCollector.addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);

        edit_storeTitleBt = (Button) findViewById(R.id.edit_store);
        edit_txtTitleTV = (TextView) findViewById(R.id.edit_textDate);
        edit_cancleTitleBt = (Button) findViewById(R.id.edit_cancle);
        edit_scrollSV = (ScrollView) findViewById(R.id.edit_scrollview);
        edit_notewordET = (EditText) findViewById(R.id.edit_NoteWord);
        edit_gridGV = (GridView) findViewById(R.id.edit_grid_picture);

        Intent intent =getIntent();
        getNumber = intent.getIntExtra(MainActivity.Intent_key_edit, -1);

        if(-1 == getNumber) {
            listInformation = new ListInformation();
        } else {
            listInformation = new ListInformation();
            listInformation = MainActivity.dairyList.get(getNumber);
        }

        edit_storeTitleBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listInformation.setTxt(edit_notewordET.getText().toString());
                if(-1 == getNumber) {
                    MainActivity.dairyList.add(0, listInformation);
                    storeDiary();
                    Intent intent = new Intent(EditActivity.this, ViewActivity.class);
                    intent.putExtra(MainActivity.Intent_key_view, 0);
                    startActivityForResult(intent,0);
                } else {
                    storeDiary();
                    Intent intent = new Intent(EditActivity.this, ViewActivity.class);
                    intent.putExtra(MainActivity.Intent_key_view, getNumber);
                    startActivityForResult(intent,0);
                }
            }
        });
        edit_cancleTitleBt.setOnClickListener(new View.OnClickListener() { //将文件传输到显式activity
            @Override
            public void onClick(View v) {
                if(-1 == getNumber) {
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivityForResult(intent,0);
                } else {
                    Intent intent = new Intent(EditActivity.this, ViewActivity.class);
                    intent.putExtra(MainActivity.Intent_key_view, getNumber);
                    startActivityForResult(intent,0);
                }
            }
        });
        edit_txtTitleTV.setText(listInformation.getLastTime());
        edit_notewordET.setText(listInformation.getTxt());
        listItems = new ArrayList<Map<String, Object>>();
        if(listInformation.isAdd()) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", R.mipmap.image_add);
            listItems.add(listItem);
        }
        for(int i = 0; i < listInformation.pictureFiles.size(); i++) {
            Bitmap addbmp=BitmapFactory.decodeFile(listInformation.pictureFiles.get(i));
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
        edit_gridGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((listItems.size() < MaxSize) && (position==0)){
                    showDialog();
                } else if((listItems.size() == MaxSize) && (position == 0) && listInformation.isAdd()) {
                    listInformation.setAdd(false);
                    listItems.remove(0);
                    showDialog();
                }
            }
        });
        edit_scrollSV.smoothScrollTo(0,0);
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
                    String outPath = listInformation.getNextPictureName();
                    CompressPicture(path, outPath);
                    if(!(new File(outPath).exists())) {
                        Log.d("图片不存在: ", outPath);
                        return;
                    }
                    listInformation.pictureFiles.add(outPath); //记录新插入的图片
                    listInformation.addjustImg(); //调整图片
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
                    edit_gridGV.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void storeDiary() {
        File writeName = new File(dairyFile);
        try {
            writeName.createNewFile();
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            for(int i = 0; i < MainActivity.dairyList.size(); i++) {
                ListInformation listInformation = MainActivity.dairyList.get(i);
                writer.write(listInformation.getDayNumber() + "\n");
                writer.write(listInformation.getDate() + "\n");
                writer.write(listInformation.getImg1() + " \n");
                writer.write(listInformation.getImg2() + " \n");
                writer.write(listInformation.getImg3() + " \n");
                writer.write(listInformation.getOther() + "\n");
                writer.write(listInformation.getDayDate() + "\n");
                writer.write(listInformation.getAdd() + "\n");
                writer.write(listInformation.getLastTime() + "\n");
                int tmpSize = listInformation.pictureFiles.size();
                writer.write(Integer.toString(tmpSize) + "\n");
                for(int j = 0; j < tmpSize; j++) {
                    writer.write(listInformation.pictureFiles.get(j) + "\n");
                }
                writer.write(listInformation.getNextNumerofPicture() + "\n");
                writer.write(listInformation.getRandomNumber() + "\n");
                writer.write(listInformation.getTxt() + "\n");
                writer.write("**@@end---8lkhljcvbn88^^^^$$**\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
