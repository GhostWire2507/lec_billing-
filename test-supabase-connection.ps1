#!/usr/bin/env pwsh

Write-Host "🔍 Testing Supabase Connection..." -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

$host = "db.tkjzwpvmqduifkbnhqdx.supabase.co"

# Test 1: DNS Resolution
Write-Host "Test 1: DNS Resolution" -ForegroundColor Yellow
Write-Host "----------------------" -ForegroundColor Yellow
try {
    $dns = Resolve-DnsName -Name $host -ErrorAction Stop
    Write-Host "✓ DNS Resolution successful" -ForegroundColor Green
    Write-Host "  Addresses found:" -ForegroundColor White
    foreach ($record in $dns) {
        if ($record.Type -eq "A") {
            Write-Host "    IPv4: $($record.IPAddress)" -ForegroundColor Cyan
        }
        elseif ($record.Type -eq "AAAA") {
            Write-Host "    IPv6: $($record.IPAddress)" -ForegroundColor Cyan
        }
    }
} catch {
    Write-Host "✗ DNS Resolution failed: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: Port 5432 (Direct Connection)
Write-Host "Test 2: Direct Connection (Port 5432)" -ForegroundColor Yellow
Write-Host "--------------------------------------" -ForegroundColor Yellow
try {
    $result = Test-NetConnection -ComputerName $host -Port 5432 -WarningAction SilentlyContinue
    if ($result.TcpTestSucceeded) {
        Write-Host "✓ Port 5432 is accessible" -ForegroundColor Green
        Write-Host "  Remote Address: $($result.RemoteAddress)" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Port 5432 is NOT accessible" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Connection test failed: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Port 6543 (Connection Pooler)
Write-Host "Test 3: Connection Pooler (Port 6543)" -ForegroundColor Yellow
Write-Host "--------------------------------------" -ForegroundColor Yellow
try {
    $result = Test-NetConnection -ComputerName $host -Port 6543 -WarningAction SilentlyContinue
    if ($result.TcpTestSucceeded) {
        Write-Host "✓ Port 6543 is accessible" -ForegroundColor Green
        Write-Host "  Remote Address: $($result.RemoteAddress)" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Port 6543 is NOT accessible" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Connection test failed: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: IPv4 vs IPv6
Write-Host "Test 4: Network Configuration" -ForegroundColor Yellow
Write-Host "------------------------------" -ForegroundColor Yellow
$ipv4Enabled = (Get-NetAdapterBinding | Where-Object {$_.ComponentID -eq "ms_tcpip" -and $_.Enabled -eq $true}).Count -gt 0
$ipv6Enabled = (Get-NetAdapterBinding | Where-Object {$_.ComponentID -eq "ms_tcpip6" -and $_.Enabled -eq $true}).Count -gt 0

Write-Host "  IPv4 Enabled: $ipv4Enabled" -ForegroundColor $(if($ipv4Enabled){"Green"}else{"Red"})
Write-Host "  IPv6 Enabled: $ipv6Enabled" -ForegroundColor $(if($ipv6Enabled){"Green"}else{"Red"})
Write-Host ""

# Recommendations
Write-Host "📋 Recommendations:" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan

if (-not $result.TcpTestSucceeded) {
    Write-Host ""
    Write-Host "⚠️  Cannot connect to Supabase database!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible solutions:" -ForegroundColor Yellow
    Write-Host "  1. Check if you're behind a firewall/proxy" -ForegroundColor White
    Write-Host "  2. Try using a VPN" -ForegroundColor White
    Write-Host "  3. Check Supabase project status at https://supabase.com" -ForegroundColor White
    Write-Host "  4. Use Supabase's IPv4 pooler connection string" -ForegroundColor White
    Write-Host "  5. Consider using a local PostgreSQL database instead" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "✓ Connection looks good!" -ForegroundColor Green
    Write-Host "  You should be able to connect to Supabase" -ForegroundColor White
}

Write-Host ""
Read-Host "Press Enter to exit"

