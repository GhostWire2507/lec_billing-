# Supabase PostgreSQL Setup - LEC Billing System

## ✅ Your Connection Details

**Database URL:**
```
postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres
```

**Parsed Details:**
- Host: `db.tkjzwpvmqduifkbnhqdx.supabase.co`
- Port: `5432`
- Database: `postgres`
- Username: `postgres`
- Password: `Thapelo03*`

---

## Quick Setup Steps

### Step 1: Set Environment Variable

**Temporary (current session only):**
```powershell
$env:DATABASE_URL="postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres"
```

**Permanent (recommended):**
```powershell
[System.Environment]::SetEnvironmentVariable('DATABASE_URL', 'postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres', 'User')

# Then restart PowerShell
```

### Step 2: Update database.properties (Alternative to HikariCP)

If HikariCP is giving you issues, let's use a simpler connection approach.

Create/update `src/main/resources/database.properties`:
```properties
db.url=jdbc:postgresql://db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres
db.username=postgres
db.password=Thapelo03*
db.ssl=true
```

### Step 3: Run Database Schema

```powershell
# Using psql (if installed)
psql "postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres" -f src\main\resources\database\postgresql_schema.sql

# OR use Supabase SQL Editor (easier!)
# 1. Go to Supabase dashboard
# 2. Click "SQL Editor"
# 3. Copy/paste contents of postgresql_schema.sql
# 4. Click "Run"
```

---

## Fixing HikariCP Issues

Let me create a simpler DatabaseConfig that doesn't use HikariCP connection pooling.

---

## Why Supabase is Great

✅ **Better than Railway:**
- Built-in SQL editor (no need for psql)
- Real-time database features
- Better free tier (500MB database, 2GB bandwidth)
- Auto-backups
- Better monitoring dashboard

✅ **Perfect for this project:**
- PostgreSQL 15
- Always-on database
- Fast connection from anywhere
- Easy to manage

---

## Supabase Dashboard Features

### SQL Editor
- Run queries directly in browser
- Save frequently used queries
- View query results

### Table Editor
- View/edit data visually
- Add/delete rows
- Export to CSV

### Database Settings
- Connection pooling (built-in!)
- SSL certificates
- Connection limits
- Backup/restore

---

## Next Steps

1. I'll fix the HikariCP issue for you
2. Run the schema on Supabase
3. Test the application

Let me update the code now!

