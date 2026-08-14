package com.fluxa.service;

import com.fluxa.model.*;
import com.fluxa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkspaceService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;
    private final RoomRepository roomRepository;
    private final SupplyRepository supplyRepository;

    public WorkspaceService(PatientRepository patientRepository,
                            DoctorRepository doctorRepository,
                            AppointmentRepository appointmentRepository,
                            PrescriptionRepository prescriptionRepository,
                            BillRepository billRepository,
                            RoomRepository roomRepository,
                            SupplyRepository supplyRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billRepository = billRepository;
        this.roomRepository = roomRepository;
        this.supplyRepository = supplyRepository;
    }

    public Map<String, Object> dashboard(User owner) {
        Map<String, Object> data = new HashMap<>();
        data.put("patientCount", patientRepository.countByOwner(owner));
        data.put("doctorCount", doctorRepository.countByOwner(owner));
        data.put("todayAppointments", appointmentRepository.countByOwnerAndAppointmentDate(owner, LocalDate.now()));
        data.put("scheduledCount", appointmentRepository.countByOwnerAndStatus(owner, "SCHEDULED"));
        data.put("checkedInCount", appointmentRepository.countByOwnerAndStatus(owner, "CHECKED_IN"));
        data.put("completedCount", appointmentRepository.countByOwnerAndStatus(owner, "COMPLETED"));
        data.put("activePrescriptions", prescriptionRepository.countByOwnerAndStatus(owner, "ACTIVE"));
        data.put("paidTotal", billRepository.sumPaidByOwner(owner));
        data.put("outstandingTotal", billRepository.sumOutstandingByOwner(owner));
        data.put("availableRooms", roomRepository.countByOwnerAndStatus(owner, "AVAILABLE"));
        data.put("occupiedRooms", roomRepository.countByOwnerAndStatus(owner, "OCCUPIED"));
        data.put("supplyCount", supplyRepository.countByOwner(owner));
        data.put("recentAppointments", appointmentRepository
                .findByOwnerOrderByAppointmentDateDescAppointmentTimeDesc(owner).stream().limit(5).toList());
        data.put("recentPatients", patientRepository.findByOwnerOrderByCreatedAtDesc(owner).stream().limit(5).toList());
        return data;
    }

    public List<Patient> patients(User owner) {
        return patientRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Patient getPatient(Long id, User owner) {
        return patientRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    @Transactional
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id, User owner) {
        patientRepository.delete(getPatient(id, owner));
    }

    public List<Doctor> doctors(User owner) {
        return doctorRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Doctor getDoctor(Long id, User owner) {
        return doctorRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    @Transactional
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id, User owner) {
        doctorRepository.delete(getDoctor(id, owner));
    }

    public List<Appointment> appointments(User owner) {
        return appointmentRepository.findByOwnerOrderByAppointmentDateDescAppointmentTimeDesc(owner);
    }

    public Appointment getAppointment(Long id, User owner) {
        return appointmentRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Transactional
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id, User owner) {
        appointmentRepository.delete(getAppointment(id, owner));
    }

    public List<Prescription> prescriptions(User owner) {
        return prescriptionRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Prescription getPrescription(Long id, User owner) {
        return prescriptionRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
    }

    @Transactional
    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public void deletePrescription(Long id, User owner) {
        prescriptionRepository.delete(getPrescription(id, owner));
    }

    public List<Bill> bills(User owner) {
        return billRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Bill getBill(Long id, User owner) {
        return billRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }

    @Transactional
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    @Transactional
    public void deleteBill(Long id, User owner) {
        billRepository.delete(getBill(id, owner));
    }

    public String nextBillNumber(User owner) {
        long count = billRepository.countByOwner(owner) + 1;
        return String.format("BILL-%04d", count);
    }

    public BigDecimal paidTotal(User owner) {
        return billRepository.sumPaidByOwner(owner);
    }

    public BigDecimal outstandingTotal(User owner) {
        return billRepository.sumOutstandingByOwner(owner);
    }

    public List<Room> rooms(User owner) {
        return roomRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Room getRoom(Long id, User owner) {
        return roomRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    @Transactional
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id, User owner) {
        roomRepository.delete(getRoom(id, owner));
    }

    public List<Supply> supplies(User owner) {
        return supplyRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Supply getSupply(Long id, User owner) {
        return supplyRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Supply not found"));
    }

    @Transactional
    public Supply saveSupply(Supply supply) {
        return supplyRepository.save(supply);
    }

    @Transactional
    public void deleteSupply(Long id, User owner) {
        supplyRepository.delete(getSupply(id, owner));
    }
}
