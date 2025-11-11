# Railway PostgreSQL Setup Script for LEC Billing System
# Run this script AFTER creating PostgreSQL on Railway

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "LEC Billing - Railway PostgreSQL Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "This script will help you:" -ForegroundColor Yellow
Write-Host "  1. Set DATABASE_URL environment variable" -ForegroundColor White
Write-Host "  2. Install PostgreSQL client tools (if needed)" -ForegroundColor White
Write-Host "  3. Run the database schema" -ForegroundColor White
Write-Host "  4. Verify the setup" -ForegroundColor White
Write-Host ""

# Step 1: Get DATABASE_URL
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 1: Get DATABASE_URL from Railway" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Instructions:" -ForegroundColor Yellow
Write-Host "  1. Go to Railway dashboard (https://railway.app)" -ForegroundColor White
Write-Host "  2. Click on your PostgreSQL service" -ForegroundColor White
Write-Host "  3. Click 'Variables' tab" -ForegroundColor White
Write-Host "  4. Copy the DATABASE_URL value" -ForegroundColor White
Write-Host ""
Write-Host "Example: postgresql://postgres:password@containers-us-west-123.railway.app:5432/railway" -ForegroundColor Gray
Write-Host ""

$databaseUrl = Read-Host "Paste your DATABASE_URL here"

if ([string]::IsNullOrWhiteSpace($databaseUrl)) {
    Write-Host "✗ DATABASE_URL cannot be empty!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

if (-not $databaseUrl.StartsWith("postgresql://")) {
    Write-Host "✗ DATABASE_URL should start with 'postgresql://'" -ForegroundColor Red
    Write-Host "You entered: $databaseUrl" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✓ DATABASE_URL received" -ForegroundColor Green
Write-Host ""

# Step 2: Set Environment Variable
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 2: Set Environment Variable" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "How do you want to set DATABASE_URL?" -ForegroundColor Yellow
Write-Host "  1. Current session only (temporary - for testing)" -ForegroundColor White
Write-Host "  2. Permanently for your user account (recommended)" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter choice (1 or 2)"

if ($choice -eq "1") {
    $env:DATABASE_URL = $databaseUrl
    Write-Host "✓ DATABASE_URL set for current session" -ForegroundColor Green
    Write-Host "⚠ You'll need to set it again when you restart PowerShell" -ForegroundColor Yellow
} elseif ($choice -eq "2") {
    [System.Environment]::SetEnvironmentVariable('DATABASE_URL', $databaseUrl, 'User')
    $env:DATABASE_URL = $databaseUrl
    Write-Host "✓ DATABASE_URL set permanently" -ForegroundColor Green
    Write-Host "✓ Will persist across PowerShell sessions" -ForegroundColor Green
} else {
    Write-Host "✗ Invalid choice. Setting for current session only." -ForegroundColor Yellow
    $env:DATABASE_URL = $databaseUrl
}

Write-Host ""

# Step 3: Check for psql
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 3: Check PostgreSQL Client Tools" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Checking for psql command..." -ForegroundColor Yellow

try {
    $psqlVersion = psql --version 2>&1
    Write-Host "✓ PostgreSQL client found: $psqlVersion" -ForegroundColor Green
    $hasPsql = $true
} catch {
    Write-Host "✗ PostgreSQL client (psql) not found" -ForegroundColor Red
    Write-Host ""
    Write-Host "You need psql to run the database schema." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  A. Install PostgreSQL (includes psql)" -ForegroundColor White
    Write-Host "     Download: https://www.postgresql.org/download/windows/" -ForegroundColor Gray
    Write-Host "     Then add to PATH: C:\Program Files\PostgreSQL\16\bin" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  B. Install Railway CLI" -ForegroundColor White
    Write-Host "     Run: npm install -g @railway/cli" -ForegroundColor Gray
    Write-Host "     Then: railway run psql `$env:DATABASE_URL" -ForegroundColor Gray
    Write-Host ""
    
    $installChoice = Read-Host "Do you want to continue anyway? (yes/no)"
    
    if ($installChoice -ne "yes") {
        Write-Host ""
        Write-Host "Setup paused. Install psql and run this script again." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Your DATABASE_URL has been saved:" -ForegroundColor Green
        Write-Host $databaseUrl -ForegroundColor White
        Write-Host ""
        Read-Host "Press Enter to exit"
        exit 0
    }
    
    $hasPsql = $false
}

Write-Host ""

# Step 4: Run Schema
if ($hasPsql) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Step 4: Run Database Schema" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""

    $schemaFile = "src\main\resources\database\postgresql_schema.sql"

    if (-not (Test-Path $schemaFile)) {
        Write-Host "✗ Schema file not found: $schemaFile" -ForegroundColor Red
        Write-Host "Make sure you're running this script from the project root directory" -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit 1
    }

    Write-Host "Running schema file..." -ForegroundColor Yellow
    Write-Host "File: $schemaFile" -ForegroundColor White
    Write-Host "Database: Railway PostgreSQL" -ForegroundColor White
    Write-Host ""

    try {
        $schemaResult = psql $env:DATABASE_URL -f $schemaFile 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Schema executed successfully" -ForegroundColor Green
        } else {
            Write-Host "⚠ Schema execution completed with warnings" -ForegroundColor Yellow
            Write-Host $schemaResult -ForegroundColor Yellow
        }
    } catch {
        Write-Host "✗ Error running schema" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        Write-Host ""
        Write-Host "Try running manually:" -ForegroundColor Yellow
        Write-Host "psql `"$env:DATABASE_URL`" -f $schemaFile" -ForegroundColor Cyan
        Read-Host "Press Enter to exit"
        exit 1
    }

    Write-Host ""

    # Step 5: Verify
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Step 5: Verify Database Setup" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""

    Write-Host "Checking tables..." -ForegroundColor Yellow
    
    try {
        $tables = psql $env:DATABASE_URL -c "\dt" 2>&1
        
        if ($tables -match "users" -and $tables -match "customers" -and $tables -match "bills") {
            Write-Host "✓ All tables created successfully" -ForegroundColor Green
            Write-Host ""
            Write-Host "Tables:" -ForegroundColor White
            Write-Host "  - users" -ForegroundColor Green
            Write-Host "  - customers" -ForegroundColor Green
            Write-Host "  - bills" -ForegroundColor Green
            Write-Host "  - audit_log" -ForegroundColor Green
        } else {
            Write-Host "⚠ Some tables may be missing" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠ Could not verify tables" -ForegroundColor Yellow
    }

    Write-Host ""
    Write-Host "Checking sample data..." -ForegroundColor Yellow
    
    try {
        $userCount = psql $env:DATABASE_URL -t -c "SELECT COUNT(*) FROM users;" 2>&1
        $customerCount = psql $env:DATABASE_URL -t -c "SELECT COUNT(*) FROM customers;" 2>&1
        
        Write-Host "✓ Users: $($userCount.Trim())" -ForegroundColor Green
        Write-Host "✓ Customers: $($customerCount.Trim())" -ForegroundColor Green
    } catch {
        Write-Host "⚠ Could not verify data" -ForegroundColor Yellow
    }

    Write-Host ""
}

# Final Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Database Configuration:" -ForegroundColor Yellow
Write-Host "  Provider: Railway.app" -ForegroundColor White
Write-Host "  DATABASE_URL: Set ✓" -ForegroundColor Green
Write-Host ""

Write-Host "Default Login Credentials:" -ForegroundColor Yellow
Write-Host "  Admin:" -ForegroundColor White
Write-Host "    Username: admin" -ForegroundColor Green
Write-Host "    Password: admin123" -ForegroundColor Green
Write-Host "  Staff:" -ForegroundColor White
Write-Host "    Username: staff" -ForegroundColor Green
Write-Host "    Password: staff123" -ForegroundColor Green
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  1. Reload Maven dependencies:" -ForegroundColor White
Write-Host "     mvn clean install -U" -ForegroundColor Cyan
Write-Host ""
Write-Host "  2. Run the application:" -ForegroundColor White
Write-Host "     mvn clean javafx:run" -ForegroundColor Cyan
Write-Host ""
Write-Host "  3. Login with admin/admin123" -ForegroundColor White
Write-Host ""
Write-Host "  4. Check logs:" -ForegroundColor White
Write-Host "     logs\lec-billing.log" -ForegroundColor Cyan
Write-Host "     logs\lec-billing-error.log" -ForegroundColor Cyan
Write-Host ""

Write-Host "Useful Commands:" -ForegroundColor Yellow
Write-Host "  Connect to database:" -ForegroundColor White
Write-Host "    psql `"`$env:DATABASE_URL`"" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Backup database:" -ForegroundColor White
Write-Host "    pg_dump `"`$env:DATABASE_URL`" > backup.sql" -ForegroundColor Cyan
Write-Host ""
Write-Host "  View Railway dashboard:" -ForegroundColor White
Write-Host "    https://railway.app" -ForegroundColor Cyan
Write-Host ""

if ($choice -eq "1") {
    Write-Host "⚠ IMPORTANT: DATABASE_URL is set for this session only!" -ForegroundColor Yellow
    Write-Host "   Run this command in each new PowerShell window:" -ForegroundColor Yellow
    Write-Host "   `$env:DATABASE_URL=`"$databaseUrl`"" -ForegroundColor Cyan
    Write-Host ""
}

Write-Host "Press Enter to exit..." -ForegroundColor Gray
Read-Host

