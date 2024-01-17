package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.simpleDB.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private String UID;
    private String userpw;
    private TextView tvnick, tvaddr;
    private Button info_btn, addr_btn;
    private ImageView logout_icon, withdrawal_icon;

    private AlertDialog msgDlg;
    private AlertDialog.Builder msgBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setTitle("설정");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");

        tvnick = findViewById(R.id.nick_lbl);
        tvaddr = findViewById(R.id.addr_dis);
        info_btn = findViewById(R.id.et_info_button);
        addr_btn = findViewById(R.id.et_addr_button);
        logout_icon = findViewById(R.id.logout_icon);
        withdrawal_icon = findViewById(R.id.withdrawal_icon);

        settinginfo(UID);

        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chkpw(UID);             //사용자 정보 변경 창으로 이동
            }
        });

        addr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SetaddrActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
            }
        });

        logout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.clear();
                autoLogin.commit();
                finishAffinity();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        withdrawal_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.clear();
                autoLogin.commit();
                withdrawal(UID);
                finishAffinity();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //파이어베이스 Realtime databas에서 유저 정보를 수정하는 함수
    private void settinginfo(String userID) {

        databaseReference.child("user").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user user = dataSnapshot.getValue(user.class);

                if(user != null) {
                    tvnick.setText(user.getnickname());
                    tvaddr.setText(user.getaddr());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //파이어베이스 Realtime databas에서 정보를 가져오는 함수
    private void chkpw(String userID) {

        databaseReference.child("user").child(userID).child("password").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userpw = dataSnapshot.getValue(String.class);

                EditText txtEdit = new EditText(SettingActivity.this);
                txtEdit.setHint("비밀번호를 입력하세요.");
                txtEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                msgBuilder = new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("사용자 정보 변경")
                        .setView(txtEdit)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(String.valueOf(txtEdit.getText()).equals(userpw)) {
                                    Intent intent = new Intent(SettingActivity.this, UserinfoActivity.class);
                                    intent.putExtra("UID", UID);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SettingActivity.this, "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(SettingActivity.this, "사용자 정보 변경을 취소하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgDlg = msgBuilder.create();
                msgDlg.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //회원 탈퇴
    private void withdrawal(String UID) {
        databaseReference.child("user").child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SettingActivity.this, "회원탈퇴에 성공했습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: "+e.getMessage());
                Toast.makeText(SettingActivity.this, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}