package controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import dto.PatientMedicalReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.MedicalRecordDTO;
import dto.PasswordDTO;
import dto.UserDTO;
import helpers.SecurePasswordHasher;
import model.*;
import model.User.UserRole;
import service.*;

@RestController
@RequestMapping(value = "api/users")
public class UserController 
{
	@Autowired
	private UserService userService;

	@Autowired
	private ClinicService clinicService;

	@Autowired
	private DiagnosisService diagnosisService;

	@Autowired
	private PatientMedicalReportService patientMedicalReportService;

	@Autowired
	private PrescriptionService prescriptionService;

	@Autowired
	private DrugService drugService;

	@Autowired
	private MedicalRecordService medicalRecordService;
	
	@PutMapping(value = "/update/{email}")
	public ResponseEntity<Void> updateUser(@RequestBody UserDTO dto,@PathVariable("email")String email)
	{	
		User user = userService.findByEmailAndDeleted(email,false);
		if(user != null) {
			user.setName(dto.getName());
			user.setSurname(dto.getSurname());
			user.setAddress(dto.getAddress());
			user.setCity(dto.getCity());
			user.setState(dto.getState());
			user.setPhone(dto.getPhone());
			userService.save(user);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PutMapping(value="/update/password/{email}",consumes ="application/json")
	public ResponseEntity<Void> updatePassword(@PathVariable("email") String email,@RequestBody PasswordDTO dto)
	{
		System.out.println(email);
		User user = userService.findByEmailAndDeleted(email,false);
		
		if(user != null)
		{		
			try {
				String oldPassword = dto.getOldPassword();
				String oldHash = SecurePasswordHasher.getInstance().encode(oldPassword);
				
				if(user.getPassword().equals(oldHash))
				{
					String newPassword = dto.getNewPassword();
					String newHash = SecurePasswordHasher.getInstance().encode(newPassword);
					user.setPassword(newHash);
					userService.save(user);
					return new ResponseEntity<>(HttpStatus.OK);
				}
				
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		
		
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping(value = "/update/firstPassword/{email}" ,consumes = "application/json")
	public ResponseEntity<Void> updateFirstPassword(@PathVariable("email") String email,@RequestBody PasswordDTO dto)
	{
		User user = userService.findByEmailAndDeleted(email,false);
		if(user != null)
		{
			String newPassword = dto.getNewPassword();
			try {
				String hashNewPassword = SecurePasswordHasher.getInstance().encode(newPassword);
				user.setPassword(hashNewPassword);
				user.setIsFirstLog(false);
				userService.save(user);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping(value = "/getUser/{email}")
	public ResponseEntity<User> getUser(@PathVariable("email") String email)
	{
		User ret = userService.findByEmailAndDeleted(email,false);
		
		if(ret == null || ret.getDeleted())
		{
			return new ResponseEntity<>(ret,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(ret,HttpStatus.OK);
	}
	
	@GetMapping(value = "/getAll/{role}")
	public ResponseEntity<List<UserDTO>> getAllUsersByRole(@PathVariable("role") UserRole role)
	{
		List<User> ret = userService.getAll(role);
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for(User u : ret) 
		{
			if(!u.getDeleted())
			{
				dtos.add(new UserDTO(u));			
			}
		}
		
		if(ret == null)
		{
			return new ResponseEntity<>(dtos,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(dtos,HttpStatus.OK);
		
		
	}
	
	@GetMapping(value = "/getAll")
	public ResponseEntity<List<UserDTO>> getAllUsers()
	{
		List<User> ret = userService.getAll();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for(User u : ret) 
		{
			if(!u.getDeleted())
			{
				dtos.add(new UserDTO(u));				
			}
		}
		
		
		if(ret == null)
		{
			return new ResponseEntity<>(dtos,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(dtos,HttpStatus.OK);
	}
	
	@GetMapping(value="/patient/getMedicalRecord/{email}")
	public ResponseEntity<MedicalRecordDTO> getMedicalRecord(@PathVariable("email")String email)
	{
		Patient patient = (Patient)userService.findByEmailAndDeleted(email,false);
		System.out.println(patient.getEmail()+" " +patient.getMedicalRecord().getHeight()+"***************************PACIJENT****************************************");
		if(patient == null)
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		MedicalRecord mr = medicalRecordService.findByPatient(patient);
		System.out.println(mr.getReports() + " " + mr.getAlergies() + mr.getBloodType() + mr.getHeight() + mr.getWeight() + "***********RECORD*********************************************");
		MedicalRecordDTO dto = new MedicalRecordDTO(mr);

		System.out.println(dto.getHeight()+dto.getWeight()+dto.getBloodType()+dto.getAlergies()+dto.getReports()+"********************DTO************************************");
		return new ResponseEntity<>(dto,HttpStatus.OK);
	}
	
	
	@PutMapping(value="/patient/updateMedicalRecord/{email}")
	public ResponseEntity<Void> updateMedicalRecord(@PathVariable("email") String email,@RequestBody MedicalRecordDTO dto)
	{
		Patient patient = (Patient)userService.findByEmailAndDeleted(email,false);

		MedicalRecord record = medicalRecordService.findByPatient(patient);

		if(patient == null)
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if(record == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		record.setBloodType(dto.getBloodType());
		record.setHeight(dto.getHeight());
		record.setWeight(dto.getWeight());
		record.setAlergies(dto.getAlergies());
		medicalRecordService.save(record);

		patient.setMedicalRecord(record);
		userService.save(patient);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/patient/addPatientMedicalReport/{email}")
	public ResponseEntity<Void> addPatientMedicalReport(@PathVariable("email") String email,@RequestBody PatientMedicalReportDTO dto)
	{
		HttpHeaders header = new HttpHeaders();

		Patient patient = (Patient)userService.findByEmailAndDeleted(email,false);
		if(patient == null)
		{
			header.set("responseText","patient not found: " + email);
			return new ResponseEntity<>(header,HttpStatus.NOT_FOUND);
		}

		Doctor doctor = (Doctor)userService.findByEmailAndDeleted(dto.getDoctorEmail(),false);
		if(doctor == null)
		{
			header.set("responseText","doctor not found: " + dto.getDoctorEmail());
			return new ResponseEntity<>(header,HttpStatus.NOT_FOUND);
		}

		Clinic clinic = clinicService.findByName(dto.getClinicName());
		if(clinic == null)
		{
			header.set("responseText","clinic not found: " + dto.getClinicName());
			return new ResponseEntity<>(header, HttpStatus.NOT_FOUND);
		}



		Prescription pr = new Prescription();
		pr.setDescription(dto.getPrescription().getDescription());
		for(String name : dto.getPrescription().getDrugs())
		{
			Drug drug = drugService.findByName(name);

			if(drug == null)
			{
				header.set("responseText","Drug not found: " + name);
				return new ResponseEntity<>(header,HttpStatus.NOT_FOUND);
			}

			pr.getDrugs().add(drug);

		}
		pr.setValid(false);
		prescriptionService.save(pr);

		PatientMedicalReport report = new PatientMedicalReport();
		report.setClinic(clinic);
		report.setDescription(dto.getDescription());
		report.setDoctor(doctor);
		report.setPrescription(pr);
		report.setDateAndTime(dto.getDateAndTime());
		report.setPatient(patient);

		for(String name : dto.getDiagnosis())
		{
			Diagnosis diagnosis = diagnosisService.findByName(name);

			if(diagnosis == null)
			{
				header.set("responseText","Diagnosis not found: " + name);
				return new ResponseEntity<>(header,HttpStatus.NOT_FOUND);
			}


			report.getDiagnosis().add(diagnosis);
		}

		patientMedicalReportService.save(report);

		MedicalRecord mr = medicalRecordService.findByPatient(patient);
		mr.getReports().add(report);
		medicalRecordService.save(mr);

		userService.save(patient);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
