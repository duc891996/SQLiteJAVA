package com.example.sqlitejava.model;

import java.io.Serializable;

public class NhanSu implements Serializable {
    private String maNS;
    private String hoTen;
    private int namSinh;
    private String gioiTinh;
    private String queQuan;
    private String trinhDo;
    private String anh; // Uri string, drawable name, or base64 string
    private double mucLuong;
    private String phongBan;

    public NhanSu() {
    }

    public NhanSu(String maNS, String hoTen, int namSinh, String gioiTinh, String queQuan, String trinhDo, String anh, double mucLuong, String phongBan) {
        this.maNS = maNS;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.gioiTinh = gioiTinh;
        this.queQuan = queQuan;
        this.trinhDo = trinhDo;
        this.anh = anh;
        this.mucLuong = mucLuong;
        this.phongBan = phongBan;
    }

    public String getMaNS() {
        return maNS;
    }

    public void setMaNS(String maNS) {
        this.maNS = maNS;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(int namSinh) {
        this.namSinh = namSinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getQueQuan() {
        return queQuan;
    }

    public void setQueQuan(String queQuan) {
        this.queQuan = queQuan;
    }

    public String getTrinhDo() {
        return trinhDo;
    }

    public void setTrinhDo(String trinhDo) {
        this.trinhDo = trinhDo;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public double getMucLuong() {
        return mucLuong;
    }

    public void setMucLuong(double mucLuong) {
        this.mucLuong = mucLuong;
    }

    public String getPhongBan() {
        return phongBan;
    }

    public void setPhongBan(String phongBan) {
        this.phongBan = phongBan;
    }
}
