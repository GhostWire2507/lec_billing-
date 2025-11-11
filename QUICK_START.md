# LEC Billing System - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Railway/Render cloud database)

---

## Step 1: Clone & Setup (1 minute)

```bash
# Navigate to project directory
cd MokopaneMakhetha

# Install dependencies
mvn clean install
```

---

## Step 2: Setup Database (2 minutes)

### Option A: Railway.app (Easiest - Recommended)

1. Go to [railway.app](https://railway.app) and sign up
2. Click "New Project" → "Provision PostgreSQL"
3. Copy the `DATABASE_URL` from Variables tab
4. Set environment variable:
   ```bash
   # Windows PowerShell
   $env:DATABASE_URL="your_database_url_here"
   
   # macOS/Linux
   export DATABASE_URL="your_database_url_here"
   ```
5. Initialize database:
   ```bash
   psql $DATABASE_URL -f src/main/resources/database/postgresql_schema.sql
   ```

### Option B: Local PostgreSQL

1. Install PostgreSQL from [postgresql.org](https://www.postgresql.org/download/)
2. Create database:
   ```bash
   createdb lec_billing_db
   ```
3. Run schema:
   ```bash
   psql lec_billing_db -f src/main/resources/database/postgresql_schema.sql
   ```
4. Update `src/main/resources/database.properties`:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/lec_billing_db
   db.username=postgres
   db.password=your_password
   ```

---

## Step 3: Run Application (1 minute)

```bash
mvn clean javafx:run
```

---

## Step 4: Login & Explore (1 minute)

### Default Credentials
```
Admin Account:
Username: admin
Password: admin123

Staff Account:
Username: staff
Password: staff123
```

### What to Try
1. **Dashboard**: View real-time statistics
2. **Manage Customers**: Add, edit, view customers
3. **Calculate Bills**: Generate bills with tiered pricing
4. **View Reports**: See analytics and trends

---

## 📊 Dashboard Features

When you login, you'll see:
- **Total Customers**: Active customer count
- **Unpaid Bills**: Bills awaiting payment
- **Outstanding Amount**: Total money owed
- **Monthly Revenue**: This month's earnings

---

## 🎯 Quick Tasks

### Add a New Customer
1. Click "Manage Customers"
2. Click "Add Customer"
3. Fill in details:
   - Customer ID: C004
   - Name: Test Customer
   - Address: Test Address
   - Meter Number: MTR004
4. Click "Save"

### Calculate a Bill
1. Click "Calculate Bills"
2. Select a customer
3. Enter electricity usage (e.g., 250 kWh)
4. Click "Calculate"
5. See tiered pricing breakdown:
   - 0-100 kWh @ M1.20 = M120.00
   - 101-250 kWh @ M1.50 = M225.00
   - **Total: M345.00**

### View Reports
1. Click "View Reports"
2. See:
   - Monthly revenue trends
   - Payment status distribution
   - Top customers by consumption

---

## 📁 Important Files

```
MokopaneMakhetha/
├── src/main/java/lecbilling/mokopanemakhetha/
│   ├── config/DatabaseConfig.java       # Database setup
│   ├── service/                         # Business logic
│   │   ├── AuthenticationService.java
│   │   ├── CustomerService.java
│   │   ├── BillingService.java
│   │   └── ReportService.java
│   └── [controllers...]
├── src/main/resources/
│   ├── database/postgresql_schema.sql   # Database schema
│   ├── database.properties              # DB config
│   └── logback.xml                      # Logging config
├── logs/                                # Application logs
│   ├── lec-billing.log
│   └── lec-billing-error.log
├── DEPLOYMENT_GUIDE.md                  # Full deployment guide
├── SYSTEM_REDESIGN_SUMMARY.md           # Architecture details
└── pom.xml                              # Maven dependencies
```

---

## 🔍 Troubleshooting

### "Connection refused" error
**Problem**: Can't connect to database  
**Solution**: 
- Check PostgreSQL is running: `pg_isready`
- Verify connection details in `database.properties`
- Check firewall settings

### "Authentication failed" error
**Problem**: Wrong database credentials  
**Solution**:
- Verify username/password in `database.properties`
- For Railway: Use exact `DATABASE_URL` from dashboard

### Application won't start
**Problem**: Missing dependencies  
**Solution**:
```bash
mvn clean install -U
```

### No logs appearing
**Problem**: Logging not configured  
**Solution**:
- Check `src/main/resources/logback.xml` exists
- Verify `logs/` directory is created
- Check console for errors

---

## 📝 Logging

### View Logs
```bash
# All logs
tail -f logs/lec-billing.log

# Errors only
tail -f logs/lec-billing-error.log

# Windows
Get-Content logs\lec-billing.log -Wait
```

### Log Levels
- **DEBUG**: Detailed information for debugging
- **INFO**: General information about application flow
- **WARN**: Warning messages
- **ERROR**: Error messages with stack traces

---

## 🔐 Security Notes

### For Development
- Default passwords are fine
- Local database is okay

### For Production
1. **Change default passwords** immediately
2. **Use environment variables** for credentials
3. **Enable SSL** for database connections
4. **Implement password hashing** (BCrypt)
5. **Regular backups** of database
6. **Monitor logs** for suspicious activity

---

## 🎓 Next Steps

### Learn More
1. Read `SYSTEM_REDESIGN_SUMMARY.md` for architecture details
2. Review `DEPLOYMENT_GUIDE.md` for production deployment
3. Check service classes for business logic
4. Explore PostgreSQL schema in `postgresql_schema.sql`

### Customize
1. Update tiered pricing rates in `BillingService.java`
2. Modify dashboard statistics in `ReportService.java`
3. Add new fields to customer/bill models
4. Create custom reports

### Deploy
1. Choose platform (Railway, Render, or own server)
2. Follow `DEPLOYMENT_GUIDE.md`
3. Set up automatic backups
4. Configure monitoring

---

## 💬 Sample Data

The system comes with:
- **2 Users**: admin, staff
- **3 Customers**: John Molapo, Mary Seleke, Peter Nkuebe
- **3 Sample Bills**: Various usage levels

You can:
- Login and explore immediately
- Add more customers
- Calculate new bills
- View reports

---

## 🆘 Getting Help

### Check These First
1. **Logs**: `logs/lec-billing-error.log`
2. **Database**: Can you connect with psql?
3. **Dependencies**: Run `mvn clean install`
4. **Configuration**: Verify `database.properties`

### Common Issues
| Issue | Solution |
|-------|----------|
| Can't login | Check database connection, verify users table has data |
| No customers showing | Run schema SQL file, check customers table |
| Bills not calculating | Check BillingService logs, verify database connection |
| Dashboard stats wrong | Click refresh, check ReportService logs |

---

## ✅ Verification Checklist

After setup, verify:
- [ ] Application starts without errors
- [ ] Can login with admin/admin123
- [ ] Dashboard shows statistics
- [ ] Can view customer list
- [ ] Can add new customer
- [ ] Can calculate bills
- [ ] Can view reports
- [ ] Logs are being written to `logs/` directory
- [ ] Database has sample data

---

## 🎉 You're Ready!

You now have a fully functional LEC Billing System with:
- ✅ PostgreSQL database
- ✅ Professional logging
- ✅ Service layer architecture
- ✅ Modern workflow
- ✅ Cloud deployment ready

**Happy billing!** 🚀

---

## 📞 Support

For detailed information:
- **Architecture**: See `SYSTEM_REDESIGN_SUMMARY.md`
- **Deployment**: See `DEPLOYMENT_GUIDE.md`
- **Database**: See `src/main/resources/database/postgresql_schema.sql`
- **Logs**: Check `logs/lec-billing.log`

---

**Last Updated**: November 2024  
**Version**: 2.0 (Complete Redesign)

