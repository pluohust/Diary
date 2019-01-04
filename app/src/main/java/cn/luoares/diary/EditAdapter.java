package cn.luoares.diary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class EditAdapter extends ArrayAdapter<Object> {
    private Activity mContext = null; // 上下文环境
    private int mResourceId; // 列表项布局资源ID
    private List<Object> mItems; // 列表内容数组

    public EditAdapter(Activity context, int resId, List<Object> items) {
        super(context, resId, items);
        mContext = context;
        mResourceId = resId;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object imgData = getItem(position);
        View view;
        EditAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image1);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (EditAdapter.ViewHolder) view.getTag();
        }
        if(imgData instanceof Bitmap)
            viewHolder.imageView.setImageBitmap((Bitmap) imgData);;
        return view;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
