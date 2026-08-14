package com.fluxa.config;

import com.fluxa.model.*;
import com.fluxa.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Value("${fluxa.demo.email}")
    private String demoEmail;

    @Value("${fluxa.demo.password}")
    private String demoPassword;

    @Bean
    CommandLineRunner seed(UserRepository users,
                           PatientRepository patients,
                           DoctorRepository doctors,
                           AppointmentRepository appointments,
                           PrescriptionRepository prescriptions,
                           BillRepository bills,
                           RoomRepository rooms,
                           SupplyRepository supplies,
                           PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail(demoEmail).isPresent()) {
                return;
            }

            User demo = new User();
            demo.setEmail(demoEmail);
            demo.setPasswordHash(encoder.encode(demoPassword));
            demo.setFullName("Dr. Maya Chen");
            demo.setCompanyName("Haven Family Clinic");
            demo.setPlan("CLINIC");
            demo.setDemo(true);
            demo = users.save(demo);

            Doctor d1 = doctor(demo, "Dr. Amir Rahman", "General Practice", "ACTIVE");
            Doctor d2 = doctor(demo, "Dr. Sofia Lim", "Pediatrics", "ACTIVE");
            Doctor d3 = doctor(demo, "Dr. Noah Tan", "Internal Medicine", "ON_LEAVE");
            doctors.save(d1);
            doctors.save(d2);
            doctors.save(d3);

            Patient p1 = patient(demo, "Aisha Abdullah", 34, "Female", "A+", "ACTIVE");
            Patient p2 = patient(demo, "Daniel Wong", 41, "Male", "O+", "ACTIVE");
            Patient p3 = patient(demo, "Mei Ling", 8, "Female", "B+", "ACTIVE");
            Patient p4 = patient(demo, "Hassan Ali", 67, "Male", "AB-", "ACTIVE");
            patients.save(p1);
            patients.save(p2);
            patients.save(p3);
            patients.save(p4);

            appointments.save(appt(demo, p1, d1, "Follow-up checkup", "SCHEDULED", LocalDate.now(), LocalTime.of(9, 30)));
            appointments.save(appt(demo, p2, d1, "Blood pressure review", "CHECKED_IN", LocalDate.now(), LocalTime.of(10, 15)));
            appointments.save(appt(demo, p3, d2, "Child fever consult", "SCHEDULED", LocalDate.now(), LocalTime.of(11, 0)));
            appointments.save(appt(demo, p4, d1, "Diabetes monitoring", "COMPLETED", LocalDate.now().minusDays(1), LocalTime.of(15, 0)));

            prescriptions.save(rx(demo, p1, d1, "Amoxicillin", "500mg", "3 times daily after meals"));
            prescriptions.save(rx(demo, p2, d1, "Amlodipine", "5mg", "Once daily in the morning"));
            prescriptions.save(rx(demo, p3, d2, "Paracetamol syrup", "5ml", "Every 6 hours if fever"));

            bills.save(bill(demo, p1, "BILL-0001", "Consultation + lab", "180.00", "PAID"));
            bills.save(bill(demo, p2, "BILL-0002", "Consultation", "80.00", "PENDING"));
            bills.save(bill(demo, p4, "BILL-0003", "Consultation + medication", "220.00", "OVERDUE"));

            rooms.save(room(demo, "Consult 1", "CONSULTATION", "AVAILABLE", 1));
            rooms.save(room(demo, "Consult 2", "CONSULTATION", "OCCUPIED", 1));
            rooms.save(room(demo, "Ward A", "WARD", "AVAILABLE", 6));
            rooms.save(room(demo, "Lab Room", "LAB", "AVAILABLE", 2));

            supplies.save(supply(demo, "Surgical masks", "PPE", 240, "box", 40));
            supplies.save(supply(demo, "Disposable gloves", "PPE", 18, "box", 20));
            supplies.save(supply(demo, "Saline solution", "Pharmacy", 55, "bottle", 15));
            supplies.save(supply(demo, "Bandages", "First Aid", 90, "pack", 25));
        };
    }

    private Doctor doctor(User owner, String name, String specialty, String status) {
        Doctor d = new Doctor();
        d.setOwner(owner);
        d.setFullName(name);
        d.setSpecialty(specialty);
        d.setStatus(status);
        d.setPhone("+60 12-800 1000");
        d.setEmail(name.toLowerCase().replace(" ", ".").replace("dr.", "") + "@haven.clinic");
        return d;
    }

    private Patient patient(User owner, String name, int age, String gender, String blood, String status) {
        Patient p = new Patient();
        p.setOwner(owner);
        p.setFullName(name);
        p.setAge(age);
        p.setGender(gender);
        p.setBloodType(blood);
        p.setStatus(status);
        p.setPhone("+60 13-200 3000");
        p.setAllergies("None recorded");
        p.setLastVisit(LocalDate.now().minusDays(4));
        return p;
    }

    private Appointment appt(User owner, Patient patient, Doctor doctor, String reason, String status, LocalDate date, LocalTime time) {
        Appointment a = new Appointment();
        a.setOwner(owner);
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setReason(reason);
        a.setStatus(status);
        a.setAppointmentDate(date);
        a.setAppointmentTime(time);
        return a;
    }

    private Prescription rx(User owner, Patient patient, Doctor doctor, String med, String dosage, String instructions) {
        Prescription p = new Prescription();
        p.setOwner(owner);
        p.setPatient(patient);
        p.setDoctor(doctor);
        p.setMedication(med);
        p.setDosage(dosage);
        p.setInstructions(instructions);
        p.setStatus("ACTIVE");
        return p;
    }

    private Bill bill(User owner, Patient patient, String number, String description, String amount, String status) {
        Bill b = new Bill();
        b.setOwner(owner);
        b.setPatient(patient);
        b.setNumber(number);
        b.setDescription(description);
        b.setAmount(new BigDecimal(amount));
        b.setStatus(status);
        return b;
    }

    private Room room(User owner, String name, String type, String status, int capacity) {
        Room r = new Room();
        r.setOwner(owner);
        r.setName(name);
        r.setType(type);
        r.setStatus(status);
        r.setCapacity(capacity);
        return r;
    }

    private Supply supply(User owner, String name, String category, int qty, String unit, int reorder) {
        Supply s = new Supply();
        s.setOwner(owner);
        s.setName(name);
        s.setCategory(category);
        s.setQuantity(qty);
        s.setUnit(unit);
        s.setReorderLevel(reorder);
        return s;
    }
}
