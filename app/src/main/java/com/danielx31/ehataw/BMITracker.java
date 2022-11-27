package com.danielx31.ehataw;

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
        OBESE("Obeses", 30, 39.9),
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


}
