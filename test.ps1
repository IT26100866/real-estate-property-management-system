$ErrorActionPreference = "Stop"

function Test-Login {
    param($username, $password)
    try {
        $loginPayload = @{username=$username; password=$password} | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method Post -Body $loginPayload -ContentType "application/json"
        Write-Output "Login $username : Success"
        return $response.token
    } catch {
        Write-Output "Login $username : Failed ($($_.Exception.Message))"
        return $null
    }
}

function Test-AdminUsers {
    param($token, $desc)
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8081/api/users" -Method Get -Headers @{Authorization="Bearer $token"}
        Write-Output "$desc GET /api/users : Success ($($response.Count) users)"
    } catch {
        Write-Output "$desc GET /api/users : Failed ($($_.Exception.Message))"
    }
}

Write-Output "--- Testing seeded BCrypt accounts ---"
$adminToken = Test-Login "admin@admin" "adminPass_master123"
$agentToken = Test-Login "chaminda@agent" "agentPass_chaminda123"
$customerToken = Test-Login "dilshan@user" "userPass_dilshan123"

Write-Output ""
Write-Output "--- Testing legacy SHA-256 account (gayan@user / gayan123) ---"
$legacyToken = Test-Login "gayan@user" "gayan123"

Write-Output ""
Write-Output "--- Testing Role Authorizations ---"
if ($adminToken) { Test-AdminUsers $adminToken "Admin" }
if ($customerToken) { Test-AdminUsers $customerToken "Customer" }
if ($agentToken) { Test-AdminUsers $agentToken "Agent" }
