package com.example.project2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project2.simpleDB.StrBmp;
import com.example.project2.simpleDB.notice;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModifyActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private static final int CAPTURE_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private ImageView regImage1, regImage2, regImage3;
    private EventHandler handler = new EventHandler();
    private String[] galleryOrCamera = {"갤러리", "카메라"};
    private Map imglist = new LinkedHashMap();
    private Map fileList = new LinkedHashMap<>();
    private Map checkList = new LinkedHashMap();
    private Bitmap bmp = null;
    private Bundle bundle = new Bundle();
    private EditText regTitle, regContent;
    private TextView regtype, regexpiry;
    private String UID, NID;

    private DatePickerDialog datePickerDialog;

    private long nNow;
    private Date nDate;
    private SimpleDateFormat nFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        getSupportActionBar().setTitle("Sharing 수정");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");
        NID = intent.getExtras().getString("NID");

        regImage1 = (ImageView) findViewById(R.id.regimg1);
        regImage2 = (ImageView) findViewById(R.id.regimg2);
        regImage3 = (ImageView) findViewById(R.id.regimg3);
        //이미지 선택 여부 초기화
        checkList.put(R.id.regimg1,false);
        checkList.put(R.id.regimg2,false);
        checkList.put(R.id.regimg3,false);
        //이미지 등록
        regImage1.setOnClickListener(handler);
        regImage2.setOnClickListener(handler);
        regImage3.setOnClickListener(handler);

        regTitle = findViewById(R.id.title_et);
        regContent = findViewById(R.id.content_et);
        regtype = findViewById(R.id.type_reg_tv);

        registerForContextMenu(regtype);

        regexpiry = findViewById(R.id.expiry_reg_tv);

        regexpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //오늘 날짜(년,월,일) 변수에 담기
                Calendar calendar = Calendar.getInstance();
                int pYear = calendar.get(Calendar.YEAR); //년
                int pMonth = calendar.get(Calendar.MONTH);//월
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);//일

                datePickerDialog = new DatePickerDialog(ModifyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                //1월은 0부터 시작하기 때문에 +1을 해준다.
                                month = month + 1;
                                String date = year + "/" + month + "/" + day;

                                regexpiry.setText(date);
                            }
                        }, pYear, pMonth, pDay);
                datePickerDialog.show();
            } //onClick
        });

        regImage1.setDrawingCacheEnabled(true);
        regImage2.setDrawingCacheEnabled(true);
        regImage3.setDrawingCacheEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.menu_notreg:
                boolean isValidate=true;
                //유효성체크
                if (regTitle.getText().toString().equals("")){
                    Toast.makeText(ModifyActivity.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                else if(regContent.getText().toString().equals("")){
                    Toast.makeText(ModifyActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }
                if (isValidate) {
                    modnot(regImage1.getDrawingCache(), regImage2.getDrawingCache(), regImage3.getDrawingCache(), UID, regtype.getText().toString(),regexpiry.getText().toString(),
                            getTime(), regTitle.getText().toString(), regContent.getText().toString());
                    finish();
                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_type, menu);

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.type1:
                regtype.setText("식자재");
                return true;
            case R.id.type2:
                regtype.setText("음식");
                return true;
        }
        return false;
    }

    private class EventHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if((boolean)checkList.get(v.getId())==false){ //한번누르면 사진추가
                checkList.put(v.getId(),true);
                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle("사진 선택")
                        .setItems(galleryOrCamera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //갤러리
                                        Intent intent = new Intent(Intent.ACTION_PICK);

                                        bundle.putInt("which", v.getId());
                                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(intent, GALLERY_REQUEST_CODE, bundle);
                                        break;
                                    case 1: //카메라
                                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        bundle.putInt("which", v.getId()); // 선택한 ImageView의 Id를 넘기기 위해 Bundle 사용
                                        startActivityForResult(intent1, CAPTURE_REQUEST_CODE, bundle);
                                        break;
                                }

                            }
                        }).show();
            } else { //다시 누르면 삭제
                checkList.put(v.getId(),false);
                imglist.remove(v.getId());
                fileList.remove(v.getId());
                switch (v.getId()){
                    case R.id.regimg1:
                        regImage1.setImageResource(R.drawable.emptyimage);//기본이미지로 다시설정
                        break;
                    case R.id.regimg2:
                        regImage2.setImageResource(R.drawable.emptyimage);
                        break;
                    default:
                        regImage3.setImageResource(R.drawable.emptyimage);
                        break;
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                bmp = (Bitmap) data.getExtras().get("data");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String photoImagePath = file.getAbsolutePath() + File.separator + dateFormat.format(new Date()) + "_camera.png";
                file = new File(photoImagePath);
                //갤러리에 촬영한 사진 추가하기
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //클릭한 이미지뷰 아이디
                int id = bundle.getInt("which");
                //파일 리스트에 추가하기
                fileList.put(id,file);
                // 이미지 리스트에 추가하기
                imglist.put(id, file.getName());

                Log.i("com.ami", imglist.toString());
                //이미지 프리뷰
                switch (id) {
                    case R.id.regimg1:
                        regImage1.setImageBitmap(bmp);
                        break;
                    case R.id.regimg2:
                        regImage2.setImageBitmap(bmp);
                        break;
                    default:
                        regImage3.setImageBitmap(bmp);
                }
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                Cursor cursor = getContentResolver().query(selectedImageUri, null, null, null, null);
                cursor.moveToNext();
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex("_data"));
                File file = new File(path);
                //클릭한 이미지뷰 아이디
                int id = bundle.getInt("which");
                //이미지 리스트에 추가하기
                imglist.put(id, file.getName());
                Log.i("com.ami", imglist.toString());
                //파일 리스트에 추가하기
                fileList.put(id, file);
                ///이미지 프리뷰
                switch (id) {
                    case R.id.regimg1:
                        regImage1.setImageURI(selectedImageUri);
                        break;
                    case R.id.regimg2:
                        regImage2.setImageURI(selectedImageUri);
                        break;
                    default:
                        regImage3.setImageURI(selectedImageUri);
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_mod_notice(NID);
    }

    private String getTime(){
        nNow = System.currentTimeMillis();
        nDate = new Date(nNow);
        return nFormat.format(nDate);
    }

    //파이어베이스 Realtime databas에서 사용자 정보를 가져오는 함수
    private void tv_mod_notice(String NID) {

        databaseReference.child("notice").child(NID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notice notice = dataSnapshot.getValue(notice.class);

                if(notice != null) {
                    regImage1.setImageBitmap(StrBmp.StringToBitmap(notice.getimage1()));
                    regImage2.setImageBitmap(StrBmp.StringToBitmap(notice.getimage2()));
                    regImage3.setImageBitmap(StrBmp.StringToBitmap(notice.getimage3()));
                    regtype.setText(notice.getnottype());
                    regexpiry.setText(notice.getnotexpiry());
                    regTitle.setText(notice.getnottitle());
                    regContent.setText(notice.getnotcontent());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //값을 파이어베이스 Realtime database로 넘기는 함수
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void modnot(Bitmap img1, Bitmap img2, Bitmap img3, String notauthor,
                        String nottype, String notexpiry, String notdate, String nottitle, String notcontent) {

        databaseReference.child("user").child(notauthor).child("addr").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String notaddr = dataSnapshot.getValue(String.class);

                String strimg1 = StrBmp.BitmapToString(img1);
                String strimg2 = StrBmp.BitmapToString(img2);
                String strimg3 = StrBmp.BitmapToString(img3);

                notice notice = new notice(NID, strimg1, strimg2, strimg3, notauthor, notaddr, nottype, notexpiry, notdate, nottitle, notcontent);

                //데이터 넣기
                databaseReference.child("notice").child(NID).setValue(notice);

                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}




