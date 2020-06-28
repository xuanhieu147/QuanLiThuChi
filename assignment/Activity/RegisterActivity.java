package com.example.assignment.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment.DataBase.DataBase;
import com.example.assignment.Model.NguoiDung;
import com.example.assignment.R;

public class RegisterActivity extends AppCompatActivity {

    EditText edtuser,edtpass;
    Button btndangky,btnhuy;
    DataBase dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        anhXa();
        dataBase = new DataBase(this);
        btndangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              checkvalid();
            }
        });
    }
    private void anhXa(){
        edtuser = findViewById(R.id.edtuserdangky);
        edtpass  = findViewById(R.id.edtpassdangky);
        btndangky = findViewById(R.id.btnDangky);
        btnhuy = findViewById(R.id.btnhuydangky);
    }
    private void checkvalid() {
        String user = edtuser.getText().toString().trim();
        String pass = edtpass.getText().toString().trim();
        if (user.isEmpty()) {
            edtuser.setError("Vui Lòng Nhập Tên!");
            edtuser.requestFocus();

        }else if(pass.isEmpty()){
            edtpass.setError("Vui Lòng Nhập Mật Khẩu");
            edtpass.requestFocus();
        }
        else {
            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setUserName(user);
            nguoiDung.setPass(pass);
            dataBase.addNguoiDung(nguoiDung);
            Toast.makeText(this, "Đăng Ký Thành Công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
//                        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

        }
    }
}
