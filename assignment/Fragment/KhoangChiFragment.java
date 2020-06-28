package com.example.assignment.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.Adapter.KhoangChiAdapter;
import com.example.assignment.Adapter.Spinner_KhoangChi_Adapter;
import com.example.assignment.DataBase.DataBase;
import com.example.assignment.Interface.SetOnItemClickListener;
import com.example.assignment.Model.KhoangChi;

import com.example.assignment.Model.KhoangThu;
import com.example.assignment.Model.LoaiChi;
import com.example.assignment.R;
import com.kinda.alert.KAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class KhoangChiFragment extends Fragment {
    Calendar calendar = Calendar.getInstance();
     DataBase dataBase;
     int id_loaichi;
    String donvi;
    Spinner spChonKhoangChi, spdonVi, spinnerss;
    RecyclerView recyclerView;
    EditText edtngay;
    FloatingActionButton floatingActionButton;
    ArrayList<String> arrTienTe = new ArrayList<>();
    static ArrayList<KhoangChi> arrKhoangChi = new ArrayList<>();
    ArrayList<LoaiChi> arrSpinner = new ArrayList<>();
     KhoangChiAdapter khoangChiAdapter;
    Spinner_KhoangChi_Adapter spinner_khoangChi_adapter;
    ArrayAdapter<String> arrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khoangchi, container, false);
        floatingActionButton = view.findViewById(R.id.floatingkhoangchi);
        recyclerView = view.findViewById(R.id.recyclerviewkhoangchi);
        dataBase = new DataBase(getActivity());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAdd();
                spinnerDonViTienTe();
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        khoangChiAdapter = new KhoangChiAdapter(getActivity(), arrKhoangChi, R.layout.dong_khoangchi);
        recyclerView.setAdapter(khoangChiAdapter);
        khoangChiAdapter.setOnItemClickListener(new SetOnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                dialogXemChiTiet(position);
            }

            @Override
            public void onEdit(int position) {
                KhoangChi khoangChi = arrKhoangChi.get(position);
                dialogSua(khoangChi);
            }

            @Override
            public void onDelete(int position) {
                KhoangChi khoangChi = arrKhoangChi.get(position);
                dialogXoa(khoangChi);
            }
        });

        getData();
        return view;
    }

    private void dialogAdd() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_add_kc);
        dialog.setCanceledOnTouchOutside(true);
        spChonKhoangChi = dialog.findViewById(R.id.spinnerchi);
        spdonVi = dialog.findViewById(R.id.spinnerDonvichi);
        final EditText edttien = dialog.findViewById(R.id.edtnhaptienchi);
        final EditText edtmota = dialog.findViewById(R.id.edtmotakc);
        final EditText edtTen = dialog.findViewById(R.id.edtthemtenchi);
        edtngay = dialog.findViewById(R.id.edtngaykc);

        final Button btnhuy = dialog.findViewById(R.id.btnhuykc);
        Button btnthem = dialog.findViewById(R.id.btnthemkc);
        chonNgay();
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tien = edttien.getText().toString();
                String mota = edtmota.getText().toString();
                String ngay = edtngay.getText().toString();
                String ten = edtTen.getText().toString();
                if (tien.isEmpty() || mota.isEmpty() || ngay.isEmpty() || ten.isEmpty()) {
                    Toasty.warning(getActivity(), "Vui Lòng Nhập Đầy Đủ Thông Tin",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                } else {
                    KhoangChi khoangChi = new KhoangChi();
//                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
//                    Date date = null;
//                    try {
//                        date = format.parse(ngay);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                    khoangChi.setId_loaichi(id_loaichi);
                    khoangChi.setTen(ten);
                    khoangChi.setGia(tien);
                    khoangChi.setDonVi(donvi);
                    Log.d("KHOANGCHI",ngay+"");

                    khoangChi.setNgay(ngay+"");
                    khoangChi.setMoTa(mota);
                    dataBase.addKhoangChi(khoangChi);
                    CustomDialogSuccess();
                    getData();
                    dialog.dismiss();
                }
            }
        });
        btnhuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        List<LoaiChi> list = getdataSpinner();
        Spinner(list);
        spinnerDonViTienTe();
        dialog.show();
    }

    private void chonNgay() {
        edtngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i, i1, i2);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        edtngay.setText(format.format(calendar.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

    }

    private void Spinner(final List<LoaiChi> arr) {
        spinner_khoangChi_adapter = new Spinner_KhoangChi_Adapter(getActivity(), arr, R.layout.dong_spinner_kc);
        spChonKhoangChi.setAdapter(spinner_khoangChi_adapter);
        spinner_khoangChi_adapter.notifyDataSetChanged();
        spChonKhoangChi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_loaichi = arr.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<LoaiChi> getdataSpinner() {
        List list = new ArrayList<LoaiChi>();
        Cursor dataSpinner = dataBase.getData("SELECT * FROM " + dataBase.KEY_TABLE_NAME_LOAICHI + " WHERE " + dataBase.KEY_TABLE_DELETEFLAG_LOAICHI + " = 0");
        list.clear();
        while (dataSpinner.moveToNext()) {
            int id = dataSpinner.getInt(0);
            String ten = dataSpinner.getString(1);
            int deleteflag = dataSpinner.getInt(2);
            list.add(new LoaiChi(id, deleteflag, ten));
        }
        return list;
    }

    public void getData() {
        Cursor cursor = dataBase.getData("SELECT * FROM " + dataBase.KEY_TABLE_NAME_KHOANGCHI + " WHERE " + dataBase.KEY_TABLE_DELETEFLAG_KHOANGCHI + " = 0");
        arrKhoangChi.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String dinhMucThu = cursor.getString(2);
            String donvithu = cursor.getString(3);
            String thoidiem = cursor.getString(4);
            id_loaichi = cursor.getInt(5);
            int danhgia = cursor.getInt(6);
            int deleteflag = cursor.getInt(7);
            String mota = cursor.getString(8);
            arrKhoangChi.add(new KhoangChi(id, ten, dinhMucThu, donvithu, thoidiem, mota, deleteflag, id_loaichi, danhgia));
        }
        khoangChiAdapter.notifyDataSetChanged();
    }

    private void daTaDonViTienTe() {
        arrTienTe.add("VND");
        arrTienTe.add("RUB");
        arrTienTe.add("AFN");
        arrTienTe.add("EUR");
        arrTienTe.add("ALL");
        arrTienTe.add("None");
        arrTienTe.add("GBP");
        arrTienTe.add("GGP[O]");
        arrTienTe.add("DZD");
        arrTienTe.add("EUR");
        arrTienTe.add("AOA");
        arrTienTe.add("XCD");
        arrTienTe.add("XCD");
        arrTienTe.add("ARS");
        arrTienTe.add("AMD");
        arrTienTe.add("AWG");
        arrTienTe.add("None");
        arrTienTe.add("SHP");
        arrTienTe.add("AUD");
        arrTienTe.add("EUR");
        arrTienTe.add("AZN");
        arrTienTe.add("BSD");
        arrTienTe.add("BHD");
        arrTienTe.add("BDT");
        arrTienTe.add("BBD");
        arrTienTe.add("BYR");
        arrTienTe.add("EUR");
        arrTienTe.add("BZD");
        arrTienTe.add("XOF");
        arrTienTe.add("BMD");
        arrTienTe.add("BTN");
        arrTienTe.add("INR");
        arrTienTe.add("BOB");
        arrTienTe.add("USD");
        arrTienTe.add("BAM");
        arrTienTe.add("BWP");
        arrTienTe.add("BRL");
        arrTienTe.add("USD");
        arrTienTe.add("None");
        arrTienTe.add("USD");
        arrTienTe.add("BND");
        arrTienTe.add("SGD");
        arrTienTe.add("BGN");
        arrTienTe.add("XOF");
        arrTienTe.add("MMK");
        arrTienTe.add("BIF");
        arrTienTe.add("KHR");
        arrTienTe.add("XAF");
        arrTienTe.add("CAD");
        arrTienTe.add("CVE");
        arrTienTe.add("KYD");
        arrTienTe.add("XAF");
        arrTienTe.add("XAF");
        arrTienTe.add("CLP");
        arrTienTe.add("CNY");
        arrTienTe.add("AUD");
        arrTienTe.add("COP");
        arrTienTe.add("KMF");
        arrTienTe.add("CDF");
        arrTienTe.add("XAF");
        arrTienTe.add("NZD");
        arrTienTe.add("None");
        arrTienTe.add("CRC");
        arrTienTe.add("XOF");
        arrTienTe.add("HRK");
        arrTienTe.add("CUC");
        arrTienTe.add("CUP");
        arrTienTe.add("ANG");
        arrTienTe.add("EUR");
        arrTienTe.add("CZK");
        arrTienTe.add("DKK");
        arrTienTe.add("DJF");
        arrTienTe.add("XCD");
        arrTienTe.add("DOP");
        arrTienTe.add("USD");
        arrTienTe.add("None");
        arrTienTe.add("USD");
        arrTienTe.add("None");
        arrTienTe.add("EGP");
        arrTienTe.add("SVC");
        arrTienTe.add("USD");
        arrTienTe.add("XAF");
        arrTienTe.add("ERN");
        arrTienTe.add("EUR");
        arrTienTe.add("ETB");
        arrTienTe.add("FKP");
        arrTienTe.add("DKK");
        arrTienTe.add("None");
        arrTienTe.add("FJD");
        arrTienTe.add("EUR");
        arrTienTe.add("EUR");
        arrTienTe.add("XPF");
        arrTienTe.add("XAF");
        arrTienTe.add("GMD");
        arrTienTe.add("GEL");
        arrTienTe.add("EUR");
        arrTienTe.add("GHS");
        arrTienTe.add("GIP");
        arrTienTe.add("EUR");
        arrTienTe.add("XCD");
        arrTienTe.add("GTQ");
        arrTienTe.add("GBP");
        arrTienTe.add("None");
        arrTienTe.add("GNF");
        arrTienTe.add("XOF");
        arrTienTe.add("GYD");
        arrTienTe.add("HTG");
        arrTienTe.add("HNL");
        arrTienTe.add("HKD");
        arrTienTe.add("HUF");
        arrTienTe.add("ISK");
        arrTienTe.add("INR");
        arrTienTe.add("IDR");
        arrTienTe.add("IRR");
        arrTienTe.add("IQD");
        arrTienTe.add("EUR");
        arrTienTe.add("GBP");
        arrTienTe.add("IMP[O]");
        arrTienTe.add("ILS");
        arrTienTe.add("EUR");
        arrTienTe.add("JMD");
        arrTienTe.add("JPY");
        arrTienTe.add("GBP");
        arrTienTe.add("JEP[O]");
        arrTienTe.add("JOD");
        arrTienTe.add("KZT");
        arrTienTe.add("KES");
        arrTienTe.add("AUD");
        arrTienTe.add("None");
        arrTienTe.add("KPW");
        arrTienTe.add("KRW");
        arrTienTe.add("EUR");
        arrTienTe.add("KWD");
        arrTienTe.add("KGS");
        arrTienTe.add("LAK");
        arrTienTe.add("EUR");
        arrTienTe.add("LBP");
        arrTienTe.add("LSL");
        arrTienTe.add("ZAR");
        arrTienTe.add("LRD");
        arrTienTe.add("LYD");
        arrTienTe.add("CHF");
        arrTienTe.add("EUR");
        arrTienTe.add("EUR");
        arrTienTe.add("MOP");
        arrTienTe.add("MKD");
        arrTienTe.add("MGA");
        arrTienTe.add("MWK");
        arrTienTe.add("MYR");
        arrTienTe.add("MVR");
        arrTienTe.add("XOF");
        arrTienTe.add("EUR");
        arrTienTe.add("USD");
        arrTienTe.add("MRO");
        arrTienTe.add("MUR");
        arrTienTe.add("MXN");
        arrTienTe.add("None");
        arrTienTe.add("USD");
        arrTienTe.add("MDL");
        arrTienTe.add("EUR");
        arrTienTe.add("MNT");
        arrTienTe.add("EUR");
        arrTienTe.add("XCD");
        arrTienTe.add("MAD");
        arrTienTe.add("MZN");
        arrTienTe.add("AMD");
        arrTienTe.add("None");
        arrTienTe.add("NAD");
        arrTienTe.add("ZAR");
        arrTienTe.add("AUD");
        arrTienTe.add("None");
        arrTienTe.add("NPR");
        arrTienTe.add("EUR");
        arrTienTe.add("XPF");
        arrTienTe.add("NZD");
        arrTienTe.add("NIO");
        arrTienTe.add("XOF");
        arrTienTe.add("NGN");
        arrTienTe.add("NZD");
        arrTienTe.add("None");
        arrTienTe.add("TRY");
        arrTienTe.add("NOK");
        arrTienTe.add("OMR");
        arrTienTe.add("PKR");
        arrTienTe.add("None");
        arrTienTe.add("USD");
        arrTienTe.add("ILS");
        arrTienTe.add("JOD");
        arrTienTe.add("PAB");
        arrTienTe.add("USD");
        arrTienTe.add("PGK");
        arrTienTe.add("PYG");
        arrTienTe.add("PEN");
        arrTienTe.add("PHP");
        arrTienTe.add("NZD");
        arrTienTe.add("None");
        arrTienTe.add("PLN");
        arrTienTe.add("EUR");
        arrTienTe.add("QAR");
        arrTienTe.add("RON");
        arrTienTe.add("RUB");
        arrTienTe.add("RWF");
        arrTienTe.add("USD");
        arrTienTe.add("DZD");
        arrTienTe.add("MRO");
        arrTienTe.add("MAD");
        arrTienTe.add("None");
        arrTienTe.add("SHP");
        arrTienTe.add("XCD");
        arrTienTe.add("XCD");
        arrTienTe.add("XCD");
        arrTienTe.add("WST");
        arrTienTe.add("EUR");
        arrTienTe.add("STD");
        arrTienTe.add("SAR");
        arrTienTe.add("XOF");
        arrTienTe.add("RSD");
        arrTienTe.add("SCR");
        arrTienTe.add("SLL");
        arrTienTe.add("BND");
        arrTienTe.add("SGD");
        arrTienTe.add("USD");
        arrTienTe.add("ANG");
        arrTienTe.add("EUR");
        arrTienTe.add("EUR");
        arrTienTe.add("SBD");
        arrTienTe.add("SOS");
        arrTienTe.add("None");
        arrTienTe.add("ZAR");
        arrTienTe.add("GBP");
        arrTienTe.add("None");
        arrTienTe.add("RUB");
        arrTienTe.add("EUR");
        arrTienTe.add("SSP");
        arrTienTe.add("LKR");
        arrTienTe.add("SDG");
        arrTienTe.add("SRD");
        arrTienTe.add("SZL");
        arrTienTe.add("SEK");
        arrTienTe.add("CHF");
        arrTienTe.add("SYP");
        arrTienTe.add("TWD");
        arrTienTe.add("TJS");
        arrTienTe.add("TZS");
        arrTienTe.add("THB");
        arrTienTe.add("XOF");
        arrTienTe.add("TOP");
        arrTienTe.add("PRB[O]");
        arrTienTe.add("TTD");
        arrTienTe.add("SHP");
        arrTienTe.add("None");
        arrTienTe.add("TND");
        arrTienTe.add("TRY");
        arrTienTe.add("TMT");
        arrTienTe.add("USD");
        arrTienTe.add("AUD");
        arrTienTe.add("None");
        arrTienTe.add("UGX");
        arrTienTe.add("UAH");
        arrTienTe.add("AED");
        arrTienTe.add("GBP");
        arrTienTe.add("USD");
        arrTienTe.add("UYU");
        arrTienTe.add("UZS");
        arrTienTe.add("VUV");
        arrTienTe.add("EUR");
        arrTienTe.add("VEF");
        arrTienTe.add("XPF");
        arrTienTe.add("YER");
        arrTienTe.add("ZMW");
        arrTienTe.add("BWP");
        arrTienTe.add("GBP");
        arrTienTe.add("EUR");
        arrTienTe.add("ZAR");
        arrTienTe.add("USD");
        arrTienTe.add("ZWL");
        ;
    }

    private void spinnerDonViTienTe() {
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrTienTe);
        daTaDonViTienTe();
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spdonVi.setAdapter(arrayAdapter);
        spdonVi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                donvi = arrTienTe.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void dialogXemChiTiet(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_xemchitiet_kc);

        TextView txtTenLoaiChi = dialog.findViewById(R.id.txttenloaichixem);
        TextView txtTen = dialog.findViewById(R.id.edttenkhoangchi);
        TextView txtSoTien = dialog.findViewById(R.id.edtsoTienchi);
        TextView txtMoTa = dialog.findViewById(R.id.edtmotachi);
        TextView txtNgay = dialog.findViewById(R.id.edtngaythang);
        TextView txtDonVi = dialog.findViewById(R.id.edtdonvichi);

        KhoangChi khoangChi = arrKhoangChi.get(position);
        String ten = khoangChi.getTen();
        String soTien = khoangChi.getGia();
        String moTa = khoangChi.getMoTa();
        String donVi = khoangChi.getDonVi();
        String ngaythang = khoangChi.getNgay();
        int idLoai = khoangChi.getId_loaichi();

//        Cursor cursor  = dataBase.getData("SELECT " + dataBase.KEY_TABLE_NAME_LOAITHU+ " .ten FROM " + dataBase.KEY_TABLE_NAME_LOAITHU +" INNER JOIN khoangthu WHERE '" + idLoai + "' loaithu.id = khoangthu.idloaithu");
        Cursor cursor = dataBase.getData("SELECT * FROM " + dataBase.KEY_TABLE_NAME_LOAICHI + " WHERE id = '" + idLoai + "' ");
        while (cursor.moveToNext()) {
            String tenLoai = cursor.getString(1);
            txtTenLoaiChi.setText(tenLoai);
        }
        txtTen.setText(ten);
        txtSoTien.setText(soTien);
        txtMoTa.setText(moTa);
        txtNgay.setText(ngaythang);
        txtDonVi.setText(donVi);
        dialog.show();
    }


    private void dialogSua(final KhoangChi khoangChi) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_sua_kc);
        dialog.setCancelable(true);
        spinnerss = dialog.findViewById(R.id.spinnersuakc);
        final EditText edtTen = dialog.findViewById(R.id.edtthemtensuakc);
        final EditText edtsotien = dialog.findViewById(R.id.edtnhaptiensuakc);
        Spinner spinner1 = dialog.findViewById(R.id.spinnerDonvisuakc);
        final EditText edtMoTa = dialog.findViewById(R.id.edtthemmotakc);
        final EditText edtNgay = dialog.findViewById(R.id.edtngaysuakc);
        final Button btnHuy = dialog.findViewById(R.id.btnhuysuakc);
        Button btnSua = dialog.findViewById(R.id.btnthemsuakc);

        final List<LoaiChi> list = getdataSpinner();
        spinner_khoangChi_adapter = new Spinner_KhoangChi_Adapter(getActivity(), list, R.layout.dong_spinner_kc);
        spinnerss.setAdapter(spinner_khoangChi_adapter);

        daTaDonViTienTe();
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrTienTe);
        spinner1.setAdapter(arrayAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                donvi = arrTienTe.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final String ten = khoangChi.getTen();
        final String moTa = khoangChi.getMoTa();
        final String ngay = khoangChi.getNgay();
        final String tien = khoangChi.getGia();

        edtMoTa.setText(moTa);
        edtTen.setText(ten);
        edtNgay.setText(ngay);
        edtsotien.setText(tien);
        spinnerss.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_loaichi = list.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTen.length() ==0 || edtMoTa.length() ==0 || edtNgay.length() ==0|| edtsotien.length() ==0) {
                    Toasty.warning(getActivity(), "Vui Lòng Nhập Đầy Đủ Thông Tin",
                            Toast.LENGTH_SHORT, true).show();
                } else {
                    khoangChi.setDonVi(donvi);
                    khoangChi.setId_loaichi(id_loaichi);
                    khoangChi.setTen(edtTen.getText().toString());
                    khoangChi.setGia(edtsotien.getText().toString());
                    khoangChi.setMoTa(edtMoTa.getText().toString());
                    khoangChi.setNgay(edtNgay.getText().toString());
                    dataBase.suaKhoangChi(khoangChi);
//                    Toast.makeText(getActivity(), "Sửa Thành Công", Toast.LENGTH_SHORT).show();
                    CustomDialogSuccess();
                    getData();
                    dialog.dismiss();
                }
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dialogXoa(final KhoangChi khoangChi) {
        final KAlertDialog kAlertDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
        kAlertDialog.setTitleText("Thông Báo?");
        kAlertDialog.setContentText("Bạn có chắc muốn xóa " + khoangChi.getTen() + " này không!");
        kAlertDialog.setCancelText("Không");
        kAlertDialog.setConfirmText("Có");
        kAlertDialog.showCancelButton(true);
        kAlertDialog.setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(KAlertDialog diaglog) {
                dataBase.QueryData("UPDATE khoangchi SET deleteflag = 1 WHERE id = '" + khoangChi.getId() + "' ");
                getData();
                Toasty.success(getActivity(), "Xóa Thành Công", Toast.LENGTH_SHORT, true).show();
                kAlertDialog.dismissWithAnimation();
            }
        });
        kAlertDialog.setCancelClickListener(new KAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(KAlertDialog kAlertDialog) {
                Toasty.error(getActivity(), "Xóa Thất Bại", Toast.LENGTH_SHORT, true).show();
                kAlertDialog.dismissWithAnimation();
            }
        });
        kAlertDialog.show();
    }

    private void CustomDialogSuccess() {
        KAlertDialog kAlertDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE);
        kAlertDialog.setTitleText("Hoàn Thành!");
        kAlertDialog.setContentText("Đã Thêm Thành Công");
        kAlertDialog.show();
    }

}


