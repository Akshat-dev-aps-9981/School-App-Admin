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
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;

public class UploadImages extends AppCompatActivity
{
	private CardView addGalleryImage;
	private AppCompatSpinner imageCategory;
	private Button uploadImageBtn;
	private ImageView galleryImageView;
	private String category;
	private final int REQ = 1;
	private Bitmap bitmap;
	private ProgressDialog progressDialog;
	private String downloadUrl;
	
	private DatabaseReference databaseReference;
	private StorageReference storageReference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_images);
		
		addGalleryImage = findViewById(R.id.addGalleryImage);
		imageCategory = findViewById(R.id.imageCategory);
		uploadImageBtn = findViewById(R.id.uploadImageBtn);
		galleryImageView = findViewById(R.id.galleryImageView);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Please wait...");
		progressDialog.setMessage("Uploading Image...");
		progressDialog.setCancelable(false);
		
		databaseReference = FirebaseDatabase.getInstance().getReference().child("Gallery");
		storageReference = FirebaseStorage.getInstance().getReference().child("gallery/g");
		
		String[] items = new String[]{"Select Category", "Convocation", "Independance Day", "Other Events"};
		imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));
		
		
		imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
							{
								category = imageCategory.getSelectedItem().toString();
							}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView)
							{
								
							}
					
				});
		
		addGalleryImage.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								openGallery();
							}
					
				});
				
		uploadImageBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
							{
								if(bitmap == null)
										{
											Toast.makeText(UploadImages.this, "Please select an image.", Toast.LENGTH_SHORT).show();
										}
										
								else if(category.equals("Select Category"))
										{
											Toast.makeText(UploadImages.this, "Please select one Image Category.", Toast.LENGTH_SHORT).show();
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
			galleryImageView.setImageBitmap(bitmap);
		}
	}
	
	private void uploadImage()
			{
				progressDialog.show();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				byte[] finalimg1 = baos.toByteArray();
				final StorageReference filePath1;
				filePath1 = storageReference.child(finalimg1 + "jpg");
				final UploadTask uploadTask = filePath1.putBytes(finalimg1);
				uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
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
									filePath1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
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
						else
						{
							progressDialog.dismiss();
							Toast.makeText(UploadImages.this, "Something went wrong. " + task.getException(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
	private void uploadData()
			{
				progressDialog.show();
				databaseReference = databaseReference.child(category);
				final String uniqueKey = databaseReference.push().getKey();
				
				databaseReference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>()
						{
							@Override
							public void onSuccess(Void aVoid)
									{
										progressDialog.dismiss();
										Toast.makeText(UploadImages.this, "Image successfully uploaded.", Toast.LENGTH_SHORT).show();
										finish();
									}
							
						}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
											{
												progressDialog.dismiss();
												Toast.makeText(UploadImages.this, "Something went wrong. Exception: " + e, Toast.LENGTH_LONG).show();
												finish();
											}
									
								});
			}
}