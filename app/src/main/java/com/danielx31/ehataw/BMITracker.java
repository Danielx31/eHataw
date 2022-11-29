package com.danielx31.ehataw;

import android.util.Log;

public class BMITracker {

    private double weightInKg;
    private double heightInCm;

    public BMITracker(double weightInKg, double heightInCm) {
        this.weightInKg = weightInKg;
        this.heightInCm = heightInCm;
    }

    public double calculateBMI() {
        return (1 * weightInKg / heightInCm / heightInCm) * 10000;
    }

    public enum BMIClassification {
        UNDERWEIGHT("Underweight", 0, 18.4),
        HEALTHY("Healthy", 18.5, 24.9),
        OVERWEIGHT("Overweight", 25.0, 29.9),
        OBESE("Obese", 30, 39.9),
        EXTREMELY_OBESE("Extremely Obese", 40, 999);

        private String name;
        private double minBMI;
        private double maxBMI;

        private BMIClassification(String name, double minBMI, double maxBMI) {
            this.name = name;
            this.minBMI = minBMI;
            this.maxBMI = maxBMI;
        }

        public String getName() {
            return name;
        }

        public double getMinBMI() {
            return minBMI;
        }

        public double getMaxBMI() {
            return maxBMI;
        }

    }

    public BMIClassification classifyBMI() {
        double bmi = calculateBMI();

        for (BMIClassification bmiClassification : BMIClassification.values()) {
            if (bmi >= bmiClassification.getMinBMI() && bmi <= bmiClassification.getMaxBMI()) {
                return bmiClassification;
            }
        }

        return null;
    }

    public double kgToCalories(double kg) {
        return kg * 7716.179176;
    }

    public double caloriesToKg(double calories) {
        return calories * 0.00013;
    }

    public int getWeightGoalPercentage(double weightGoalFromInKg, double weightGoalInKg) {
        double total = weightGoalFromInKg - weightGoalInKg;
        double progress = weightGoalFromInKg - weightInKg;
        Log.d("CHECK", "progress: " + progress + " total: " + total);
        return getPercentage(progress, total);
    }

    public int getPercentage(double number, double total) {
        if (number <= 0) {
            return 0;
        }

        if (total <= 0) {
            return 100;
        }

        int percentage =  new Double(number / total * 100).intValue();

        if (percentage > 100) {
            return 100;
        }

        return percentage;
    }

    public int getPercentage(long number, long total) {
        if (number <= 0) {
            return 0;
        }

        if (total <= 0) {
            return 100;
        }

        int percentage =  new Long(number / total * 100).intValue();

        if (percentage > 100) {
            return 100;
        }

        return percentage;
    }


}
