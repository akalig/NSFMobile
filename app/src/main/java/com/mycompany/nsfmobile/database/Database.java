package com.mycompany.nsfmobile.database;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mycompany.nsfmobile.CustomerList;
import com.mycompany.nsfmobile.MainActivity;
import com.mycompany.nsfmobile.ProductList;
import com.mycompany.nsfmobile.adapters.BookingData;
import com.mycompany.nsfmobile.adapters.ProductData;
import com.mycompany.nsfmobile.adapters.ProductSizeData;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.postgresql.util.PGobject;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Database {

    private final String database = "rmm";
    private final int port = 5432;
    private final String user = "postgres";
    private final String pass = "postgres";
    // For Local PostgreSQL
    public String host = "";
    public String DRNo = "";
    Context mContext;
    String booknoChecker = "";
    private Connection connection;
    private String url = "jdbc:postgresql://%s:%d/%s";
    private boolean status;
    private String agent = "";
    private String pono = "";
    private String productid = "";
    private String size = "";
    private String bookingNoSelected = "", bookno = "";

    private String selectedProduct = "";
    private String selectedCustomer = "";
    public Database() {
        System.out.println("HELLO NSF");
//        setIpAddress("192.168.1.29");
//        this.url = String.format(this.url, this.host, this.port, this.database);
//        connect();
//        //this.disconnect();
//        System.out.println("connection status:" + status);
    }

    public void createConnection(String strIpAddress) {
        this.host = strIpAddress;
        System.out.println("value of url " + this.url);
        System.out.println("value of host " + this.host);
        System.out.println("value of port " + this.port);
        System.out.println("value of database " + this.database);

        this.url = String.format(this.url, this.host, this.port, this.database);
        connect();
        System.out.println("connection status:" + status);
    }

    public String getBooknoChecker() {
        return booknoChecker;
    }

    public void setBooknoChecker(String booknoChecker) {
        this.booknoChecker = booknoChecker;
    }

    private void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                    status = true;
                    System.out.println("connected:" + status);
                } catch (Exception e) {
                    status = false;
                    System.out.print(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
    }

    public Connection getExtraConnection() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public void retrieveBookingData(Context context) {

        if (context instanceof AppCompatActivity) {
            Intent intent = ((AppCompatActivity) context).getIntent();
            agent = intent.getStringExtra("agentName");
            pono = intent.getStringExtra("poNo");
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    String query = "SELECT DISTINCT (bookingno), pono, customername FROM booking WHERE agentname = '" + agent + "' AND pono = '" + pono + "' ORDER BY bookingno";
                    ResultSet resultSet = statement.executeQuery(query);

                    ArrayList<BookingData> bookingDataList = new ArrayList<>();

                    while (resultSet.next()) {
                        String bookingNo = resultSet.getString("bookingno");
                        String pono = resultSet.getString("pono");
                        String customerName = resultSet.getString("customername");

                        // Create a BookingData object and add it to the list
                        BookingData bookingData = new BookingData(bookingNo, pono, customerName);
                        bookingDataList.add(bookingData);
                    }

                    // Pass the booking data list to the CustomerList activity
                    passBookingDataToActivity(context, bookingDataList);

                    resultSet.close();
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void passBookingDataToActivity(Context context, final ArrayList<BookingData> bookingDataList) {
        ((CustomerList) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CustomerList) context).updateBookingData(bookingDataList);
            }
        });
    }

    public void retrieveProductData(Context context) {

        if (context instanceof AppCompatActivity) {
            Intent intent = ((AppCompatActivity) context).getIntent();
            bookingNoSelected = intent.getStringExtra("bookingNoSelected");
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    String query = "SELECT id, productid, description, bookingno, price, quantity, (SELECT size1 FROM product WHERE CAST (productcode AS CHARACTER VARYING) = productid) as size FROM booking WHERE bookingno = '" + bookingNoSelected + "' ORDER BY id";
                    ResultSet resultSet = statement.executeQuery(query);

                    ArrayList<ProductData> productDataList = new ArrayList<>();

                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String productId = resultSet.getString("productid");
                        String description = resultSet.getString("description");
                        String bookingNo = resultSet.getString("bookingno");
                        String price = resultSet.getString("price");
                        String quantity = resultSet.getString("quantity");
                        String size = resultSet.getString("size");

                        // Create a ProductData object and add it to the list
                        ProductData productData = new ProductData(id, productId, description, bookingNo, price, quantity, size);
                        productDataList.add(productData);
                    }

                    // Pass the product data list to the CustomerList activity
                    passProductDataToActivity(context, productDataList);

                    resultSet.close();
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void passProductDataToActivity(Context context, final ArrayList<ProductData> productDataList) {
        ((ProductList) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ProductList) context).updateProductData(productDataList);
            }
        });
    }

    public void checkBookingData(String bookingNo) {
        System.out.println("Value of number 2 " + bookingNo);
        bookno = bookingNo;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bookingNoLoop = "";
                    Statement statement = connection.createStatement();

                    System.out.println("Value of bookno " + bookno);
                    String query = "SELECT DISTINCT bookingno FROM deliveryheaderapp WHERE bookingno = '" + bookno + "' LIMIT 1";
                    ResultSet resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        bookingNoLoop = resultSet.getString("bookingno");
                    }

                    resultSet.close();
                    statement.close();

                    System.out.println("Value of bookingNoLoop " + bookingNoLoop);
                    setBooknoChecker(bookingNoLoop);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void deleteDeliveryDetailsData(String currentDate, String deliveryReceiptNoCw, String bookingNoCw, String agentNameCw, String truckPlatNoCw, String productID) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String query = "DELETE FROM deliveryheaderapp WHERE deliverydate ='" + currentDate + "' AND deliveryno ='" + deliveryReceiptNoCw + "' AND bookingno ='" + bookingNoCw + "' AND agentname ='" + agentNameCw + "' AND truckno ='" + truckPlatNoCw + "' AND productid ='" + productID + "'";
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                    statement.close();

                    String query2 = "DELETE FROM deliverydetailsapp WHERE deliverydate ='" + currentDate + "' AND deliveryno ='" + deliveryReceiptNoCw + "' AND bookingno ='" + bookingNoCw + "' AND agentname ='" + agentNameCw + "' AND truckno ='" + truckPlatNoCw + "' AND productid ='" + productID + "'";
                    Statement statement2 = connection.createStatement();
                    statement2.executeUpdate(query2);
                    statement2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void insertDeliveryHeaderData(String currentDate, String currentTime, String deliveryReceiptNoCw, String bookingNoCw, String agentNameCw, String truckPlatNoCw, String productIdCw, String productCw, double total, String priceCw, String Customer) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        if (selectedCustomer == null || selectedCustomer.isEmpty() || selectedCustomer .equalsIgnoreCase("")) {
                            selectedCustomer = Customer;
                        }

                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                        String strDateFrom = date.format(new Date()).substring(0, 8) + "01";

                        String drnoCount = "";
                        Calendar cal = Calendar.getInstance();
                        int res = cal.getActualMaximum(Calendar.DATE);
                        String strDay = "" + res;
                        if (res < 10) {
                            strDay = "0" + res;
                        } else {
                            strDay = "" + res;
                        }
                        String strDateTo = strDateFrom.substring(0, 8) + strDay;

                        Statement statement = connection.createStatement();
//                        String query = "SELECT COUNT(deliveryno) AS count FROM deliveryheaderapp WHERE deliverydate BETWEEN '" + strDateFrom + "' AND '" + strDateTo + "'";
                        String query = "SELECT CAST(deliveryno AS INTEGER) FROM deliveryheaderapp WHERE deliverydate BETWEEN '" + strDateFrom + "' AND '" + strDateTo + "' AND cast(deliveryno as text) LIKE '" + deliveryReceiptNoCw.replaceAll("nsf", "") + "%' ORDER BY CAST(deliveryno AS INTEGER) DESC LIMIT 1";
                        ResultSet resultSetDR = statement.executeQuery(query);
                        while (resultSetDR.next()) {
                            drnoCount = resultSetDR.getString("deliveryno");
                        }

                        System.out.println("Database Uname from Pref: " + deliveryReceiptNoCw);

                        String drFrontCount = deliveryReceiptNoCw.replaceAll("nsf", "");
                        System.out.println("drFrontCount: " + drFrontCount);

                        if (drnoCount == null || "".equalsIgnoreCase(drnoCount) || Integer.parseInt(drnoCount) == 0) {
                            DRNo = (String.valueOf(drFrontCount) + strDateFrom.substring(2, 4) + strDateFrom.substring(5, 7) + 1);
                        } else {
                            DRNo = "" + (Integer.parseInt(drnoCount) + 1);
                        }

                        System.out.println("DB DRNo " + DRNo);
                        resultSetDR.close();
                        statement.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String query = "SELECT MAX(id) FROM deliveryheaderapp";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    int nextId = 1;
                    if (resultSet.next()) {
                        int maxId = resultSet.getInt(1);
                        nextId = maxId + 1;
                    }
                    resultSet.close();
                    statement.close();

                    String query2 = "SELECT deliveryno FROM deliveryheaderapp WHERE customername = '" + Customer + "' AND deliverydate = '" + currentDate + "' ORDER BY deliveryno DESC LIMIT 1";
                    Statement statement2 = connection.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery(query2);
                    String deliveryNo = "";
                    if (resultSet2.next()) {
                        deliveryNo = resultSet2.getString("deliveryno");
                    }

                    resultSet2.close();
                    statement2.close();

                    if (deliveryNo == null || deliveryNo.equals("") || deliveryNo.isEmpty()) {
                        query = "INSERT INTO deliveryheaderapp (id, deliverydate, deliverytime, deliveryno, bookingno, agentname, truckno, status, delivered, customername) " +
                                "VALUES ('" + nextId + "', '" + currentDate + "', '" + currentTime + "', '" + DRNo + "', '" + DRNo + "', '" + agentNameCw + "', '" + truckPlatNoCw + "', 'FALSE','FALSE','" + Customer + "')";
                    } else {
                        System.out.println("EXISTING DATA IN THE HEADER");
                    }

//                    query = "INSERT INTO deliveryheaderapp (id, deliverydate, deliverytime, deliveryno, bookingno, agentname, truckno, status, delivered, customername) " +
//                            "VALUES ('" + nextId + "', '" + currentDate + "', '" + currentTime + "', '" + DRNo + "', '" + DRNo + "', '" + agentNameCw + "', '" + truckPlatNoCw + "', 'FALSE','FALSE','" + Customer + "')";

                    // Execute the INSERT statement
                    statement = connection.createStatement();
                    statement.executeUpdate(query);
                    statement.close();

                    // Show a toast message indicating the successful insertion
                } catch (Exception e) {
                    e.printStackTrace();
                    // Show a toast message indicating the error
                }
            }
        });
        thread.start();
    }

    public void insertDeliveryDetailsData(String currentDate, String currentTime, String deliveryReceiptNoCw, String bookingNoCw, String agentNameCw, String truckPlatNoCw, int positionNumber, String productIdCw, String productCw, Double enteredValue, String priceCw, String imageBase64, String option, Double noofbox, String customerName) {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(currentDate);
                    java.sql.Date deliveryDate = new java.sql.Date(parsedDate.getTime());

                    String query = "SELECT id FROM deliverydetailsapp ORDER BY id DESC LIMIT 1";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    int nextId = 0;
                    if (resultSet.next()) {
                        String strID = "";
                        strID = strID.equals("") ? "0" : strID;
                        nextId = Integer.parseInt(strID);
                        nextId = (nextId + 1);
                    }

                    resultSet.close();
                    statement.close();

                    query = "INSERT INTO deliverydetailsapp (deliverydate, deliverytime, deliveryno, bookingno, agentname, truckno, lineitem, productid, description, quantitycw, buyprice, imageblob, type, noofbox, customername) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    System.out.println(query);

                    System.out.println("DETAILS DR NO " + DRNo);
                    // Create a PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setDate(1, deliveryDate);
                    preparedStatement.setString(2, currentTime);

                    System.out.println("QUERY DETAILS : " + "SELECT deliveryno, truckno FROM deliveryheaderapp WHERE customername = '" + selectedCustomer + "' AND deliverydate = '" + currentDate + "' ORDER BY deliveryno DESC LIMIT 1");
                    String query2 = "SELECT deliveryno, truckno FROM deliveryheaderapp WHERE customername = '" + selectedCustomer + "' AND deliverydate = '" + currentDate + "' ORDER BY deliveryno DESC LIMIT 1";
                    Statement statement2 = connection.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery(query2);
                    String deliveryNo = "", truckPlatNo = "";
                    if (resultSet2.next()) {
                        deliveryNo = resultSet2.getString("deliveryno");
                        truckPlatNo = resultSet2.getString("truckno");
                    }
                    resultSet2.close();
                    statement2.close();

                    if (deliveryNo == null || deliveryNo.equals("") || deliveryNo.isEmpty()) {
                        preparedStatement.setString(3, DRNo);
                        preparedStatement.setString(4, DRNo);
                        preparedStatement.setString(6, truckPlatNoCw);
                    } else {
                        preparedStatement.setString(3, deliveryNo);
                        preparedStatement.setString(4, deliveryNo);
                        preparedStatement.setString(6, truckPlatNo);
                    }


                    preparedStatement.setString(5, agentNameCw);
                    preparedStatement.setInt(7, positionNumber);
                    preparedStatement.setString(8, productIdCw);
                    preparedStatement.setString(9, productCw);
                    preparedStatement.setDouble(10, enteredValue);

                    // Convert the priceCw value to a Double if it's not null
                    Double priceValue = null;
                    if (priceCw != null) {
                        priceValue = Double.parseDouble(priceCw);
                    }
                    preparedStatement.setDouble(11, priceValue);

                    preparedStatement.setString(13, option);
                    preparedStatement.setDouble(14, noofbox);
                    preparedStatement.setString(15, customerName);

                    // Disable auto-commit mode
                    connection.setAutoCommit(false);

                    // Convert the imageBase64 to a valid OID value
                    LargeObjectManager largeObjectManager = ((PGConnection) connection).getLargeObjectAPI();

                    // Create a new large object
                    int oid = (int) largeObjectManager.createLO();
                    LargeObject largeObject = largeObjectManager.open(oid, LargeObjectManager.WRITE);

                    // Write the image data to the large object
                    if (imageBase64 != null) {
                        byte[] imageData = Base64.getDecoder().decode(imageBase64);
                        largeObject.write(imageData);
                        largeObject.close();
                    } else {
                        Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
                    }

                    preparedStatement.setInt(12, oid);

                    // Execute the INSERT statement
                    preparedStatement.executeUpdate();

                    // Commit the transaction
                    connection.commit();

                    // Enable auto-commit mode
//                    connection.setAutoCommit(true);

                    preparedStatement.close();

                    // Show a toast message indicating the successful insertion
                } catch (Exception e) {
                    e.printStackTrace();
                    // Show a toast message indicating the error
                }
            }
        });
        thread.start();
    }

    public boolean usernameExists(String username) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM users WHERE username = '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);

            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

            resultSet.close();
            statement.close();

            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getUserPassword(String username) {
        String password = null;

        try {
            String query = "SELECT userpassword FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("userpassword");
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return password;
    }

    public void fetchCustomerNames(Context context, ArrayAdapter<String> adapter, Spinner spinner) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();

                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentDate);

                    String query = "SELECT firstname, middlename, lastname FROM customers WHERE type = 'Load' ORDER BY lastname, firstname";
                    ResultSet resultSet = statement.executeQuery(query);

                    List<String> customerNames = new ArrayList<>();
                    // Add the default value to the list
                    customerNames.add("▼ Select Customer");
                    while (resultSet.next()) {
                        String firstname = resultSet.getString("firstname");
                        String middlename = resultSet.getString("middlename");
                        String lastname = resultSet.getString("lastname");

                        String fullname = lastname + ", " + firstname + " " + middlename;
                        customerNames.add(fullname);
                    }

                    resultSet.close();
                    statement.close();

                    // Pass the plate numbers to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(customerNames);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public synchronized void setSelectedCustomer(String customerName) {
        selectedCustomer = customerName;
    }


    public void fetchAgentNames(Context context, ArrayAdapter<String> adapterAgent, Spinner spinnerAgent) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    String queryAgent = "SELECT assignedagent FROM customers WHERE fullname = '" + selectedCustomer + "'";
                    ResultSet resultSetAgent = statement.executeQuery(queryAgent);

                    List<String> agentNames = new ArrayList<>();
                    while (resultSetAgent.next()) {
                        String assignedagent = resultSetAgent.getString("assignedagent");
                        agentNames.add(assignedagent);
                    }
                    resultSetAgent.close();
                    statement.close();

                    // Pass the agent names to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterAgent.clear();
                            adapterAgent.addAll(agentNames);
                            adapterAgent.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void fetchTruckPlateNumbers(Context context, ArrayAdapter<String> adapter, Spinner spinner) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    String query = "SELECT plateno FROM trucks ORDER BY plateno";
                    ResultSet resultSet = statement.executeQuery(query);

                    List<String> plateNumbers = new ArrayList<>();
                    // Add the default value to the list
                    plateNumbers.add("▼ Select Truck Plate No.");
                    while (resultSet.next()) {
                        String plateNumber = resultSet.getString("plateno");
                        plateNumbers.add(plateNumber);
                    }

                    resultSet.close();
                    statement.close();

                    // Pass the plate numbers to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(plateNumbers);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void fetchProducts(Context context, ArrayAdapter<String> adapterProducts, Spinner spinnerProducts) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentDate);

                    Statement statement = connection.createStatement();
//                    String queryProducts = "SELECT description FROM product ORDER BY description";
                    String queryProducts = "SELECT DISTINCT CAST(purchasing.productcode as integer), (SELECT description FROM product WHERE productcode = CAST(purchasing.productcode as integer)) as description FROM purchasing WHERE deliverydate = '" + formattedDate + "' ORDER BY CAST(purchasing.productcode as integer)";
                    ResultSet resultSetProducts = statement.executeQuery(queryProducts);

                    List<String> products = new ArrayList<>();
                    products.add("▼ Select Product");
                    while (resultSetProducts.next()) {
                        String product = resultSetProducts.getString("description");

                        products.add(product);
                    }
                    resultSetProducts.close();
                    statement.close();

                    // Pass the agent names to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterProducts.clear();
                            adapterProducts.addAll(products);
                            adapterProducts.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public synchronized void setSelectedProduct(String product) {
        selectedProduct = product;
    }

    public void fetchProductInfo(Context context, ArrayList<String> arrayList, OnDataFetchedListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Statement statement = connection.createStatement();
                    String queryProducts = "SELECT productcode, price1, size1 FROM product WHERE description ='" + selectedProduct + "'";
                    ResultSet resultSetProducts = statement.executeQuery(queryProducts);

                    List<String> productInfo = new ArrayList<>();
                    while (resultSetProducts.next()) {
                        String productCode = resultSetProducts.getString("productcode");
                        String productPrice = resultSetProducts.getString("price1");
                        String productSize = resultSetProducts.getString("size1");

                        productInfo.add(productCode);
                        productInfo.add(productPrice);
                        productInfo.add(productSize);
                    }
                    resultSetProducts.close();
                    statement.close();

                    // Pass the agent names to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayList.clear();
                            arrayList.addAll(productInfo);
                            listener.onDataFetched(arrayList);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void fetchReceipt(Context context, ArrayList<String> arrayList) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = connection.createStatement();
                    String query = "SELECT deliveryno FROM deliveryheaderapp";
                    ResultSet resultSet = statement.executeQuery(query);

                    List<String> deliveryno = new ArrayList<>();
                    // Add the default value to the list
                    while (resultSet.next()) {
                        String delivery = resultSet.getString("deliveryno");
                        deliveryno.add(delivery);
                    }

                    resultSet.close();
                    statement.close();

                    // Pass the plate numbers to the activity
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayList.clear();
                            arrayList.addAll(deliveryno);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public interface OnDataFetchedListener {
        void onDataFetched(ArrayList<String> productInfo);
    }
}

