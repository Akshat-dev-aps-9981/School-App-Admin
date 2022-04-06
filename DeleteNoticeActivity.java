package com.aksapps.svmadminapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aksapps.svmadminapp.NoticeAdapter;
import com.aksapps.svmadminapp.NoticeData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class DeleteNoticeActivity extends AppCompatActivity
{
	private RecyclerView deleteNoticeRecycler;
	private ProgressBar progressBar;
	private ArrayList<NoticeData> list;
	private NoticeAdapter adapter;
	
	private DatabaseReference databaseReference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_notice);
		
		deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
		progressBar = findViewById(R.id.progressBar);
		
		databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");
		
		deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(this));
		deleteNoticeRecycler.setHasFixedSize(true);
		
		getNotice();
	}
	
	private void getNotice()
			{
				databaseReference.addValueEventListener(new ValueEventListener()
						{
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot)
									{
										list = new ArrayList<>();
										for(DataSnapshot snapshot : dataSnapshot.getChildren())
												{
													NoticeData data = snapshot.getValue(NoticeData.class);
													list.add(data);
												}
										
										adapter = new NoticeAdapter(DeleteNoticeActivity.this, list);
										adapter.notifyDataSetChanged();
										progressBar.setVisibility(View.GONE);
										deleteNoticeRecycler.setAdapter(adapter);
									}

							@Override
							public void onCancelled(@NonNull DatabaseError databaseError)
									{
										progressBar.setVisibility(View.GONE);
										Toast.makeText(DeleteNoticeActivity.this, "Error while fetching data. Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
									}
							
						});
			}
}