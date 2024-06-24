package com.mycompany.nsfmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mycompany.nsfmobile.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnRemoveItemClickListener removeItemClickListener;
    Context context;

    public ProductAdapter(Context context, List<Product> productList, OnRemoveItemClickListener listener) {
        this.productList = productList;
        this.removeItemClickListener = listener;
        this.context = context;
    }

    public interface OnRemoveItemClickListener {
        void onRemoveItem(int position);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        // Bind data to the ViewHolder's views here
        // Example: holder.textViewDescription.setText(product.getDescription());

        holder.productDescription.setText(product.getDescription());
        holder.quantity.setText(product.getQuantity());
        holder.type.setText(product.getType());
        holder.noofbox.setText(product.getNoofbox());
        holder.price.setText(product.getPrice());

        // Set the label count for each item
        int labelCount = position + 1;
        holder.numberCount.setText(String.valueOf(labelCount));

        holder.ibRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeItemClickListener.onRemoveItem(position);

                    Toast.makeText(v.getContext(), "Item removed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void addProduct(Product product) {
        // Check if the product with the same description already exists in the list
        boolean isDuplicate = false;
        for (Product existingProduct : productList) {
            if (existingProduct.getDescription().equals(product.getDescription())) {
                isDuplicate = true;
                break;
            }
        }

        if (isDuplicate) {
            // Show a message indicating that the item is already in the list
            Toast.makeText(context, "The product is already on the list.", Toast.LENGTH_SHORT).show();
        } else {
            // Add the product to the list
            productList.add(product);
            notifyItemInserted(productList.size() - 1);
        }
    }


    public Product getProductAtPosition(int position) {
        return productList.get(position);
    }

    public double calculateTotalQuantity() {
        double totalQuantity = 0;
        for (Product product : productList) {
            try {
                double quantity = Double.parseDouble(product.getQuantity());
                totalQuantity += quantity;
            } catch (NumberFormatException e) {
                // Handle any invalid quantity values gracefully
            }
        }
        return totalQuantity;
    }




    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView productDescription;
        public TextView noofbox;
        public TextView price;
        public TextView quantity;
        public TextView type;
        public TextView numberCount;
        public ImageButton ibRemoveButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productDescription = itemView.findViewById(R.id.tv_product_description);
            noofbox = itemView.findViewById(R.id.tv_noofbox);
            price = itemView.findViewById(R.id.tv_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            type = itemView.findViewById(R.id.tv_type);
            numberCount = itemView.findViewById(R.id.number_label);
            ibRemoveButton = itemView.findViewById(R.id.remove_btn);

        }
    }
}
