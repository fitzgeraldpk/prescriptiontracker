/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-06-19 16:59:56 UTC)
 * on 2013-06-19 at 17:40:34 UTC 
 * Modify at your own risk.
 */

package com.prescriptionwriter.prescriptionendpoint.model;

/**
 * Model definition for Prescription.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Prescription extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dateFrom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime dateTo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<com.google.api.client.util.DateTime> datesTaken;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String patientId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String patientName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer perDay;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String pharmacyId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String prescription;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<com.google.api.client.util.DateTime> prescriptionDates;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime prescriptionDatesTaken;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String prescriptionDoctorName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String prescriptionId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String prescriptionName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer takePerDay;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime toDateTo;

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDateFrom() {
    return dateFrom;
  }

  /**
   * @param dateFrom dateFrom or {@code null} for none
   */
  public Prescription setDateFrom(com.google.api.client.util.DateTime dateFrom) {
    this.dateFrom = dateFrom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getDateTo() {
    return dateTo;
  }

  /**
   * @param dateTo dateTo or {@code null} for none
   */
  public Prescription setDateTo(com.google.api.client.util.DateTime dateTo) {
    this.dateTo = dateTo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<com.google.api.client.util.DateTime> getDatesTaken() {
    return datesTaken;
  }

  /**
   * @param datesTaken datesTaken or {@code null} for none
   */
  public Prescription setDatesTaken(java.util.List<com.google.api.client.util.DateTime> datesTaken) {
    this.datesTaken = datesTaken;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPatientId() {
    return patientId;
  }

  /**
   * @param patientId patientId or {@code null} for none
   */
  public Prescription setPatientId(java.lang.String patientId) {
    this.patientId = patientId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPatientName() {
    return patientName;
  }

  /**
   * @param patientName patientName or {@code null} for none
   */
  public Prescription setPatientName(java.lang.String patientName) {
    this.patientName = patientName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getPerDay() {
    return perDay;
  }

  /**
   * @param perDay perDay or {@code null} for none
   */
  public Prescription setPerDay(java.lang.Integer perDay) {
    this.perDay = perDay;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPharmacyId() {
    return pharmacyId;
  }

  /**
   * @param pharmacyId pharmacyId or {@code null} for none
   */
  public Prescription setPharmacyId(java.lang.String pharmacyId) {
    this.pharmacyId = pharmacyId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrescription() {
    return prescription;
  }

  /**
   * @param prescription prescription or {@code null} for none
   */
  public Prescription setPrescription(java.lang.String prescription) {
    this.prescription = prescription;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<com.google.api.client.util.DateTime> getPrescriptionDates() {
    return prescriptionDates;
  }

  /**
   * @param prescriptionDates prescriptionDates or {@code null} for none
   */
  public Prescription setPrescriptionDates(java.util.List<com.google.api.client.util.DateTime> prescriptionDates) {
    this.prescriptionDates = prescriptionDates;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getPrescriptionDatesTaken() {
    return prescriptionDatesTaken;
  }

  /**
   * @param prescriptionDatesTaken prescriptionDatesTaken or {@code null} for none
   */
  public Prescription setPrescriptionDatesTaken(com.google.api.client.util.DateTime prescriptionDatesTaken) {
    this.prescriptionDatesTaken = prescriptionDatesTaken;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrescriptionDoctorName() {
    return prescriptionDoctorName;
  }

  /**
   * @param prescriptionDoctorName prescriptionDoctorName or {@code null} for none
   */
  public Prescription setPrescriptionDoctorName(java.lang.String prescriptionDoctorName) {
    this.prescriptionDoctorName = prescriptionDoctorName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrescriptionId() {
    return prescriptionId;
  }

  /**
   * @param prescriptionId prescriptionId or {@code null} for none
   */
  public Prescription setPrescriptionId(java.lang.String prescriptionId) {
    this.prescriptionId = prescriptionId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrescriptionName() {
    return prescriptionName;
  }

  /**
   * @param prescriptionName prescriptionName or {@code null} for none
   */
  public Prescription setPrescriptionName(java.lang.String prescriptionName) {
    this.prescriptionName = prescriptionName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getTakePerDay() {
    return takePerDay;
  }

  /**
   * @param takePerDay takePerDay or {@code null} for none
   */
  public Prescription setTakePerDay(java.lang.Integer takePerDay) {
    this.takePerDay = takePerDay;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getToDateTo() {
    return toDateTo;
  }

  /**
   * @param toDateTo toDateTo or {@code null} for none
   */
  public Prescription setToDateTo(com.google.api.client.util.DateTime toDateTo) {
    this.toDateTo = toDateTo;
    return this;
  }

  @Override
  public Prescription set(String fieldName, Object value) {
    return (Prescription) super.set(fieldName, value);
  }

  @Override
  public Prescription clone() {
    return (Prescription) super.clone();
  }

}
