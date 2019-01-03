package cn.luoares.diary;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiaryInformation {
    private String date = "";
    private String txt = "";
    private int numberToday = 0;
    private String txtFile = "";
    private boolean add = true;
    private String lastTime = "";
    public List<String> pictureFiles;

    public void saveToFile() {
        String fileName = getTxtFile();
        File writeName = new File(fileName);
        try {
            writeName.createNewFile();
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            writer.write(date+"\n");
            writer.write(Integer.toString(numberToday) + "\n");
            writer.write(txtFile+"\n");
            writer.write(Boolean.toString(add)+"\n");
            writer.write(lastTime+"\n");
            writer.write(Integer.toString(pictureFiles.size())+"\n");
            for(int i =0; i<pictureFiles.size(); i++) {
                writer.write(pictureFiles.get(i) + "\n");
            }
            writer.write(txt);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void readFromFile(String pathname) {
        try {
            FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader);
            date = br.readLine();
            numberToday = Integer.parseInt(br.readLine());
            txtFile = br.readLine();
            add = Boolean.parseBoolean(br.readLine());
            lastTime = br.readLine();
            int numOfPicture = Integer.parseInt(br.readLine());
            pictureFiles.clear();
            String line;
            for(int i = 0; i < numOfPicture; i++) {
                line = br.readLine();
                pictureFiles.add(line);
            }
            txt = "";
            while ((line = br.readLine()) != null) {
                txt = txt + line + "\n";
            }
            txt = txt.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeLastTime() {
        Date nowData = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日/HH:mm\tEEEE", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        lastTime = simpleDateFormat.format(nowData);
    }
    public String getLastTime() {
        return lastTime;
    }

    public String getNextPictureFileName() {
        String pictureFileName = Environment.getExternalStorageDirectory() + "/diary/" + date + "-" + Integer.toString(numberToday) + "-" + Integer.toString(pictureFiles.size()+1)+".jpg";
        pictureFiles.add(pictureFileName);
        return pictureFileName;
    }

    public String getTxtFile() {
        txtFile = Environment.getExternalStorageDirectory() + "/diary/" + date + "-" + Integer.toString(numberToday) + ".txt";
        return txtFile;
    }

    public DiaryInformation() {
        pictureFiles = new ArrayList<String>();
        storeLastTime();
    }

    public String getDate() {
        return date;
    }
    public void storeDate(String date) {
        this.date = date;
    }

    public String getTxt() {
        return txt;
    }
    public void storeTxt(String txt) {
        this.txt = txt;
    }

    public boolean isAdd() {
        return add;
    }
    public void setAdd(boolean add) {
        this.add = add;
    }
}
