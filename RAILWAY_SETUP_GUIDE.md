# Railway.app PostgreSQL Setup Guide

## Step-by-Step Setup (5 minutes)

### Step 1: Sign Up for Railway

1. **Go to Railway.app** (opened in your browser)
   - URL: https://railway.app

2. **Sign Up with GitHub**
   - Click "Login" or "Start a New Project"
   - Click "Login with GitHub"
   - Authorize Railway to access your GitHub account
   - **Free tier includes $5/month credit** (enough for development)

---

### Step 2: Create PostgreSQL Database

1. **Create New Project**
   - Click "New Project" button
   - Or click "Start a New Project" if it's your first time

2. **Provision PostgreSQL**
   - Click "Provision PostgreSQL"
   - Wait 30-60 seconds for deployment
   - You'll see a PostgreSQL service appear

3. **Database is Ready!**
   - The database is now running in the cloud
   - Railway automatically creates the database and user

---

### Step 3: Get Connection Details

1. **Click on the PostgreSQL Service**
   - Click on the PostgreSQL card in your project

2. **Go to Variables Tab**
   - Click "Variables" tab at the top
   - You'll see several environment variables

3. **Copy DATABASE_URL**
   - Find the variable named `DATABASE_URL`
   - Click the copy icon next to it
   - It looks like: `postgresql://postgres:password@host.railway.app:port/railway`

4. **Also Note These (for reference):**
   - `PGHOST` - Database host
   - `PGPORT` - Database port (usually 5432)
   - `PGUSER` - Username (usually postgres)
   - `PGPASSWORD` - Password
   - `PGDATABASE` - Database name (usually railway)

---

### Step 4: Set Environment Variable on Your Computer

**Option A: Set for Current PowerShell Session (Quick Test)**

```powershell
# Replace with your actual DATABASE_URL from Railway
$env:DATABASE_URL="postgresql://postgres:password@host.railway.app:port/railway"

# Verify it's set
echo $env:DATABASE_URL
```

**Option B: Set Permanently (Recommended)**

```powershell
# Replace with your actual DATABASE_URL from Railway
[System.Environment]::SetEnvironmentVariable('DATABASE_URL', 'postgresql://postgres:password@host.railway.app:port/railway', 'User')

# Restart PowerShell after this, then verify:
echo $env:DATABASE_URL
```

---

### Step 5: Install PostgreSQL Client Tools (for running schema)

You need `psql` command to run the schema file.

**Option A: Install Full PostgreSQL (Recommended)**

1. Download from: https://www.postgresql.org/download/windows/
2. Install (you only need the command-line tools)
3. Add to PATH: `C:\Program Files\PostgreSQL\16\bin`
4. Restart PowerShell

**Option B: Use Railway CLI (Alternative)**

```powershell
# Install Railway CLI
npm install -g @railway/cli

# Or using scoop
scoop install railway

# Login
railway login

# Link to your project
railway link

# Connect to database
railway run psql $DATABASE_URL
```

---

### Step 6: Run Database Schema

Once you have `psql` installed:

```powershell
# Navigate to project directory
cd c:\Users\dell\IdeaProjects\MokopaneMakhetha

# Run the schema using your DATABASE_URL
# Replace with your actual DATABASE_URL
psql "postgresql://postgres:password@host.railway.app:port/railway" -f src\main\resources\database\postgresql_schema.sql
```

**Expected Output:**
```
CREATE TABLE
CREATE TABLE
CREATE TABLE
CREATE TABLE
CREATE INDEX
CREATE INDEX
...
INSERT 0 1
INSERT 0 1
...
```

---

### Step 7: Verify Database Setup

```powershell
# Connect to your Railway database
psql "postgresql://postgres:password@host.railway.app:port/railway"

# In psql prompt, run these commands:
\dt                          # List tables (should see users, customers, bills, audit_log)
SELECT * FROM users;         # Should see admin and staff users
SELECT * FROM customers;     # Should see 3 sample customers
\q                          # Exit
```

---

### Step 8: Update Application Configuration

Your application is already configured to use `DATABASE_URL` environment variable!

The `DatabaseConfig.java` file automatically checks for `DATABASE_URL` first:

```java
String databaseUrl = System.getenv("DATABASE_URL");
if (databaseUrl != null) {
    // Use Railway database
} else {
    // Fall back to local database.properties
}
```

**No code changes needed!** ✅

---

### Step 9: Test the Application

```powershell
# Reload Maven dependencies
mvn clean install -U

# Run the application
mvn clean javafx:run
```

**Login:**
- Username: `admin`
- Password: `admin123`

**Test Features:**
- View dashboard (should show statistics)
- Add a customer
- Calculate a bill
- View reports

---

## Troubleshooting

### "psql: command not found"

**Solution:** Install PostgreSQL client tools
```powershell
# Download from: https://www.postgresql.org/download/windows/
# Or install Railway CLI and use: railway run psql $DATABASE_URL
```

### "connection refused" or "could not connect"

**Solution:** Check your DATABASE_URL
```powershell
# Verify environment variable is set
echo $env:DATABASE_URL

# Make sure it starts with postgresql://
# Make sure there are no extra spaces or quotes
```

### "password authentication failed"

**Solution:** Copy DATABASE_URL again from Railway
- Go to Railway dashboard
- Click on PostgreSQL service
- Variables tab
- Copy DATABASE_URL again (passwords may have special characters)

### "database does not exist"

**Solution:** Use the exact DATABASE_URL from Railway
- Railway creates a database automatically
- Don't try to create a new database
- Use the database name from DATABASE_URL (usually "railway")

### Application can't connect

**Solution:** Restart PowerShell after setting environment variable
```powershell
# Close and reopen PowerShell
# Verify variable is set
echo $env:DATABASE_URL

# Then run application
mvn clean javafx:run
```

---

## Railway Dashboard Features

### Monitor Your Database

1. **Metrics Tab**
   - CPU usage
   - Memory usage
   - Network traffic

2. **Logs Tab**
   - Database logs
   - Connection logs
   - Error logs

3. **Settings Tab**
   - Restart database
   - Delete database
   - Change plan

### Backup Your Database

```powershell
# Backup to file
pg_dump "postgresql://postgres:password@host.railway.app:port/railway" > backup.sql

# Restore from file
psql "postgresql://postgres:password@host.railway.app:port/railway" < backup.sql
```

---

## Cost & Limits

### Free Tier ($5/month credit)
- ✅ Enough for development and testing
- ✅ 512 MB RAM
- ✅ 1 GB storage
- ✅ Shared CPU
- ✅ No credit card required initially

### When You Need More
- Upgrade to Hobby plan ($5/month)
- Or Developer plan ($20/month)
- More storage, RAM, and dedicated resources

---

## Connecting from Other Tools

### pgAdmin (GUI Tool)

1. Download pgAdmin: https://www.pgadmin.org/download/
2. Create new server:
   - Host: (from PGHOST variable)
   - Port: (from PGPORT variable)
   - Database: (from PGDATABASE variable)
   - Username: (from PGUSER variable)
   - Password: (from PGPASSWORD variable)

### DBeaver (GUI Tool)

1. Download DBeaver: https://dbeaver.io/download/
2. New Connection → PostgreSQL
3. Paste your DATABASE_URL
4. Test connection

### VS Code Extension

1. Install "PostgreSQL" extension
2. Add connection using DATABASE_URL
3. Browse tables and run queries

---

## Security Best Practices

### 1. Keep DATABASE_URL Secret
- ❌ Don't commit to Git
- ❌ Don't share publicly
- ✅ Use environment variables
- ✅ Add to .gitignore

### 2. Rotate Passwords Regularly
- Go to Railway dashboard
- Settings → Reset Database
- Update DATABASE_URL in your environment

### 3. Use Different Databases
- Development: Railway free tier
- Production: Railway paid tier or dedicated server
- Testing: Local PostgreSQL

---

## Next Steps After Setup

1. **Test All Features**
   - Login
   - Add customers
   - Calculate bills
   - View reports
   - Check logs

2. **Monitor Logs**
   - Check `logs/lec-billing.log`
   - Check `logs/lec-billing-error.log`
   - Check Railway dashboard logs

3. **Backup Regularly**
   ```powershell
   # Weekly backup
   pg_dump "$env:DATABASE_URL" > "backup_$(Get-Date -Format 'yyyy-MM-dd').sql"
   ```

4. **Monitor Usage**
   - Check Railway dashboard
   - Monitor credit usage
   - Upgrade if needed

---

## Quick Reference

### Essential Commands

```powershell
# Set DATABASE_URL (replace with yours)
$env:DATABASE_URL="postgresql://postgres:password@host.railway.app:port/railway"

# Connect to database
psql "$env:DATABASE_URL"

# Run schema
psql "$env:DATABASE_URL" -f src\main\resources\database\postgresql_schema.sql

# Backup database
pg_dump "$env:DATABASE_URL" > backup.sql

# Restore database
psql "$env:DATABASE_URL" < backup.sql

# Run application
mvn clean javafx:run
```

### Railway CLI Commands

```powershell
# Install
npm install -g @railway/cli

# Login
railway login

# Link project
railway link

# View logs
railway logs

# Connect to database
railway run psql $DATABASE_URL

# Open dashboard
railway open
```

---

## Summary

✅ **What Railway Provides:**
- Managed PostgreSQL database
- Automatic backups
- Monitoring and metrics
- Easy scaling
- $5/month free credit

✅ **What You Need to Do:**
1. Sign up (1 minute)
2. Create PostgreSQL service (1 minute)
3. Copy DATABASE_URL (30 seconds)
4. Set environment variable (30 seconds)
5. Run schema (1 minute)
6. Test application (2 minutes)

**Total Time: ~5 minutes** ⚡

---

**Ready to start?** Follow the steps above and you'll have a cloud PostgreSQL database running in minutes!

