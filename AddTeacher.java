package com.aksapps.svmadminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import com.aksapps.svmadminapp.TeacherData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddTeacher extends AppCompatActivity
{
	private CircleImageView profileImage;
	private TextInputEditText addTeacherName, addTeacherEmail, addTeacherPhoneNumber;
	private Button addTeacherBtn;
	private AppCompatSpinner addTeacherCategory;
	private String category;
	private String name, email, phoneNumber, downloadUrl = "";
	
	private DatabaseReference databaseReference, dbRef;
	private StorageReference storageReference;
	
	private ProgressDialog progressDialog;
	
	private final int REQ = 1;
	private Bitmap bitmap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_teacher);
		
		profileImage = findViewById(R.id.profile_image);
		addTeacherName = findViewById(R.id.addTeacherName);
		addTeacherEmail = findViewById(R.id.addTeacherEmail);
		addTeacherPhoneNumber = findViewById(R.id.addTeacherPhoneNumber);
		addTeacherCategory = findViewById(R.id.addTeacherCategory);
		addTeacherBtn = findViewById(R.id.addTeacherBtn);
		
		databaseReference = FirebaseDatabase.getInstance().getReference().child("Teacher");
		storageReference = FirebaseStorage.getInstance().getReference();
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Please Wait...");
		progressDialog.setMessage("Uploading Teacher's Data");
		progressDialog.setCancelable(false);
		
		String[] items = new String[]{"Select Category", "Principal", "Head Master", "Managment Staff", "Sanskrit", "Heigher-Mathematics", "Biology", "Physics", "Chemistry", "History", "Geography", "Political Science", "Economics", "Account", "Mathematics", "Science", "Social Science", "Computer Science", "Mechanical", "Hindi", "English", "ATAL lab", "Sports Teacher", "Other Teachers"};
		addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));
		
		
		addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
							{
								category = addTeacherCategory.getSelectedItem().toString();
							}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView)
							{
								
							}
					
				});
		
		profileImage.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								openGallery();
							}
					
				});
				
		addTeacherBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								checkValidation();
							}
					
				});
	}
	
	private void checkValidation()
			{
				name = addTeacherName.getText().toString();
				email = addTeacherEmail.getText().toString();
				phoneNumber = addTeacherPhoneNumber.getText().toString();
				
				if(name.isEmpty())
						{
							addTeacherName.setError("Empty");
							addTeacherName.requestFocus();
						}
				else if(email.isEmpty())
						{
							addTeacherEmail.setError("Empty");
							addTeacherEmail.requestFocus();
						}
				else if(phoneNumber.isEmpty())
						{
							addTeacherPhoneNumber.setError("Empty");
							addTeacherPhoneNumber.requestFocus();
						}
				else if(category.equals("Select Category"))
						{
							Toast.makeText(AddTeacher.this, "Please provide teacher's category.", Toast.LENGTH_SHORT).show();
						}
				else if(bitmap == null)
						{
							insertData();
						}
				else 
						{
							insertImage();
						}
			}
			
	private void insertImage()
			{
				progressDialog.show();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				byte[] finalimg = baos.toByteArray();
				final StorageReference filePath;
				filePath = storageReference.child("Teachers").child(finalimg + "jpg");
				final UploadTask uploadTask = filePath.putBytes(finalimg);
				uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>()
						{
							@Override
							public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
									{
										if(task.isSuccessful())
												{
													uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
															{
																@Override
																public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
																		{
																			filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
																					{
																						@Override
																						public void onSuccess(Uri uri)
																								{
																									progressDialog.dismiss();
																									downloadUrl = String.valueOf(uri);
																									insertData();
																								}	
																					});
																		}
															});
												}
										else	{
													progressDialog.dismiss();
													Toast.makeText(AddTeacher.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
												}
									}
						});
			}
			
	private void insertData()
			{
				progressDialog.show();
				dbRef = databaseReference.child(category);
				final String uniqueKey = dbRef.push().getKey();
				
				TeacherData teacherData = new TeacherData(name, email, phoneNumber, downloadUrl, uniqueKey);
				
				dbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(AddTeacher.this, new OnSuccessListener<Void>()
						{
							@Override
							public void onSuccess(Void aVoid)
									{
										progressDialog.dismiss();
										Toast.makeText(AddTeacher.this, "Teacher's data Uploaded.", Toast.LENGTH_SHORT).show();
										finish();
									}
							
						}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
											{
												progressDialog.dismiss();
												Toast.makeText(AddTeacher.this, "An error occured. Exception: " + e, Toast.LENGTH_LONG).show();
												finish();
											}
									
								});
			}
			
	private void openGallery()
	{
		Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickImage, REQ);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK)
		{
            Uri uri = data.getData();
            try {
                	bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				}
			catch (IOException e)
				{
					e.printStackTrace();
				}
			profileImage.setImageBitmap(bitmap);
		}
	}
	
}