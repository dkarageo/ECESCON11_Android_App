/*
 * MainMenuAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainMenuAdapter extends BaseAdapter {

    private static final int BUTTON_TYPE_ID = 0;
    private static final int SEPARATOR_TYPE_ID = 1;

    List<MenuItem> mMenuItems;


    public MainMenuAdapter() {
        mMenuItems = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItem item = mMenuItems.get(position);

        View itemView;

        switch(item.getItemType()) {
            case BUTTON_TYPE_ID:

                if (convertView == null) {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.main_menu_item_layout, parent, false
                    );
                } else itemView = convertView;

                ButtonItem curButton = (ButtonItem) item;

                TextView text = itemView.findViewById(R.id.main_menu_item_text);
                ImageView icon  = itemView.findViewById(R.id.main_menu_item_icon);
                View wrapper = itemView.findViewById(R.id.main_menu_item_wrapper);

                text.setText(curButton.getText());
                icon.setImageDrawable(curButton.getIcon());
                wrapper.setOnClickListener(curButton.getOnClickListener());

                break;

            case SEPARATOR_TYPE_ID:

                if (convertView == null) {
                    itemView = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.main_menu_item_separator_layout, parent, false
                    );
                } else itemView = convertView;

                break;

            default:
                throw new RuntimeException("Invalid menu item type");
        }

        return itemView;
    }

    @Override
    public MenuItem getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMenuItems.get(position).getItemType();
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMenuItems.get(position).getItemType();
    }

    public MainMenuAdapter addButtonItem(String text, Drawable icon,
                                         View.OnClickListener callback) {

        ButtonItem item = new ButtonItem();
        item.setIcon(icon);
        item.setText(text);
        item.setOnClickListener(callback);
        mMenuItems.add(item);

        return this;
    }

    public MainMenuAdapter addSeparator() {
        mMenuItems.add(new SeparatorItem());
        return this;
    }

    private class MenuItem {
        private int mItemType;

        public int getItemType() { return mItemType; }
        public void setItemType(int itemType) { mItemType = itemType; }
    }

    private class ButtonItem extends MenuItem {
        private String mText;
        private Drawable mIcon;
        private View.OnClickListener mCallback;

        public ButtonItem() {
            setItemType(BUTTON_TYPE_ID);
        }

        public String getText() { return mText; }
        public Drawable getIcon() { return mIcon; }
        public View.OnClickListener getOnClickListener() { return mCallback; }

        public void setText(String text) { mText = text; }
        public void setIcon(Drawable icon) { mIcon = icon; }
        public void setOnClickListener(View.OnClickListener callback) { mCallback = callback; }
    }

    private class SeparatorItem extends MenuItem {
        public SeparatorItem() {
            setItemType(SEPARATOR_TYPE_ID);
        }
    }
}
