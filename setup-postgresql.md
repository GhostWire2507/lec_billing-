# PostgreSQL Setup Guide for Windows

## Option 1: Install PostgreSQL Locally (Recommended for Development)

### Step 1: Download PostgreSQL

1. Go to: https://www.postgresql.org/download/windows/
2. Click "Download the installer"
3. Download the latest version (PostgreSQL 16.x recommended)
4. Run the installer

### Step 2: Installation

1. **Installation Directory**: Use default `C:\Program Files\PostgreSQL\16`
2. **Components**: Select all (PostgreSQL Server, pgAdmin 4, Stack Builder, Command Line Tools)
3. **Data Directory**: Use default
4. **Password**: Set a password for the `postgres` superuser (remember this!)
   - Suggested: `postgres` (for development only)
5. **Port**: Use default `5432`
6. **Locale**: Use default
7. Click "Next" and wait for installation to complete

### Step 3: Add PostgreSQL to PATH

1. Open System Environment Variables:
   - Press `Win + X` → System → Advanced system settings → Environment Variables
2. Under "System variables", find "Path" and click "Edit"
3. Click "New" and add: `C:\Program Files\PostgreSQL\16\bin`
4. Click "OK" on all dialogs
5. **Restart PowerShell** or open a new terminal

### Step 4: Verify Installation

Open a new PowerShell window and run:
```powershell
psql --version
```

You should see: `psql (PostgreSQL) 16.x`

### Step 5: Create Database

```powershell
# Connect to PostgreSQL (password: postgres)
psql -U postgres

# In psql prompt, create database:
CREATE DATABASE lec_billing_db;

# List databases to verify:
\l

# Exit psql:
\q
```

### Step 6: Run Schema

```powershell
# Navigate to project directory
cd c:\Users\dell\IdeaProjects\MokopaneMakhetha

# Run the schema file
psql -U postgres -d lec_billing_db -f src\main\resources\database\postgresql_schema.sql
```

### Step 7: Verify Data

```powershell
# Connect to database
psql -U postgres -d lec_billing_db

# Check tables
\dt

# View sample data
SELECT * FROM users;
SELECT * FROM customers;
SELECT * FROM bills;

# Exit
\q
```

### Step 8: Update Configuration

The file `src/main/resources/database.properties` should already have:
```properties
db.url=jdbc:postgresql://localhost:5432/lec_billing_db
db.username=postgres
db.password=postgres
```

If you used a different password, update it in this file.

---

## Option 2: Use Railway.app (Cloud - Easiest)

### Step 1: Sign Up

1. Go to: https://railway.app
2. Sign up with GitHub (free)
3. You get $5/month free credit

### Step 2: Create PostgreSQL Database

1. Click "New Project"
2. Click "Provision PostgreSQL"
3. Wait for deployment (30 seconds)

### Step 3: Get Connection Details

1. Click on the PostgreSQL service
2. Go to "Variables" tab
3. Copy the `DATABASE_URL` value
   - Format: `postgresql://user:password@host:port/database`

### Step 4: Set Environment Variable

In PowerShell:
```powershell
# Set for current session
$env:DATABASE_URL="postgresql://user:password@host:port/database"

# Or set permanently (requires admin)
[System.Environment]::SetEnvironmentVariable('DATABASE_URL', 'postgresql://user:password@host:port/database', 'User')
```

### Step 5: Run Schema

```powershell
# Install PostgreSQL client tools (if not already installed)
# Or use Railway's built-in psql

# Navigate to project
cd c:\Users\dell\IdeaProjects\MokopaneMakhetha

# Run schema (replace with your DATABASE_URL)
psql "postgresql://user:password@host:port/database" -f src\main\resources\database\postgresql_schema.sql
```

### Step 6: Test Connection

Your application will automatically use the `DATABASE_URL` environment variable.

---

## Option 3: Use Render.com (Cloud - Free Tier)

### Step 1: Sign Up

1. Go to: https://render.com
2. Sign up with GitHub (free)

### Step 2: Create PostgreSQL Database

1. Click "New +" → "PostgreSQL"
2. Name: `lec-billing-db`
3. Database: `lec_billing_db`
4. User: `lec_user`
5. Region: Choose closest to you
6. Plan: Free
7. Click "Create Database"

### Step 3: Get Connection Details

1. Wait for database to be ready (2-3 minutes)
2. Copy "External Database URL"
3. Set as environment variable (same as Railway option)

### Step 4: Run Schema

Same as Railway option above.

---

## Troubleshooting

### "psql: command not found"

**Solution**: PostgreSQL bin directory not in PATH
1. Add `C:\Program Files\PostgreSQL\16\bin` to PATH
2. Restart terminal

### "password authentication failed"

**Solution**: Wrong password
1. Check password in `database.properties`
2. Or reset postgres password:
   ```powershell
   # As admin
   psql -U postgres
   ALTER USER postgres PASSWORD 'newpassword';
   ```

### "database does not exist"

**Solution**: Create the database first
```powershell
psql -U postgres
CREATE DATABASE lec_billing_db;
\q
```

### "connection refused"

**Solution**: PostgreSQL service not running
1. Open Services (Win + R → `services.msc`)
2. Find "postgresql-x64-16"
3. Right-click → Start

### Port 5432 already in use

**Solution**: Another service using the port
1. Change port in PostgreSQL config
2. Or stop the conflicting service

---

## Quick Commands Reference

```powershell
# Connect to PostgreSQL
psql -U postgres

# Connect to specific database
psql -U postgres -d lec_billing_db

# Run SQL file
psql -U postgres -d lec_billing_db -f schema.sql

# List databases
\l

# List tables
\dt

# Describe table
\d table_name

# View data
SELECT * FROM table_name;

# Exit psql
\q

# Backup database
pg_dump -U postgres lec_billing_db > backup.sql

# Restore database
psql -U postgres lec_billing_db < backup.sql
```

---

## Next Steps After Setup

1. **Reload Maven Dependencies**
   ```powershell
   mvn clean install -U
   ```

2. **Run the Application**
   ```powershell
   mvn clean javafx:run
   ```

3. **Login**
   - Username: `admin`
   - Password: `admin123`

4. **Test Features**
   - View dashboard statistics
   - Add a customer
   - Calculate a bill
   - View reports

---

## Recommended: Local PostgreSQL

For development, I recommend **Option 1 (Local PostgreSQL)** because:
- ✅ No internet required
- ✅ Faster performance
- ✅ Full control
- ✅ Free forever
- ✅ Easy to backup/restore

For production deployment, use **Railway** or **Render**.

---

## Need Help?

If you encounter any issues:
1. Check the error message in logs: `logs/lec-billing-error.log`
2. Verify PostgreSQL is running
3. Check connection details in `database.properties`
4. Test connection with `psql` command line

---

**Ready to install?** Choose your option and follow the steps above!

