
# Green Vehicle Exchange Initiative (GVEI) Desktop Application

![Java](https://img.shields.io/badge/Java-AWT-blue) ![MySQL](https://img.shields.io/badge/MySQL-database-green) ![License](https://img.shields.io/badge/License-Academic-orange)

## Table of Contents
- [Project Overview](#project-overview)
- [Core Features](#core-features)
- [Technologies Used](#technologies-used)
- [Database Schema](#database-schema)
- [Installation Instructions](#installation-instructions)
- [Usage Instructions](#usage-instructions)
- [Extra Features](#extra-features)
- [Author](#author)
- [License](#license)

---

## Project Overview
The **Green Vehicle Exchange Initiative (GVEI)** is a government-led program in Rwanda designed to promote the transition from fuel-powered vehicles to electric vehicles (EVs).  

This **Java AWT desktop application** helps manage vehicle registration, exchange eligibility, government subsidy offers, and reporting.

**Benefits:**  
- Citizens receive cleaner, cost-efficient vehicles.  
- The government reduces carbon emissions and recycles old vehicles.  

---

## Core Features

1. **User Registration & Login**  
   - Citizens can create accounts, log in, and manage vehicle data.  
   - Admins have full access to manage offers.  

2. **Vehicle Registration**  
   - Register vehicle details: Owner ID, Plate number, Type, Fuel type, Year, Mileage.

3. **Exchange Eligibility Check**  
   - Automatically evaluates if a vehicle qualifies for exchange:  
     - Age > 5 years  
     - Fuel type = Petrol/Diesel  

4. **Exchange Offer Management**  
   - Calculates exchange value and subsidy percentage.  
   - Admins can approve or reject applications.  

5. **Reporting**  
   - View statistics: total exchanged vehicles, total subsidies, carbon reduction.  
   - Optional report export to `.txt` or `.csv`.  

---

## Technologies Used
- **Frontend:** Java AWT (Frame, Panel, Label, TextField, Button, Choice)  
- **Backend:** MySQL / Oracle Database  
- **Database Access:** JDBC  

---

## Database Schema

### Users Table
| Column | Type | Description |
|--------|------|-------------|
| user_id | INT | Primary Key |
| name | VARCHAR | Full name |
| email | VARCHAR | Email address |
| password | VARCHAR | Account password |
| role | VARCHAR | User role (Citizen/Admin) |

### Vehicles Table
| Column | Type | Description |
|--------|------|-------------|
| vehicle_id | INT | Primary Key |
| owner_id | INT | Foreign key linking to users |
| plate_no | VARCHAR | Vehicle plate number |
| fuel_type | VARCHAR | Fuel type |
| year | INT | Manufacture year |
| mileage | INT | Estimated mileage |

### Exchange Offers Table
| Column | Type | Description |
|--------|------|-------------|
| offer_id | INT | Primary Key |
| vehicle_id | INT | Foreign key linking to vehicles |
| exchange_value | DECIMAL | Estimated exchange value |
| subsidy_percent | DECIMAL | Subsidy percentage |
| status | VARCHAR | Offer status (Pending/Approved/Rejected) |

---

## Installation Instructions
1. Install **Java JDK** (v8 or higher).  
2. Install **MySQL / Oracle** and create a database.  
3. Clone this repository:  
   ```bash
   git clone <repository_url>
``

4. Update database credentials in `DBConnection.java`.
5. Compile the Java code:

   ```bash
   javac -cp .;mysql-connector-java-x.x.x.jar ui/*.java
   ```
6. Run the application:

   ```bash
   java -cp .;mysql-connector-java-x.x.x.jar ui.Main
   ```

---

## Usage Instructions

1. Register or log in as a citizen or admin.
2. **Citizens:**

   * Register vehicles
   * Check eligibility
   * Apply for exchange offers
3. **Admins:**

   * View vehicles and offers
   * Approve/reject offers
   * Generate reports/statistics

---

## Extra Features

* Search and filter vehicles or offers
* Export reports to `.txt` or `.csv`
* Visualize statistics using **AWT Canvas**

---

## Author

**Group3 Team** – Software Developer

---

## License

For academic and demonstration purposes

```


