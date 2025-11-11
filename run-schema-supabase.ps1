# Run PostgreSQL Schema on Supabase
# Quick script to setup the database

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Supabase Database Schema Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$databaseUrl = "postgresql://postgres:Thapelo03*@db.tkjzwpvmqduifkbnhqdx.supabase.co:5432/postgres"
$schemaFile = "src\main\resources\database\postgresql_schema.sql"

# Check if schema file exists
if (-not (Test-Path $schemaFile)) {
    Write-Host "✗ Schema file not found: $schemaFile" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Database: Supabase PostgreSQL" -ForegroundColor Yellow
Write-Host "Schema file: $schemaFile" -ForegroundColor Yellow
Write-Host ""

# Check if psql is available
Write-Host "Checking for psql..." -ForegroundColor Yellow
try {
    $psqlVersion = psql --version 2>&1
    Write-Host "✓ Found: $psqlVersion" -ForegroundColor Green
    Write-Host ""
    
    # Run schema
    Write-Host "Running schema..." -ForegroundColor Yellow
    psql $databaseUrl -f $schemaFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✓ Schema executed successfully!" -ForegroundColor Green
        Write-Host ""
        
        # Verify
        Write-Host "Verifying tables..." -ForegroundColor Yellow
        psql $databaseUrl -c "\dt"
        
        Write-Host ""
        Write-Host "Checking sample data..." -ForegroundColor Yellow
        psql $databaseUrl -c "SELECT username, role FROM users;"
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "Setup Complete!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Login credentials:" -ForegroundColor Yellow
        Write-Host "  Username: admin" -ForegroundColor Green
        Write-Host "  Password: admin123" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "⚠ Schema execution completed with warnings" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "✗ psql not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Option 1: Install PostgreSQL client tools" -ForegroundColor Yellow
    Write-Host "  Download: https://www.postgresql.org/download/windows/" -ForegroundColor White
    Write-Host ""
    Write-Host "Option 2: Use Supabase SQL Editor (Recommended)" -ForegroundColor Yellow
    Write-Host "  1. Go to your Supabase dashboard" -ForegroundColor White
    Write-Host "  2. Click 'SQL Editor' in the left menu" -ForegroundColor White
    Write-Host "  3. Click 'New Query'" -ForegroundColor White
    Write-Host "  4. Copy the contents of: $schemaFile" -ForegroundColor White
    Write-Host "  5. Paste into the editor" -ForegroundColor White
    Write-Host "  6. Click 'Run' or press Ctrl+Enter" -ForegroundColor White
    Write-Host ""
    Write-Host "The schema file is ready to copy!" -ForegroundColor Green
    Write-Host ""
    
    $openFile = Read-Host "Open schema file in notepad? (yes/no)"
    if ($openFile -eq "yes") {
        notepad $schemaFile
    }
}

Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. mvn clean install -U" -ForegroundColor Cyan
Write-Host "  2. mvn clean javafx:run" -ForegroundColor Cyan
Write-Host ""

Read-Host "Press Enter to exit"

