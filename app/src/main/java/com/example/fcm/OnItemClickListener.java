package com.example.fcm;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);


    void onCheckboxClick(View view, int position, boolean isChecked);
}