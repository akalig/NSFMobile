package com.mycompany.nsfmobile.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mycompany.nsfmobile.R;
import com.mycompany.nsfmobile.adapters.BookingData;
import com.mycompany.nsfmobile.ProductList;
import com.mycompany.nsfmobile.database.Database;

import java.util.ArrayList;

public class BookingDataAdapter extends RecyclerView.Adapter<BookingDataAdapter.ViewHolder> {

    private ArrayList<BookingData> bookingDataList;
    private OnItemClickListener onItemClickListener;
    private boolean clickable = true;

    public String ipAddress = "";

    public void setIPAddress(String strIPAddress) {
        this.ipAddress = strIPAddress;
    }

    public BookingDataAdapter(ArrayList<BookingData> bookingDataList) {
        this.bookingDataList = bookingDataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingData bookingData = bookingDataList.get(position);

        Database database = new Database();
        database.createConnection(this.ipAddress);
        database.checkBookingData(bookingData.getBookingNo().toString());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String checker = database.getBooknoChecker();

        if (checker.equals(null) || checker.isEmpty()) {
            holder.textBookingNo.setText(bookingData.getBookingNo());
            holder.textPONo.setText(bookingData.getPono());
            holder.textCustomerName.setText(bookingData.getCustomerName());
        } else {
            holder.textBookingNo.setText(bookingData.getBookingNo());
            holder.textPONo.setText(bookingData.getPono());
            holder.textCustomerName.setText("Processed");
            holder.textBookingNo.setTextColor(Color.parseColor("#ffffff"));
            holder.textPONo.setTextColor(Color.parseColor("#ffffff"));
            holder.textCustomerName.setTextColor(Color.parseColor("#ffffff"));
            holder.itemView.setBackgroundColor(Color.parseColor("#086e08"));
//            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return bookingDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(BookingData bookingData);


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textBookingNo;
        TextView textPONo;
        TextView textCustomerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBookingNo = itemView.findViewById(R.id.text_booking_no);
            textPONo = itemView.findViewById(R.id.text_po_no);
            textCustomerName = itemView.findViewById(R.id.text_customer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookingData bookingData = bookingDataList.get(position);
                    onItemClickListener.onItemClick(bookingData);
                }
            }
        }
    }
}
