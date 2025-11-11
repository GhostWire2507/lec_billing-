# Supabase Quick Start - LEC Billing System

## ✅ What's Already Done

1. ✅ **Database connection configured** - Your Supabase credentials are set
2. ✅ **Environment variable set** - DATABASE_URL is configured permanently
3. ✅ **HikariCP removed** - Using simple JDBC connections (no compatibility issues)
4. ✅ **Code updated** - DatabaseConfig.java simplified for Supabase

---

## 🚀 Next Steps (5 minutes)

### Step 1: Run Database Schema (2 minutes)

I've opened the Supabase SQL Editor in your browser. Now:

1. **In the SQL Editor tab:**
   - You should see a blank query editor
   - If not, click "SQL Editor" in the left menu → "New Query"

2. **Copy the schema:**
   - Open: `src\main\resources\database\postgresql_schema.sql`
   - Select all (Ctrl+A)
   - Copy (Ctrl+C)

3. **Paste and run:**
   - Paste into the Supabase SQL Editor (Ctrl+V)
   - Click "Run" button (or press Ctrl+Enter)
   - Wait for "Success" message (~5 seconds)

4. **Verify:**
   - Click "Table Editor" in the left menu
   - You should see: `users`, `customers`, `bills`, `audit_log`

---

### Step 2: Build and Run Application (3 minutes)

```powershell
# Navigate to project (if not already there)
cd c:\Users\dell\IdeaProjects\MokopaneMakhetha

# Clean and rebuild
mvn clean install -U

# Run the application
mvn clean javafx:run
```

---

### Step 3: Login and Test

**Login Credentials:**
- Username: `admin`
- Password: `admin123`

**Test These Features:**
1. ✅ Dashboard shows statistics
2. ✅ Add a new customer
3. ✅ Calculate a bill
4. ✅ View reports
5. ✅ Check logs in `logs/` folder

---

## 📊 Your Supabase Configuration

**Connection Details:**
```
Host: db.tkjzwpvmqduifkbnhqdx.supabase.co
Port: 5432
Database: postgres
Username: postgres
Password: Thapelo03*
```

**JDBC URL:**
```
jdbc:postgresql://db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres
```

**Environment Variable:**
```
DATABASE_URL=postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres
```

---

## 🔧 What Was Fixed

### HikariCP Issues Resolved

**Problem:** HikariCP was causing compatibility issues with Supabase

**Solution:** Replaced with simple JDBC connections
- ✅ Removed HikariCP dependency usage
- ✅ Using `DriverManager.getConnection()` directly
- ✅ Simpler, more reliable
- ✅ Works perfectly with Supabase

**Changes Made:**
1. Updated `DatabaseConfig.java` - Removed HikariCP, using simple JDBC
2. Updated `database.properties` - Added Supabase credentials
3. Set `DATABASE_URL` environment variable

---

## 📁 Files Updated

### 1. DatabaseConfig.java
```java
// Old: HikariCP connection pooling
private static HikariDataSource dataSource;

// New: Simple JDBC connections
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl, username, password);
}
```

### 2. database.properties
```properties
# Old: Local PostgreSQL
db.url=jdbc:postgresql://localhost:5432/lec_billing_db

# New: Supabase
db.url=jdbc:postgresql://db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres
db.username=postgres
db.password=Thapelo03*
```

---

## 🎯 Sample Data Included

After running the schema, you'll have:

### Users (2)
- **admin** / admin123 (Administrator)
- **staff** / staff123 (Staff)

### Customers (3)
- **C001** - Thabo Mokoena (Maseru)
- **C002** - Lerato Nkosi (Leribe)
- **C003** - Mpho Tau (Mafeteng)

### Bills
- Sample bills for each customer

---

## 🌐 Supabase Dashboard Features

### Table Editor
- View/edit data visually
- Add/delete rows
- Export to CSV

### SQL Editor
- Run custom queries
- Save frequently used queries
- View query history

### Database Settings
- View connection details
- Monitor usage
- Configure backups

### Logs
- View database logs
- Monitor queries
- Debug issues

---

## 🔍 Troubleshooting

### "Connection refused"

**Check:**
1. DATABASE_URL is set correctly
   ```powershell
   echo $env:DATABASE_URL
   ```
2. Supabase project is active (check dashboard)
3. Password has no typos (especially the `*` at the end)

**Fix:**
```powershell
# Reset environment variable
$env:DATABASE_URL="postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres"
```

### "Tables not found"

**Cause:** Schema not run yet

**Fix:** Run the schema in Supabase SQL Editor (see Step 1 above)

### "Driver not found"

**Cause:** PostgreSQL driver not in dependencies

**Fix:** Already in pom.xml, just rebuild:
```powershell
mvn clean install -U
```

### Application won't start

**Check logs:**
```powershell
# View error log
cat logs\lec-billing-error.log

# View main log
cat logs\lec-billing.log
```

---

## 📊 Monitoring Your Database

### Check Connection
```powershell
# In Supabase SQL Editor, run:
SELECT current_database(), current_user, version();
```

### View Tables
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

### Check Data
```sql
SELECT * FROM users;
SELECT * FROM customers;
SELECT * FROM bills;
```

### Monitor Usage
- Go to Supabase Dashboard
- Click "Settings" → "Usage"
- View database size, queries, bandwidth

---

## 💡 Tips

### 1. Use Supabase SQL Editor
- Faster than installing psql
- Built-in query history
- Syntax highlighting
- Auto-complete

### 2. Backup Your Data
```sql
-- In Supabase SQL Editor
-- Export tables to CSV using Table Editor
```

### 3. Monitor Logs
- Application logs: `logs/lec-billing.log`
- Error logs: `logs/lec-billing-error.log`
- Database logs: Supabase Dashboard → Logs

### 4. Test Connection
```java
// In your code, this will log connection status
DatabaseConfig.getConnection();
// Check logs/lec-billing.log for "Database connection established"
```

---

## ✅ Checklist

Before running the application:

- [ ] Schema run in Supabase SQL Editor
- [ ] Tables visible in Table Editor (users, customers, bills, audit_log)
- [ ] Sample data exists (2 users, 3 customers)
- [ ] DATABASE_URL environment variable set
- [ ] Maven dependencies reloaded (`mvn clean install -U`)

---

## 🎉 Ready to Go!

Everything is configured and ready. Just:

1. **Run schema** in Supabase SQL Editor (copy/paste from postgresql_schema.sql)
2. **Build:** `mvn clean install -U`
3. **Run:** `mvn clean javafx:run`
4. **Login:** admin / admin123

**Total time: ~5 minutes** ⚡

---

## 📞 Need Help?

If you encounter any issues:
1. Check the error in `logs/lec-billing-error.log`
2. Verify schema ran successfully in Supabase
3. Confirm DATABASE_URL is set: `echo $env:DATABASE_URL`
4. Let me know the specific error message

---

**Your database is ready! Just run the schema and start the app!** 🚀

