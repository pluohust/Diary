package cn.luoares.diary;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class ListInformation implements Cloneable {
    private String dayNumber = "";
    private String date = "";
    private String txt = "";
    private String img1 = "";
    private String img2 = "";
    private String img3 = "";
    private String other = "";

    private String dayDate = "";
    private boolean add = true;
    private String lastTime = "";
    public List<String> pictureFiles = new ArrayList<String>();
    private int nextNumerofPicture = 0;
    private int randomNumber = 0;

    public Object clone()
    {
        Object tmp = null;
        try {
            tmp =(ListInformation) super.clone(); //Object 中的clone()识别出你要复制的是哪一个对象。
        } catch(CloneNotSupportedException e) {
            System.out.println(e.toString());
        }
        return tmp;
    }

    public String getRandomNumber() {
        return Integer.toString(randomNumber);
    }
    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getNextNumerofPicture() {
        return Integer.toString(nextNumerofPicture);
    }
    public void setNextNumerofPicture(int nextNumerofPicture) {
        this.nextNumerofPicture = nextNumerofPicture;
    }

    public String getAdd() {
        return Boolean.toString(add);
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }
    public String getDayDate() {
        return dayDate;
    }

    public void adjustImg() {
        if(pictureFiles.size() > 2) {
            img1 = pictureFiles.get(0);
            img2 = pictureFiles.get(1);
            img3 = pictureFiles.get(2);
        } else if(pictureFiles.size() > 1) {
            img1 = pictureFiles.get(0);
            img2 = pictureFiles.get(1);
            img3 = "";
        } else if(pictureFiles.size() > 0) {
            img1 = pictureFiles.get(0);
            img2 = "";
            img3 = "";
        } else {
            img1 = "";
            img2 = "";
            img3 = "";
        }
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isAdd() {
        return add;
    }

    public String getLastTime() {
        return lastTime;
    }
    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getNextPictureName() {
        nextNumerofPicture ++;
        String pictureFileName = Environment.getExternalStorageDirectory() + "/diary/" +
                dayDate + "-" + Integer.toString(randomNumber) + "-" + Integer.toString(nextNumerofPicture)+".jpg";
        return pictureFileName;
    }

    public void setDate(Date inDate) {
        Date nowData = inDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日/HH:mm\tEEEE", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        lastTime = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("dd", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        dayNumber = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("MM月/EE", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        date = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        other = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        dayDate = simpleDateFormat.format(nowData);
    }

    public ListInformation() {
        Date nowData = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日/HH:mm\tEEEE", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        lastTime = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("dd", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        dayNumber = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("MM月/EE", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        date = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        other = simpleDateFormat.format(nowData);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        dayDate = simpleDateFormat.format(nowData);
        Random rand = new Random();
        randomNumber = rand.nextInt(10000);
    }

    public ListInformation(String dayNumber, String date, String txt, String img1, String img2, String img3, String other) {
        this.dayNumber = dayNumber;
        this.date = date;
        this.txt = txt;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.other = other;
    }

    public String getDayNumber() {
        return dayNumber;
    }
    public void setDayNumber(String dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTxt() {
        return txt;
    }
    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getImg1() {
        return img1;
    }
    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }
    public void setImg2(String img) {
        this.img2 = img;
    }

    public String getImg3() {
        return img3;
    }
    public void setImg3(String img) {
        this.img3 = img;
    }

    public void setOther(String other) {
        this.other = other;
    }
    public String getOther() {
        return other;
    }
}
