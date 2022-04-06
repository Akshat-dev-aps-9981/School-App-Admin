package com.aksapps.svmadminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.aksapps.svmadminapp.NoticeData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity
{	
	private CardView addImage;
	private EditText noticeBox;
	private Button uploadNoticeBtn;
	private ImageView noticeImageView;
	
	private final int REQ = 1;
	private Bitmap bitmap;
	String downloadUrl = "";
	private ProgressDialog progressDialog;
	
	private DatabaseReference databaseReference, dbRef;
	private StorageReference storageReference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_notice);
		
		addImage = findViewById(R.id.addImage);
		noticeBox = findViewById(R.id.noticeBox);
		uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);
		noticeImageView = findViewById(R.id.noticeImageView);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Please wait...");
		progressDialog.setMessage("Uploading Notice...");
		progressDialog.setCancelable(false);
		
		databaseReference = FirebaseDatabase.getInstance().getReference();
		storageReference = FirebaseStorage.getInstance().getReference();
		
		addImage.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								openGallery();
							}	
				});
				
		uploadNoticeBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								if(noticeBox.getText().toString().isEmpty())
										{
											noticeBox.setError("Type something here.");
											Toast.makeText(UploadNotice.this, "Please enter a title of the notice.", Toast.LENGTH_SHORT).show();
											noticeBox.requestFocus();
										}
										
								else if(bitmap == null)
										{
											uploadData();
										}
										
								else 
										{
											uploadImage();
										}
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
			noticeImageView.setImageBitmap(bitmap);
		}
	}
	
	private void uploadImage()
			{
				progressDialog.show();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				byte[] finalimg = baos.toByteArray();
				final StorageReference filePath;
				filePath = storageReference.child("Notice").child(finalimg + "jpg");
				final UploadTask uploadTask = filePath.putBytes(finalimg);
				uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>()
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
																									uploadData();
																								}	
																					});
																		}
															});
												}
										else	{
													progressDialog.dismiss();
													Toast.makeText(UploadNotice.this, "Something went wrong. " + task.getException(), Toast.LENGTH_SHORT).show();
												}
									}
						});
			}
			
	private void uploadData()
			{
				progressDialog.show();
				dbRef = databaseReference.child("Notice");
				final String uniqueKey = dbRef.push().getKey();
				
				String title = noticeBox.getText().toString();
				
				Calendar calForDate = Calendar.getInstance();
				SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
				String date = currentDate.format(calForDate.getTime());
				
				Calendar calForTime = Calendar.getInstance();
				SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
				String time = currentTime.format(calForTime.getTime());
				
				NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey);
				
				dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(UploadNotice.this, new OnSuccessListener<Void>()
						{
							@Override
							public void onSuccess(Void aVoid)
									{
										progressDialog.dismiss();
										Toast.makeText(UploadNotice.this, "Notice Successfully Uploaded.", Toast.LENGTH_SHORT).show();
										finish();
									}
							
						}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
											{
												progressDialog.dismiss();
												Toast.makeText(UploadNotice.this, "An error occured. Exception: " + e, Toast.LENGTH_LONG).show();
												finish();
											}
									
								});
			}
}