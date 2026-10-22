package com.example.sqlitejava;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlitejava.database.DatabaseHelper;
import com.example.sqlitejava.model.NhanSu;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class AddEditNhanSuActivity extends AppCompatActivity {

    public static final String EXTRA_NHAN_SU = "EXTRA_NHAN_SU";

    private TextView tvFormTitle;
    private ImageView imgFormAvatar;
    private MaterialButton btnChooseImage, btnSave, btnCancel;
    private EditText etMaNS, etHoTen, etNamSinh, etQueQuan, etMucLuong;
    private Spinner spinnerPhongBan, spinnerTrinhDo;
    private RadioButton rbNam, rbNu;

    private DatabaseHelper dbHelper;
    private NhanSu editingNhanSu;
    private String selectedImageUriStr = "";
    private boolean isEditMode = false;

    private static final String[] PHONG_BAN_ARRAY = {"Hành chính", "Phần mềm", "Kỹ thuật", "Dự án"};
    private static final String[] TRINH_DO_ARRAY = {"Trung cấp", "Cao đẳng", "Đại học", "Thạc sĩ", "Tiến sĩ"};

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {
                    }
                    selectedImageUriStr = uri.toString();
                    imgFormAvatar.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_nhan_su);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupSpinners();

        if (getIntent().hasExtra(EXTRA_NHAN_SU)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                editingNhanSu = getIntent().getSerializableExtra(EXTRA_NHAN_SU, NhanSu.class);
            } else {
                editingNhanSu = (NhanSu) getIntent().getSerializableExtra(EXTRA_NHAN_SU);
            }
            if (editingNhanSu != null) {
                isEditMode = true;
                fillFormForEdit();
            }
        }

        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNhanSu());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        imgFormAvatar = findViewById(R.id.imgFormAvatar);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        etMaNS = findViewById(R.id.etMaNS);
        etHoTen = findViewById(R.id.etHoTen);
        etNamSinh = findViewById(R.id.etNamSinh);
        etQueQuan = findViewById(R.id.etQueQuan);
        etMucLuong = findViewById(R.id.etMucLuong);

        spinnerPhongBan = findViewById(R.id.spinnerPhongBan);
        spinnerTrinhDo = findViewById(R.id.spinnerTrinhDo);

        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);
    }

    private void setupSpinners() {
        ArrayAdapter<String> phongBanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PHONG_BAN_ARRAY);
        phongBanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhongBan.setAdapter(phongBanAdapter);

        ArrayAdapter<String> trinhDoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TRINH_DO_ARRAY);
        trinhDoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrinhDo.setAdapter(trinhDoAdapter);
    }

    private void fillFormForEdit() {
        tvFormTitle.setText(R.string.title_edit_nhan_su);

        etMaNS.setText(editingNhanSu.getMaNS());
        etMaNS.setEnabled(false);

        etHoTen.setText(editingNhanSu.getHoTen());
        etNamSinh.setText(String.valueOf(editingNhanSu.getNamSinh()));
        etQueQuan.setText(editingNhanSu.getQueQuan());
        etMucLuong.setText(String.valueOf((long) editingNhanSu.getMucLuong()));

        if ("Nữ".equalsIgnoreCase(editingNhanSu.getGioiTinh())) {
            rbNu.setChecked(true);
        } else {
            rbNam.setChecked(true);
        }

        setSpinnerSelection(spinnerPhongBan, PHONG_BAN_ARRAY, editingNhanSu.getPhongBan());
        setSpinnerSelection(spinnerTrinhDo, TRINH_DO_ARRAY, editingNhanSu.getTrinhDo());

        selectedImageUriStr = editingNhanSu.getAnh() != null ? editingNhanSu.getAnh() : "";
        if (!selectedImageUriStr.trim().isEmpty()) {
            try {
                imgFormAvatar.setImageURI(Uri.parse(selectedImageUriStr));
            } catch (Exception e) {
                imgFormAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            imgFormAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveNhanSu() {
        String maNS = etMaNS.getText().toString().trim();
        String hoTen = etHoTen.getText().toString().trim();
        String namSinhStr = etNamSinh.getText().toString().trim();
        String queQuan = etQueQuan.getText().toString().trim();
        String mucLuongStr = etMucLuong.getText().toString().trim();

        if (maNS.isEmpty()) {
            etMaNS.setError("Vui lòng nhập mã nhân sự!");
            etMaNS.requestFocus();
            return;
        }

        if (hoTen.isEmpty()) {
            etHoTen.setError("Vui lòng nhập họ và tên!");
            etHoTen.requestFocus();
            return;
        }

        int namSinh;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        try {
            namSinh = Integer.parseInt(namSinhStr);
            if (namSinh < 1900 || namSinh > currentYear) {
                etNamSinh.setError("Năm sinh từ 1900 đến " + currentYear);
                etNamSinh.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etNamSinh.setError("Vui lòng nhập năm sinh hợp lệ!");
            etNamSinh.requestFocus();
            return;
        }

        if (queQuan.isEmpty()) {
            etQueQuan.setError("Vui lòng nhập quê quán!");
            etQueQuan.requestFocus();
            return;
        }

        double mucLuong;
        try {
            mucLuong = Double.parseDouble(mucLuongStr);
            if (mucLuong <= 0) {
                etMucLuong.setError("Mức lương phải lớn hơn 0!");
                etMucLuong.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etMucLuong.setError("Vui lòng nhập mức lương hợp lệ!");
            etMucLuong.requestFocus();
            return;
        }

        String gioiTinh = rbNu.isChecked() ? "Nữ" : "Nam";
        String phongBan = spinnerPhongBan.getSelectedItem() != null ? spinnerPhongBan.getSelectedItem().toString() : PHONG_BAN_ARRAY[0];
        String trinhDo = spinnerTrinhDo.getSelectedItem() != null ? spinnerTrinhDo.getSelectedItem().toString() : TRINH_DO_ARRAY[0];

        if (!isEditMode && dbHelper.checkMaNSExists(maNS)) {
            etMaNS.setError("Mã nhân sự đã tồn tại!");
            etMaNS.requestFocus();
            return;
        }

        NhanSu ns = new NhanSu(maNS, hoTen, namSinh, gioiTinh, queQuan, trinhDo, selectedImageUriStr, mucLuong, phongBan);

        boolean success;
        if (isEditMode) {
            success = dbHelper.updateNhanSu(ns);
        } else {
            success = dbHelper.insertNhanSu(ns);
        }

        if (success) {
            Toast.makeText(this, isEditMode ? "Cập nhật thành công!" : "Thêm mới thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
