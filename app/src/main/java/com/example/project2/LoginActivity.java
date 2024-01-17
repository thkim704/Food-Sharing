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

public class LoginActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();

    private String userID, password;

    // 사용할 컴포넌트 선언
    EditText userid_et, passwd_et;
    Button login_button, join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //자동 로그인
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("inputId",null);
        String loginPwd = auto.getString("inputPwd",null);
        if(loginId !=null && loginPwd != null) {
            login(loginId, loginPwd);
        }

        getSupportActionBar().setTitle("로그인");

        // 사용할 컴포넌트 초기화
        userid_et = findViewById(R.id.userid_et);
        passwd_et = findViewById(R.id.passwd_et);
        login_button = findViewById(R.id.login_button);
        join_button = findViewById(R.id.join_button);

        // 로그인 버튼 이벤트 추가
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = userid_et.getText().toString();
                password = passwd_et.getText().toString();

                boolean isValidate=true;
                //유효성체크
                if (userID.equals("")){
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                if (isValidate) {
                    login(userID,password);
                }
            }
        });

        // 조인 버튼 이벤트 추가
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });



    }

    //파이어베이스 Realtime databas에서 사용자 정보를 가져오는 함수
    private void login(String userID, String password) {

        databaseReference.child("user").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user user = dataSnapshot.getValue(user.class);

                //중복체크
                if(user == null) {
                    Toast.makeText(LoginActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(user.getpassword())) {
                    Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("UID",userID);
                    startActivity(intent);
                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    //auto의 loginId와 loginPwd에 값을 저장
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("inputId", userID);
                    autoLogin.putString("inputPwd", password);
                    autoLogin.commit();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
