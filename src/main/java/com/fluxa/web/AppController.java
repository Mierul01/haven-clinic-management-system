package com.fluxa.web;

import com.fluxa.model.*;
import com.fluxa.service.UserService;
import com.fluxa.service.WorkspaceService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class AppController {

    private final UserService userService;
    private final WorkspaceService workspaceService;

    public AppController(UserService userService, WorkspaceService workspaceService) {
        this.userService = userService;
        this.workspaceService = workspaceService;
    }

    @ModelAttribute
    public void addCommon(Model model, Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("currentUser", userService.findByEmail(authentication.getName()));
        }
    }

    private User current(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping("/app")
    public String dashboard(Authentication authentication, Model model) {
        model.addAllAttributes(workspaceService.dashboard(current(authentication)));
        model.addAttribute("page", "dashboard");
        return "app/dashboard";
    }

    @GetMapping("/app/patients")
    public String patients(Authentication authentication, Model model) {
        model.addAttribute("patients", workspaceService.patients(current(authentication)));
        model.addAttribute("page", "patients");
        return "app/patients";
    }

    @PostMapping("/app/patients")
    public String createPatient(Authentication authentication,
                                @RequestParam String fullName,
                                @RequestParam(required = false) Integer age,
                                @RequestParam(required = false) String gender,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String bloodType,
                                @RequestParam(required = false) String allergies,
                                @RequestParam(defaultValue = "ACTIVE") String status,
                                RedirectAttributes ra) {
        Patient patient = new Patient();
        patient.setOwner(current(authentication));
        patient.setFullName(fullName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setStatus(status);
        workspaceService.savePatient(patient);
        ra.addFlashAttribute("message", "Patient added");
        return "redirect:/app/patients";
    }

    @PostMapping("/app/patients/delete")
    public String deletePatient(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deletePatient(id, current(authentication));
        ra.addFlashAttribute("message", "Patient removed");
        return "redirect:/app/patients";
    }

    @GetMapping("/app/doctors")
    public String doctors(Authentication authentication, Model model) {
        model.addAttribute("doctors", workspaceService.doctors(current(authentication)));
        model.addAttribute("page", "doctors");
        return "app/doctors";
    }

    @PostMapping("/app/doctors")
    public String createDoctor(Authentication authentication,
                               @RequestParam String fullName,
                               @RequestParam String specialty,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String email,
                               @RequestParam(defaultValue = "ACTIVE") String status,
                               RedirectAttributes ra) {
        Doctor doctor = new Doctor();
        doctor.setOwner(current(authentication));
        doctor.setFullName(fullName);
        doctor.setSpecialty(specialty);
        doctor.setPhone(phone);
        doctor.setEmail(email);
        doctor.setStatus(status);
        workspaceService.saveDoctor(doctor);
        ra.addFlashAttribute("message", "Doctor added");
        return "redirect:/app/doctors";
    }

    @PostMapping("/app/doctors/delete")
    public String deleteDoctor(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deleteDoctor(id, current(authentication));
        ra.addFlashAttribute("message", "Doctor removed");
        return "redirect:/app/doctors";
    }

    @GetMapping("/app/appointments")
    public String appointments(Authentication authentication, Model model) {
        User user = current(authentication);
        model.addAttribute("appointments", workspaceService.appointments(user));
        model.addAttribute("patients", workspaceService.patients(user));
        model.addAttribute("doctors", workspaceService.doctors(user));
        model.addAttribute("page", "appointments");
        return "app/appointments";
    }

    @PostMapping("/app/appointments")
    public String createAppointment(Authentication authentication,
                                    @RequestParam String reason,
                                    @RequestParam(required = false) Long patientId,
                                    @RequestParam(required = false) Long doctorId,
                                    @RequestParam(required = false) String appointmentDate,
                                    @RequestParam(required = false) String appointmentTime,
                                    @RequestParam(defaultValue = "SCHEDULED") String status,
                                    RedirectAttributes ra) {
        User user = current(authentication);
        Appointment appointment = new Appointment();
        appointment.setOwner(user);
        appointment.setReason(reason);
        appointment.setStatus(status);
        if (patientId != null) appointment.setPatient(workspaceService.getPatient(patientId, user));
        if (doctorId != null) appointment.setDoctor(workspaceService.getDoctor(doctorId, user));
        if (appointmentDate != null && !appointmentDate.isBlank()) {
            appointment.setAppointmentDate(LocalDate.parse(appointmentDate));
        }
        if (appointmentTime != null && !appointmentTime.isBlank()) {
            appointment.setAppointmentTime(LocalTime.parse(appointmentTime));
        }
        workspaceService.saveAppointment(appointment);
        ra.addFlashAttribute("message", "Appointment booked");
        return "redirect:/app/appointments";
    }

    @PostMapping("/app/appointments/status")
    public String updateAppointmentStatus(Authentication authentication,
                                          @RequestParam Long id,
                                          @RequestParam String status,
                                          RedirectAttributes ra) {
        Appointment appointment = workspaceService.getAppointment(id, current(authentication));
        appointment.setStatus(status);
        workspaceService.saveAppointment(appointment);
        ra.addFlashAttribute("message", "Appointment updated");
        return "redirect:/app/appointments";
    }

    @PostMapping("/app/appointments/delete")
    public String deleteAppointment(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deleteAppointment(id, current(authentication));
        ra.addFlashAttribute("message", "Appointment removed");
        return "redirect:/app/appointments";
    }

    @GetMapping("/app/prescriptions")
    public String prescriptions(Authentication authentication, Model model) {
        User user = current(authentication);
        model.addAttribute("prescriptions", workspaceService.prescriptions(user));
        model.addAttribute("patients", workspaceService.patients(user));
        model.addAttribute("doctors", workspaceService.doctors(user));
        model.addAttribute("page", "prescriptions");
        return "app/prescriptions";
    }

    @PostMapping("/app/prescriptions")
    public String createPrescription(Authentication authentication,
                                     @RequestParam String medication,
                                     @RequestParam(required = false) String dosage,
                                     @RequestParam(required = false) String instructions,
                                     @RequestParam(required = false) Long patientId,
                                     @RequestParam(required = false) Long doctorId,
                                     RedirectAttributes ra) {
        User user = current(authentication);
        Prescription prescription = new Prescription();
        prescription.setOwner(user);
        prescription.setMedication(medication);
        prescription.setDosage(dosage);
        prescription.setInstructions(instructions);
        if (patientId != null) prescription.setPatient(workspaceService.getPatient(patientId, user));
        if (doctorId != null) prescription.setDoctor(workspaceService.getDoctor(doctorId, user));
        workspaceService.savePrescription(prescription);
        ra.addFlashAttribute("message", "Prescription added");
        return "redirect:/app/prescriptions";
    }

    @PostMapping("/app/prescriptions/delete")
    public String deletePrescription(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deletePrescription(id, current(authentication));
        ra.addFlashAttribute("message", "Prescription removed");
        return "redirect:/app/prescriptions";
    }

    @GetMapping("/app/billing")
    public String billing(Authentication authentication, Model model) {
        User user = current(authentication);
        model.addAttribute("bills", workspaceService.bills(user));
        model.addAttribute("patients", workspaceService.patients(user));
        model.addAttribute("nextNumber", workspaceService.nextBillNumber(user));
        model.addAttribute("paidTotal", workspaceService.paidTotal(user));
        model.addAttribute("outstandingTotal", workspaceService.outstandingTotal(user));
        model.addAttribute("page", "billing");
        return "app/billing";
    }

    @PostMapping("/app/billing")
    public String createBill(Authentication authentication,
                             @RequestParam String description,
                             @RequestParam String amount,
                             @RequestParam String status,
                             @RequestParam(required = false) Long patientId,
                             RedirectAttributes ra) {
        User user = current(authentication);
        Bill bill = new Bill();
        bill.setOwner(user);
        bill.setNumber(workspaceService.nextBillNumber(user));
        bill.setDescription(description);
        bill.setAmount(new BigDecimal(amount));
        bill.setStatus(status);
        if (patientId != null) bill.setPatient(workspaceService.getPatient(patientId, user));
        workspaceService.saveBill(bill);
        ra.addFlashAttribute("message", "Bill created");
        return "redirect:/app/billing";
    }

    @PostMapping("/app/billing/status")
    public String updateBillStatus(Authentication authentication,
                                   @RequestParam Long id,
                                   @RequestParam String status,
                                   RedirectAttributes ra) {
        Bill bill = workspaceService.getBill(id, current(authentication));
        bill.setStatus(status);
        workspaceService.saveBill(bill);
        ra.addFlashAttribute("message", "Bill updated");
        return "redirect:/app/billing";
    }

    @PostMapping("/app/billing/delete")
    public String deleteBill(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deleteBill(id, current(authentication));
        ra.addFlashAttribute("message", "Bill removed");
        return "redirect:/app/billing";
    }

    @GetMapping("/app/rooms")
    public String rooms(Authentication authentication, Model model) {
        model.addAttribute("rooms", workspaceService.rooms(current(authentication)));
        model.addAttribute("page", "rooms");
        return "app/rooms";
    }

    @PostMapping("/app/rooms")
    public String createRoom(Authentication authentication,
                             @RequestParam String name,
                             @RequestParam String type,
                             @RequestParam(defaultValue = "AVAILABLE") String status,
                             @RequestParam(defaultValue = "1") Integer capacity,
                             RedirectAttributes ra) {
        Room room = new Room();
        room.setOwner(current(authentication));
        room.setName(name);
        room.setType(type);
        room.setStatus(status);
        room.setCapacity(capacity);
        workspaceService.saveRoom(room);
        ra.addFlashAttribute("message", "Room added");
        return "redirect:/app/rooms";
    }

    @PostMapping("/app/rooms/status")
    public String updateRoomStatus(Authentication authentication,
                                   @RequestParam Long id,
                                   @RequestParam String status,
                                   RedirectAttributes ra) {
        Room room = workspaceService.getRoom(id, current(authentication));
        room.setStatus(status);
        workspaceService.saveRoom(room);
        ra.addFlashAttribute("message", "Room updated");
        return "redirect:/app/rooms";
    }

    @PostMapping("/app/rooms/delete")
    public String deleteRoom(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deleteRoom(id, current(authentication));
        ra.addFlashAttribute("message", "Room removed");
        return "redirect:/app/rooms";
    }

    @GetMapping("/app/inventory")
    public String inventory(Authentication authentication, Model model) {
        model.addAttribute("supplies", workspaceService.supplies(current(authentication)));
        model.addAttribute("page", "inventory");
        return "app/inventory";
    }

    @PostMapping("/app/inventory")
    public String createSupply(Authentication authentication,
                               @RequestParam String name,
                               @RequestParam String category,
                               @RequestParam Integer quantity,
                               @RequestParam(defaultValue = "pcs") String unit,
                               @RequestParam(defaultValue = "10") Integer reorderLevel,
                               RedirectAttributes ra) {
        Supply supply = new Supply();
        supply.setOwner(current(authentication));
        supply.setName(name);
        supply.setCategory(category);
        supply.setQuantity(quantity);
        supply.setUnit(unit);
        supply.setReorderLevel(reorderLevel);
        workspaceService.saveSupply(supply);
        ra.addFlashAttribute("message", "Supply added");
        return "redirect:/app/inventory";
    }

    @PostMapping("/app/inventory/delete")
    public String deleteSupply(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
        workspaceService.deleteSupply(id, current(authentication));
        ra.addFlashAttribute("message", "Supply removed");
        return "redirect:/app/inventory";
    }

    @GetMapping("/app/reports")
    public String reports(Authentication authentication, Model model) {
        model.addAllAttributes(workspaceService.dashboard(current(authentication)));
        model.addAttribute("page", "reports");
        return "app/reports";
    }

    @GetMapping("/app/settings")
    public String settings(Model model) {
        model.addAttribute("page", "settings");
        return "app/settings";
    }

    @PostMapping("/app/settings")
    public String updateSettings(Authentication authentication,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String companyName,
                                 RedirectAttributes ra) {
        userService.updateProfile(current(authentication), fullName, companyName);
        ra.addFlashAttribute("message", "Clinic profile updated");
        return "redirect:/app/settings";
    }
}
