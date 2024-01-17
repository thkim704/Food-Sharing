package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.simpleDB.comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    private TextView com_author_tv, com_date_tv, com_content_tv;
    private Button drarepbtn;
    private ListView replistview;

    private String UID, NID, CID;

    // 리스트뷰에 사용할 제목 배열
    ArrayList<comment> replylist = new ArrayList<>();
    private Customreplist adapter;

    private AlertDialog msgDlg;
    private AlertDialog.Builder msgBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        getSupportActionBar().setTitle("답글");

        Intent intent = getIntent();
        UID = intent.getExtras().getString("UID");
        NID = intent.getExtras().getString("NID");
        CID = intent.getExtras().getString("CID");


        com_author_tv = findViewById(R.id.comauthor);
        com_date_tv = findViewById(R.id.comdate);
        com_content_tv = findViewById(R.id.comcontnent);

        drarepbtn = findViewById(R.id.reg_rep_button);

        drarepbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ReplyActivity.this, ComregActivity.class);
                intent.putExtra("UID", UID);
                intent.putExtra("prece", CID);

                intent.putExtra("type", 2);
                startActivity(intent);

            }
        });

        replistview = findViewById(R.id.rep_listView);

        replistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                comment con = (comment) adapter.getItem(i);
                String num = String.valueOf(con.getcomID());
                String repauthor = String.valueOf(con.getcomauthor());

                msgBuilder = new AlertDialog.Builder(ReplyActivity.this)
                        .setTitle("답글 삭제")
                        .setMessage("삭제하시겠습니까?");
                msgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(repauthor.equals(UID)) {
                                    deletereply(CID, num);
                                }
                                else{
                                    Toast.makeText(ReplyActivity.this, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ReplyActivity.this, "삭제를 취소하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgDlg = msgBuilder.create();
                msgDlg.show();
            }
        });

        adapter = new ReplyActivity.Customreplist(replylist);
        replistview.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.menu_setting:
                Intent intent = new Intent(ReplyActivity.this, SettingActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
                break;

            case R.id.menu_delete:
                msgBuilder = new AlertDialog.Builder(ReplyActivity.this)
                        .setTitle("댓글 삭제")
                        .setMessage("삭제하시겠습니까?");
                msgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(String.valueOf(com_author_tv.getText()).equals(UID)) {
                                    deletecomment(NID, CID);
                                    finish();
                                }
                                else{
                                    Toast.makeText(ReplyActivity.this, "작성자가 아닙니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ReplyActivity.this, "삭제를 취소하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgDlg = msgBuilder.create();
                msgDlg.show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        replylist.clear();
        adapter.notifyDataSetChanged();

        tv_comment(NID, CID);

        getValue(CID);

        adapter.notifyDataSetChanged();

    }

    public class Customreplist extends BaseAdapter {
        private final ArrayList<comment> reply;
        public Customreplist(ArrayList<comment> replylist){
            this.reply = replylist;
        }


        @Override
        public int getCount() {
            return reply.size();
        }

        @Override
        public Object getItem(int position) {
            return reply.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View repview =inflater.inflate(R.layout.custom_reply, null, true);
            TextView author = (TextView) repview.findViewById(R.id.repauthor);
            TextView content = (TextView) repview.findViewById(R.id.repcontnent);
            TextView date = (TextView) repview.findViewById(R.id.repdate);

            comment replyadapter = reply.get(position);
            author.setText(replyadapter.getcomauthor());
            content.setText(replyadapter.getcomcontent());
            date.setText(replyadapter.getcomdate());

            return  repview;
        }
    }

    //파이어베이스 Realtime database에서 댓글 정보를 가져오는 함수
    private void tv_comment(String NID, String CID) {

        databaseReference.child("comment").child(NID).child(CID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comment comment = dataSnapshot.getValue(comment.class);

                if(comment != null) {
                    com_author_tv.setText(comment.getcomauthor());
                    com_date_tv.setText(comment.getcomdate());
                    com_content_tv.setText(comment.getcomcontent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //파이어베이스에서 댓글 데이터 가져오기
    private void getValue(String CID) {

        databaseReference.child("reply").child(CID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //리스트 초기화
                replylist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //데이터 가져오기(value 이름으로 된 값을 변수에 담는다.
                    comment comment = dataSnapshot.getValue(comment.class);
                    //리스트에 변수를 담는다.
                    replylist.add(comment);
                }
                //리스트뷰 어뎁터 설정
                replistview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ReplyActivity.this, "error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//getValue

    private void deletecomment(String NID, String CID) {
        databaseReference.child("comment").child(NID).child(CID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ReplyActivity.this, "댓글을 삭제하였습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: "+e.getMessage());
                Toast.makeText(ReplyActivity.this, "댓글 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.child("reply").child(CID).removeValue();
    }

    private void deletereply(String CID, String RID) {
        databaseReference.child("reply").child(CID).child(RID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ReplyActivity.this, "답글을 삭제하였습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: "+e.getMessage());
                Toast.makeText(ReplyActivity.this, "답글 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}