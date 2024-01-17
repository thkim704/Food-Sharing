package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class JoinActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    // 로그 찍을 때 사용하는 TAG 변수
    final private String TAG = getClass().getSimpleName();

    private String userID, password, nickname, email;

    // 사용할 컴포넌트 선언
    EditText userid_et, passwd_et,passwdcon_et,nickname_et,email_et;
    Button join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        getSupportActionBar().setTitle("회원가입");

        // 컴포넌트 초기화
        userid_et = findViewById(R.id.userid_et);
        passwd_et = findViewById(R.id.passwd_et);
        passwdcon_et = findViewById(R.id.passwdcon_et);
        nickname_et = findViewById(R.id.nickname_et);
        email_et = findViewById(R.id.email_et);
        join_button = findViewById(R.id.join_button);

        // 버튼 이벤트 추가
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = userid_et.getText().toString();
                password = passwd_et.getText().toString();
                nickname = nickname_et.getText().toString();
                email = email_et.getText().toString();

                boolean isValidate=true;
                //유효성체크
                if (userID.equals("")){
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(password.equals("")){
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(!isPW(password)){
                    Toast.makeText(JoinActivity.this, "비밀번호는 숫자, 문자, 특수문자 무조건 1개 이상 최소 8자에서 최대 16자로 입력해야합니다.", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(passwdcon_et.getText().toString().equals("")){
                    Toast.makeText(JoinActivity.this, "비밀번호 확인을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(!password.equals(passwdcon_et.getText().toString())){
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(nickname.equals("")){
                    Toast.makeText(JoinActivity.this, "별명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(email.equals("")){
                    Toast.makeText(JoinActivity.this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                if (isValidate) {
                    join(userID, password, nickname, email);
                }

            }
        });
    }

    //값을 파이어베이스 Realtime database로 넘기는 함수 + 아이디 중복체크
    private void join(String userID, String password, String nickname, String email) {

        databaseReference.child("user").child(userID).child("userID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String douchk = dataSnapshot.getValue(String.class);

                //중복체크
                if(douchk!=null){
                    Toast.makeText(JoinActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //여기에서 직접 변수를 만들어서 값을 직접 넣는것도 가능합니다.
                    // ex) 갓 태어난 동물만 입력해서 int age=1; 등을 넣는 경우
                    String addr = "임시 주소";

                    //animal.java에서 선언했던 함수.
                    user user = new user(userID, password, nickname, email, addr);

                    //child는 해당 키 위치로 이동하는 함수입니다.
                    //키가 없는데 "zoo"와 name같이 값을 지정한 경우 자동으로 생성합니다.
                    databaseReference.child("user").child(userID).setValue(user);
                    Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(JoinActivity.this, SetaddrActivity.class);
                    intent.putExtra("UID", userID);
                    intent.putExtra("way", 1);
                    finish();

                    startActivity(intent);
                }
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
