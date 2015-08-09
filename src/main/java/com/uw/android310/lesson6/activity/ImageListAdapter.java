package com.uw.android310.lesson6.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uw.android310.lesson6.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 8/8/2015.
 */
public class ImageListAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mImageUrls;

    public ImageListAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.image_view, imageUrls);

        mContext = context;
        mImageUrls = imageUrls;
        mInflater = LayoutInflater.from(mContext);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.image_view, parent, false);
        }

        Log.d("***********", "getting image at " + mImageUrls.get(position));

        Picasso
                .with(mContext)
                .load(mImageUrls.get(position))
                .fit() // will explain later
                .placeholder(R.drawable.ic_photo_library_black)
                .error(R.drawable.abc_btn_radio_material)
                .into((ImageView) convertView);

        return convertView;
    }
}
