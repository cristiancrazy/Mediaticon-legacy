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

#============================[UTILITY]==================================
function Countdown {
    param (
        $Seconds
    )

    for($i = $Seconds; $i -gt 0; $i--){
        Clear-Host
        Write-Output "Waiting...$i" #Print Waiting text
        Start-Sleep 1 #Sleep
        Clear-Host
    }
    
}

#============================[JAVA JRE 17]==================================
function DownloadJavaJRE17 {
    switch ($env:PROCESSOR_ARCHITECTURE) {
        ("AMD64") {
            #Architecture x86_64
            try{
                Invoke-WebRequest https://download.bell-sw.com/java/17.0.2+9/bellsoft-jre17.0.2+9-windows-amd64.msi -OutFile "bs-jre17-amd64.msi"
            }catch{
                Write-Output "Error while downloading Java JRE"
                exit 1
            }

        }
        ("ARM64") {
            #Architecture ARM
            try{
                Invoke-WebRequest https://download.bell-sw.com/java/17.0.2+9/bellsoft-jre17.0.2+9-windows-aarch64.msi -OutFile "bs-jre17-aarch64.msi"
            }catch{
                Write-Output "Error while downloading Java JRE"
                exit 1
            }
        }
        ("X86", "x86") {
            #Architecture x86
            try{
                Invoke-WebRequest https://download.bell-sw.com/java/17.0.2+9/bellsoft-jre17.0.2+9-windows-i586.msi -OutFile "bs-jre17-i586.msi"
            }catch{
                Write-Output "Error while downloading Java JRE"
                exit 1
            }
        }
        Default {
            Write-Output "Error while downloading Java JRE - Not available for your architecture."
            exit 1
        }

    }

    #Update progress bar
    foreach($ProgressBarStatus in 0..10){
        Write-Progress -Id 1 -Activity "Installing Java JRE" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}

# Install Java
function InstallJavaJRE17 {
    switch ($env:PROCESSOR_ARCHITECTURE) {
        ("AMD64") { 
            try{
                Start-Process .\bs-jre17-amd64.msi -Wait
            }catch{
                Write-Output "Error while installing Java JRE"
                exit 1
            }
        }
        ("ARM64") {
            try{
                Start-Process .\bs-jre17-aarch64.msi -Wait
            }catch{
                Write-Output "Error while installing Java JRE"
                exit 1
            }
        }

        ("x86", "X86") {
            try{
                Start-Process .\bs-jre17-i586.msi -Wait
            }catch{
                Write-Output "Error while installing Java JRE"
                exit 1
            }
        }
    }

    #Update progress bar
    foreach($ProgressBarStatus in 10..20){
        Write-Progress -Id 1 -Activity "Downloading Python 3" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}

#============================[PYTHON 3]==================================
function DownloadPython3 {
    switch ($env:PROCESSOR_ARCHITECTURE) {
        ("AMD64") {
            #Architecture x86_64
            try{
                Invoke-WebRequest https://www.python.org/ftp/python/3.9.10/python-3.9.10-amd64.exe -OutFile "py3-amd64.exe"
            }catch{
                Write-Output "Error while downloading Python 3"
                exit 1
            }

        }
        ("ARM64") {
            #Architecture ARM
            try{
                Invoke-WebRequest https://www.python.org/ftp/python/3.11.0/python-3.11.0a5-arm64.exe -OutFile "py3-arm64.exe"
            }catch{
                Write-Output "Error while downloading Python 3"
                exit 1
            }
        }
        ("X86", "x86") {
            #Architecture x86
            try{
                Invoke-WebRequest https://www.python.org/ftp/python/3.9.10/python-3.9.10.exe -OutFile "py3-x86.exe"
            }catch{
                Write-Output "Error while downloading Python 3"
                exit 1
            }
        }
        Default {
            Write-Output "Error while downloading Python 3 - Not available for your architecture."
            exit 1
        }

    }

    #Update progress bar
    foreach($ProgressBarStatus in 20..30){
        Write-Progress -Id 1 -Activity "Installing Python 3" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}

# Install Python
function InstallPython3 {
    switch ($env:PROCESSOR_ARCHITECTURE) {
        ("AMD64") { 
            try{
                Start-Process .\py3-amd64.exe -Wait
            }catch{
                Write-Output "Error while installing Python 3"
                exit 1
            }
        }
        ("ARM64") {
            try{
                Start-Process .\py3-arm64.exe -Wait
            }catch{
                Write-Output "Error while installing Python 3"
                exit 1
            }
        }

        ("x86", "X86") {
            try{
                Start-Process .\py3-x86.exe -Wait
            }catch{
                Write-Output "Error while installing Python 3"
                exit 1
            }
        }
    }
    
    #Update progress bar
    foreach($ProgressBarStatus in 30..40){
        Write-Progress -Id 1 -Activity "Installing Python Libraries" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}

#============================[PYTHON DEPENDENCIES]==================================
function InstallPip3Libraries{
    param($dependenciesName) #Require an array

    try{
        # Upgrade pip
        pip.exe install --upgrade pip | Out-Null

        # Install libraries
        foreach($libName in $dependenciesName){
            pip3.exe install $libName | Out-Null
        }
    }catch{
        Write-Output "Error while installing Python 3 Libraries" 
        exit 1
    }

    foreach($ProgressBarStatus in 40..100){
        Write-Progress -Id 1 -Activity "Installation Completed" -PercentComplete ($ProgressBarStatus)
        Start-Sleep -Milliseconds 25
    }
}

#===============[Script]===============

# Admin required to continue

#Requires -RunAsAdministrator

# Welcome msg
Clear-Host
Write-Output "Welcome to Dependencies installer for Windows.`n`nInstaller will start soon..."
Start-Sleep 5
Countdown 3

#Show a progress bar
Write-Progress -Id 1 -Activity "Downloading Java JRE" -PercentComplete 0

# Download and Install Java JRE 17
DownloadJavaJRE17
InstallJavaJRE17

Start-Sleep 5

# Download and Install Python 3
DownloadPython3
InstallPython3

Start-Sleep 5

# Download and Install Python 3 Libs
InstallPip3Libraries $pipDependencies

Start-Sleep 5

exit 0 #End routine