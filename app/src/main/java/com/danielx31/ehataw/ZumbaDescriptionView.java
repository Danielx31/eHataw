package com.danielx31.ehataw;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZumbaDescriptionView {

    private ConstraintLayout layout;
    private Zumba zumba;
    private User user;

    private Map<String, TextView> textViewMap;

    public ZumbaDescriptionView(ConstraintLayout layout, Zumba zumba, User user) {
        this.zumba = zumba;
        this.user = user;
        this.layout = layout;
    }

    public ConstraintLayout getLayout() {
        boolean isUserNull = isUserNull();

        Map<String, Object> systemTags = zumba.getSystemTags();

        ConstraintLayout warningLayout = layout.findViewById(R.id.container_warning);

        warningLayout.setVisibility(View.GONE);

        if (!isUserNull()) {
            List<String> userHealthConditions = user.getHealthConditions();
            BMITracker bmiTracker = new BMITracker(user.getWeightInKg(), user.getHeightInCm());

            Double userBMI = bmiTracker.calculateBMI();
            Double minBMI = (Double) systemTags.get("minBMI");
            Double maxBMI = (Double) systemTags.get("maxBMI");

            if (!userHealthConditions.isEmpty() ||
                !(userBMI >= minBMI ||
                userBMI <= maxBMI)) {
                warningLayout.setVisibility(View.VISIBLE);
            }
        }

        textViewMap = new HashMap<>();
        textViewMap.put("title", layout.findViewById(R.id.textview_title));
        textViewMap.put("category", layout.findViewById(R.id.textview_category));
        textViewMap.put("viewCount", layout.findViewById(R.id.textview_viewcount));
        textViewMap.put("date", layout.findViewById(R.id.textview_date));
        textViewMap.put("year", layout.findViewById(R.id.textview_yeardate));
        textViewMap.put("duration", layout.findViewById(R.id.textview_duration));
        textViewMap.put("calorie", layout.findViewById(R.id.textview_calorie));
        textViewMap.put("calorieConstant", layout.findViewById(R.id.textview_constant_calorie));
        textViewMap.put("description", layout.findViewById(R.id.textview_description));

        textViewMap.get("title").setText(zumba.getTitle());
        textViewMap.get("category").setText(zumba.getCategory());
        textViewMap.get("viewCount").setText("" + zumba.getViewCount());

        Date creationDate = zumba.getCreatedDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(creationDate);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
        String dateString = simpleDateFormat.format(creationDate);
        textViewMap.get("date").setText(dateString);
        textViewMap.get("year").setText("" + calendar.get(Calendar.YEAR));

        textViewMap.get("duration").setText(zumba.getDuration());

        String[] durationParts = zumba.getDuration().split("");
        double minute = Double.parseDouble(durationParts[0]);

        Double MET = (Double) systemTags.get("MET");

        textViewMap.get("calorie").setText("" + MET);
        textViewMap.get("calorieConstant").setText("MET");

        if (!isUserNull) {
            double calorieBurned = calorieBurned(MET, user.getWeightInKg(), minute);

            DecimalFormat decimalFormat = new DecimalFormat("##.00");
            textViewMap.get("calorie").setText(decimalFormat.format(calorieBurned));
            textViewMap.get("calorieConstant").setText("Calories");

            if (calorieBurned <= 1) {
                textViewMap.get("calorieConstant").setText("Calorie");
            }
        }
        textViewMap.get("description").setText(zumba.getDescription());

        return layout;
    }

    public boolean isUserNull() {
        return user == null;
    }

    private double calorieBurned(double MET, double weightInKg, double minute) {
        double calorieBurnedPerMinute = (MET * weightInKg * 3.5) / (200 * 1);
        return calorieBurnedPerMinute * minute;
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        layout.setOnTouchListener(onTouchListener);
    }

}
