package com.manager.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class User
{
	@Id
	@GeneratedValue
	@Column(name="user_id")
	private int id;
	
	@NotBlank(message="Name Required")
	@Column(name="user_name")
	private String name;
	
	@NotBlank(message="Email Required")
	@Email(regexp="[a-z0-9]+@[a-z]+\\.[a-z]{2,3}", message="Invalid Email")
	@Column(name="user_email", unique = true)
	private String email;
	
	@NotBlank(message="Password Required")
	@Size(min=5, message="Password must be minimum 5 characters")
	@Column(name="user_password")
	private String password;
	
	@Column(name="user_role")
	private String role;
	
	@Column(name="about_user", length = 500)
	private String about;
	
	@Column(name="user_image")
	private String imageUrl;
	
	@Column(name="enabled_status")
	private boolean isEnabled;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Contact> contacts = new ArrayList<>();
	
	
	// SETTERS AND GETTERS
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

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public String getAbout()
	{
		return about;
	}

	public void setAbout(String about)
	{
		this.about = about;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public boolean isEnabled()
	{
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}

	public List<Contact> getContacts()
	{
		return contacts;
	}
	
	public void addContact(Contact contact)
	{
		this.contacts.add(contact);
	}
	
	// CONSTRUCTORS
	public User()
	{

	}

	public User(String name, String email, String password, String role, String about, String imageUrl,
			boolean isEnabled)
	{
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.about = about;
		this.imageUrl = imageUrl;
		this.isEnabled = isEnabled;
	}

	
	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role="
				+ role + ", about=" + about + ", imageUrl=" + imageUrl + ", isEnabled=" + isEnabled + "]";
	}

}
