package com.mycompany.nsfmobile.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mycompany.nsfmobile.R;

import java.util.ArrayList;
import java.util.List;

public class CatchWeightAdapter extends RecyclerView.Adapter<CatchWeightAdapter.CatchWeightViewHolder> {
    private List<Bitmap> itemList;
    private List<String> itemListString;
    private String productCw;
    private String bookingNoCw;
    private String priceCw;
    private String quantityCw;
    private String sizeCw;
    private String optionCw;

    public CatchWeightAdapter(List<Bitmap> itemList, List<String> itemListString, String productCw, String bookingNoCw, String priceCw, String quantityCw, String sizeCw, String optionCw) {
        this.itemList = itemList;
        this.itemListString = itemListString;
        this.productCw = productCw;
        this.bookingNoCw = bookingNoCw;
        this.priceCw = priceCw;
        this.quantityCw = quantityCw;
        this.sizeCw = sizeCw;
        this.optionCw = optionCw;
    }

    @Override
    public CatchWeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catch_weight_list, parent, false);
        return new CatchWeightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CatchWeightViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position >= 0 && position < itemListString.size()) {
            String item = itemListString.get(position);
            holder.tvProduct.setText(productCw);
            holder.tvBookingNo.setText(bookingNoCw);
            holder.tvPrice.setText(priceCw);

            Bitmap imageBitmap = itemList.get(position);
            holder.ivCamera.setImageBitmap(imageBitmap);

            holder.ivCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                            ((Activity) v.getContext()).startActivityForResult(takePictureIntent, position);
                        }
                    } else {
                        Toast.makeText(v.getContext(), "Camera permission not granted", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set the label count for each item
            int labelCount = position + 1;
            holder.tvLabelCount.setText(String.valueOf(labelCount));

            holder.etCatchWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemListString.set(position, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public void updateItem(int position, Bitmap imageBitmap) {
        itemList.set(position, imageBitmap);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        // Return the quantityCw to determine the number of items in the RecyclerView

        if (optionCw.equals("standard")) {
//            String finalQuantity = "" + (Double.parseDouble(quantityCw) / Double.parseDouble(quantityCw));
//
//            if (finalQuantity != null && !finalQuantity.isEmpty()) {
                try {
//                    double quantity = Double.parseDouble(finalQuantity);
//                    int qty = (int) Math.ceil(quantity);
                    int qty = 1;
                    return qty;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
//            }

        } else {

//            String finalQuantity = "" + (Double.parseDouble(quantityCw) / Double.parseDouble(sizeCw));
            String finalQuantity = "" + Double.parseDouble(quantityCw);
            if (finalQuantity != null && !finalQuantity.isEmpty()) {
                try {
                    double quantity = Double.parseDouble(finalQuantity);
                    int qty = (int) Math.ceil(quantity);
                    return qty;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Double> getEnteredValues() {
        List<Double> enteredValues = new ArrayList<>();
        for (int i = 0; i < itemListString.size(); i++) {
            String value = itemListString.get(i);
            if (!value.isEmpty()) {
                try {
                    double enteredValue = Double.parseDouble(value);
                    enteredValues.add(enteredValue);
                } catch (NumberFormatException e) {
                    // Handle non-numeric values
                    e.printStackTrace();
                }
            }
        }
        return enteredValues;
    }

    public List<Bitmap> getItemList() {
        return itemList;
    }

    public List<Integer> getPositionNumberList() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            items.add(i);
        }
        return items;
    }


    public class CatchWeightViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProduct;
        public TextView tvBookingNo;
        public TextView tvPrice;
        public ImageView ivCamera;
        public EditText etCatchWeight;
        public TextView tvLabelCount;

        public CatchWeightViewHolder(View itemView) {
            super(itemView);
            ivCamera = itemView.findViewById(R.id.camera_button);
            tvProduct = itemView.findViewById(R.id.tv_product_description);
            tvBookingNo = itemView.findViewById(R.id.tv_type);
            tvPrice = itemView.findViewById(R.id.tv_quantity);
            ivCamera = itemView.findViewById(R.id.camera_button);
            etCatchWeight = itemView.findViewById(R.id.et_catch_weight);
            tvLabelCount = itemView.findViewById(R.id.number_label);
        }
    }
}
