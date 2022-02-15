<#
.Description
THIS SCRIPT IS PART OF MEDIATICON PROJECT.

It will install some python3 dependencies on your system:
These dependencies are needed to run Python 3 Scraper
programs.
~ Cristian Capraro, Member of Mediaticon Group.

.SYNOPSIS
Python 3 dependencies installer (MEDIATICON GROUP)
#>

#===================================#
# Author: Cristian Capraro
# Date: February 2022
# Made for Mediaticon Project
# In Collaboration with:
# 1. Emanuele Trento
# 2. Simone Destro
# 3. Giovanni Bellini
#===================================#

#======================[PIP DEPENDENCIES NAME]=========================
$pipDependencies = "beautifulsoup4", "bs4", "certifi", "charset-normalizer", "idna", "requests", "soupsieve", "urllib3"

#============================[PYTHON DEPENDENCIES]==================================
function InstallPip3Libraries{
    param($dependenciesName) #Require an array

    try{
        # Upgrade pip
        pip3.exe install --upgrade pip | Out-Null


        # Install libraries
        foreach($libName in $dependenciesName){
            pip3.exe install $libName | Out-Null
        }
    }catch{
        Write-Output "Error while installing Python 3 Libraries" 
        exit 1
    }

    foreach($ProgressBarStatus in 0..100){
        Write-Progress -Id 1 -Activity "Completed" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}


#===============[Script Global]===============

# Admin required to continue

<#Requires -RunAsAdministrator #>

# Welcome msg
Clear-Host
Write-Output "Welcome to Python Dependencies installer for Windows.`nInstaller will start soon..."
Start-Sleep 5
Clear-Host
$PSStyle
# Progress Bar
Write-Progress -Id 1 -Activity "Installing Python 3 Libraries" -PercentComplete 0

# Download and Install Python 3 Libs
InstallPip3Libraries $pipDependencies

Write-Output "Installation Completed Successfully!"

Start-Sleep 5
exit 0 #End routine