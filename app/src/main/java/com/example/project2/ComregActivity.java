package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.project2.simpleDB.comment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComregActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private EditText regcomauthor, regcompw, regcomcontnet;
    private RatingBar regcomrating;

    private String UID, preceID;
    private int type;
    private String key = null;

    private static int comindex = 0;

    private long cNow;
    private Date cDate;
    private SimpleDateFormat cFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comreg);

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");
        preceID = intent.getExtras().getString("prece");
        type = intent.getExtras().getInt("type");

        getSupportActionBar().setTitle("댓글 등록");

        regcomcontnet = findViewById(R.id.comcontent_et);

    }

    private String getTime(){
        cNow = System.currentTimeMillis();
        cDate = new Date(cNow);
        return cFormat.format(cDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();
        key = intent.getStringExtra("noticeno");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comreg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.menu_comreg:
                boolean isValidate=true;
                //유효성체크
                if(regcomcontnet.getText().toString().equals("")){
                    Toast.makeText(ComregActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                if (isValidate) {
                    add_com_rep(UID,getTime(), regcomcontnet.getText().toString(), preceID, type);
                    finish();

                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//    //뒤로가기 버튼 눌렀을 때
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(ComregActivity.this, DetailActivity.class);
//        intent.putExtra("board_seq", key);
//        startActivity(intent);
//        finish();
//    }

    public static int getComindex() {
        return comindex;
    }

    public static void setComindex(int comindex) {
        ComregActivity.comindex = comindex;
    }

    //값을 파이어베이스 Realtime database로 넘기는 함수
    private void add_com_rep(String comauthor, String comdate, String comcontent, String preceID, int type) {
        comment comment;

        switch (type) {
            case 1 :
                //키로 아이디 생성
                String comId = databaseReference.child("comment").push().getKey();

                comment = new comment(comId, comauthor, comdate, comcontent);

                //데이터 넣기
                databaseReference.child("comment").child(preceID).child(comId).setValue(comment);

                Toast.makeText(ComregActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                break;

            case 2 :
                //키로 아이디 생성
                String repId = databaseReference.child("reply").push().getKey();

                comment = new comment(repId, comauthor, comdate, comcontent);

                //데이터 넣기
                databaseReference.child("reply").child(preceID).child(repId).setValue(comment);

                Toast.makeText(ComregActivity.this, "답글이 등록되었습니다.", Toast.LENGTH_SHORT).show();


        }


    }

}