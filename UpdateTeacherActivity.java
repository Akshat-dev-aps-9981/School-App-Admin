package com.aksapps.svmadminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.aksapps.svmadminapp.UpdateFaculty;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;

public class UpdateTeacherActivity extends AppCompatActivity
{
	private CircleImageView updateTeacherImage;
	private TextInputEditText updateTeacherName, updateTeacherEmail, updateTeacherPhoneNumber;
	private Button updateTeacherBtn, deleteTeacherBtn;
	
	private String name, email, phoneNumber, image;
	
	private String downloadUrl;
	private String uniqueKey;
	private String category;
	
	private final int REQ = 1;
	private Bitmap bitmap = null;
	
	private StorageReference storageReference;
	private DatabaseReference databaseReference;
	
	private ProgressDialog progressDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);
		
		name = getIntent().getStringExtra("name");
		email = getIntent().getStringExtra("email");
		phoneNumber = getIntent().getStringExtra("phoneNumber");
		image = getIntent().getStringExtra("image");
		uniqueKey = getIntent().getStringExtra("key");
		category = getIntent().getStringExtra("category");
		
		storageReference = FirebaseStorage.getInstance().getReference();
		databaseReference = FirebaseDatabase.getInstance().getReference().child("Teacher");
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Please Wait...");
		progressDialog.setMessage("Updating Data...");
		progressDialog.setCancelable(false);
		
		updateTeacherImage = findViewById(R.id.updateTeacherImage);
		updateTeacherName = findViewById(R.id.updateTeacherName);
		updateTeacherEmail = findViewById(R.id.updateTeacherEmail);
		updateTeacherPhoneNumber = findViewById(R.id.updateTeacherPhoneNumber);
		updateTeacherBtn = findViewById(R.id.updateTeacherBtn);
		deleteTeacherBtn = findViewById(R.id.deleteTeacherBtn);
		
		try 
			{
				Picasso.get().load(image).into(updateTeacherImage);
			}
		catch (Exception e)
				{
					e.printStackTrace();
				}
		updateTeacherName.setText(name);
		updateTeacherEmail.setText(email);
		updateTeacherPhoneNumber.setText(phoneNumber);
		
		updateTeacherImage.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								openGallery();
							}
					
				});
		
		updateTeacherBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								checkValidation();
							}
					
				});
				
		
		deleteTeacherBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								deleteData();
							}
					
				});
	}
	
	private void checkValidation()
			{
				name = updateTeacherName.getText().toString();
				email = updateTeacherEmail.getText().toString();
				phoneNumber = updateTeacherPhoneNumber.getText().toString();
				
				if(name.isEmpty())
						{
							updateTeacherName.setError("Empty");
							updateTeacherName.requestFocus();
						}
				else if(email.isEmpty())
						{
							updateTeacherEmail.setError("Empty");
							updateTeacherEmail.requestFocus();
						}
				else if(phoneNumber.isEmpty())
						{
							updateTeacherPhoneNumber.setError("Empty");
							updateTeacherPhoneNumber.requestFocus();
						}
				else if(bitmap == null)
						{
							updateData(image);
						}
				else 
						{
							updateImage();
						}
			}
			
	private void updateData(String s)
			{
				HashMap hp = new HashMap();
				hp.put("name", name);
				hp.put("email", email);
				hp.put("phoneNumber", phoneNumber);
				hp.put("image", s);
				
				databaseReference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener()
						{
							@Override
							public void onSuccess(Object o)
									{
										Toast.makeText(UpdateTeacherActivity.this, "Teacher updated successfully.", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFaculty.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
									}
							
						}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
										{
											Toast.makeText(UpdateTeacherActivity.this, "Updating Failed. Exception: " + e, Toast.LENGTH_LONG).show();
										}
									
								});
			}
			
	private void updateImage()
			{
				progressDialog.show();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				byte[] finalimg = baos.toByteArray();
				final StorageReference filePath;
				filePath = storageReference.child("Teachers").child(finalimg + "jpg");
				final UploadTask uploadTask = filePath.putBytes(finalimg);
				uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>()
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
																									updateData(downloadUrl);
																								}	
																					});
																		}
															});
												}
										else	{
													progressDialog.dismiss();
													Toast.makeText(UpdateTeacherActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
												}
									}
						});
			}
				
	private void deleteData()
			{
				databaseReference.child(category).child(uniqueKey).removeValue()
						.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<Void>()
						{
							@Override
							public void onComplete(@NonNull Task<Void> task)
								{
									Toast.makeText(UpdateTeacherActivity.this, "Successfully deleted teacher's data.", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFaculty.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}
							
						}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
											{
												Toast.makeText(UpdateTeacherActivity.this, "Can not delete teacher's data. Exception: " + e, Toast.LENGTH_LONG).show();
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
			updateTeacherImage.setImageBitmap(bitmap);
		}
	}
}