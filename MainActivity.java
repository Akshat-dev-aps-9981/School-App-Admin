package com.aksapps.svmadminapp;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.aksapps.svmadminapp.Test;
import com.aksapps.svmadminapp.UploadImages;
import com.aksapps.svmadminapp.UploadNotice;

public class MainActivity extends AppCompatActivity
{

	private CardView uploadNotice, uploadGalleryImage, uploadPdf, faculty, deleteNotice;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		uploadNotice = findViewById(R.id.addNotice);
		uploadGalleryImage = findViewById(R.id.addGalleryImage);
		uploadPdf = findViewById(R.id.addEbook);
		faculty = findViewById(R.id.faculty);
		deleteNotice = findViewById(R.id.deleteNotice);
		
		uploadNotice.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, UploadNotice.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "Upload Notice.", Toast.LENGTH_SHORT).show();
			}
		});
		
		uploadGalleryImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, UploadImages.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "Upload Gallery Image.", Toast.LENGTH_SHORT).show();
			}
		});
		
		uploadPdf.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, UploadPdf.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "Upload Pdf.", Toast.LENGTH_SHORT).show();
			}
		});
		
		faculty.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, UpdateFaculty.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "Faculty.", Toast.LENGTH_SHORT).show();
			}
		});
		
		deleteNotice.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "Delete Notice.", Toast.LENGTH_SHORT).show();
			}
		});
    }
}