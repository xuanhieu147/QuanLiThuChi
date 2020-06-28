package com.example.assignment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.assignment.DataBase.DataBase;
import com.example.assignment.Model.NguoiDung;
import com.example.assignment.R;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout txtusernam, txtpass;
    CheckBox cbmk;
    Button btndangnhap, btndangky;
    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhXa();
        dataBase = new DataBase(this);
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkvalid();
            }
        });
        btndangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    

    private boolean checkvalid() {
        String user = txtusernam.getEditText().getText().toString().trim();
        String pass = txtpass.getEditText().getText().toString().trim();
        if (user.isEmpty()) {
            txtusernam.setError("Vui Lòng Nhập Tên!");
            txtusernam.requestFocus();
            return false;
        } else if (pass.isEmpty()) {
            txtpass.setError("Vui Lòng Nhập Mật Khẩu");
            txtpass.requestFocus();
            return false;
        } else if (user.equalsIgnoreCase("admin") && pass.equalsIgnoreCase("admin")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toasty.success(LoginActivity.this, "Đăng Nhập Thành Công",
                    Toast.LENGTH_SHORT, true).show();
            finish();
            return true;
        } else {
            boolean kt = dataBase.kiemTraDangNhap(user, pass);
            if (kt) {
                Toasty.success(LoginActivity.this, "Đăng Nhập Thành Công",
                        Toast.LENGTH_SHORT, true).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return true;
            }else {
                Toasty.error(LoginActivity.this, "Đăng Nhập Thất Bại",
                        Toast.LENGTH_SHORT, true).show();
                return false;
            }
        }

    }

    private void anhXa() {
        txtusernam = findViewById(R.id.edtusername);
        txtpass = findViewById(R.id.edtpass);
        btndangky = findViewById(R.id.btndangky);
        btndangnhap = findViewById(R.id.btndangnhap);
        cbmk = findViewById(R.id.cbtaikhoan);
    }

}
