package com.example.sqlitejava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlitejava.adapter.NhanSuAdapter;
import com.example.sqlitejava.database.DatabaseHelper;
import com.example.sqlitejava.model.NhanSu;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NhanSuAdapter.OnNhanSuActionListener {

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private Spinner spinnerSort;
    private MaterialButton btnAddNhanSu;
    private RecyclerView rvNhanSu;
    private TextView tvEmptyList;

    private DatabaseHelper dbHelper;
    private NhanSuAdapter adapter;
    private List<NhanSu> currentList = new ArrayList<>();

    private String currentSearchQuery = "";
    private String currentSortMode = "DEFAULT";

    private static final String[] SORT_OPTIONS = {
            "Sắp xếp: Mặc định",
            "Lương: Thấp -> Cao",
            "Lương: Cao -> Thấp"
    };

    private final ActivityResultLauncher<Intent> addEditLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupSortSpinner();
        setupRecyclerView();
        setupSearchListener();

        btnAddNhanSu.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNhanSuActivity.class);
            addEditLauncher.launch(intent);
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));

        loadData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        spinnerSort = findViewById(R.id.spinnerSort);
        btnAddNhanSu = findViewById(R.id.btnAddNhanSu);
        rvNhanSu = findViewById(R.id.rvNhanSu);
        tvEmptyList = findViewById(R.id.tvEmptyList);
    }

    private void setupSortSpinner() {
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, SORT_OPTIONS);
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        currentSortMode = "SALARY_ASC";
                        break;
                    case 2:
                        currentSortMode = "SALARY_DESC";
                        break;
                    default:
                        currentSortMode = "DEFAULT";
                        break;
                }
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        rvNhanSu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NhanSuAdapter(this, currentList, this);
        rvNhanSu.setAdapter(adapter);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                btnClearSearch.setVisibility(currentSearchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                loadData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadData() {
        currentList = dbHelper.searchAndSort(currentSearchQuery, currentSortMode);
        adapter.setList(currentList);

        if (currentList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            rvNhanSu.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvNhanSu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(NhanSu nhanSu) {
        showDetailDialog(nhanSu);
    }

    @Override
    public void onEditClick(NhanSu nhanSu) {
        Intent intent = new Intent(MainActivity.this, AddEditNhanSuActivity.class);
        intent.putExtra(AddEditNhanSuActivity.EXTRA_NHAN_SU, nhanSu);
        addEditLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(NhanSu nhanSu) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage("Bạn có chắc chắn muốn xóa nhân sự \"" + nhanSu.getHoTen() + "\" (Mã: " + nhanSu.getMaNS() + ") không?")
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                    boolean success = dbHelper.deleteNhanSu(nhanSu.getMaNS());
                    if (success) {
                        Toast.makeText(MainActivity.this, "Đã xóa nhân sự thành công!", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(MainActivity.this, "Xóa thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    private void showDetailDialog(NhanSu ns) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_detail_nhan_su, null);

        ImageView imgAvatar = view.findViewById(R.id.imgDetailAvatar);
        TextView tvHoTen = view.findViewById(R.id.tvDetailHoTen);
        TextView tvMaNS = view.findViewById(R.id.tvDetailMaNS);
        TextView tvPhongBan = view.findViewById(R.id.tvDetailPhongBan);
        TextView tvTrinhDo = view.findViewById(R.id.tvDetailTrinhDo);
        TextView tvNamSinh = view.findViewById(R.id.tvDetailNamSinh);
        TextView tvGioiTinh = view.findViewById(R.id.tvDetailGioiTinh);
        TextView tvQueQuan = view.findViewById(R.id.tvDetailQueQuan);
        TextView tvMucLuong = view.findViewById(R.id.tvDetailMucLuong);
        MaterialButton btnClose = view.findViewById(R.id.btnCloseDetail);

        tvHoTen.setText(ns.getHoTen());
        tvMaNS.setText("Mã NS: " + ns.getMaNS());
        tvPhongBan.setText("Phòng ban: " + ns.getPhongBan());
        tvTrinhDo.setText("Trình độ: " + ns.getTrinhDo());
        tvNamSinh.setText("Năm sinh: " + ns.getNamSinh());
        tvGioiTinh.setText("Giới tính: " + ns.getGioiTinh());
        tvQueQuan.setText("Quê quán: " + ns.getQueQuan());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        tvMucLuong.setText("Mức lương: " + formatter.format(ns.getMucLuong()) + " VNĐ");

        if (ns.getAnh() != null && !ns.getAnh().trim().isEmpty()) {
            try {
                imgAvatar.setImageURI(Uri.parse(ns.getAnh()));
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
