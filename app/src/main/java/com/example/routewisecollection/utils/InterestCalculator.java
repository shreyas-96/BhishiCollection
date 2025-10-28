package com.example.routewisecollection.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class InterestCalculator {
    
    /**
     * Calculate simple interest
     * @param principal Principal amount
     * @param rate Interest rate (percentage)
     * @param days Number of days
     * @return Interest amount
     */
    public static double calculateSimpleInterest(double principal, double rate, int days) {
        // Formula: (P * R * T) / 100
        // Where T is in years, so days/365
        return (principal * rate * days) / (365 * 100);
    }
    
    /**
     * Calculate interest for a month (30 days)
     * @param principal Principal amount
     * @param rate Monthly interest rate (percentage)
     * @return Interest amount
     */
    public static double calculateMonthlyInterest(double principal, double rate) {
        return (principal * rate) / 100;
    }
    
    /**
     * Calculate daily interest
     * @param principal Principal amount
     * @param rate Annual interest rate (percentage)
     * @return Daily interest amount
     */
    public static double calculateDailyInterest(double principal, double rate) {
        return (principal * rate) / (365 * 100);
    }
    
    /**
     * Calculate number of days between two dates
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return Number of days
     */
    public static int calculateDaysBetween(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            if (start != null && end != null) {
                long diffInMillis = end.getTime() - start.getTime();
                return (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Calculate total amount with interest
     * @param principal Principal amount
     * @param interestAmount Interest amount
     * @return Total amount
     */
    public static double calculateTotalAmount(double principal, double interestAmount) {
        return principal + interestAmount;
    }
    
    /**
     * Format amount to 2 decimal places
     * @param amount Amount to format
     * @return Formatted amount
     */
    public static double formatAmount(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
    
    /**
     * Calculate interest based on deposit amount and rate
     * @param depositAmount Deposit amount
     * @param rate Interest rate (percentage)
     * @return Interest amount
     */
    public static double calculateInterestOnDeposit(double depositAmount, double rate) {
        return formatAmount((depositAmount * rate) / 100);
    }
}
