package com.prescriptiontracker;




import java.util.Date;




import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Prescription")
public class Prescription {
	@Id
	private String  prescriptionId;
	private String pharmacyId;
	private String prescriptionName;
	private String prescriptionDoctorName;
	private String patientId;
	private String patientName;
	//List<Date> prescriptionDates = new ArrayList<Date>();
	private Integer takePerDay;
	private Date dateFrom;
	private Date todateTo;
	
	
	
	
	public void setPrescription(String prescriptionId){
		this.prescriptionId=prescriptionId;
			
		}
	
	public void setPharmacyId(String pharmacyId){
	this.pharmacyId=pharmacyId;
		
	}
	
	
	public void setPrescriptionName(String prescriptionName){
		this.prescriptionName=prescriptionName;
			
		}
	
	public void setPrescriptionDoctorName(String prescriptionDoctorName){
		this.prescriptionDoctorName=prescriptionDoctorName;
			
		}
	
	
	public void setPatientId(String patientId){
		this.patientId=patientId;
			
		}
	
		
	
	public void setPatientName(String patientName){
		this.patientName=patientName;
			
		}
	
	public void setDateFrom(Date dateFrom){
		this.dateFrom=dateFrom;
			
		}
	
	public void setToDateTo(Date todateTo){
		this.todateTo=todateTo;
			
		}
	
	public void setPerDay(Integer takePerDay){
		this.takePerDay=takePerDay;
			
		}
	
	
	
	//get prescription ID
		public String  getPrescriptionId (){
			return this.prescriptionId;
		
		}
	
	//get pharmacyID
	public String getPharmacyId(){
			return this.pharmacyId;
		
	}
	
	
	//get prescriptionName
	public String getPrescriptionName(){
				return this.prescriptionName;
			
		}
	
	//get patientName
		public String getPatientName(){
					return this.patientName;
				
			}
		
	//get prescriptionName
	public String getPrescriptionDoctorName(){
			return this.prescriptionDoctorName;
					
		}
	
	//get prescriptionName
	public String getPatientId(){
			return this.patientId;
							
		}
	//get patient ID
	public Date getDateTo (){
		return this.todateTo;
	
	}
	
	//get patient ID
		public Date getDateFrom (){
			return this.dateFrom;
		
		}
	
	//get number of tables per day
	public Integer getTakePerDay(){
		return this.takePerDay;
						
	}
	
	
	
	
	
	/*
	@SuppressWarnings("deprecation")
	public List<Date> getDatesBetween(final Date date1, final Date date2) {
	    List<Date> dates = new ArrayList<Date>();

	    Calendar calendar = new GregorianCalendar() {
			private static final long serialVersionUID = 1L;

		{
	        set(Calendar.YEAR, date1.getYear());
	        set(Calendar.MONTH, date1.getMonth());
	        set(Calendar.DATE, date1.getDate());
	    }};

	    while (calendar.get(Calendar.YEAR) != date2.getYear() && calendar.get(Calendar.MONTH) != date2.getMonth() && calendar.get(Calendar.DATE) != date2.getDate()) {
	        calendar.add(Calendar.DATE, 1);
	        dates.add(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
	    }

	    return dates;
	}*/
	
}
