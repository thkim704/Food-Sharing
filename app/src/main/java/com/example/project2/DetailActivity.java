package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.simpleDB.notice;
import com.example.project2.simpleDB.comment;
import com.example.project2.simpleDB.StrBmp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private TextView tvTitle, tvAuthor, tvDate, tvContent, tvtype, tyaddr, tvexpiry;
    private ImageView tvImg1, tvImg2, tvImg3;
    private Button drawcombtn;
    private ListView comlistview;
    private String UID, NID;

    // 리스트뷰에 사용할 제목 배열
    ArrayList<comment> commentlist = new ArrayList<>();
    private Customcomlist adapter;

    private AlertDialog msgDlg;
    private AlertDialog.Builder msgBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");
        NID = intent.getExtras().getString("NID");

        getSupportActionBar().setTitle("Food-Sharing");

        comlistview = findViewById(R.id.com_listView);

        comlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

                // 댓글의 번호를 가지고 DetailActivity 로 이동
                Intent intent = new Intent(DetailActivity.this, ReplyActivity.class);

                comment con = (comment) adapter.getItem(i-1);
                String num = String.valueOf(con.getcomID());

                intent.putExtra("CID", num);
                intent.putExtra("NID", NID);
                intent.putExtra("UID", UID);
                startActivity(intent);
                commentlist.clear();
                adapter.notifyDataSetChanged();

            }
        });

        View header = getLayoutInflater().inflate(R.layout.detail_header, null, false) ;

        comlistview.addHeaderView(header) ;

        tvTitle = findViewById(R.id.title_tv);
        tvAuthor = findViewById(R.id.author_tv);
        tvDate = findViewById(R.id.date_tv);
        tvContent = findViewById(R.id.content_tv);
        tvtype = findViewById(R.id.type_tv);
        tyaddr = findViewById(R.id.addr_tv);
        tvexpiry = findViewById(R.id.expiry_tv);
        tvImg1 = findViewById(R.id.img_tv1);
        tvImg2 = findViewById(R.id.img_tv2);
        tvImg3 = findViewById(R.id.img_tv3);

        drawcombtn = findViewById(R.id.reg_button);

        drawcombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ComregActivity.class);
                intent.putExtra("UID", UID);
                intent.putExtra("prece", NID);
                intent.putExtra("type", 1);
                startActivity(intent);
                commentlist.clear();
                adapter.notifyDataSetChanged();

            }
        });

        adapter = new Customcomlist(commentlist);
        comlistview.setAdapter(adapter);

    }

    protected void onResume() {
        super.onResume();

        tv_notice(NID);

        getValue(NID);

        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.menu_modify:
                msgBuilder = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("게시글 수정")
                        .setMessage("수정하시겠습니까?");
                msgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(String.valueOf(tvAuthor.getText()).equals(UID)) {
                                    Intent intent = new Intent(DetailActivity.this, ModifyActivity.class);
                                    intent.putExtra("NID", NID);
                                    intent.putExtra("UID",UID);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(DetailActivity.this, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DetailActivity.this, "수정을 취소하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgDlg = msgBuilder.create();
                msgDlg.show();
                break;
            case R.id.menu_delete:
                msgBuilder = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("게시글 삭제")
                        .setMessage("삭제하시겠습니까?");
                msgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(String.valueOf(tvAuthor.getText()).equals(UID)) {
                                    deletenotice(NID);
                                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                    intent.putExtra("UID",UID);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(DetailActivity.this, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DetailActivity.this, "삭제를 취소하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgDlg = msgBuilder.create();
                msgDlg.show();
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(DetailActivity.this, SettingActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    public class Customcomlist extends BaseAdapter {
        private final ArrayList<comment> comment;
        public Customcomlist(ArrayList<comment> comlist){
            this.comment = comlist;
        }

        @Override
        public int getCount() {
            return comment.size();
        }

        @Override
        public Object getItem(int position) { return comment.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View comview =inflater.inflate(R.layout.custom_comment, null, true);
            TextView author = (TextView) comview.findViewById(R.id.comauthor);
            TextView content = (TextView) comview.findViewById(R.id.comcontnent);
            TextView date = (TextView) comview.findViewById(R.id.comdate);

            comment commentadapter = comment.get(position);
            author.setText(commentadapter.getcomauthor());
            content.setText(commentadapter.getcomcontent());
            date.setText(commentadapter.getcomdate());

            return  comview;
        }
    }

    //파이어베이스 Realtime database에서 게시글 정보를 가져오는 함수
    private void tv_notice(String NID) {

        databaseReference.child("notice").child(NID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notice notice = dataSnapshot.getValue(notice.class);

                if(notice != null) {

                    tvImg1.setImageBitmap(StrBmp.StringToBitmap(notice.getimage1()));
                    tvImg2.setImageBitmap(StrBmp.StringToBitmap(notice.getimage2()));
                    tvImg3.setImageBitmap(StrBmp.StringToBitmap(notice.getimage3()));
                    tvtype.setText(notice.getnottype());
                    tyaddr.setText(notice.getnotaddr());
                    tvexpiry.setText(notice.getnotexpiry());
                    tvTitle.setText(notice.getnottitle());
                    tvAuthor.setText(notice.getnotauthor());
                    tvDate.setText(notice.getnotdate());
                    tvContent.setText(notice.getnotcontent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deletenotice(String NID) {
        databaseReference.child("notice").child(NID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetailActivity.this, "게시글을 삭제하였습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: "+e.getMessage());
                Toast.makeText(DetailActivity.this, "게시글 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.child("comment").child(NID).removeValue();
    }

    //파이어베이스에서 댓글 데이터 가져오기
    private void getValue(String NID) {

        databaseReference.child("comment").child(NID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //리스트 초기화
                commentlist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //데이터 가져오기(value 이름으로 된 값을 변수에 담는다.
                    comment comment = dataSnapshot.getValue(comment.class);
                    //리스트에 변수를 담는다.
                    commentlist.add(comment);
                }
                //리스트뷰 어뎁터 설정
                comlistview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DetailActivity.this, "error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//getValue


}