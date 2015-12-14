#!/bin/bash
clear

if (test "$1" = "-s") then
	cd /mnt/samba/j2sdk1.4.0/002_dev
	java -cp /mnt/samba/j2sdk1.4.0/002_dev RMIAdmin.RMIServer -p 5555 -k

else 
	if (test "$1" = "-c") then
		cd /mnt/samba/j2sdk1.4.0/002_dev
		java -cp /mnt/samba/j2sdk1.4.0/002_dev RMIAdmin.initRMIClient

	else
		echo "Error!"
		echo "Usage: ./rmi.sh -s or ./rmi.sh -c"
		echo
	fi
fi

