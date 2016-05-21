package com.zqbweather.app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zqbweather.app.R;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by admin on 2016/2/20.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;

    private final int VIEW_TYPE = 2;

    private int resourceId;

    public DrawerAdapter(Context context, int viewResourceId, List<String> objects){
        super(context, viewResourceId, objects);
        resourceId = viewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String itemName = getItem(position);
        int type = getItemViewType(position);
        View view = null;
        ViewHolder viewHolder = null;
        if(convertView == null) {
            switch (type) {
                case TYPE_1:
                    view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                    viewHolder = new ViewHolder();
                    viewHolder.itemName = (TextView) view.findViewById(R.id.drawer_item);
                    view.setTag(viewHolder);
                    break;
                case TYPE_2:
                    view = LayoutInflater.from(getContext()).inflate(R.layout.drawer_item2, null);
                    break;
                default:
                    break;
            }
        }else {
            switch (type) {
                case TYPE_1:
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                    break;
                case TYPE_2:
                    view = convertView;
                    break;
                default:
                    break;
            }

        }
        switch (type) {
            case TYPE_1:
                viewHolder.itemName.setText(itemName);
                break;
            case TYPE_2:
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position){
        if("".equals(getItem(position))){
            return false;
        }
        return true;
    }

    @Override
    public int getItemViewType(int position){
        if("".equals(getItem(position))){
            return TYPE_2;
        }
        else {
            return TYPE_1;
        }
    }

    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE;
    }

    class ViewHolder{
        TextView itemName;
    }
}
