package com.example.assignment.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
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

import com.example.assignment.Adapter.KhoangThuAdapter;
import com.example.assignment.Adapter.LoaiThuAdapter;
import com.example.assignment.Adapter.Spinner_KhoangThu_Adapter;
import com.example.assignment.DataBase.DataBase;
import com.example.assignment.Interface.SetOnItemClickListener;
import com.example.assignment.Model.KhoangThu;
import com.example.assignment.Model.LoaiThu;
import com.example.assignment.R;
import com.kinda.alert.KAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class KhoangThuFragment extends Fragment {
    FloatingActionButton floatingActionButton;
    Spinner spChonKhoangThu, spDonVi, spinnerdss;
    int id_LoaiThu;
    RecyclerView recyclerView;
    EditText edtngay;
    String donVi;
    DataBase dataBase;
    Spinner_KhoangThu_Adapter spinnerApdater;
    KhoangThuAdapter khoangThuAdapter;
    ArrayList<LoaiThu> arrSpinner = new ArrayList<>();
    ArrayList<KhoangThu> arrKhoangThu = new ArrayList<>();
    ArrayList<String> arrDonVi = new ArrayList<String>();
    ArrayAdapter<String> apdaterDonVi;
    Date date;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khoangthu, container, false);
        floatingActionButton = view.findViewById(R.id.floatingkhoangthu);
        recyclerView = view.findViewById(R.id.recyclerviewkhoangthu);
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
        khoangThuAdapter = new KhoangThuAdapter(getActivity(), R.layout.dong_khoangthu, arrKhoangThu);
        recyclerView.setAdapter(khoangThuAdapter);
        khoangThuAdapter.setOnItemClickListener(new SetOnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                dialogXemChiTiet(position);
            }

            @Override
            public void onEdit(int position) {
                KhoangThu khoangThu = arrKhoangThu.get(position);
                dialogSua(khoangThu);
            }

            @Override
            public void onDelete(int position) {
                KhoangThu khoangThu = arrKhoangThu.get(position);
                alerDialogDelete(khoangThu);
            }
        });
        getData();
        return view;
    }

    private void dialogAdd() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_add_kt);
        dialog.setCanceledOnTouchOutside(true);
        spChonKhoangThu = dialog.findViewById(R.id.spinnerthu);
        spDonVi = dialog.findViewById(R.id.spinnerDonvi);
        final EditText edttien = dialog.findViewById(R.id.edtnhaptienthu);
        final EditText edtmota = dialog.findViewById(R.id.edtmotakt);
        final EditText edtTen = dialog.findViewById(R.id.edtthemten);
        edtngay = dialog.findViewById(R.id.edtngay);

        Button btnhuy = dialog.findViewById(R.id.btnhuykt);
        Button btnthem = dialog.findViewById(R.id.btnthemkt);
        chonNgay();
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tien = edttien.getText().toString();
                String mota = edtmota.getText().toString();
                String ngay = edtngay.getText().toString();
                String ten = edtTen.getText().toString();

                if (edttien.length() == 0 || edtmota.length() == 0 || edtngay.length() == 0 || edtTen.length() == 0) {
                    Toasty.warning(getActivity(), "Xóa Thành Công", Toast.LENGTH_SHORT, true).show();
                    return;
                } else {
                    KhoangThu khoangThu = new KhoangThu();
//                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
//                    try {
//                        date = format.parse(ngay);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                    khoangThu.setId_loaithu(id_LoaiThu);
                    khoangThu.setTen(ten);
                    khoangThu.setGia(tien);
                    khoangThu.setDonVi(donVi);
                    Log.d("NGAYKHOANGTHU", ngay + "");
                    khoangThu.setNgay(ngay);
                    khoangThu.setMoTa(mota);
                    dataBase.addKhoangThu(khoangThu);
                    getData();
                    CustomDialogSuccess();
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
        List<LoaiThu> arr = getDataSpiner();
        Spinner(arr);
        dialog.show();
    }

    private void Spinner(final List<LoaiThu> list) {
        spinnerApdater = new Spinner_KhoangThu_Adapter(getActivity(), R.layout.dong_spinner_kt, list);
        spChonKhoangThu.setAdapter(spinnerApdater);
        spinnerApdater.notifyDataSetChanged();
        spChonKhoangThu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_LoaiThu = list.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private List<LoaiThu> getDataSpiner() {
        List list = new ArrayList<LoaiThu>();
        Cursor dataspinner = dataBase.getData("SELECT * FROM " + DataBase.KEY_TABLE_NAME_LOAITHU + " WHERE " + DataBase.KEY_TABLE_DELETEFLAG_LOAITHU + " = 0");
        list.clear();
        while (dataspinner.moveToNext()) {
            int id = dataspinner.getInt(0);
            String ten = dataspinner.getString(1);
            int deleflag = dataspinner.getInt(2);
            list.add(new LoaiThu(id, ten, deleflag));
        }
        return list;
    }

    private void getData() {
        Cursor cursor = dataBase.getData("SELECT * FROM " + DataBase.KEY_TABLE_NAME_KHOANGTHU + " WHERE " + DataBase.KEY_TABLE_DELETEFLAG_KHOANGTHU + " = 0");
        arrKhoangThu.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String dinhMucThu = cursor.getString(2);
            String donvithu = cursor.getString(3);
            String thoidiem = cursor.getString(4);
            id_LoaiThu = cursor.getInt(5);
            int danhgia = cursor.getInt(6);
            int deleteflag = cursor.getInt(7);
            String mota = cursor.getString(8);
            arrKhoangThu.add(new KhoangThu(id, ten, dinhMucThu, donvithu, thoidiem, mota, deleteflag, id_LoaiThu, danhgia));
        }
        khoangThuAdapter.notifyDataSetChanged();
    }

    private void spinnerDonViTienTe() {
        apdaterDonVi = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrDonVi);
        dataDonViTienTe();
        apdaterDonVi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDonVi.setAdapter(apdaterDonVi);
        spDonVi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                donVi = arrDonVi.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void dataDonViTienTe() {
        arrDonVi.add("VND");
        arrDonVi.add("USD");
        arrDonVi.add("RUB");
        arrDonVi.add("AFN");
        arrDonVi.add("EUR");
        arrDonVi.add("ALL");
        arrDonVi.add("None");
        arrDonVi.add("GBP");
        arrDonVi.add("GGP[O]");
        arrDonVi.add("DZD");
        arrDonVi.add("EUR");
        arrDonVi.add("AOA");
        arrDonVi.add("XCD");
        arrDonVi.add("XCD");
        arrDonVi.add("ARS");
        arrDonVi.add("AMD");
        arrDonVi.add("AWG");
        arrDonVi.add("None");
        arrDonVi.add("SHP");
        arrDonVi.add("AUD");
        arrDonVi.add("EUR");
        arrDonVi.add("AZN");
        arrDonVi.add("BSD");
        arrDonVi.add("BHD");
        arrDonVi.add("BDT");
        arrDonVi.add("BBD");
        arrDonVi.add("BYR");
        arrDonVi.add("EUR");
        arrDonVi.add("BZD");
        arrDonVi.add("XOF");
        arrDonVi.add("BMD");
        arrDonVi.add("BTN");
        arrDonVi.add("INR");
        arrDonVi.add("BOB");
        arrDonVi.add("USD");
        arrDonVi.add("BAM");
        arrDonVi.add("BWP");
        arrDonVi.add("BRL");
        arrDonVi.add("USD");
        arrDonVi.add("None");
        arrDonVi.add("USD");
        arrDonVi.add("BND");
        arrDonVi.add("SGD");
        arrDonVi.add("BGN");
        arrDonVi.add("XOF");
        arrDonVi.add("MMK");
        arrDonVi.add("BIF");
        arrDonVi.add("KHR");
        arrDonVi.add("XAF");
        arrDonVi.add("CAD");
        arrDonVi.add("CVE");
        arrDonVi.add("KYD");
        arrDonVi.add("XAF");
        arrDonVi.add("XAF");
        arrDonVi.add("CLP");
        arrDonVi.add("CNY");
        arrDonVi.add("AUD");
        arrDonVi.add("COP");
        arrDonVi.add("KMF");
        arrDonVi.add("CDF");
        arrDonVi.add("XAF");
        arrDonVi.add("NZD");
        arrDonVi.add("None");
        arrDonVi.add("CRC");
        arrDonVi.add("XOF");
        arrDonVi.add("HRK");
        arrDonVi.add("CUC");
        arrDonVi.add("CUP");
        arrDonVi.add("ANG");
        arrDonVi.add("EUR");
        arrDonVi.add("CZK");
        arrDonVi.add("DKK");
        arrDonVi.add("DJF");
        arrDonVi.add("XCD");
        arrDonVi.add("DOP");
        arrDonVi.add("USD");
        arrDonVi.add("None");
        arrDonVi.add("USD");
        arrDonVi.add("None");
        arrDonVi.add("EGP");
        arrDonVi.add("SVC");
        arrDonVi.add("USD");
        arrDonVi.add("XAF");
        arrDonVi.add("ERN");
        arrDonVi.add("EUR");
        arrDonVi.add("ETB");
        arrDonVi.add("FKP");
        arrDonVi.add("DKK");
        arrDonVi.add("None");
        arrDonVi.add("FJD");
        arrDonVi.add("EUR");
        arrDonVi.add("EUR");
        arrDonVi.add("XPF");
        arrDonVi.add("XAF");
        arrDonVi.add("GMD");
        arrDonVi.add("GEL");
        arrDonVi.add("EUR");
        arrDonVi.add("GHS");
        arrDonVi.add("GIP");
        arrDonVi.add("EUR");
        arrDonVi.add("XCD");
        arrDonVi.add("GTQ");
        arrDonVi.add("GBP");
        arrDonVi.add("None");
        arrDonVi.add("GNF");
        arrDonVi.add("XOF");
        arrDonVi.add("GYD");
        arrDonVi.add("HTG");
        arrDonVi.add("HNL");
        arrDonVi.add("HKD");
        arrDonVi.add("HUF");
        arrDonVi.add("ISK");
        arrDonVi.add("INR");
        arrDonVi.add("IDR");
        arrDonVi.add("IRR");
        arrDonVi.add("IQD");
        arrDonVi.add("EUR");
        arrDonVi.add("GBP");
        arrDonVi.add("IMP[O]");
        arrDonVi.add("ILS");
        arrDonVi.add("EUR");
        arrDonVi.add("JMD");
        arrDonVi.add("JPY");
        arrDonVi.add("GBP");
        arrDonVi.add("JEP[O]");
        arrDonVi.add("JOD");
        arrDonVi.add("KZT");
        arrDonVi.add("KES");
        arrDonVi.add("AUD");
        arrDonVi.add("None");
        arrDonVi.add("KPW");
        arrDonVi.add("KRW");
        arrDonVi.add("EUR");
        arrDonVi.add("KWD");
        arrDonVi.add("KGS");
        arrDonVi.add("LAK");
        arrDonVi.add("EUR");
        arrDonVi.add("LBP");
        arrDonVi.add("LSL");
        arrDonVi.add("ZAR");
        arrDonVi.add("LRD");
        arrDonVi.add("LYD");
        arrDonVi.add("CHF");
        arrDonVi.add("EUR");
        arrDonVi.add("EUR");
        arrDonVi.add("MOP");
        arrDonVi.add("MKD");
        arrDonVi.add("MGA");
        arrDonVi.add("MWK");
        arrDonVi.add("MYR");
        arrDonVi.add("MVR");
        arrDonVi.add("XOF");
        arrDonVi.add("EUR");
        arrDonVi.add("USD");
        arrDonVi.add("MRO");
        arrDonVi.add("MUR");
        arrDonVi.add("MXN");
        arrDonVi.add("None");
        arrDonVi.add("USD");
        arrDonVi.add("MDL");
        arrDonVi.add("EUR");
        arrDonVi.add("MNT");
        arrDonVi.add("EUR");
        arrDonVi.add("XCD");
        arrDonVi.add("MAD");
        arrDonVi.add("MZN");
        arrDonVi.add("AMD");
        arrDonVi.add("None");
        arrDonVi.add("NAD");
        arrDonVi.add("ZAR");
        arrDonVi.add("AUD");
        arrDonVi.add("None");
        arrDonVi.add("NPR");
        arrDonVi.add("EUR");
        arrDonVi.add("XPF");
        arrDonVi.add("NZD");
        arrDonVi.add("NIO");
        arrDonVi.add("XOF");
        arrDonVi.add("NGN");
        arrDonVi.add("NZD");
        arrDonVi.add("None");
        arrDonVi.add("TRY");
        arrDonVi.add("NOK");
        arrDonVi.add("OMR");
        arrDonVi.add("PKR");
        arrDonVi.add("None");
        arrDonVi.add("USD");
        arrDonVi.add("ILS");
        arrDonVi.add("JOD");
        arrDonVi.add("PAB");
        arrDonVi.add("USD");
        arrDonVi.add("PGK");
        arrDonVi.add("PYG");
        arrDonVi.add("PEN");
        arrDonVi.add("PHP");
        arrDonVi.add("NZD");
        arrDonVi.add("None");
        arrDonVi.add("PLN");
        arrDonVi.add("EUR");
        arrDonVi.add("QAR");
        arrDonVi.add("RON");
        arrDonVi.add("RUB");
        arrDonVi.add("RWF");
        arrDonVi.add("USD");
        arrDonVi.add("DZD");
        arrDonVi.add("MRO");
        arrDonVi.add("MAD");
        arrDonVi.add("None");
        arrDonVi.add("SHP");
        arrDonVi.add("XCD");
        arrDonVi.add("XCD");
        arrDonVi.add("XCD");
        arrDonVi.add("WST");
        arrDonVi.add("EUR");
        arrDonVi.add("STD");
        arrDonVi.add("SAR");
        arrDonVi.add("XOF");
        arrDonVi.add("RSD");
        arrDonVi.add("SCR");
        arrDonVi.add("SLL");
        arrDonVi.add("BND");
        arrDonVi.add("SGD");
        arrDonVi.add("USD");
        arrDonVi.add("ANG");
        arrDonVi.add("EUR");
        arrDonVi.add("EUR");
        arrDonVi.add("SBD");
        arrDonVi.add("SOS");
        arrDonVi.add("None");
        arrDonVi.add("ZAR");
        arrDonVi.add("GBP");
        arrDonVi.add("None");
        arrDonVi.add("RUB");
        arrDonVi.add("EUR");
        arrDonVi.add("SSP");
        arrDonVi.add("LKR");
        arrDonVi.add("SDG");
        arrDonVi.add("SRD");
        arrDonVi.add("SZL");
        arrDonVi.add("SEK");
        arrDonVi.add("CHF");
        arrDonVi.add("SYP");
        arrDonVi.add("TWD");
        arrDonVi.add("TJS");
        arrDonVi.add("TZS");
        arrDonVi.add("THB");
        arrDonVi.add("XOF");
        arrDonVi.add("TOP");
        arrDonVi.add("PRB[O]");
        arrDonVi.add("TTD");
        arrDonVi.add("SHP");
        arrDonVi.add("None");


    }

    private void chonNgay() {
        edtngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i, i1, i2);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        edtngay.setText(format.format(calendar.getTime()));
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

    }

    private void dialogXemChiTiet(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_xemchitiet_kt);

        TextView txtTenLoaiThu = dialog.findViewById(R.id.txttenloaithuxem);
        TextView txtTen = dialog.findViewById(R.id.edttenkhoangthu);
        TextView txtSoTien = dialog.findViewById(R.id.edtsoTien);
        TextView txtMoTa = dialog.findViewById(R.id.edtmota);
        TextView txtNgay = dialog.findViewById(R.id.edtngathang);
        TextView txtDonVi = dialog.findViewById(R.id.edtdonvi);

        KhoangThu khoangThu = arrKhoangThu.get(position);
        String ten = khoangThu.getTen();
        String soTien = khoangThu.getGia();
        String moTa = khoangThu.getMoTa();
        String donVi = khoangThu.getDonVi();
        String ngaythang = khoangThu.getNgay();
        int idLoai = khoangThu.getId_loaithu();

//        Cursor cursor  = dataBase.getData("SELECT " + dataBase.KEY_TABLE_NAME_LOAITHU+ " .ten FROM " + dataBase.KEY_TABLE_NAME_LOAITHU +" INNER JOIN khoangthu WHERE '" + idLoai + "' loaithu.id = khoangthu.idloaithu");

        Cursor cursor = dataBase.getData("SELECT * FROM " + dataBase.KEY_TABLE_NAME_LOAITHU + " WHERE id = '" + idLoai + "' ");
        while (cursor.moveToNext()) {
            String tenLoai = cursor.getString(1);
            txtTenLoaiThu.setText(tenLoai);
        }
        txtTen.setText(ten);
        txtSoTien.setText(soTien);
        txtMoTa.setText(moTa);
        txtNgay.setText(ngaythang);
        txtDonVi.setText(donVi);
        dialog.show();
    }

    private void dialogSua(final KhoangThu khoangThu) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_sua_kt);
        dialog.setCancelable(true);
        spinnerdss = dialog.findViewById(R.id.spinnerthusuakt);
        final EditText edtsotien = dialog.findViewById(R.id.edtnhaptienthusuakt);
        Spinner spinner1 = dialog.findViewById(R.id.spinnerDonvisuakt);
        final EditText edtTen = dialog.findViewById(R.id.edtthemtensuakt);
        final EditText edtNgay = dialog.findViewById(R.id.edtngaysuakt);
        final EditText edtMoTa = dialog.findViewById(R.id.edtmotasuakt);
        Button btnHuy = dialog.findViewById(R.id.btnhuysuakt);
        Button btnSua = dialog.findViewById(R.id.btnthemsuakt);

        dataDonViTienTe();
        final List<LoaiThu> list = getDataSpiner();
        spinnerApdater = new Spinner_KhoangThu_Adapter(getActivity(), R.layout.dong_spinner_kt, list);
        apdaterDonVi = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrDonVi);
        spinner1.setAdapter(apdaterDonVi);
        // voi lai, khoi tao adapter sau khi lay data
        spinnerdss.setAdapter(spinnerApdater);
        donVi = khoangThu.getDonVi();
        final String ten = khoangThu.getTen();
        final String soTien = khoangThu.getGia();
        final String moTa = khoangThu.getMoTa();
        final String ngay = khoangThu.getNgay();
        edtMoTa.setText(moTa);
        edtsotien.setText(soTien);
        edtTen.setText(ten);
        edtNgay.setText(ngay);
        spinnerdss.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_LoaiThu = list.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                donVi = arrDonVi.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtsotien.length() == 0 || edtTen.length() == 0 || edtNgay.length() == 0 || edtMoTa.length() == 0) {
                    Toasty.warning(getActivity(), "Vui Lòng Nhập Đầy Đủ Thông Tin",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                } else {
                    khoangThu.setDonVi(donVi);
                    khoangThu.setId_loaithu(id_LoaiThu);
                    khoangThu.setTen(edtTen.getText().toString());
                    khoangThu.setMoTa(edtMoTa.getText().toString());
                    khoangThu.setGia(edtsotien.getText().toString());
                    khoangThu.setNgay(edtNgay.getText().toString());
                    dataBase.suaKhoangThu(khoangThu);
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
        getData();
        dialog.show();
    }

    // xóa khoảng thu
    private void alerDialogDelete(final KhoangThu khoangThu) {
        final KAlertDialog kAlertDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
        kAlertDialog.setTitleText("Thông Báo?");
        kAlertDialog.setContentText("Bạn có chắc muốn xóa " + khoangThu.getTen() + " này không!");
        kAlertDialog.setCancelText("Không");
        kAlertDialog.setConfirmText("Có");
        kAlertDialog.showCancelButton(true);
        kAlertDialog.setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(KAlertDialog diaglog) {
                dataBase.QueryData("UPDATE khoangthu SET deleteflag = 1 WHERE id = '" + khoangThu.getId() + "' ");
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