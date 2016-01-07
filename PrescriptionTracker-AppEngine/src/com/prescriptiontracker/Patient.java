package com.prescriptiontracker;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity(name = "Patient")
public class Patient {

	@Id
	private String patientId;
	
	private String patientName;
	private Date patientDob;
	private Integer patientMob;
	

	public Patient(String patientId) {
        this.patientId = patientId;
    }
	
	 public String getPatientId() {
	        return patientId;
	    }
	
	 public void setPatientName (String patientName){
		 this.patientName=patientName;
		 
	 }
	 
	 public String getPatientName (){
		return patientName; 
	
	 }
	 
	 public void setPatientDob (Date patientDob){
		 this.patientDob=patientDob;
		 
	 }
	 
	 public Date getPatientDob(){
		return patientDob; 
	
	 }
	 
	 public void setPatientMob (Integer patientMob){
		 this.patientMob=patientMob;
		 
	 }
	 
	 public Integer getPatientMob(){
		return patientMob; 
	
	 }
}
