package Project.ChauPhim.Models;

import java.time.LocalDate;

public abstract class Person {
    protected String name;
    protected LocalDate dob;
    protected String gender;
    protected String nationality;
    protected String imageURL;
    protected String bio; 
    public Person(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }
    // CÁC TRƯỜNG KHÔNG BẮT BUỘC NHẬP MÀ KHÔNG CÓ DỮ LIỆU THÌ MẶC ĐỊNH LÀ NULL
	public Person(String name, LocalDate dob, String gender, String nationality, String imageURL, String bio) {
		super();
		this.name = name;	// BẮT BUỘC PHẢI NHẬP
		this.dob = dob;
		this.gender = gender;
		this.nationality = nationality;
		this.imageURL = imageURL;	// BẮT BUỘC PHẢI NHẬP
		this.bio = bio;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}

}
