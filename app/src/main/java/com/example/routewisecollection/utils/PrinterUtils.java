package com.example.routewisecollection.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrinterUtils {
    
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothSocket bluetoothSocket;
    private static OutputStream outputStream;
    
    /**
     * Check if Bluetooth permission is granted
     * @param context Application context
     * @return true if permission granted, false otherwise
     */
    public static boolean isBluetoothPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request Bluetooth permission
     * @param activity Activity instance
     */
    public static void requestBluetoothPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, 
                new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 
                Constants.PERMISSION_BLUETOOTH);
    }
    
    /**
     * Get paired Bluetooth devices
     * @param context Application context
     * @return Set of paired devices
     */
    public static Set<BluetoothDevice> getPairedDevices(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        if (!isBluetoothPermissionGranted(context)) {
            Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        return bluetoothAdapter.getBondedDevices();
    }
    
    /**
     * Connect to Bluetooth printer
     * @param device Bluetooth device
     * @return true if connected successfully, false otherwise
     */
    public static boolean connectPrinter(BluetoothDevice device) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Disconnect from Bluetooth printer
     */
    public static void disconnectPrinter() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Print text to Bluetooth printer
     * @param text Text to print
     * @return true if printed successfully, false otherwise
     */
    public static boolean printText(String text) {
        if (outputStream == null) {
            return false;
        }
        
        try {
            outputStream.write(text.getBytes());
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate receipt text for printing
     * @param companyName Company name
     * @param companyAddress Company address
     * @param companyPhone Company phone
     * @param customerName Customer name
     * @param accountNumber Account number
     * @param amount Deposit amount
     * @param interestAmount Interest amount
     * @param totalAmount Total amount
     * @param receiptNumber Receipt number
     * @param paymentMethod Payment method
     * @return Receipt text
     */
    public static String generateReceipt(String companyName, String companyAddress, String companyPhone,
                                        String customerName, String accountNumber, double amount,
                                        double interestAmount, double totalAmount, String receiptNumber,
                                        String paymentMethod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n");
        receipt.append(centerText(companyName, 32)).append("\n");
        receipt.append(centerText(companyAddress, 32)).append("\n");
        receipt.append(centerText(companyPhone, 32)).append("\n");
        receipt.append("--------------------------------\n");
        receipt.append(centerText("DEPOSIT RECEIPT", 32)).append("\n");
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Receipt No: %s\n", receiptNumber));
        receipt.append(String.format("Date: %s\n", currentDateTime));
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Customer: %s\n", customerName));
        receipt.append(String.format("A/C No: %s\n", accountNumber));
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Deposit Amt: Rs. %.2f\n", amount));
        receipt.append(String.format("Interest: Rs. %.2f\n", interestAmount));
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Total: Rs. %.2f\n", totalAmount));
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Payment: %s\n", paymentMethod));
        receipt.append("--------------------------------\n");
        receipt.append(centerText("Thank You!", 32)).append("\n");
        receipt.append("\n\n\n");
        
        return receipt.toString();
    }
    
    /**
     * Center text for printing
     * @param text Text to center
     * @param width Width of the line
     * @return Centered text
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }
}
