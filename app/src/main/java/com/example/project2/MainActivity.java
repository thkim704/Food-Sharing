package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.simpleDB.notice;
import com.example.project2.simpleDB.StrBmp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private ListView listView;
    private FloatingActionButton reg_fab;

    private String UID;

    // 리스트뷰에 사용할 제목 배열
    ArrayList<notice> noticelist = new ArrayList<>();
    private Customnotlist adapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Food-Sharing");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");

        listView = findViewById(R.id.listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 게시물의 번호를 가지고 DetailActivity 로 이동
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                notice con = (notice) adapter.getItem(i);
                String num = String.valueOf(con.getnotID());

                intent.putExtra("NID", num);
                intent.putExtra("UID", UID);
                startActivity(intent);
                noticelist.clear();
                adapter.notifyDataSetChanged();
            }
        });

        // ListView 에서 사용할 arrayAdapter를 생성하고, ListView 와 연결
        adapter = new Customnotlist(noticelist);
        listView.setAdapter(adapter);

        reg_fab = findViewById(R.id.fab);

        reg_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
            }
        });

        checkDangerousPermissions();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.menu_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
        noticelist.clear();
        adapter.notifyDataSetChanged();

        getValue();
        // arrayAdapter의 데이터가 변경되었을때 새로고침
        adapter.notifyDataSetChanged();
    }

    public class Customnotlist extends BaseAdapter {
        private final ArrayList<notice> notice;
        public Customnotlist(ArrayList<notice> notice){
            this.notice = notice;
        }

        @Override
        public int getCount() {
            return notice.size();
        }

        @Override
        public Object getItem(int position) {
            return notice.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View notview =inflater.inflate(R.layout.custom_list, null, true);
            ImageView image = (ImageView) notview.findViewById(R.id.image) ;
            TextView title = (TextView) notview.findViewById(R.id.listtitle);
            TextView author = (TextView) notview.findViewById(R.id.listauthor);
            TextView date = (TextView) notview.findViewById(R.id.listdate);
            TextView addr = (TextView) notview.findViewById(R.id.listaddr);

            notice noticeadapter = notice.get(position);
            image.setImageBitmap(StrBmp.StringToBitmap(noticeadapter.getimage1()));
            title.setText(noticeadapter.getnottitle());
            author.setText(noticeadapter.getnotauthor());
            date.setText(noticeadapter.getnotdate());
            addr.setText(noticeadapter.getnotaddr());

            return  notview;
        }
    }

    //파이어베이스에서 게시글 데이터 가져오기
    private void getValue() {

        databaseReference.child("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //리스트 초기화
                noticelist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //데이터 가져오기(value 이름으로 된 값을 변수에 담는다.
                    notice notice = dataSnapshot.getValue(notice.class);
                    //리스트에 변수를 담는다.
                    noticelist.add(notice);
                }
                //리스트뷰 어뎁터 설정
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//getValue


    //------------------권한 설정 시작------------------------
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
    //------------------권한 설정 끝------------------------




}

