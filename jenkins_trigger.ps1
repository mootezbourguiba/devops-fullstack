$JenkinsUser = "admin"
$JenkinsPassword = "1083acf1be084fd2830a3f43e79ff282"

$JenkinsUrl = "http://localhost:8080"

$credential = New-Object System.Management.Automation.PSCredential ($JenkinsUser, (ConvertTo-SecureString $JenkinsPassword -AsPlainText -Force))

$crumbResponse = Invoke-RestMethod -Uri "$JenkinsUrl/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,'%3A',//crumb)" -Credential $credential

Write-Host "Contenu de crumbResponse: $($crumbResponse)"

$crumbField = ($crumbResponse -split ':')[0]
$crumbValue = ($crumbResponse -split ':')[1]

Write-Host "Champ crumb: $($crumbField)"
Write-Host "Valeur crumb: $($crumbValue)"

$buildUrl = "$JenkinsUrl/job/devops-fullstack-pipeline/build"

$headers = @{$crumbField = $crumbValue}

try {
    Invoke-RestMethod -Uri $buildUrl -Method Post -Headers $headers -Credential $credential
    Write-Host "Construction déclenchée avec succès!"
} catch {
    Write-Host "Erreur lors du déclenchement de la construction:"
    Write-Host $_.Exception.Message
}