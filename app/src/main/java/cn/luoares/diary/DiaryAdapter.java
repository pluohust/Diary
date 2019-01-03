package cn.luoares.diary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class DiaryAdapter extends ArrayAdapter<ListInformation> {
    private Activity mContext = null; // 上下文环境
    private int mResourceId; // 列表项布局资源ID
    private List<ListInformation> mItems; // 列表内容数组

    public DiaryAdapter(Activity context, int resId, List<ListInformation> items) {
        super(context, resId, items);
        mContext = context;
        mResourceId = resId;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListInformation listInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayNumber = (TextView) view.findViewById(R.id.list_dayNumber);
            viewHolder.date = (TextView) view.findViewById(R.id.list_date);
            viewHolder.txt = (TextView) view.findViewById(R.id.list_txt);
            viewHolder.img1 = (ImageView) view.findViewById(R.id.list_img1);
            viewHolder.img2 = (ImageView) view.findViewById(R.id.list_img2);
            viewHolder.img3 = (ImageView) view.findViewById(R.id.list_img3);
            viewHolder.other = (TextView) view.findViewById(R.id.list_other);
            viewHolder.imgLayout = (LinearLayout) view.findViewById(R.id.list_imgLayout);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.dayNumber.setText(listInformation.getDayNumber());
        viewHolder.date.setText(listInformation.getDate());
        viewHolder.txt.setText(listInformation.getTxt());
        if (new File(listInformation.getImg1()).exists()) {
            Bitmap bmp=BitmapFactory.decodeFile(listInformation.getImg1());
            viewHolder.img1.setImageBitmap(bmp);
            viewHolder.imgLayout.setVisibility(View.VISIBLE);
        }
        if (new File(listInformation.getImg2()).exists()) {
            Bitmap bmp=BitmapFactory.decodeFile(listInformation.getImg2());
            viewHolder.img2.setImageBitmap(bmp);
        }
        if (new File(listInformation.getImg3()).exists()) {
            Bitmap bmp=BitmapFactory.decodeFile(listInformation.getImg3());
            viewHolder.img3.setImageBitmap(bmp);
        }
        viewHolder.other.setText(listInformation.getOther());
        return view;
    }

    class ViewHolder {
        TextView dayNumber;
        TextView date;
        TextView txt;
        ImageView img1;
        ImageView img2;
        ImageView img3;
        TextView other;
        LinearLayout imgLayout;
    }
}
