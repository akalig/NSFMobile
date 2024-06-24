package com.mycompany.nsfmobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mycompany.nsfmobile.R;

import java.util.ArrayList;

public class ProductDataAdapter extends RecyclerView.Adapter<ProductDataAdapter.ViewHolder> {

    private ArrayList<ProductData> productDataList;
    private OnItemClickListener onItemClickListener;

    public ProductDataAdapter(ArrayList<ProductData> productDataList) {
        this.productDataList = productDataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductData productData = productDataList.get(position);

        holder.textProductDescription.setText(productData.getDescription());
        holder.textBookingNo.setText(productData.getBookingNo());
        holder.textPrice.setText(productData.getPrice());
        holder.textQuantity.setText(productData.getQuantity());

    }

    @Override
    public int getItemCount() {
        return productDataList.size();
    }

    public interface OnItemClickListener {
        void onItemProductClick(ProductData productData);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textProductDescription;
        TextView textBookingNo;
        TextView textPrice;
        TextView textQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textProductDescription = itemView.findViewById(R.id.tv_product_description);
            textBookingNo = itemView.findViewById(R.id.tv_type);
            textPrice = itemView.findViewById(R.id.tv_quantity);
            textQuantity = itemView.findViewById(R.id.tv_quantity_cw);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ProductData productData = productDataList.get(position);
                    onItemClickListener.onItemProductClick(productData);
                }
            }
        }
    }
}
