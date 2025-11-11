# LEC Billing System - PostgreSQL Deployment Guide

## Overview
This guide will help you deploy the LEC Billing System with PostgreSQL database on Railway.app (recommended) or Render.com.

---

## Option 1: Railway.app (Recommended - Easiest Setup)

### Why Railway?
- ✅ Free tier with $5 monthly credit
- ✅ Automatic PostgreSQL provisioning
- ✅ Simple environment variable management
- ✅ Great for development and small production apps

### Step-by-Step Setup

#### 1. Create Railway Account
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub (recommended) or email
3. Verify your account

#### 2. Create New Project
1. Click "New Project"
2. Select "Provision PostgreSQL"
3. Railway will automatically create a PostgreSQL database

#### 3. Get Database Connection Details
1. Click on your PostgreSQL service
2. Go to "Variables" tab
3. You'll see these variables:
   ```
   DATABASE_URL=postgresql://user:password@host:port/database
   PGHOST=containers-us-west-xxx.railway.app
   PGPORT=5432
   PGUSER=postgres
   PGPASSWORD=xxxxxxxxxxxxx
   PGDATABASE=railway
   ```

#### 4. Connect to Database
You can connect using:

**Option A: Using DATABASE_URL (Recommended)**
- The application automatically uses `DATABASE_URL` environment variable
- No code changes needed!

**Option B: Using psql CLI**
```bash
psql postgresql://user:password@host:port/database
```

**Option C: Using GUI Tool (DBeaver, pgAdmin, etc.)**
- Host: `containers-us-west-xxx.railway.app`
- Port: `5432`
- Database: `railway`
- Username: `postgres`
- Password: (from PGPASSWORD variable)

#### 5. Initialize Database Schema
1. Connect to your Railway PostgreSQL database
2. Run the schema file:
   ```bash
   psql $DATABASE_URL -f src/main/resources/database/postgresql_schema.sql
   ```

   Or copy and paste the contents of `postgresql_schema.sql` into Railway's Query tab

#### 6. Configure Local Development
Create a `.env` file in your project root (add to .gitignore):
```properties
DATABASE_URL=postgresql://user:password@host:port/database
```

Or update `src/main/resources/database.properties`:
```properties
db.url=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
db.username=postgres
db.password=your_password_here
```

#### 7. Test Connection
Run the application locally:
```bash
mvn clean javafx:run
```

---

## Option 2: Render.com

### Why Render?
- ✅ Free tier available
- ✅ Automatic backups
- ✅ Good for production deployments

### Step-by-Step Setup

#### 1. Create Render Account
1. Go to [render.com](https://render.com)
2. Sign up with GitHub or email

#### 2. Create PostgreSQL Database
1. Click "New +" → "PostgreSQL"
2. Fill in details:
   - **Name**: `lec-billing-db`
   - **Database**: `lec_billing_db`
   - **User**: `lec_billing_user`
   - **Region**: Choose closest to you
   - **Plan**: Free

3. Click "Create Database"

#### 3. Get Connection Details
After creation, you'll see:
- **Internal Database URL**: For apps on Render
- **External Database URL**: For external connections
- **PSQL Command**: For command-line access

Example:
```
External Database URL:
postgres://lec_billing_user:xxxxx@dpg-xxxxx.oregon-postgres.render.com/lec_billing_db
```

#### 4. Initialize Database
```bash
psql postgres://lec_billing_user:xxxxx@dpg-xxxxx.oregon-postgres.render.com/lec_billing_db -f src/main/resources/database/postgresql_schema.sql
```

#### 5. Configure Application
Update `database.properties`:
```properties
db.url=jdbc:postgresql://dpg-xxxxx.oregon-postgres.render.com:5432/lec_billing_db
db.username=lec_billing_user
db.password=your_password_here
```

---

## Option 3: Local PostgreSQL Installation

### Windows

#### 1. Download PostgreSQL
1. Go to [postgresql.org/download/windows](https://www.postgresql.org/download/windows/)
2. Download PostgreSQL 15 or 16
3. Run installer

#### 2. Installation
1. Set password for `postgres` user (remember this!)
2. Port: `5432` (default)
3. Locale: Default
4. Complete installation

#### 3. Create Database
Open pgAdmin or use psql:
```sql
CREATE DATABASE lec_billing_db;
```

#### 4. Run Schema
```bash
psql -U postgres -d lec_billing_db -f src/main/resources/database/postgresql_schema.sql
```

#### 5. Configure Application
Update `database.properties`:
```properties
db.url=jdbc:postgresql://localhost:5432/lec_billing_db
db.username=postgres
db.password=your_password_here
```

### macOS

#### Using Homebrew
```bash
# Install PostgreSQL
brew install postgresql@15

# Start PostgreSQL
brew services start postgresql@15

# Create database
createdb lec_billing_db

# Run schema
psql lec_billing_db -f src/main/resources/database/postgresql_schema.sql
```

### Linux (Ubuntu/Debian)

```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database
sudo -u postgres createdb lec_billing_db

# Run schema
sudo -u postgres psql lec_billing_db -f src/main/resources/database/postgresql_schema.sql
```

---

## Environment Variables (Production Best Practice)

Instead of hardcoding credentials, use environment variables:

### Set Environment Variable

**Windows (PowerShell)**
```powershell
$env:DATABASE_URL="postgresql://user:password@host:port/database"
```

**Windows (Command Prompt)**
```cmd
set DATABASE_URL=postgresql://user:password@host:port/database
```

**macOS/Linux**
```bash
export DATABASE_URL="postgresql://user:password@host:port/database"
```

### Application Configuration
The application automatically checks for `DATABASE_URL` environment variable first, then falls back to `database.properties`.

---

## Verification Steps

### 1. Test Database Connection
Run the test class:
```bash
mvn test -Dtest=DatabaseConnectionTest
```

### 2. Check Tables
```sql
-- List all tables
\dt

-- Check users
SELECT * FROM users;

-- Check customers
SELECT * FROM customers;

-- Check bills
SELECT * FROM bills;
```

### 3. Verify Application
1. Run the application
2. Login with: `admin` / `admin123`
3. Check if customers load
4. Try creating a new customer
5. Calculate a bill

---

## Troubleshooting

### Connection Refused
- Check if PostgreSQL is running
- Verify host and port
- Check firewall settings

### Authentication Failed
- Verify username and password
- Check `pg_hba.conf` for authentication method
- Ensure user has proper permissions

### SSL Connection Error
For cloud databases, you might need SSL. Update connection URL:
```
jdbc:postgresql://host:port/database?sslmode=require
```

### Connection Pool Issues
Check logs in `logs/lec-billing.log` for HikariCP errors

---

## Monitoring & Maintenance

### View Logs
Application logs are in:
- `logs/lec-billing.log` - All logs
- `logs/lec-billing-error.log` - Error logs only

### Database Backups

**Railway**
- Automatic backups on paid plans
- Manual export: Use Railway dashboard

**Render**
- Automatic daily backups on free tier
- Manual backup:
  ```bash
  pg_dump $DATABASE_URL > backup.sql
  ```

**Local**
```bash
pg_dump -U postgres lec_billing_db > backup_$(date +%Y%m%d).sql
```

### Restore from Backup
```bash
psql $DATABASE_URL < backup.sql
```

---

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for production
3. **Change default passwords** immediately
4. **Use SSL** for cloud databases
5. **Regular backups** - automate if possible
6. **Monitor logs** for suspicious activity
7. **Update PostgreSQL** regularly

---

## Support & Resources

- **PostgreSQL Documentation**: [postgresql.org/docs](https://www.postgresql.org/docs/)
- **Railway Docs**: [docs.railway.app](https://docs.railway.app)
- **Render Docs**: [render.com/docs](https://render.com/docs)
- **HikariCP**: [github.com/brettwooldridge/HikariCP](https://github.com/brettwooldridge/HikariCP)

---

## Quick Reference

### Default Credentials
```
Username: admin
Password: admin123

Username: staff
Password: staff123
```

### Connection String Format
```
jdbc:postgresql://host:port/database
```

### Common Ports
- PostgreSQL: 5432
- Application: N/A (JavaFX desktop app)

---

**Need Help?** Check the logs first, then review this guide. Most issues are related to connection strings or credentials.

