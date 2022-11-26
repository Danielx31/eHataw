package com.danielx31.ehataw;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;

public class ZumbaDescriptionView {

    private ConstraintLayout layout;
    private Zumba zumba;
    private TextView titleTextView;

    public ZumbaDescriptionView(ConstraintLayout layout, Zumba zumba) {
        this.layout = layout;
        this.zumba = zumba;
    }

    public ConstraintLayout getLayout() {
        titleTextView = layout.findViewById(R.id.textview_title);

        titleTextView.setText(zumba.getTitle());
        return layout;
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        layout.setOnTouchListener(onTouchListener);
    }

}
