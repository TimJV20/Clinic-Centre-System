package repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import model.Appointment;
import model.Clinic;
import model.Doctor;
import model.Hall;
import model.Patient;
import model.User;

public interface AppointmentRepository extends JpaRepository<Appointment,Long>{

	public Appointment findByDateAndHallAndClinic(Date date, Hall hall, Clinic clinic);
	
	public List<Appointment> findAllByPatient(Patient p);
	
	
	public List<Appointment> findAllByDoctors(Doctor d);
	public List<Appointment> findAllByHall(Hall hall);
}