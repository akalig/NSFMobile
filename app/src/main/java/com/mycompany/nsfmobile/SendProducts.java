package com.mycompany.nsfmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.nsfmobile.adapters.Product;
import com.mycompany.nsfmobile.adapters.ProductAdapter;
import com.mycompany.nsfmobile.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SendProducts extends AppCompatActivity implements ProductAdapter.OnRemoveItemClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView tvCustomerName;
    TextView tvAgentName;
    TextView tvTruckPlateNumber;
    Spinner spProductDescription;
    RadioButton rbStandard;
    RadioButton rbCatchWeight;
    String selectedRadio = "";
    EditText etQuantity;
    ImageView ivCameraButton;
    Button btnAddProduct;
    RecyclerView rvProductsList;
    Button btnSaveButton;
    Database database;
    ProductAdapter productAdapter;
    String customerName = "";
    String agentName = "";
    String truckPlateNumber = "";
    String productCode = "";
    String productPrice = "";
    String productSize = "";
    Bitmap imageBitmap;
    File mainFolder;
    EditText etNoOfBox;
    EditText etPrice;
    EditText etSearchProduct;
    String ipAddress = "";
    String username = "";

//    Button btnCompute;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_products);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        tvCustomerName = (TextView) findViewById(R.id.tv_customer_name_sp);
        tvAgentName = (TextView) findViewById(R.id.tv_agent_name_sp);
        tvTruckPlateNumber = (TextView) findViewById(R.id.tv_truck_plate_number_sp);

        spProductDescription = (Spinner) findViewById(R.id.product_description_sp);
        rbStandard = (RadioButton) findViewById(R.id.rb_standard_sp);
        rbCatchWeight = (RadioButton) findViewById(R.id.rb_catch_weight_sp);
        etQuantity = (EditText) findViewById(R.id.et_quantity_sp);
        ivCameraButton = (ImageView) findViewById(R.id.camera_button_sp);
        btnAddProduct = (Button) findViewById(R.id.btn_add_product_sp);

        rvProductsList = (RecyclerView) findViewById(R.id.rv_products_list_sp);
        btnSaveButton = (Button) findViewById(R.id.btn_save_sp);

        etNoOfBox = (EditText) findViewById(R.id.et_no_of_box);
        etPrice = (EditText) findViewById(R.id.et_price);
        etSearchProduct = (EditText) findViewById(R.id.et_search_product);
//        btnCompute = (Button) findViewById(R.id.btn_compute);

        customerName = getIntent().getStringExtra("customerName");
        agentName = getIntent().getStringExtra("agentName");
        truckPlateNumber = getIntent().getStringExtra("truckPlatNo");
        ipAddress = getIntent().getStringExtra("ipAddress");
        username = getIntent().getStringExtra("loggedUser");

        System.out.println("Send Products UName: " + username);

        tvCustomerName.setText("Customer: " + customerName);
        tvAgentName.setText("Agent: " + agentName);
        tvTruckPlateNumber.setText("Truck Plate No.: " + truckPlateNumber);

        database = new Database();
        database.createConnection(ipAddress);

        ArrayAdapter<String> adapterProducts = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductDescription.setAdapter(adapterProducts);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SendProducts.this);
        String usernamePref = preferences.getString("username", "");
        System.out.println("Logged User Send Products: " + usernamePref);

        database.fetchProducts(this, adapterProducts, spProductDescription);

        productAdapter = new ProductAdapter(SendProducts.this, new ArrayList<>(), this);
        rvProductsList.setAdapter(productAdapter);
        rvProductsList.setLayoutManager(new LinearLayoutManager(this));

        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method will be called every time the user types or deletes text
                String searchText = charSequence.toString().toLowerCase();

                // Filter the adapter based on the search text
                filterAdapter(adapterProducts, searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });

        if (rbStandard.isChecked()) {
            selectedRadio = rbStandard.getText().toString();
        } else if (rbCatchWeight.isChecked()) {
            selectedRadio = rbCatchWeight.getText().toString();
        }

        String mainFolderName = "NSF Orders";

        mainFolder = new File(Environment.getExternalStoragePublicDirectory("DCIM"), mainFolderName);
        if (!mainFolder.exists()) {
            mainFolder.mkdirs();
        }

        ivCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        spProductDescription.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

//        btnCompute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rbStandard.isChecked()) {
//                    String stNoOfBox = etNoOfBox.getText().toString();
//                    String description = spProductDescription.getSelectedItem().toString();
//
//                    if (description.equals("▼ Select Product")) {
//                        Toast.makeText(SendProducts.this, "Please select product.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    if (stNoOfBox.equals("") || stNoOfBox == null) {
//                        etNoOfBox.setError("Enter number of Box");
//                        return;
//                    }
//
//                    database.setSelectedProduct(description);
//
//                    ArrayList<String> arrayListProductInfo = new ArrayList<>();
//                    database.fetchProductInfo(SendProducts.this, arrayListProductInfo, new Database.OnDataFetchedListener() {
//                        @Override
//                        public void onDataFetched(ArrayList<String> productInfo) {
//                            if (!productInfo.isEmpty() && productInfo.size() >= 3) {
//                                String productCode1 = productInfo.get(0);
//                                String productPrice1 = productInfo.get(1);
//                                String productSize1 = productInfo.get(2);
//
//                                int noOfBox = Integer.parseInt(stNoOfBox);
//                                int productSize = Integer.parseInt(productSize1);
//
//                                double finalQuantity = noOfBox * productSize;
//
//                                etQuantity.setText("" + finalQuantity);
//
//                            } else {
//                                Toast.makeText(SendProducts.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } else {
//                    etQuantity.setText("");
//                }
//            }
//        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = spProductDescription.getSelectedItem().toString();
                String type = rbStandard.isChecked() ? "Standard" : "Catch Weight";
                String quantity = etQuantity.getText().toString();
                String noOfBox = etNoOfBox.getText().toString();
                String price = etPrice.getText().toString();

                if (description.equals("▼ Select Product")) {
                    Toast.makeText(SendProducts.this, "Please select product.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity.equals("") || quantity == null) {
                    Toast.makeText(SendProducts.this, "Please input quantity.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!rbStandard.isChecked() && !rbCatchWeight.isChecked()) {
                    Toast.makeText(SendProducts.this, "Please select type.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (noOfBox.equals("") || noOfBox == null) {
                    Toast.makeText(SendProducts.this, "Please input Number of Box.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (price.equals("") || price == null) {
                    Toast.makeText(SendProducts.this, "Please input price.", Toast.LENGTH_SHORT).show();
                    return;
                }

                database.setSelectedProduct(description);

                ArrayList<String> arrayListProductInfo = new ArrayList<>();
                database.fetchProductInfo(SendProducts.this, arrayListProductInfo, new Database.OnDataFetchedListener() {
                    @Override
                    public void onDataFetched(ArrayList<String> productInfo) {
                        if (!productInfo.isEmpty() && productInfo.size() >= 3) {
                            productCode = productInfo.get(0);
                            productPrice = productInfo.get(1);
                            productSize = productInfo.get(2);

                            // Create a new Product instance with the collected data
                            Product product = new Product(description, type, quantity, noOfBox, price, productCode);
                            // Add the product to your RecyclerView's adapter
                            productAdapter.addProduct(product);

                        } else {
                            Toast.makeText(SendProducts.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                spProductDescription.requestFocus();
                spProductDescription.setSelection(0);
                rbCatchWeight.setChecked(false);
                rbStandard.setChecked(false);
                etNoOfBox.setText("");
                etPrice.setText("");
                etQuantity.setText("");
                etSearchProduct.setText("");
                etSearchProduct.requestFocus();

                Toast.makeText(SendProducts.this, "Successfully Added.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendProducts.this);
                builder.setMessage("Are you sure to save the Products?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int itemCount = productAdapter.getItemCount();

                                if (itemCount == 0) {
                                    Toast.makeText(SendProducts.this, "Please Add Product", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                btnSaveButton.setEnabled(false);
                                btnAddProduct.setEnabled(false);

                                StringBuilder toastMessage = new StringBuilder();

                                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                String currentDate = date.format(new Date());

                                SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                                String currentTime = time.format(new Date());

                                // Convert bitmap to Base64-encoded string if the image exists
                                String imageBase64 = null;
                                if (imageBitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] imageByteArray = stream.toByteArray();
                                    imageBase64 = Base64.encodeToString(imageByteArray, Base64.NO_WRAP);
                                } else {
                                    Toast.makeText(SendProducts.this, "Please capture an Image before saving.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Double finalValueHeader = 0.0;
                                double totalQuantity = productAdapter.calculateTotalQuantity();

                                System.out.println("Quantity: " + totalQuantity + "\n" + "Product size: " + productSize);

                                if (productSize == null || productSize == "") {
                                    productSize = "1";
                                }

                                if (rbStandard.isChecked()) {
                                    // Insert data for each item
                                    Double enteredValue = Double.valueOf(totalQuantity);
                                    int size = Integer.parseInt(productSize);

                                    finalValueHeader = enteredValue * size;

                                } else if (rbCatchWeight.isChecked()) {

                                    finalValueHeader = Double.valueOf(totalQuantity);
                                }

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                System.out.println("save to header");
                                database.insertDeliveryHeaderData(currentDate, currentTime, usernamePref, "", agentName, truckPlateNumber, productCode, spProductDescription.getSelectedItem().toString(), finalValueHeader, etPrice.getText().toString(), customerName);

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                for (int i = 0; i < productAdapter.getItemCount(); i++) {
                                    Product product = productAdapter.getProductAtPosition(i);

                                    toastMessage
                                            .append("Current date: ").append(currentDate).append(", \n")
                                            .append("Current time: ").append(currentTime).append(", \n")
                                            .append("Delivery Receipt No.: ").append("").append(", \n")
                                            .append("Booking No.: ").append("").append(", \n")
                                            .append("Agent Name: ").append(agentName).append(", \n")
                                            .append("Truck Plate No.: ").append(truckPlateNumber).append(", \n")
                                            .append("Position No.: ").append(i + 1).append(", \n")
                                            .append("Product ID: ").append(product.getProductid()).append(", \n")
                                            .append("Product Description: ").append(product.getDescription()).append(", \n")
                                            .append("Value: ").append(product.getQuantity()).append(", \n")
                                            .append("Price: ").append(product.getPrice()).append(", \n")
                                            .append("Image: ").append(imageBase64).append(", \n")
                                            .append("Type: ").append(product.getType()).append("\n\n")
                                            .append("NoOfBox: ").append(product.getNoofbox()).append("\n\n");

                                    System.out.println("save to details");

                                    database.insertDeliveryDetailsData(currentDate, currentTime, usernamePref, "", agentName, truckPlateNumber, i + 1, product.getProductid(), product.getDescription(), Double.parseDouble(product.getQuantity()), product.getPrice(), imageBase64, product.getType(), Double.parseDouble(product.getNoofbox()), customerName);

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                }

                                saveImagesInLocal(currentDate, customerName, spProductDescription.getSelectedItem().toString());

                                System.out.println(toastMessage.toString());
                                Intent intent = new Intent(SendProducts.this, CreateDeliveryReceipt.class);
                                intent.putExtra("ipAddress", ipAddress);
                                startActivity(intent);
                                finish();

                                // Display a toast indicating that the data has been saved
                                Toast.makeText(SendProducts.this, "Data saved", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog and do nothing
                                btnAddProduct.setEnabled(true);
                                btnSaveButton.setEnabled(true);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    // Create a method to filter the adapter
    private void filterAdapter(ArrayAdapter<String> adapter, String searchText) {
        adapter.getFilter().filter(searchText);
    }

    public void saveImagesInLocal(String currentDate, String customerName, String product) {

        // Create the folder name
        String folderName = currentDate + "_" + customerName;

        // Create a directory for the folder
        File folder = new File(mainFolder, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imageFile = new File(folder, currentDate + "_" + customerName + "_" + product + "_" + ".png");

        try (OutputStream os = new FileOutputStream(imageFile)) {
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRemoveItem(int position) {

        if (rbStandard.isChecked()) {
            etQuantity.setEnabled(true);
            btnAddProduct.setEnabled(true);
        }

        // Remove the item from the adapter
        productAdapter.removeItem(position);
        // Notify the adapter that an item has been removed
        productAdapter.notifyItemRemoved(position);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                ivCameraButton.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendProducts.this);
        builder.setMessage("Are you sure you want to back to Create Delivery Receipt?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SendProducts.this, CreateDeliveryReceipt.class);
                        intent.putExtra("ipAddress", ipAddress);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }

}