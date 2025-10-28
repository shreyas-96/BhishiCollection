package com.example.routewisecollection.activities;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.R;
import com.example.routewisecollection.utils.Constants;
import com.example.routewisecollection.utils.PrinterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrintReceiptActivity extends AppCompatActivity {

    private ListView listViewDevices;
    private Button btnRefresh, btnPrint;
    private TextView tvReceiptPreview;
    private ArrayAdapter<String> deviceAdapter;
    private List<BluetoothDevice> deviceList;
    private BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_receipt);

        initializeViews();
        setupClickListeners();
        loadPairedDevices();
    }

    private void initializeViews() {
        listViewDevices = findViewById(R.id.listViewDevices);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnPrint = findViewById(R.id.btnPrint);
        tvReceiptPreview = findViewById(R.id.tvReceiptPreview);
        
        deviceList = new ArrayList<>();
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> loadPairedDevices());
        
        btnPrint.setOnClickListener(v -> {
            if (selectedDevice != null) {
                printReceipt();
            } else {
                Toast.makeText(this, "Please select a printer", Toast.LENGTH_SHORT).show();
            }
        });

        listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            selectedDevice = deviceList.get(position);
            Toast.makeText(this, "Selected: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPairedDevices() {
        if (!PrinterUtils.isBluetoothPermissionGranted(this)) {
            PrinterUtils.requestBluetoothPermission(this);
            return;
        }

        Set<BluetoothDevice> pairedDevices = PrinterUtils.getPairedDevices(this);
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            deviceList.clear();
            List<String> deviceNames = new ArrayList<>();
            
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
                deviceNames.add(device.getName() + "\n" + device.getAddress());
            }
            
            deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
            listViewDevices.setAdapter(deviceAdapter);
        } else {
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show();
        }
    }

    private void printReceipt() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String companyName = prefs.getString(Constants.PREF_COMPANY_NAME, Constants.DEFAULT_COMPANY_NAME);
        String companyAddress = prefs.getString(Constants.PREF_COMPANY_ADDRESS, "");
        String companyPhone = prefs.getString(Constants.PREF_COMPANY_PHONE, "");

        // Sample receipt data - in real app, this would come from intent extras
        String receipt = PrinterUtils.generateReceipt(
                companyName, companyAddress, companyPhone,
                "Sample Customer", "ACC001", 1000.0, 20.0, 1020.0,
                "RCP20250109001", "Cash"
        );

        tvReceiptPreview.setText(receipt);

        if (PrinterUtils.connectPrinter(selectedDevice)) {
            if (PrinterUtils.printText(receipt)) {
                Toast.makeText(this, "Receipt printed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to print receipt", Toast.LENGTH_SHORT).show();
            }
            PrinterUtils.disconnectPrinter();
        } else {
            Toast.makeText(this, "Failed to connect to printer", Toast.LENGTH_SHORT).show();
        }
    }
}
