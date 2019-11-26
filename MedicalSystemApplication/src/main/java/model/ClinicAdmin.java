
package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ClinicAdmin extends User {

	
    @ManyToOne(fetch = FetchType.EAGER)
    private Clinic clinic;

    @OneToMany(fetch = FetchType.LAZY)
    private List<AppointmentRequest> appointmentRequests;

    @OneToMany(fetch = FetchType.LAZY)
    private  List<VacationRequest> vacationRequests;

    public ClinicAdmin(){
    	setRole(UserRole.ClinicAdmin);
    	this.setIsFirstLog(true);
    }

    public ClinicAdmin(String username, String password, String email, String name, String surname, String city, String address, String state, String phone, Clinic clinic) {
        super(username, password, email, name, surname, city, address, state, phone, UserRole.ClinicAdmin);
        this.clinic = clinic;
        this.appointmentRequests = new ArrayList<>();
        this.vacationRequests = new ArrayList<>();
        this.setIsFirstLog(true);
    }


    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public List<AppointmentRequest> getAppointmentRequests() {
        return appointmentRequests;
    }

    public void setAppointmentRequests(List<AppointmentRequest> appointmentRequests) {
        this.appointmentRequests = appointmentRequests;
    }

    public List<VacationRequest> getVacationRequests() {
        return vacationRequests;
    }

    public void setVacationRequests(List<VacationRequest> vacationRequests) {
        this.vacationRequests = vacationRequests;
    }
}

