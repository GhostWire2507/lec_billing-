# LEC Billing System - Version 2.0

> **Professional Electricity Billing Management System for Lesotho Electricity Company**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 🌟 Overview

The LEC Billing System is a comprehensive desktop application designed for managing electricity billing operations. This version 2.0 represents a complete architectural redesign with modern best practices, professional logging, and cloud-ready database infrastructure.

### Key Features

- 🔐 **Secure Authentication** - Role-based access control (Admin/Staff)
- 👥 **Customer Management** - Complete CRUD operations for customer records
- 💰 **Tiered Billing** - Automatic calculation with three-tier pricing structure
- 📊 **Analytics & Reports** - Real-time statistics and revenue tracking
- 🗄️ **PostgreSQL Database** - Robust, scalable data storage with connection pooling
- 📝 **Professional Logging** - Comprehensive logging with SLF4J + Logback
- ☁️ **Cloud Ready** - Deploy to Railway, Render, or any cloud platform
- 🎨 **Modern UI** - Clean, intuitive JavaFX interface

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 12+** - [Download](https://www.postgresql.org/download/) or use cloud service

### Installation (5 Minutes)

```bash
# 1. Clone the repository
cd MokopaneMakhetha

# 2. Install dependencies
mvn clean install

# 3. Setup database (choose one option)

# Option A: Railway.app (Recommended)
# - Sign up at railway.app
# - Create PostgreSQL database
# - Copy DATABASE_URL and run:
psql $DATABASE_URL -f src/main/resources/database/postgresql_schema.sql

# Option B: Local PostgreSQL
createdb lec_billing_db
psql lec_billing_db -f src/main/resources/database/postgresql_schema.sql

# 4. Configure database connection
# Edit src/main/resources/database.properties
# OR set environment variable:
export DATABASE_URL="postgresql://user:pass@host:port/database"

# 5. Run the application
mvn clean javafx:run
```

### Default Login Credentials

```
Admin Account:
Username: admin
Password: admin123

Staff Account:
Username: staff
Password: staff123
```

---

## 📖 Documentation

| Document | Description |
|----------|-------------|
| [QUICK_START.md](QUICK_START.md) | Get started in 5 minutes |
| [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) | Deploy to Railway, Render, or local |
| [SYSTEM_REDESIGN_SUMMARY.md](SYSTEM_REDESIGN_SUMMARY.md) | Architecture and design details |
| [NEXT_STEPS.md](NEXT_STEPS.md) | Implementation roadmap |

---

## 🏗️ Architecture

### Technology Stack

```
Frontend:  JavaFX 17
Backend:   Java 17+ with Service Layer Architecture
Database:  PostgreSQL 12+
Pooling:   HikariCP 5.1.0
Logging:   SLF4J 2.0 + Logback 1.4
Build:     Maven 3.6+
```

### Project Structure

```
MokopaneMakhetha/
├── src/main/java/lecbilling/mokopanemakhetha/
│   ├── config/
│   │   └── DatabaseConfig.java          # Database & connection pooling
│   ├── service/
│   │   ├── AuthenticationService.java   # User authentication
│   │   ├── CustomerService.java         # Customer operations
│   │   ├── BillingService.java          # Billing calculations
│   │   └── ReportService.java           # Analytics & reports
│   ├── model/
│   │   ├── Bill.java                    # Bill entity
│   │   ├── BillingCalculation.java      # Calculation breakdown
│   │   └── ReportData.java              # Report data
│   ├── [controllers...]                 # JavaFX controllers
│   ├── Customer.java                    # Customer entity
│   ├── User.java                        # User entity
│   └── HelloApplication.java            # Main application
├── src/main/resources/
│   ├── database/
│   │   └── postgresql_schema.sql        # Database schema
│   ├── lecbilling/mokopanemakhetha/
│   │   └── [FXML files...]              # UI definitions
│   ├── database.properties              # DB configuration
│   └── logback.xml                      # Logging configuration
├── logs/                                # Application logs
├── pom.xml                              # Maven configuration
└── [documentation...]
```

### Service Layer Architecture

```
Controllers → Services → Database
     ↓           ↓          ↓
   FXML    Business Logic  PostgreSQL
```

**Benefits:**
- ✅ Separation of concerns
- ✅ Testable business logic
- ✅ Reusable services
- ✅ Easy to maintain

---

## 💡 Features in Detail

### 1. Customer Management
- Add, edit, delete customers
- Search by name or meter number
- Track customer details (ID, name, address, meter)
- Soft delete (data preservation)

### 2. Billing System
- **Tiered Pricing Structure:**
  - Tier 1: 0-100 kWh @ M1.20/kWh
  - Tier 2: 101-300 kWh @ M1.50/kWh
  - Tier 3: Above 300 kWh @ M2.00/kWh
- Automatic calculation
- Detailed breakdown
- Payment tracking
- Bill history

### 3. Reports & Analytics
- Dashboard statistics
- Monthly revenue trends
- Payment status distribution
- Top customers by consumption
- Real-time updates

### 4. Professional Logging
- Structured logging (DEBUG, INFO, WARN, ERROR)
- Automatic log rotation
- Separate error log file
- 30-day retention (regular logs)
- 90-day retention (error logs)

---

## 🗄️ Database Schema

### Tables
- **users** - System users with roles
- **customers** - Customer information
- **bills** - Billing records with tiered breakdown
- **audit_log** - System activity tracking

### Features
- Foreign key constraints
- Indexes for performance
- Automatic timestamp updates
- Views for common queries
- Sample data included

---

## 🔐 Security

- ✅ Prepared statements (SQL injection protection)
- ✅ Connection pooling (prevents exhaustion)
- ✅ Environment variable support
- ✅ Audit logging
- ✅ Soft deletes (data preservation)
- ⚠️ **TODO**: Password hashing (BCrypt)

---

## 📊 Dashboard

The main dashboard provides:
- Welcome message with user role
- Total customers count
- Unpaid bills count
- Outstanding amount
- Monthly revenue
- Quick access to all modules

Each module opens in a separate window, keeping the dashboard accessible.

---

## 🚢 Deployment

### Cloud Deployment (Recommended)

#### Railway.app
```bash
# 1. Sign up at railway.app
# 2. Create PostgreSQL database
# 3. Get DATABASE_URL
# 4. Deploy application
```

#### Render.com
```bash
# 1. Sign up at render.com
# 2. Create PostgreSQL database
# 3. Configure connection
# 4. Deploy application
```

See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for detailed instructions.

### Local Deployment

```bash
# Install PostgreSQL
# Create database
createdb lec_billing_db

# Run schema
psql lec_billing_db -f src/main/resources/database/postgresql_schema.sql

# Configure connection
# Edit database.properties

# Run application
mvn clean javafx:run
```

---

## 🧪 Testing

### Manual Testing
```bash
# Run application
mvn clean javafx:run

# Test checklist:
# ✓ Login with admin/admin123
# ✓ View dashboard statistics
# ✓ Add new customer
# ✓ Calculate bill
# ✓ View reports
# ✓ Check logs in logs/ directory
```

### Database Testing
```sql
-- Connect to database
psql $DATABASE_URL

-- Check tables
\dt

-- View sample data
SELECT * FROM users;
SELECT * FROM customers;
SELECT * FROM bills;
```

---

## 📝 Logging

### Log Files
- `logs/lec-billing.log` - All application logs
- `logs/lec-billing-error.log` - Errors only

### View Logs
```bash
# All logs
tail -f logs/lec-billing.log

# Errors only
tail -f logs/lec-billing-error.log
```

### Log Levels
- **DEBUG** - Detailed debugging information
- **INFO** - General application flow
- **WARN** - Warning messages
- **ERROR** - Error messages with stack traces

---

## 🛠️ Development

### Build
```bash
mvn clean install
```

### Run
```bash
mvn clean javafx:run
```

### Package
```bash
mvn clean package
```

### Clean
```bash
mvn clean
```

---

## 📈 Roadmap

### Version 2.0 (Current)
- [x] PostgreSQL migration
- [x] Service layer architecture
- [x] Professional logging
- [x] Improved dashboard
- [x] Cloud deployment ready

### Version 2.1 (Planned)
- [ ] Password hashing (BCrypt)
- [ ] Session management
- [ ] Audit logging implementation
- [ ] PDF receipt generation
- [ ] Email notifications

### Version 3.0 (Future)
- [ ] REST API
- [ ] Mobile app
- [ ] Payment gateway integration
- [ ] Advanced analytics
- [ ] Multi-language support

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- **Original System** - Basic MySQL implementation
- **Version 2.0 Redesign** - Complete architectural overhaul

---

## 🙏 Acknowledgments

- Lesotho Electricity Company (LEC)
- JavaFX Community
- PostgreSQL Community
- HikariCP Team
- SLF4J & Logback Teams

---

## 📞 Support

### Documentation
- [Quick Start Guide](QUICK_START.md)
- [Deployment Guide](DEPLOYMENT_GUIDE.md)
- [System Architecture](SYSTEM_REDESIGN_SUMMARY.md)
- [Next Steps](NEXT_STEPS.md)

### Troubleshooting
1. Check `logs/lec-billing-error.log`
2. Verify database connection
3. Ensure dependencies are loaded
4. Review documentation

### Common Issues
| Issue | Solution |
|-------|----------|
| Connection refused | Check PostgreSQL is running |
| Authentication failed | Verify credentials in database.properties |
| Module errors | Reload Maven project |
| No logs | Create logs/ directory |

---

## 🎉 Getting Started

Ready to use the LEC Billing System?

1. **Read**: [QUICK_START.md](QUICK_START.md)
2. **Setup**: Database (Railway or local)
3. **Run**: `mvn clean javafx:run`
4. **Login**: admin / admin123
5. **Explore**: Dashboard, Customers, Billing, Reports

**Happy billing!** ⚡

---

**Version**: 2.0.0  
**Last Updated**: November 2024  
**Status**: Production Ready (with minor TODOs)

