package com.aksapps.svmadminapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aksapps.svmadminapp.AddTeacher;
import com.aksapps.svmadminapp.TeacherAdapter;
import com.aksapps.svmadminapp.TeacherData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity
{
	FloatingActionButton floatingActionButton;
	private RecyclerView csDepartment, mechanicalDepartment, physicsDepartment, chemistryDepartment;
	private LinearLayout csNoData, mechanicalNoData, physicsNoData, chemistryNoData;
	private List<TeacherData> list1, list2, list3, list4;
	private DatabaseReference databaseReference, dbRef;
	private TeacherAdapter adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);
		
		csDepartment = findViewById(R.id.csDepartment);
		mechanicalDepartment = findViewById(R.id.mechanicalDepartment);
		physicsDepartment = findViewById(R.id.physicsDepartment);
		chemistryDepartment = findViewById(R.id.chemistryDepartment);
		
		csNoData = findViewById(R.id.csNoData);
		mechanicalNoData = findViewById(R.id.mechanicalNoData);
		physicsNoData = findViewById(R.id.physicsNoData);
		chemistryNoData = findViewById(R.id.chemistryNoData);
		
		databaseReference = FirebaseDatabase.getInstance().getReference().child("Teacher");
		
		
		csDepartment();
		mechanicalDepartment();
		physicsDepartment();
		chemistryDepartment();
		
		floatingActionButton = findViewById(R.id.fab);
		
		floatingActionButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								startActivity(new Intent(UpdateFaculty.this, AddTeacher.class));
							}
					
				});
	}
	
	private void csDepartment()
			{
				dbRef = databaseReference.child("Computer Science");
				dbRef.addValueEventListener(new ValueEventListener()
						{
							@Override
							public void onDataChange(DataSnapshot dataSnapshot)
									{
										list1 = new ArrayList<>();
										if(!dataSnapshot.exists())
												{
													csNoData.setVisibility(View.VISIBLE);
													csDepartment.setVisibility(View.GONE);
												}
										else 
												{
													csNoData.setVisibility(View.GONE);
													csDepartment.setVisibility(View.VISIBLE);
													
													for(DataSnapshot snapshot : dataSnapshot.getChildren())
															{
																TeacherData data = snapshot.getValue(TeacherData.class);
																list1.add(data);
															}
															
													csDepartment.setHasFixedSize(true);
													csDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
													adapter = new TeacherAdapter(list1, UpdateFaculty.this, "Computer Science");
													csDepartment.setAdapter(adapter);
												}
									}

							@Override
							public void onCancelled(DatabaseError databaseError)
									{
										Toast.makeText(UpdateFaculty.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
									}
							
						});
			}
			
	private void mechanicalDepartment()
			{
				dbRef = databaseReference.child("Mechanical");
				dbRef.addValueEventListener(new ValueEventListener()
						{
							@Override
							public void onDataChange(DataSnapshot dataSnapshot)
									{
										list2 = new ArrayList<>();
										if(!dataSnapshot.exists())
												{
													mechanicalNoData.setVisibility(View.VISIBLE);
													mechanicalDepartment.setVisibility(View.GONE);
												}
										else 
												{
													mechanicalNoData.setVisibility(View.GONE);
													mechanicalDepartment.setVisibility(View.VISIBLE);
													
													for(DataSnapshot snapshot : dataSnapshot.getChildren())
															{
																TeacherData data = snapshot.getValue(TeacherData.class);
																list2.add(data);
															}
															
													mechanicalDepartment.setHasFixedSize(true);
													mechanicalDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
													adapter = new TeacherAdapter(list2, UpdateFaculty.this, "Mechanical");
													mechanicalDepartment.setAdapter(adapter);
												}
									}

							@Override
							public void onCancelled(DatabaseError databaseError)
									{
										Toast.makeText(UpdateFaculty.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
									}
							
						});
			}
			
	private void physicsDepartment()
	{
		dbRef = databaseReference.child("Physics");
		dbRef.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				list3 = new ArrayList<>();
				if(!dataSnapshot.exists())
				{
					physicsNoData.setVisibility(View.VISIBLE);
					physicsDepartment.setVisibility(View.GONE);
				}
				else
				{
					physicsNoData.setVisibility(View.GONE);
					physicsDepartment.setVisibility(View.VISIBLE);
					
					for(DataSnapshot snapshot : dataSnapshot.getChildren())
					{
						TeacherData data = snapshot.getValue(TeacherData.class);
						list3.add(data);
					}
					
					physicsDepartment.setHasFixedSize(true);
					physicsDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
					adapter = new TeacherAdapter(list3, UpdateFaculty.this, "Physics");
					physicsDepartment.setAdapter(adapter);
				}
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Toast.makeText(UpdateFaculty.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
			}
			
		});
	}
	
	private void chemistryDepartment()
	{
		dbRef = databaseReference.child("Chemistry");
		dbRef.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				list4 = new ArrayList<>();
				if(!dataSnapshot.exists())
				{
					chemistryNoData.setVisibility(View.VISIBLE);
					chemistryDepartment.setVisibility(View.GONE);
				}
				else
				{
					chemistryNoData.setVisibility(View.GONE);
					chemistryDepartment.setVisibility(View.VISIBLE);
					
					for(DataSnapshot snapshot : dataSnapshot.getChildren())
					{
						TeacherData data = snapshot.getValue(TeacherData.class);
						list4.add(data);
					}
					
					chemistryDepartment.setHasFixedSize(true);
					chemistryDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
					adapter = new TeacherAdapter(list4, UpdateFaculty.this, "Chemistry");
					chemistryDepartment.setAdapter(adapter);
				}
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Toast.makeText(UpdateFaculty.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
			}
			
		});
	}
}