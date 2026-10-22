package com.example.sqlitejava.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlitejava.R;
import com.example.sqlitejava.model.NhanSu;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhanSuAdapter extends RecyclerView.Adapter<NhanSuAdapter.NhanSuViewHolder> {

    public interface OnNhanSuActionListener {
        void onItemClick(NhanSu nhanSu);
        void onEditClick(NhanSu nhanSu);
        void onDeleteClick(NhanSu nhanSu);
    }

    private final Context context;
    private List<NhanSu> listNhanSu;
    private final OnNhanSuActionListener listener;
    private final DecimalFormat formatter;

    public NhanSuAdapter(Context context, List<NhanSu> listNhanSu, OnNhanSuActionListener listener) {
        this.context = context;
        this.listNhanSu = listNhanSu != null ? listNhanSu : new ArrayList<>();
        this.listener = listener;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        this.formatter = new DecimalFormat("#,###", symbols);
    }

    public void setList(List<NhanSu> newList) {
        this.listNhanSu = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NhanSuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nhan_su, parent, false);
        return new NhanSuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhanSuViewHolder holder, int position) {
        NhanSu ns = listNhanSu.get(position);

        holder.tvHoTen.setText(ns.getHoTen());
        holder.tvMaAndPhongBan.setText(String.format("%s - Phòng: %s", ns.getMaNS(), ns.getPhongBan()));
        holder.tvChiTietShort.setText(String.format("Giới tính: %s | Năm sinh: %d", ns.getGioiTinh(), ns.getNamSinh()));
        holder.tvTrinhDoQueQuan.setText(String.format("Trình độ: %s | Quê: %s", ns.getTrinhDo(), ns.getQueQuan()));

        String salaryFormatted = formatter.format(ns.getMucLuong()) + " VNĐ";
        holder.tvMucLuong.setText(String.format("Lương: %s", salaryFormatted));

        if (ns.getAnh() != null && !ns.getAnh().trim().isEmpty()) {
            try {
                holder.imgAvatar.setImageURI(Uri.parse(ns.getAnh()));
            } catch (Exception e) {
                holder.imgAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_avatar);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(ns);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(ns);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(ns);
        });
    }

    @Override
    public int getItemCount() {
        return listNhanSu.size();
    }

    public static class NhanSuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvHoTen, tvMaAndPhongBan, tvChiTietShort, tvTrinhDoQueQuan, tvMucLuong;
        ImageButton btnEdit, btnDelete;

        public NhanSuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvMaAndPhongBan = itemView.findViewById(R.id.tvMaAndPhongBan);
            tvChiTietShort = itemView.findViewById(R.id.tvChiTietShort);
            tvTrinhDoQueQuan = itemView.findViewById(R.id.tvTrinhDoQueQuan);
            tvMucLuong = itemView.findViewById(R.id.tvMucLuong);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
