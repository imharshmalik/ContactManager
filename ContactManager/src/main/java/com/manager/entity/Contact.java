package com.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Contact
{
	@Id
	@GeneratedValue
	@Column(name="contact_id")
	private int id;
	
	@NotBlank(message="Contact Name Required")
	@Column(name="contact_name")
	private String name;
	
	@Column(name="contact_email")
	private String email;
	
	@Column(name="contact_city")
	private String city;
	
	@NotBlank(message="Phone Number Required")
	@Pattern(regexp = "^\\+?\\d{6,10}$", message = "Enter Valid Phone Number (Minimum 6 Digits)")
	@Column(name="contact_phone")
	private String phone;
	
	@Column(name="contact_description", length=500)
	private String description;
	
	@Column(name="contact_image")
	private String image;

	@JsonIgnore
	@ManyToOne
	private User user;
	
	
	// GETTERS AND SETTERS
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}	
	
	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	// CONSTRUCTORS
	public Contact()
	{
		
	}

	public Contact(String name, String email, String city, String phone, String description, String image, User user)
	{
		this.name = name;
		this.email = email;
		this.city = city;
		this.phone = phone;
		this.description = description;
		this.image = image;
		this.user = user;
	}

	
	@Override
	public String toString()
	{
		return "Contact [id=" + id + ", name=" + name + ", email=" + email + ", city=" + city + ", phone=" + phone
				+ ", description=" + description + ", image=" + image + "]";
	}
		
}
