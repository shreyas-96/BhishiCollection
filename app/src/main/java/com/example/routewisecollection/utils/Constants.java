package com.example.routewisecollection.utils;

public class Constants {
    
    // Shared Preferences
    public static final String PREFS_NAME = "RouteWiseCollectionPrefs";
    public static final String PREF_COMPANY_NAME = "company_name";
    public static final String PREF_COMPANY_ADDRESS = "company_address";
    public static final String PREF_COMPANY_PHONE = "company_phone";
    public static final String PREF_DEFAULT_INTEREST_RATE = "default_interest_rate";
    public static final String PREF_SMS_ENABLED = "sms_enabled";
    public static final String PREF_PRINT_ENABLED = "print_enabled";
    
    // Intent Extras
    public static final String EXTRA_CUSTOMER_ID = "customer_id";
    public static final String EXTRA_ROUTE_ID = "route_id";
    public static final String EXTRA_DEPOSIT_ID = "deposit_id";
    
    // Payment Methods
    public static final String PAYMENT_CASH = "Cash";
    public static final String PAYMENT_UPI = "UPI";
    public static final String PAYMENT_CHEQUE = "Cheque";
    public static final String PAYMENT_BANK_TRANSFER = "Bank Transfer";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    
    // Receipt Number Format
    public static final String RECEIPT_PREFIX = "RCP";
    
    // Request Codes
    public static final int REQUEST_CODE_ADD_CUSTOMER = 1001;
    public static final int REQUEST_CODE_EDIT_CUSTOMER = 1002;
    public static final int REQUEST_CODE_DEPOSIT = 1003;
    public static final int REQUEST_CODE_PRINT = 1004;
    
    // Permission Request Codes
    public static final int PERMISSION_SEND_SMS = 2001;
    public static final int PERMISSION_BLUETOOTH = 2002;
    
    // Default Values
    public static final double DEFAULT_INTEREST_RATE = 2.0;
    public static final String DEFAULT_COMPANY_NAME = "BhishiGroup";
}
