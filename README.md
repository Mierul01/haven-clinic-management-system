# Haven Clinic Management System

**Haven** is a complete clinic/hospital management SaaS built with **Java + Spring Boot**.  
It includes a public marketing website, optional login/register, one-click demo access, and a full clinic workspace for day-to-day care operations.

Live local URL (after running): [http://localhost:8088](http://localhost:8088)

---

## What Haven Does

Haven helps small clinics and hospital wings manage:

- Patient records
- Doctor roster
- Appointment scheduling and check-in
- Prescriptions
- Billing
- Room availability
- Medical inventory/stock
- Simple clinic reports
- User profile and sign-out

It is designed as a **sellable SaaS product**: visitors can try the demo without signing up, or register for their own clinic workspace.

---

## Public Website (Marketing)

| Page | Purpose |
|------|---------|
| **Landing** | Brand intro, CTAs (Try Demo / Register / Login), features overview, pricing |
| **Features** | Extra product details |
| **Pricing** | Solo / Practice / Hospital Wing plans |
| **Login** | Sign in to an existing account |
| **Register** | Create a new clinic account (optional) |
| **Try Demo** | Instant access — no signup required |

---

## App Modules (Workspace)

After login or demo entry, users get a sidebar workspace with these functions:

### 1. Dashboard
Clinic overview at a glance:
- Today’s appointment count
- Total patients
- Available rooms
- Outstanding billing amount
- Recent appointments and patient list
- Care pulse (Scheduled / Check In / Completed)

### 2. Patients
Manage people under clinic care:
- Add patient (name, age, gender, phone, blood type, allergies)
- View patient list and status
- Remove patients

### 3. Appointments
Daily visit scheduling:
- Book appointment (reason, patient, doctor, date, time)
- Update status: **Scheduled**, **Check In**, **Completed**, **Cancelled**
- Remove appointments

### 4. Doctors
Care team management:
- Add doctor (name, specialty, phone, email)
- Set status: **Active** or **On Leave**
- View roster and remove doctors

### 5. Prescriptions
Medication records:
- Create prescription (medication, dosage, instructions)
- Link to patient and doctor
- View/remove prescription history

### 6. Billing
Patient billing:
- Create bills with auto number (`BILL-0001`, …)
- Link bill to patient
- Status: **Pending**, **Paid**, **Overdue**
- See paid vs outstanding totals

### 7. Rooms
Facility board:
- Add rooms (Consultation, Ward, ICU, Lab)
- Capacity tracking
- Status: **Available**, **Occupied**, **Maintenance**

### 8. Inventory
Clinic supplies/stock:
- Add items (PPE, Pharmacy, First Aid, Lab, Other)
- Quantity + reorder level
- Low-stock warning when quantity is at/below reorder level

### 9. Reports
Simple operations snapshot:
- Patients, doctors, active prescriptions, supplies
- Visit counts by status
- Room occupancy
- Paid vs outstanding billing

### 10. Profile (via name click)
Not in the main menu — open by clicking the user name in the sidebar footer:
- View profile hero (name, clinic, email, plan)
- Edit name and clinic name
- Sign out (with SweetAlert confirmation)

---

## Authentication & Access

| Method | How it works |
|--------|----------------|
| **Try Demo** | One click → enters seeded demo clinic workspace |
| **Register** | Creates a new account, then user signs in |
| **Login** | Email + password |
| **Sign out** | Sidebar “Sign out” or Profile page → SweetAlert confirm → logout |

### Demo account

- **Email:** `demo@haven.clinic`
- **Password:** `demo1234`
- Or click **Try Demo** on the landing page

Demo data includes sample patients, doctors, appointments, prescriptions, bills, rooms, and inventory.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security (form login + session) |
| UI | Thymeleaf + custom CSS |
| Database | H2 (file-based, auto-created) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |

---

## How to Run

### Requirements
- JDK 17+
- Maven 3.9+ (or use the local Maven under `.tools/` if present)

### Start the app

```bash
mvn spring-boot:run
```

Windows (if using the bundled Maven):

```bash
.\.tools\maven\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

Then open: **http://localhost:8088**

### Useful paths

| Path | Description |
|------|-------------|
| `/` | Landing page |
| `/demo` | Enter demo workspace |
| `/login` | Login |
| `/register` | Register |
| `/app` | Dashboard (after auth) |
| `/h2-console` | H2 database console (dev) |

---

## Project Structure

```
src/main/java/com/fluxa/
  config/          # Demo data seeder
  model/           # Patient, Doctor, Appointment, Bill, Room, etc.
  repository/      # JPA repositories
  security/        # Spring Security + user details
  service/         # Business logic
  web/             # Controllers (marketing + app)

src/main/resources/
  static/css/      # Haven UI styles
  templates/       # Landing + app pages
  application.properties
```

---

## Selling / Customizing

Haven is structured so you can:
1. Rebrand colors/fonts in `static/css/haven.css`
2. Host on a VPS, Railway, Render, or similar
3. Point your website CTAs to `/demo`, `/register`, or pricing
4. Later upgrade H2 → PostgreSQL and add real payment (Stripe) on pricing plans

---

## License / Ownership

Built as a portfolio / sellable SaaS starter for clinic and hospital operations.
