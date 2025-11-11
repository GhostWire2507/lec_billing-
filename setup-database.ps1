# PostgreSQL Database Setup Script for LEC Billing System
# Run this script AFTER installing PostgreSQL

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "LEC Billing System - Database Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if psql is available
Write-Host "Checking PostgreSQL installation..." -ForegroundColor Yellow
try {
    $psqlVersion = psql --version 2>&1
    Write-Host "✓ PostgreSQL found: $psqlVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ PostgreSQL not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install PostgreSQL first:" -ForegroundColor Yellow
    Write-Host "1. Download from: https://www.postgresql.org/download/windows/" -ForegroundColor White
    Write-Host "2. Run the installer" -ForegroundColor White
    Write-Host "3. Add PostgreSQL bin to PATH" -ForegroundColor White
    Write-Host "4. Restart PowerShell and run this script again" -ForegroundColor White
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 1: Database Configuration" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get PostgreSQL credentials
$dbHost = Read-Host "Enter PostgreSQL host (default: localhost)"
if ([string]::IsNullOrWhiteSpace($dbHost)) { $dbHost = "localhost" }

$dbPort = Read-Host "Enter PostgreSQL port (default: 5432)"
if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "5432" }

$dbUser = Read-Host "Enter PostgreSQL username (default: postgres)"
if ([string]::IsNullOrWhiteSpace($dbUser)) { $dbUser = "postgres" }

$dbPassword = Read-Host "Enter PostgreSQL password" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword)
)

$dbName = "lec_billing_db"

Write-Host ""
Write-Host "Configuration:" -ForegroundColor Yellow
Write-Host "  Host: $dbHost" -ForegroundColor White
Write-Host "  Port: $dbPort" -ForegroundColor White
Write-Host "  User: $dbUser" -ForegroundColor White
Write-Host "  Database: $dbName" -ForegroundColor White
Write-Host ""

# Set PGPASSWORD environment variable for this session
$env:PGPASSWORD = $dbPasswordPlain

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 2: Create Database" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if database exists
Write-Host "Checking if database exists..." -ForegroundColor Yellow
$dbExists = psql -h $dbHost -p $dbPort -U $dbUser -lqt 2>&1 | Select-String -Pattern $dbName

if ($dbExists) {
    Write-Host "⚠ Database '$dbName' already exists!" -ForegroundColor Yellow
    $overwrite = Read-Host "Do you want to drop and recreate it? (yes/no)"
    
    if ($overwrite -eq "yes") {
        Write-Host "Dropping existing database..." -ForegroundColor Yellow
        psql -h $dbHost -p $dbPort -U $dbUser -c "DROP DATABASE IF EXISTS $dbName;" 2>&1 | Out-Null
        Write-Host "✓ Database dropped" -ForegroundColor Green
    } else {
        Write-Host "Skipping database creation..." -ForegroundColor Yellow
        $skipCreate = $true
    }
}

if (-not $skipCreate) {
    Write-Host "Creating database '$dbName'..." -ForegroundColor Yellow
    $createResult = psql -h $dbHost -p $dbPort -U $dbUser -c "CREATE DATABASE $dbName;" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Database created successfully" -ForegroundColor Green
    } else {
        Write-Host "✗ Failed to create database" -ForegroundColor Red
        Write-Host $createResult -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 3: Run Schema" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$schemaFile = "src\main\resources\database\postgresql_schema.sql"

if (-not (Test-Path $schemaFile)) {
    Write-Host "✗ Schema file not found: $schemaFile" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Running schema file..." -ForegroundColor Yellow
Write-Host "File: $schemaFile" -ForegroundColor White
Write-Host ""

$schemaResult = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -f $schemaFile 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Schema executed successfully" -ForegroundColor Green
} else {
    Write-Host "⚠ Schema execution completed with warnings" -ForegroundColor Yellow
    Write-Host $schemaResult -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 4: Verify Installation" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Checking tables..." -ForegroundColor Yellow
$tables = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -c "\dt" 2>&1

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
    Write-Host $tables -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Checking sample data..." -ForegroundColor Yellow
$userCount = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -t -c "SELECT COUNT(*) FROM users;" 2>&1
$customerCount = psql -h $dbHost -p $dbPort -U $dbUser -d $dbName -t -c "SELECT COUNT(*) FROM customers;" 2>&1

Write-Host "✓ Users: $($userCount.Trim())" -ForegroundColor Green
Write-Host "✓ Customers: $($customerCount.Trim())" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 5: Update Configuration" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$configFile = "src\main\resources\database.properties"

Write-Host "Updating $configFile..." -ForegroundColor Yellow

$configContent = @"
# PostgreSQL Database Configuration
db.url=jdbc:postgresql://${dbHost}:${dbPort}/${dbName}
db.username=$dbUser
db.password=$dbPasswordPlain
db.pool.maxSize=10
db.pool.minIdle=2
"@

$configContent | Out-File -FilePath $configFile -Encoding UTF8

Write-Host "✓ Configuration file updated" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Database Details:" -ForegroundColor Yellow
Write-Host "  URL: jdbc:postgresql://${dbHost}:${dbPort}/${dbName}" -ForegroundColor White
Write-Host "  Username: $dbUser" -ForegroundColor White
Write-Host "  Password: ********" -ForegroundColor White
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
Write-Host "  1. Reload Maven dependencies:" -ForegroundColor White
Write-Host "     mvn clean install -U" -ForegroundColor Cyan
Write-Host ""
Write-Host "  2. Run the application:" -ForegroundColor White
Write-Host "     mvn clean javafx:run" -ForegroundColor Cyan
Write-Host ""
Write-Host "  3. Login with admin/admin123" -ForegroundColor White
Write-Host ""

Write-Host "Logs will be in: logs\lec-billing.log" -ForegroundColor Yellow
Write-Host ""

# Clear password from environment
$env:PGPASSWORD = $null

Write-Host "Press Enter to exit..." -ForegroundColor Gray
Read-Host

