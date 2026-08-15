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
import java.util.Collections;
import java.util.List;

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
            User demo = users.findByEmail(demoEmail).orElse(null);
            if (demo == null) {
                demo = new User();
                demo.setEmail(demoEmail);
                demo.setPasswordHash(encoder.encode(demoPassword));
                demo.setDemo(true);
                demo.setPlan("CLINIC");
            }
            demo.setFullName("Ahmad Faizal");
            demo.setCompanyName("Klinik Haven Kajang");
            demo = users.save(demo);

            List<Doctor> existingDoctors = doctors.findByOwnerOrderByCreatedAtDesc(demo);
            Collections.reverse(existingDoctors);
            if (existingDoctors.isEmpty()) {
                doctors.save(doctor(demo, "Dr. Ahmad Faizal", "Pengamal Perubatan Am", "ACTIVE", "+60 12-334 8890", "ahmad.faizal@haven.clinic"));
                doctors.save(doctor(demo, "Dr. Nur Aisyah Kamal", "Pediatrik", "ACTIVE", "+60 13-227 4412", "nur.aisyah@haven.clinic"));
                doctors.save(doctor(demo, "Dr. Kavitha Rajendran", "Perubatan Dalaman", "ON_LEAVE", "+60 16-908 5521", "kavitha.rajendran@haven.clinic"));
                existingDoctors = doctors.findByOwnerOrderByCreatedAtDesc(demo);
                Collections.reverse(existingDoctors);
            } else {
                applyDoctor(existingDoctors, 0, "Dr. Ahmad Faizal", "Pengamal Perubatan Am", "ACTIVE", "+60 12-334 8890", "ahmad.faizal@haven.clinic");
                applyDoctor(existingDoctors, 1, "Dr. Nur Aisyah Kamal", "Pediatrik", "ACTIVE", "+60 13-227 4412", "nur.aisyah@haven.clinic");
                applyDoctor(existingDoctors, 2, "Dr. Kavitha Rajendran", "Perubatan Dalaman", "ON_LEAVE", "+60 16-908 5521", "kavitha.rajendran@haven.clinic");
                doctors.saveAll(existingDoctors);
            }

            List<Patient> existingPatients = patients.findByOwnerOrderByCreatedAtDesc(demo);
            Collections.reverse(existingPatients);
            if (existingPatients.isEmpty()) {
                patients.save(patient(demo, "Siti Aminah binti Osman", 34, "Female", "A+", "+60 11-2345 7781"));
                patients.save(patient(demo, "Tan Wei Ming", 41, "Male", "O+", "+60 12-678 3344"));
                patients.save(patient(demo, "Nur Qalesya binti Hafiz", 8, "Female", "B+", "+60 17-445 2209"));
                patients.save(patient(demo, "Rajan a/l Muthusamy", 67, "Male", "AB-", "+60 19-331 8704"));
                existingPatients = patients.findByOwnerOrderByCreatedAtDesc(demo);
                Collections.reverse(existingPatients);
            } else {
                applyPatient(existingPatients, 0, "Siti Aminah binti Osman", 34, "Female", "A+", "+60 11-2345 7781");
                applyPatient(existingPatients, 1, "Tan Wei Ming", 41, "Male", "O+", "+60 12-678 3344");
                applyPatient(existingPatients, 2, "Nur Qalesya binti Hafiz", 8, "Female", "B+", "+60 17-445 2209");
                applyPatient(existingPatients, 3, "Rajan a/l Muthusamy", 67, "Male", "AB-", "+60 19-331 8704");
                patients.saveAll(existingPatients);
            }

            List<Room> existingRooms = rooms.findByOwnerOrderByCreatedAtDesc(demo);
            Collections.reverse(existingRooms);
            if (existingRooms.isEmpty()) {
                rooms.save(room(demo, "Bilik Rundingan 1", "CONSULTATION", "AVAILABLE", 1));
                rooms.save(room(demo, "Bilik Rundingan 2", "CONSULTATION", "OCCUPIED", 1));
                rooms.save(room(demo, "Wad A", "WARD", "AVAILABLE", 6));
                rooms.save(room(demo, "Bilik Makmal", "LAB", "AVAILABLE", 2));
            } else {
                applyRoom(existingRooms, 0, "Bilik Rundingan 1");
                applyRoom(existingRooms, 1, "Bilik Rundingan 2");
                applyRoom(existingRooms, 2, "Wad A");
                applyRoom(existingRooms, 3, "Bilik Makmal");
                rooms.saveAll(existingRooms);
            }

            if (appointments.findByOwnerOrderByAppointmentDateDescAppointmentTimeDesc(demo).isEmpty()) {
                Doctor d1 = existingDoctors.get(0);
                Doctor d2 = existingDoctors.size() > 1 ? existingDoctors.get(1) : d1;
                Patient p1 = existingPatients.get(0);
                Patient p2 = existingPatients.size() > 1 ? existingPatients.get(1) : p1;
                Patient p3 = existingPatients.size() > 2 ? existingPatients.get(2) : p1;
                Patient p4 = existingPatients.size() > 3 ? existingPatients.get(3) : p1;

                appointments.save(appt(demo, p1, d1, "Semakan susulan", "SCHEDULED", LocalDate.now(), LocalTime.of(9, 30)));
                appointments.save(appt(demo, p2, d1, "Semakan tekanan darah", "CHECKED_IN", LocalDate.now(), LocalTime.of(10, 15)));
                appointments.save(appt(demo, p3, d2, "Demam kanak-kanak", "SCHEDULED", LocalDate.now(), LocalTime.of(11, 0)));
                appointments.save(appt(demo, p4, d1, "Pemantauan kencing manis", "COMPLETED", LocalDate.now().minusDays(1), LocalTime.of(15, 0)));

                prescriptions.save(rx(demo, p1, d1, "Amoxicillin", "500mg", "3 kali sehari selepas makan"));
                prescriptions.save(rx(demo, p2, d1, "Amlodipine", "5mg", "Sekali sehari pada waktu pagi"));
                prescriptions.save(rx(demo, p3, d2, "Paracetamol syrup", "5ml", "Setiap 6 jam jika demam"));

                bills.save(bill(demo, p1, "BILL-0001", "Konsultasi + ujian makmal", "80.00", "PAID"));
                bills.save(bill(demo, p2, "BILL-0002", "Konsultasi", "45.00", "PENDING"));
                bills.save(bill(demo, p4, "BILL-0003", "Konsultasi + ubat", "150.00", "OVERDUE"));

                supplies.save(supply(demo, "Pelitup muka", "PPE", 240, "kotak", 40));
                supplies.save(supply(demo, "Sarung tangan pakai buang", "PPE", 18, "kotak", 20));
                supplies.save(supply(demo, "Larutan saline", "Farmasi", 55, "botol", 15));
                supplies.save(supply(demo, "Pembalut", "Pertolongan cemas", 90, "pek", 25));
            } else {
                List<Appointment> existingAppts = appointments.findByOwnerOrderByAppointmentDateDescAppointmentTimeDesc(demo);
                applyReason(existingAppts, "Follow-up checkup", "Semakan susulan");
                applyReason(existingAppts, "Blood pressure review", "Semakan tekanan darah");
                applyReason(existingAppts, "Child fever consult", "Demam kanak-kanak");
                applyReason(existingAppts, "Diabetes monitoring", "Pemantauan kencing manis");
                appointments.saveAll(existingAppts);

                List<Prescription> existingRx = prescriptions.findByOwnerOrderByCreatedAtDesc(demo);
                applyRx(existingRx, "3 times daily after meals", "3 kali sehari selepas makan");
                applyRx(existingRx, "Once daily in the morning", "Sekali sehari pada waktu pagi");
                applyRx(existingRx, "Every 6 hours if fever", "Setiap 6 jam jika demam");
                prescriptions.saveAll(existingRx);

                List<Bill> existingBills = bills.findByOwnerOrderByCreatedAtDesc(demo);
                applyBill(existingBills, "Consultation + lab", "Konsultasi + ujian makmal", "80.00");
                applyBill(existingBills, "Consultation", "Konsultasi", "45.00");
                applyBill(existingBills, "Consultation + medication", "Konsultasi + ubat", "150.00");
                bills.saveAll(existingBills);

                List<Supply> existingSupplies = supplies.findByOwnerOrderByCreatedAtDesc(demo);
                applySupply(existingSupplies, "Surgical masks", "Pelitup muka", "PPE", "kotak");
                applySupply(existingSupplies, "Disposable gloves", "Sarung tangan pakai buang", "PPE", "kotak");
                applySupply(existingSupplies, "Saline solution", "Larutan saline", "Farmasi", "botol");
                applySupply(existingSupplies, "Bandages", "Pembalut", "Pertolongan cemas", "pek");
                supplies.saveAll(existingSupplies);
            }
        };
    }

    private void applyDoctor(List<Doctor> list, int index, String name, String specialty, String status, String phone, String email) {
        if (index >= list.size()) {
            return;
        }
        Doctor d = list.get(index);
        d.setFullName(name);
        d.setSpecialty(specialty);
        d.setStatus(status);
        d.setPhone(phone);
        d.setEmail(email);
    }

    private void applyPatient(List<Patient> list, int index, String name, int age, String gender, String blood, String phone) {
        if (index >= list.size()) {
            return;
        }
        Patient p = list.get(index);
        p.setFullName(name);
        p.setAge(age);
        p.setGender(gender);
        p.setBloodType(blood);
        p.setPhone(phone);
    }

    private void applyRoom(List<Room> list, int index, String name) {
        if (index >= list.size()) {
            return;
        }
        list.get(index).setName(name);
    }

    private void applyReason(List<Appointment> list, String from, String to) {
        for (Appointment a : list) {
            if (from.equals(a.getReason())) {
                a.setReason(to);
            }
        }
    }

    private void applyRx(List<Prescription> list, String from, String to) {
        for (Prescription p : list) {
            if (from.equals(p.getInstructions())) {
                p.setInstructions(to);
            }
        }
    }

    private void applyBill(List<Bill> list, String from, String to, String amount) {
        for (Bill b : list) {
            if (from.equals(b.getDescription())) {
                b.setDescription(to);
                b.setAmount(new BigDecimal(amount));
            }
        }
    }

    private void applySupply(List<Supply> list, String from, String to, String category, String unit) {
        for (Supply s : list) {
            if (from.equals(s.getName())) {
                s.setName(to);
                s.setCategory(category);
                s.setUnit(unit);
            }
        }
    }

    private Doctor doctor(User owner, String name, String specialty, String status, String phone, String email) {
        Doctor d = new Doctor();
        d.setOwner(owner);
        d.setFullName(name);
        d.setSpecialty(specialty);
        d.setStatus(status);
        d.setPhone(phone);
        d.setEmail(email);
        return d;
    }

    private Patient patient(User owner, String name, int age, String gender, String blood, String phone) {
        Patient p = new Patient();
        p.setOwner(owner);
        p.setFullName(name);
        p.setAge(age);
        p.setGender(gender);
        p.setBloodType(blood);
        p.setStatus("ACTIVE");
        p.setPhone(phone);
        p.setAllergies("Tiada direkodkan");
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
