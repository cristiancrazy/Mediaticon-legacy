
#!/bin/bash

# Author: Cristian Capraro
# Made for: Mediaticon Group
# in collabs with:
# - Destro Simone
# - Trento Emanuele
# - Bellini Giovanni


# ~ COORD FUNCTIONS ~
countdown(){
    for (( i = 1 ; i < $1 ; ++i )) ; do
	printf "%sWaiting...%s sec %s\r" $green $(($1 - $i)) $reset
	sleep 1
    done
    
}

# ~ INSTALLER FUNCTIONS ~

verifier(){
    if test $? -eq 0; then
	printf "%s%s installed successfully!%s\n" $green $1 $reset
    else
	printf "%sError Occurred retrieving/installing %s! %sExiting.\n" $red $1 $reset
	exit 1 # Error exit
    fi
}

# PYTHON 3
install_python_base(){
    # Python 3 installing
    apt-get -y install python3 >/dev/null
    verifier "Python3"
    apt-get -y install python3-pip >/dev/null
    verifier "Python3-pip"
    # Optimize
    apt-get clean
}

# ==============SCRAPER=================
install_scraper_dependencies(){
    # For Python - PIP3
    pip3 install beautifulsoup4 >/dev/null
    verifier "PIP-Bsoup4"
    
    pip3 install bs4 >/dev/null
    verifier "PIP-bs4"
    
    pip3 install certifi >/dev/null
    verifier "PIP-certifi"
    
    pip3 install charset-normalizer >/dev/null
    verifier "PIP-charset-normalizer"
    
    pip3 install idna >/dev/null
    verifier "PIP-idna"
    
    pip3 install requests >/dev/null
    verifier "PIP-requests"
    
    pip3 install soupsieve >/dev/null
    verifier "PIP-soupsieve"
    
    pip3 install urllib3 >/dev/null
    verifier "PIP-urllib3"
    
    pip3 install ftfy >/dev/null
    verifier "PIP-ftfy"
}

# JAVA JRE 17 FROM Bell Soft
install_java_jre(){
    # Install CURL
    apt-get -y install curl >/dev/null
    verifier "curl"

    # Verify system architecture
    Arch_x86_64=$(lscpu | grep "Architecture:        x86_64")

    # Download and install x86_64 JRE Package
    if test $? -eq 0; then
	printf "%sNow downloading Liberica JRE x86_64...\n%s" $green $reset
	curl https://download.bell-sw.com/java/17.0.2+9/bellsoft-jre17.0.2+9-linux-amd64.deb --output ./bellsoft-jre17.0.2+9-linux-amd64.deb
	#verifier "JRE - Step 1"
	apt-get -y install ./bellsoft-jre17.0.2+9-linux-amd64.deb >>/dev/null
	verifier "JRE - Step 2"
	# Verify installed java
	java --version
	verifier "JRE - Step 3"
    fi

    # Verify system architecture
    Arch_ARM=$(lscpu | grep "Architecture:        arm")

    # Download and install ARM JRE Package
    if test $? -eq 0; then
	printf "%sNow downloading Liberica JRE for ARM...%s\n" $green $reset
	curl https://download.bell-sw.com/java/17.0.2+9/bellsoft-jre17.0.2+9-linux-arm32-vfp-hflt.deb --output ./bellsoft-jre17.0.2+9-linux-arm32-vfp-hflt.deb
	verifier "JRE - Step 1"
	apt-get -y install ./bellsoft-jre17.0.2+9-linux-arm32-vfp-hflt.deb >>/dev/null
	verifier "JRE - Step 2"
	# Verify installed java
	java --version
	verifier "JRE - Step 3"
    fi

    # Optimize
    apt-get clean
}

# ============= MAIN ============= #

# ~ GRAPHICS BASH SETTINGS ~

# Color shortcuts
reset=$(printf "\e[0m")
red=$(printf "\e[31m")
green=$(printf "\e[32m")

# Welcome messages
printf "%sWELCOME TO DEPENDENCIES INSTALLER FOR DEBIAN BASED OS%s\n" $red $reset
printf "This program will %sdownloads%s dependencies needed to run the %spython%s based scrapers programs\n" $green $reset $green $reset

countdown 10 # waiting

# Verify if the programs can run with the privilege level given
sudo_cmd=$(sudo -nv 2>&1)

if test $? -eq 0; then
    # Installing
    printf "%sInstalling dependencies...%s\n" $green $reset

    install_python_base
    countdown 5
    install_scraper_dependencies
    countdown 5
    install_java_jre
    countdown 3
    printf "\e[42m Completed all task successfully!\e[0m\nBye.\n"
    
else
    # Cannot continue - exit error 
    printf "%sYou must run this program as root!\n%s" $red $reset
    exit 1
fi



# ~ END ROUTINE ~
exit 0
