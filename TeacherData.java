package com.aksapps.svmadminapp;

public class TeacherData
{
	private String name, email, phoneNumber, image, key;
	
	public TeacherData()
			{
				
			}
			
	public TeacherData(String name, String email, String phoneNumber, String image, String key)
			{
				this.name = name;
				this.email = email;
				this.phoneNumber = phoneNumber;
				this.image = image;
				this.key = key;
			}
			
	public String getName()
			{
				return name;
			}
			
	public void setName(String name)
			{
				this.name = name;
			}
			
	public String getEmail()
			{
				return email;
			}
			
	public void setEmail(String email)
			{
				this.email = email;
			}
			
	public String getPhoneNumber()
			{
				return phoneNumber;
			}
	
	public void setPhoneNumber(String phoneNumber)
			{
				this.phoneNumber = phoneNumber;
			}
	
	public String getImage()
			{
				return image;
			}
	
	public void setImage(String image)
			{
				this.image = image;
			}
			
	public String getKey()
			{
				return key;
			}
	
	public void setKey(String key)
			{
				this.key = key;
			}
}