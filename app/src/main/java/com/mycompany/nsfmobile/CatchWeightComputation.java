package com.mycompany.nsfmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.mycompany.nsfmobile.adapters.CatchWeightAdapter;
import com.mycompany.nsfmobile.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CatchWeightComputation extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int PERMISSION_REQUEST_CODE = 7;
    TextView tvProduct;
    TextView tvBookingNo;
    TextView tvPrice;
    TextView tvQuantity;
    TextView tvDeliveryReceiptNo;
    TextView tvTruckPlateNo;
    TextView tvAgentName;
    Button btnSaveButton;
    RecyclerView rvCatchWeight;
    CatchWeightAdapter adapter;
    LottieAnimationView loadingAnimation;
    File mainFolder;
    private Database database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_weight_computation);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        tvProduct = findViewById(R.id.tv_product_description);
        tvBookingNo = findViewById(R.id.tv_type);
        tvPrice = findViewById(R.id.tv_quantity);
        tvQuantity = findViewById(R.id.tv_quantity_cw);
        tvDeliveryReceiptNo = (TextView) findViewById(R.id.tv_delivery_receipt_no_cw);
        tvTruckPlateNo = (TextView) findViewById(R.id.tv_truck_plate_no_cw);
        tvAgentName = (TextView) findViewById(R.id.tv_agent_name_cw);
        btnSaveButton = (Button) findViewById(R.id.save_button);
        rvCatchWeight = findViewById(R.id.rv_catch_weight);
        loadingAnimation = (LottieAnimationView) findViewById(R.id.loading_animation_3);
        loadingAnimation.setVisibility(View.GONE);

        Intent intentGetData = getIntent();
        String productCw = intentGetData.getStringExtra("productCw");
        String bookingNoCw = intentGetData.getStringExtra("bookingNoCw");
        String productIdCw = intentGetData.getStringExtra("productIdCw");
        String priceCw = intentGetData.getStringExtra("priceCw");
        String quantityCw = intentGetData.getStringExtra("quantityCw");

        String deliveryReceiptNoCw = intentGetData.getStringExtra("deliveryReceiptNoCw");
        String truckPlatNoCw = intentGetData.getStringExtra("truckPlatNoCw");
        String agentNameCw = intentGetData.getStringExtra("agentNameCw");
        String customerNameCw = intentGetData.getStringExtra("customerNameCw");
        String sizeCw = intentGetData.getStringExtra("sizeCw");
        String option = intentGetData.getStringExtra("optionCw");

        Toast.makeText(this, "" + option, Toast.LENGTH_SHORT).show();

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = date.format(new Date());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String currentTime = time.format(new Date());

        tvProduct.setText(productCw);
        tvBookingNo.setText(bookingNoCw);
        tvPrice.setText(priceCw);
        tvQuantity.setText(quantityCw);

        tvDeliveryReceiptNo.setText(deliveryReceiptNoCw);
        tvTruckPlateNo.setText(truckPlatNoCw);
        tvAgentName.setText(agentNameCw);

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        List<Bitmap> itemList = new ArrayList<>();
        String finalQuantity = "" + Double.parseDouble(quantityCw);
        double quantity = Double.parseDouble(finalQuantity);
        int qty = (int) Math.ceil(quantity);
        System.out.println("Value of qty " + qty);
        for (int i = 0; i < qty; i++) {
            itemList.add(null);
        }

        List<String> itemListString = new ArrayList<>();
        double quantity2 = Double.parseDouble(finalQuantity);
        int qty2 = (int) Math.ceil(quantity2);
        System.out.println("Value of qty2 " + qty2);
        for (int i = 0; i < qty2; i++) {
            itemListString.add(String.valueOf((0.0)));
        }

        adapter = new CatchWeightAdapter(itemList, itemListString, productCw, bookingNoCw, priceCw, quantityCw, sizeCw, option);
        rvCatchWeight.setLayoutManager(new LinearLayoutManager(this));
        rvCatchWeight.setAdapter(adapter);

        String mainFolderName = "NSF Orders";

        mainFolder = new File(Environment.getExternalStoragePublicDirectory("DCIM"), mainFolderName);
        if (!mainFolder.exists()) {
            mainFolder.mkdirs();
        }

        btnSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Double> enteredValues = adapter.getEnteredValues();
                List<Bitmap> itemListImages = adapter.getItemList();
                List<Integer> positionNumbers = adapter.getPositionNumberList();
                List<Integer> modifiedPositionNumbers = new ArrayList<>();

                database = new Database();
                Intent intent2 = getIntent();
                String ipAddress = intent2.getStringExtra("ipAddress");
                database.createConnection(ipAddress);

                for (int i = 0; i < positionNumbers.size(); i++) {
                    modifiedPositionNumbers.add(positionNumbers.get(i) + 1);
                }

                double total = 0;
                for (double value : enteredValues) {
                    total += value;
                }

//                double totalChecker = total / Double.parseDouble(sizeCw);
//                double totalChecker = total;

//                if (totalChecker <= quantity) {
                // insert data for Header
                database.deleteDeliveryDetailsData(currentDate, deliveryReceiptNoCw, bookingNoCw, agentNameCw, truckPlatNoCw, productIdCw);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Double finalValueHeader = 0.0;
                if (option.equals("standard")) {
                    // Insert data for each item
                    Double enteredValue = total;
                    Double size = Double.parseDouble(sizeCw);

                    finalValueHeader = enteredValue * size;
                } else if (option.equals("catchWeight")) {

                    Double enteredValue = total;
                    finalValueHeader = enteredValue;
                }

//                database.insertDeliveryHeaderData(currentDate, currentTime, deliveryReceiptNoCw, bookingNoCw, agentNameCw, truckPlatNoCw, productIdCw, productCw, finalValueHeader, priceCw);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (option.equals("catchWeight")) {

                    // Insert data for each item
                    for (int i = 1; i <= (int) Math.ceil(quantity); i++) {
                        Double enteredValue = enteredValues.get(i - 1);
                        Bitmap itemImage = itemListImages.get(i - 1);
                        int positionNumber = modifiedPositionNumbers.get(i - 1);

                        // Convert bitmap to Base64-encoded string if the image exists
                        String imageBase64 = null;
                        if (itemImage != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            itemImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] imageByteArray = stream.toByteArray();
                            imageBase64 = Base64.encodeToString(imageByteArray, Base64.NO_WRAP); // Use NO_WRAP flag instead of DEFAULT
                        }
                        // Insert data Details

//                        database.insertDeliveryDetailsData(currentDate, currentTime, deliveryReceiptNoCw, bookingNoCw, agentNameCw, truckPlatNoCw, positionNumber, productIdCw, productCw, enteredValue, priceCw, imageBase64, option);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else if (option.equals("standard")) {

                    // Insert data for each item
                    Double enteredValue = enteredValues.get(0);
                    Double size = Double.parseDouble(sizeCw);

                    Double finalValue = enteredValue * size;

                    Bitmap itemImage = itemListImages.get(0);
                    int positionNumber = modifiedPositionNumbers.get(0);

                    // Convert bitmap to Base64-encoded string if the image exists
                    String imageBase64 = null;
                    if (itemImage != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        itemImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageByteArray = stream.toByteArray();
                        imageBase64 = Base64.encodeToString(imageByteArray, Base64.NO_WRAP); // Use NO_WRAP flag instead of DEFAULT
                    }

                    Toast.makeText(CatchWeightComputation.this, ""+finalValue, Toast.LENGTH_SHORT).show();

                    // Insert data Details
//                    database.insertDeliveryDetailsData(currentDate, currentTime, deliveryReceiptNoCw, bookingNoCw, agentNameCw, truckPlatNoCw, positionNumber, productIdCw, productCw, finalValue, priceCw, imageBase64, option);

                } else {

                    Toast.makeText(CatchWeightComputation.this, "Something goes wrong", Toast.LENGTH_SHORT).show();

                }


                saveImagesInLocal(currentDate, deliveryReceiptNoCw, productCw.trim().toLowerCase(), option);

                Intent intent3 = getIntent();
                String ip = intent3.getStringExtra("ipAddress");

                Intent intent = new Intent(CatchWeightComputation.this, ProductList.class);
                intent.putExtra("bookingNoSelected", bookingNoCw);
                intent.putExtra("customerName", customerNameCw);
                intent.putExtra("deliveryReceiptNoToProdList", deliveryReceiptNoCw);
                intent.putExtra("truckPlatNoToProdList", truckPlatNoCw);
                intent.putExtra("agentNameToProdList", agentNameCw);
                intent.putExtra("ipAddress", ip);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
//                } else {
//                    Toast.makeText(CatchWeightComputation.this, "Invalid Total Quantity, please input information.", Toast.LENGTH_SHORT).show();
//                    btnSaveButton.requestFocus();
//                }
            }
        });
    }

    public void saveImagesInLocal(String currentDate, String deliveryReceiptNumber, String product, String type) {

        // Create the folder name
        String folderName = currentDate + "_" + deliveryReceiptNumber;

        // Create a directory for the folder
        File folder = new File(mainFolder, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Get the list of images
        List<Bitmap> itemListImages = adapter.getItemList();

        if (type.equals("catchWeight")) {

            // Save images to the folder
            for (int i = 0; i < itemListImages.size(); i++) {
                Bitmap image = itemListImages.get(i);
                File imageFile = new File(folder, "cw_" + product + "_" + (i + 1) + ".png");

                try (OutputStream os = new FileOutputStream(imageFile)) {
                    image.compress(Bitmap.CompressFormat.PNG, 100, os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Notify the user that images are saved
            Toast.makeText(getApplicationContext(), "Images saved to " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        }

        if (type.equals("standard")) {

            // Save images to the folder
            Bitmap image = itemListImages.get(0);
            File imageFile = new File(folder,"st_" + product + ".png");

            try (OutputStream os = new FileOutputStream(imageFile)) {
                image.compress(Bitmap.CompressFormat.PNG, 100, os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Notify the user that images are saved
            Toast.makeText(getApplicationContext(), "Images saved to " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle camera button click
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    // Update the corresponding item in the list with the captured image
                    adapter.updateItem(requestCode, imageBitmap);
                }
            }
        }
    }
}