package com.sfhmmy.mobile.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.utils.DrawableUtils;

import java.util.ArrayList;
import java.util.List;


public class EditableListAdapter extends BaseAdapter {

    private List<EditableListAdapter.EditableItem> mEditableItems;


    public EditableListAdapter() {
        mEditableItems = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView;

        if (convertView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.editable_list_item_layout, parent, false
            );
        } else itemView = convertView;

        final EditableItem curItem = mEditableItems.get(position);

        final MaterialEditText text  = itemView.findViewById(R.id.user_profile_user_detail_list_item_text);
        final ImageButton editButton = itemView.findViewById(R.id.user_profile_user_detail_list_item_edit_button);

        text.setFloatingLabelText(curItem.getTitle());
        text.setText(curItem.getText());
        text.setIconLeft(curItem.getIcon());
        text.setFocusFraction(1f);

        editButton.setVisibility(curItem.getAllowEdit() ? View.VISIBLE : View.GONE);
        editButton.setBackground(DrawableUtils.applyTintToDrawable(
                App.getAppResources().getDrawable(R.drawable.icon_edit),
                R.color.colorPrimaryDark
        ));
        editButton.setOnClickListener(new View.OnClickListener() {

            private boolean listenerIsEditActive = false;
            private ImageButton listenerButton = editButton;
            private MaterialEditText listenerText = text;
            private View.OnClickListener editDoneListener = curItem.mCallback;

            @Override
            public void onClick(View v) {

                if (listenerIsEditActive) {  // Disable edit.

                    listenerButton.setBackground(DrawableUtils.applyTintToDrawable(
                            App.getAppResources().getDrawable(R.drawable.icon_edit),
                            R.color.colorPrimaryDark
                    ));

                    listenerText.setEnabled(false);
                    listenerText.setHideUnderline(true);

                    if (editDoneListener != null) editDoneListener.onClick(v);

                    listenerIsEditActive = false;

                    listenerText.clearFocus();

                } else {  // Enable edit.
                    listenerButton.setBackground(DrawableUtils.applyTintToDrawable(
                            App.getAppResources().getDrawable(R.drawable.check_mark),
                            R.color.colorPrimaryDark
                    ));

                    listenerText.setEnabled(true);
                    listenerText.setHideUnderline(false);

                    listenerIsEditActive = true;

                    listenerText.requestFocus();
                    InputMethodManager imm = (InputMethodManager)
                            App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(listenerText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        return itemView;
    }

    @Override
    public EditableItem getItem(int position) {
        return mEditableItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mEditableItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public EditableListAdapter addEditableItem(String text, String title, Drawable icon,
                                               View.OnClickListener callback, boolean allowEdit) {

        EditableItem item = new EditableItem();
        item.setIcon(icon);
        item.setText(text);
        item.setTitle(title);
        item.setOnClickListener(callback);
        item.setAllowEdit(allowEdit);
        mEditableItems.add(item);

        return this;
    }

    private class EditableItem {
        private String mText;
        private String mTitle;
        private Drawable mIcon;
        private View.OnClickListener mCallback;
        private boolean mAllowEdit;

        String getText() { return mText; }
        String getTitle() { return mTitle; }
        Drawable getIcon() { return mIcon; }
        View.OnClickListener getOnClickListener() { return mCallback; }
        boolean getAllowEdit() { return mAllowEdit; }

        void setText(String text) { mText = text; }
        void setTitle(String title) { mTitle = title; }
        void setIcon(Drawable icon) { mIcon = icon; }
        void setOnClickListener(View.OnClickListener callback) { mCallback = callback; }
        void setAllowEdit(boolean allowEdit) { mAllowEdit = allowEdit; }
    }
}
