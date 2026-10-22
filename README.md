# Hotel Tezor - Hotel Booking Web Application

A complete hotel booking system built with Java Servlets, JSP, JSTL, and JDBC for learning purposes.

## Tech Stack

- **Backend**: Java 17, Jakarta Servlet 6.0
- **View Layer**: JSP with JSTL 3.0
- **Database**: MySQL 8
- **Build Tool**: Maven 3.8+
- **Server**: Apache Tomcat 10.1+

## Project Structure

```
Hotel_Tezor_Backend/
├── src/main/java/org/example/
│   ├── dao/           # Data Access Objects (BookingDAO, RoomDAO, CustomerDAO)
│   ├── model/         # Domain models (Booking, Room, Customer, BookingView)
│   ├── servlet/       # Request handlers (6 servlet classes)
│   └── util/          # Utilities (DBConnection, WebUtil)
├── src/main/webapp/
│   ├── WEB-INF/views/ # JSP templates (5 dynamic pages)
│   ├── index.html     # Landing page
│   └── style.css      # Stylesheet
├── src/test/java/     # Unit tests
├── pom.xml            # Maven configuration
├── .env               # Environment variables (DO NOT COMMIT)
└── .gitignore         # Git ignore rules
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8
- Maven 3.8+
- Tomcat 10.1+

### 1. Database Setup

Create the database schema:

```sql
CREATE DATABASE practice;
USE practice;

-- Customers table
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_no INT UNIQUE NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) UNIQUE NOT NULL,
    room_id INT NOT NULL,
    customer_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
    payment_status ENUM('PENDING', 'PAID') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
```

### 2. Environment Configuration

Create a `.env` file in the project root (already added to `.gitignore`):

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=practice
DB_USER=root
DB_PASSWORD=your_password
DB_TIMEZONE=Asia/Kolkata
```

**Note**: The `.env` file is NOT committed to the repository. Each developer should create their own based on their local setup.

### 3. Build the Project

```bash
cd Hotel_Tezor_Backend
mvn clean package
```

This generates `target/Hotel_Tezor.war` ready for deployment.

### 4. Deploy to Tomcat

```bash
# Copy WAR to Tomcat webapps directory
cp target/Hotel_Tezor.war /path/to/tomcat/webapps/

# Restart Tomcat
/path/to/tomcat/bin/shutdown.sh
/path/to/tomcat/bin/startup.sh
```

Access the application at: `http://localhost:8080/Hotel_Tezor`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/Hotel_Tezor/` | Landing page |
| GET | `/Hotel_Tezor/search-rooms` | Search available rooms |
| GET | `/Hotel_Tezor/book-room` | Booking form |
| POST | `/Hotel_Tezor/confirm-booking` | Create booking |
| GET | `/Hotel_Tezor/success?code=CODE` | Booking confirmation |
| GET | `/Hotel_Tezor/admin` | Admin dashboard |
| POST | `/Hotel_Tezor/cancel-booking` | Cancel booking |

## Key Features

- ✓ Room search by date and guest count
- ✓ Real-time availability checking (strict same-day turnover support)
- ✓ Booking creation with transaction rollback on errors
- ✓ Unique booking and customer codes generation
- ✓ Customer find-or-create by phone
- ✓ Server-side price calculation (BigDecimal)
- ✓ Booking cancellation (soft delete)
- ✓ Admin dashboard with all bookings, rooms, customers
- ✓ Full SQL injection protection (PreparedStatements)
- ✓ Proper error handling and logging

## Database Rules

- **Overlap Detection**: Uses strict `<` and `>` operators allowing same-day turnover (checkout date = next guest's checkin date)
- **Soft Cancellation**: Bookings marked as CANCELLED, never deleted
- **Transaction Safety**: All booking operations wrapped in transactions with automatic rollback on errors
- **Unique Codes**: 
  - Booking codes: `TZBK` + 6 random digits
  - Customer codes: `TZID` + 6 random digits

## Testing

Run the comprehensive test suite:

```bash
mvn test
# or manually:
cd target
java -cp classes:test-classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) org.example.dao.BookingDAOTest
```

All tests verify:
- Booking creation and overlap detection
- Customer reuse from same phone
- Capacity validation
- Booking cancellation
- Data cleanup and integrity

## Security Notes

⚠️ **Never commit sensitive files:**
- `.env` - Database credentials (included in `.gitignore`)
- `.idea/` - IDE settings
- `target/` - Build artifacts

**Environment Variables**: All sensitive data (DB host, user, password) is read from `.env` file, not hardcoded.

## Project Phases

1. **Phase 1**: Project structure, Maven config, database connection
2. **Phase 2**: Models, search functionality, room listing
3. **Phase 3**: Booking form, availability checking, dynamic JSPs
4. **Phase 4**: Booking creation with transactions, success page
5. **Phase 5**: Admin dashboard, booking cancellation, reporting

## Learning Points

This project demonstrates:
- Jakarta Servlet 6 with @WebServlet annotations
- JSP + JSTL for server-side rendering
- JDBC with PreparedStatements
- Transaction management (setAutoCommit, commit, rollback)
- Pessimistic locking (SELECT ... FOR UPDATE)
- MVC pattern with request attributes
- Date/time handling (LocalDate, ChronoUnit)
- BigDecimal for financial calculations
- Environment-based configuration

## License

Educational project for learning Java web development.

## Author

Created for Hotel Tezor learning project - October 2026
