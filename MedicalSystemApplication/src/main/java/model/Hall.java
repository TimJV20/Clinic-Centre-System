package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Entity
public class Hall
{
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clinic_id")
	private Clinic clinic;
	
	@Column(name= "number",nullable = false)
	private int number; 	

	public Hall() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Hall(Clinic clinic, int number) {
		super();
		this.clinic = clinic;
		this.number = number;
	
	}


	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Clinic getClinic() {
		return clinic;
	}
	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}


	@Override
	public String toString() {
		return "Hall [hallID=" + id + ", clinic=" + clinic + ", number=" + number + ", scheduleAppointments="
				+ "]";
	}
	
	
}
