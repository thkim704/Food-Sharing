package com.example.project2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SetaddrActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    //로그캣 사용 설정
    private static final String TAG = "SetaddrActivity";

    //객체 선언
    private List<Address> addrlist, resultllist;


    private EditText search_addr;
    private ListView addrlistview;
    private ImageView search_icon;
    private String UID;
    private double lat, lon;
    ArrayList<String> returnlist = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setaddr);


        getSupportActionBar().setTitle("주소 설정");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");

        search_addr = findViewById(R.id.addr_Search);
        search_icon = findViewById(R.id.search_icon);

        search_addr.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                addr_search();
            }
        });

        addrlistview = findViewById(R.id.addrlistView);

        addrlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String regaddr = (String) returnlist.get(i);
                setaddr(UID, regaddr);

            }
        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, returnlist);
        addrlistview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addr_search() {

        if (search_addr.getText().toString().length() > 0) {
            String str = search_addr.getText().toString();
            returnlist.clear();
            final Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            Thread thread1 = new Thread(() -> {
                try {
                    addrlist = geocoder.getFromLocationName(str, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread1.start();

            if (addrlist != null) {
                if (addrlist.size() == 0) {
                    Toast.makeText(SetaddrActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Address address = addrlist.get(0);
                    lat = address.getLatitude();
                    lon = address.getLongitude();
                }
            }
            Thread thread2 = new Thread(() -> {
                try {
                    resultllist = geocoder.getFromLocation(lat, lon, 30);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();

            if (resultllist != null) {
                if (resultllist.size() == 0) {
                    Log.e("reverseGeocoding", "해당 결과 없음");
                } else {
                    int i;
                    for (i = 0; i < resultllist.size(); i++) {
                        String loca1, loca2, loca3;
                        if (resultllist.get(i).getAdminArea() != null) {
                            loca1 = resultllist.get(i).getAdminArea();
                        } else {
                            loca1 = "";
                        }
                        if (resultllist.get(i).getLocality() != null) {
                            loca2 = resultllist.get(i).getLocality();
                        } else {
                            loca2 = "";
                        }
                        if (resultllist.get(i).getSubLocality() != null) {
                            loca3 = resultllist.get(i).getSubLocality();
                        } else {
                            loca3 = "";
                        }
                        if (resultllist.get(i).getThoroughfare() != null) {
                            String addr = loca1 + " " + loca2 + " " + loca3 + " " + resultllist.get(i).getThoroughfare();
                            returnlist.add(addr);

                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }


    //값을 파이어베이스 Realtime database로 넘기는 함수 + 아이디 중복체크
    private void setaddr(String userID, String addr) {

        if (addr != null) {

            //child는 해당 키 위치로 이동하는 함수입니다.
            databaseReference.child("user").child(userID).child("addr").setValue(addr);
            Toast.makeText(SetaddrActivity.this, "주소 지정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}



