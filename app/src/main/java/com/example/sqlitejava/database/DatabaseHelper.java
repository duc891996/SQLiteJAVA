package com.example.sqlitejava.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sqlitejava.model.NhanSu;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuanLyNhanSu.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NHAN_SU = "nhan_su";
    public static final String COLUMN_MA_NS = "ma_ns";
    public static final String COLUMN_HO_TEN = "ho_ten";
    public static final String COLUMN_NAM_SINH = "nam_sinh";
    public static final String COLUMN_GIOI_TINH = "gioi_tinh";
    public static final String COLUMN_QUE_QUAN = "que_quan";
    public static final String COLUMN_TRINH_DO = "trinh_do";
    public static final String COLUMN_ANH = "anh";
    public static final String COLUMN_MUC_LUONG = "muc_luong";
    public static final String COLUMN_PHONG_BAN = "phong_ban";

    private static final String CREATE_TABLE_NHAN_SU = "CREATE TABLE " + TABLE_NHAN_SU + " (" +
            COLUMN_MA_NS + " TEXT PRIMARY KEY, " +
            COLUMN_HO_TEN + " TEXT NOT NULL, " +
            COLUMN_NAM_SINH + " INTEGER, " +
            COLUMN_GIOI_TINH + " TEXT, " +
            COLUMN_QUE_QUAN + " TEXT, " +
            COLUMN_TRINH_DO + " TEXT, " +
            COLUMN_ANH + " TEXT, " +
            COLUMN_MUC_LUONG + " REAL, " +
            COLUMN_PHONG_BAN + " TEXT" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NHAN_SU);
        seedSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHAN_SU);
        onCreate(db);
    }

    private void seedSampleData(SQLiteDatabase db) {
        insertSample(db, "NS001", "Nguyễn Văn An", 1992, "Nam", "Hà Nội", "Đại học", "", 18500000, "Phần mềm");
        insertSample(db, "NS002", "Trần Thị Bích", 1995, "Nữ", "Đà Nẵng", "Thạc sĩ", "", 22000000, "Hành chính");
        insertSample(db, "NS003", "Lê Hoàng Nam", 1990, "Nam", "TP. Hồ Chí Minh", "Đại học", "", 15000000, "Kỹ thuật");
        insertSample(db, "NS004", "Phạm Minh Dung", 1998, "Nữ", "Cần Thơ", "Cao đẳng", "", 12500000, "Dự án");
    }

    private void insertSample(SQLiteDatabase db, String ma, String ten, int nam, String gt, String qq, String td, String anh, double luong, String pb) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_NS, ma);
        values.put(COLUMN_HO_TEN, ten);
        values.put(COLUMN_NAM_SINH, nam);
        values.put(COLUMN_GIOI_TINH, gt);
        values.put(COLUMN_QUE_QUAN, qq);
        values.put(COLUMN_TRINH_DO, td);
        values.put(COLUMN_ANH, anh);
        values.put(COLUMN_MUC_LUONG, luong);
        values.put(COLUMN_PHONG_BAN, pb);
        db.insert(TABLE_NHAN_SU, null, values);
    }

    public boolean checkMaNSExists(String maNS) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NHAN_SU, new String[]{COLUMN_MA_NS},
                COLUMN_MA_NS + "=?", new String[]{maNS}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean insertNhanSu(NhanSu ns) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_NS, ns.getMaNS());
        values.put(COLUMN_HO_TEN, ns.getHoTen());
        values.put(COLUMN_NAM_SINH, ns.getNamSinh());
        values.put(COLUMN_GIOI_TINH, ns.getGioiTinh());
        values.put(COLUMN_QUE_QUAN, ns.getQueQuan());
        values.put(COLUMN_TRINH_DO, ns.getTrinhDo());
        values.put(COLUMN_ANH, ns.getAnh());
        values.put(COLUMN_MUC_LUONG, ns.getMucLuong());
        values.put(COLUMN_PHONG_BAN, ns.getPhongBan());

        long result = db.insert(TABLE_NHAN_SU, null, values);
        return result != -1;
    }

    public boolean updateNhanSu(NhanSu ns) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HO_TEN, ns.getHoTen());
        values.put(COLUMN_NAM_SINH, ns.getNamSinh());
        values.put(COLUMN_GIOI_TINH, ns.getGioiTinh());
        values.put(COLUMN_QUE_QUAN, ns.getQueQuan());
        values.put(COLUMN_TRINH_DO, ns.getTrinhDo());
        values.put(COLUMN_ANH, ns.getAnh());
        values.put(COLUMN_MUC_LUONG, ns.getMucLuong());
        values.put(COLUMN_PHONG_BAN, ns.getPhongBan());

        int result = db.update(TABLE_NHAN_SU, values, COLUMN_MA_NS + "=?", new String[]{ns.getMaNS()});
        return result > 0;
    }

    public boolean deleteNhanSu(String maNS) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NHAN_SU, COLUMN_MA_NS + "=?", new String[]{maNS});
        return result > 0;
    }

    public List<NhanSu> getAllNhanSu() {
        return searchAndSort("", "DEFAULT");
    }

    public List<NhanSu> searchAndSort(String query, String sortMode) {
        List<NhanSu> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;

        if (query != null && !query.trim().isEmpty()) {
            selection = COLUMN_MA_NS + " LIKE ? OR " +
                        COLUMN_HO_TEN + " LIKE ? OR " +
                        COLUMN_PHONG_BAN + " LIKE ? OR " +
                        COLUMN_QUE_QUAN + " LIKE ?";
            String wildQuery = "%" + query.trim() + "%";
            selectionArgs = new String[]{wildQuery, wildQuery, wildQuery, wildQuery};
        }

        String orderBy = null;
        if ("SALARY_ASC".equalsIgnoreCase(sortMode)) {
            orderBy = COLUMN_MUC_LUONG + " ASC";
        } else if ("SALARY_DESC".equalsIgnoreCase(sortMode)) {
            orderBy = COLUMN_MUC_LUONG + " DESC";
        }

        Cursor cursor = db.query(TABLE_NHAN_SU, null, selection, selectionArgs, null, null, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                NhanSu ns = new NhanSu();
                ns.setMaNS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MA_NS)));
                ns.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HO_TEN)));
                ns.setNamSinh(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAM_SINH)));
                ns.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GIOI_TINH)));
                ns.setQueQuan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUE_QUAN)));
                ns.setTrinhDo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRINH_DO)));
                ns.setAnh(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANH)));
                ns.setMucLuong(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MUC_LUONG)));
                ns.setPhongBan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONG_BAN)));
                list.add(ns);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }
}
