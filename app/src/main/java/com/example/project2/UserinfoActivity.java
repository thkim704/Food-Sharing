package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2.simpleDB.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class UserinfoActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    // 로그 찍을 때 사용하는 TAG 변수
    final private String TAG = getClass().getSimpleName();

    private String UID, password, nickname, email;

    // 사용할 컴포넌트 선언
    EditText passwd_et,passwdcon_et,nickname_et,email_et;
    Button chg_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        getSupportActionBar().setTitle("내 정보 수정");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");

        // 컴포넌트 초기화
        passwd_et = findViewById(R.id.passwd_et);
        passwdcon_et = findViewById(R.id.passwdcon_et);
        nickname_et = findViewById(R.id.nickname_et);
        email_et = findViewById(R.id.email_et);
        chg_button = findViewById(R.id.chg_button);

//        Intent intent =
//        userID = ;

        // 버튼 이벤트 추가
        chg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwd_et.getText().toString();
                nickname = nickname_et.getText().toString();
                email = email_et.getText().toString();

                boolean isValidate=true;
                //유효성체크
                if(password.equals("")){
                    Toast.makeText(UserinfoActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(!isPW(password)){
                    Toast.makeText(UserinfoActivity.this, "비밀번호는 숫자, 문자, 특수문자 무조건 1개 이상 최소 8자에서 최대 16자로 입력해야합니다.", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(passwdcon_et.getText().toString().equals("")){
                    Toast.makeText(UserinfoActivity.this, "비밀번호 확인을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(!password.equals(passwdcon_et.getText().toString())){
                    Toast.makeText(UserinfoActivity.this, "비밀번호가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(nickname.equals("")){
                    Toast.makeText(UserinfoActivity.this, "별명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(email.equals("")){
                    Toast.makeText(UserinfoActivity.this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                if (isValidate) {
                    chguser(UID, password, nickname, email);

                }

            }
        });

    }

    protected void onResume() {
        super.onResume();

        presetinfo(UID);

    }

    //값을 파이어베이스 Realtime database로 넘기는 함수
    private void chguser(String userID, String password, String nickname, String email) {

        //child는 해당 키 위치로 이동하는 함수입니다.
        //키가 없는데 "zoo"와 name같이 값을 지정한 경우 자동으로 생성합니다.
        databaseReference.child("user").child(userID).child("password").setValue(password);
        databaseReference.child("user").child(userID).child("nickname").setValue(nickname);
        databaseReference.child("user").child(userID).child("email").setValue(email);
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        //auto의 loginId와 loginPwd에 값을 저장
        SharedPreferences.Editor autoLogin = auto.edit();
        autoLogin.putString("inputPwd", password);
        autoLogin.commit();
        Toast.makeText(UserinfoActivity.this, "유저 정보 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        finish();

    }

    //파이어베이스 Realtime databas에서 사용자 정보를 가져오는 함수
    private void presetinfo(String userID) {

        databaseReference.child("user").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user user = dataSnapshot.getValue(user.class);

                nickname_et.setText(user.getnickname());
                email_et.setText(user.getemail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean isPW(String str) {
        return Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", str);
    }
}