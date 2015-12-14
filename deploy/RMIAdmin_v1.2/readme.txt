/*****************************************************************
*
* RMIAdmin (version 1.2 since Sept 2005)
* http://www.rmiAdmin.net
* 
* This file is the setup instruction for RMIAdmin.
* Please make sure you've read this document before 
* using RMIAdmin.
* 
* To purchase RMIAdmin, please visit http://www.rmiAdmin.net
*
* For any technical support or customer service issue, please 
* email us to rmi_cs@yahoo.com.hk
* 
* Copyright 2005 @ RMIAdmin. All rights reserved.
*
******************************************************************/


/*--- Welcome ---*/
This README file explains how to execute the RMIAdmin Server and 
RMIAdmin Client.



/*--- What is RMIAdmin ---*/
RMIAdmin is a powerful software developed based on the Java RMI 
technology.

RMIAdmin allows System Administrators/Developers to manage remote 
computers over the network regardless of the target computer platform. 
With the use of RMIAdmin, we could easily perform routine administrative 
tasks in just one single interface. As a result easing the management 
of multiple machine in a complex environment with mixed computer platform.

Here are the list of features provided:
[Core Utility]
- Invoke Operating System's command of remote computers locally.
- Perform file operation of remote computers locally
- Transfer of files between remote and local computers
- Monitoring the connection status of managed computers

[Extended Feature of the Core Utility]
- Command mode for customized shell scripting (Batch automation)
- API for programming plugin, integration with external system (not yet available)
- Server-side & Client-side logging feature
- Managed Server List export & import (not yet available)
- Encrypted Key Authentication (only for registered user)



/*--- Installation and Setup ---*/
RMIAdmin is purely written using the JAVA technology and 
therefore is cross-platform supported. It can be executed 
on nearly all kind of computers which operating systems, 
the only pre-requisite is that Java Runtime Environment 
must exist in the target computer

[Windows-based computer installation]
- Install Sun's JAVA Runtime (RMIAdmin is best supported on JRE v1.5 or above)   
- Obtained the RMIAdmin (RMIAdmin.zip) from http://www.rmiAdmin.net   
- Unzip the RMIAdmin.zip to any folder on your computer   
- To start the RMI Server, run the following command:
	java -cp fullpath_&_name_of_the_RMIAdmin.jar RMIAdmin.RMIServer  
- To start the RMI Client, double click the RMIAdmin.jar, or run the command:
	java -jar fullpath_&_name_of_the_RMIAdmin.jar

[Unix-based or MAC computer installation]
- Install Sun's JAVA Runtime (RMIAdmin is best supported on JRE v1.5 or above)   
- Obtained the RMIAdmin (RMIAdmin.tar) from http://www.rmiAdmin.net   
- Extracted the RMIAdmin.tar (by tar -xvf RMIAdmin.tar) to any folder on your computer   
- To start the RMI Server, run the following command:
	java -cp fullpath_&_name_of_the_RMIAdmin.jar RMIAdmin.RMIServer
- To start the RMI Client, double click the RMIAdmin.jar, or run the command:
	java -jar fullpath_&_name_of_the_RMIAdmin.jar

[Getting Sun's JAVA Runtime Environment(JRE)]
- Auto Detect (Require Direct Internet Access)
	http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=www.java.com 
- Manual Install (Require Direct Internet Access)
	http://www.java.com/en/download/download_the_latest.jsp



/*--- RMIAdmin User License Agreement ---*/
END-USER LICENSE AGREEMENT FOR RMIADMIN NOTICE TO ALL USERS:
CAREFULLY READ THE FOLLOWING LEGAL AGREEMENT, FOR THE LICENSE OF SPECIFIED SOFTWARE - RMIADMIN. 
BY INSTALLING THE SOFTWARE, YOU (EITHER AN INDIVIDUAL OR A SINGLE ENTITY) CONSENT TO BE BOUND 
BY AND BECOME A PARTY TO THIS AGREEMENT. IF YOU DO NOT AGREE TO ALL OF THE TERMS OF THIS 
AGREEMENT, CLICK THE BUTTON THAT INDICATES THAT YOU DO NOT ACCEPT THE TERMS OF THIS AGREEMENT 
AND DO NOT INSTALL THE SOFTWARE. 

- GENERAL
This End-User License Agreement ("EULA") is a legal agreement between you and RMIAdmin 
Software for the RMIAdmin Software products identified above, which may include computer software 
and associated media, electronic documentation and printed materials ("The Software"). By installing, 
copying, distributing or otherwise using The Software you agree to be bound by the terms of this EULA. 
If you do not agree to the terms of this EULA, you must not install, use or distribute The Software, 
and you must destroy all copies of The Software that you have. The Software is protected by copyright 
laws and international copyright treaties, as well as other intellectual property laws and treaties. 
The Software is licensed, not sold and always remains the property of RMIAdmin Software. 
  
- LICENSE RESTRICTIONS
YOU MAY NOT (a) Sublicense, sell, assign, transfer, pledge, distribute, rent or remove any proprietary 
notices on the Software except as expressly permitted in this Agreement; (b) Use, copy, adapt, disassemble, 
decompile, reverse engineer or modify the Software, in whole or in part, except as expressly permitted 
in this Agreement; (c) Take any action designed to unlock or bypass any Company-implemented restrictions 
on usage, access to, or number of installations of the Software; or (d) Use the Software if you fail to 
pay any license fee due and the Company notifies you that your license is terminated. IF YOU DO ANY OF 
THE FOREGOING, YOUR RIGHTS UNDER THIS LICENSE WILL AUTOMATICALLY TERMINATE. SUCH TERMINATION SHALL BE 
IN ADDITION TO AND NOT IN LIEU OF ANY CRIMINAL, CIVIL OR OTHER REMEDIES AVAILABLE TO RMIADMIN SOFTWARE. 
  
- TRANSFER OF RIGHTS 
You may permanently transfer all of your rights under this EULA, provided you retain no copies of The 
Software, you transfer all of The Software (including all component parts, documentation upgrades, 
and this EULA), and the recipient agrees to the terms of this EULA. If The Software is an upgrade, 
any transfer must include all prior versions of The Software. 
  
- TERMINATION
Without prejudice to any other rights, RMIAdmin Software may terminate this EULA if you fail to comply 
with the terms and conditions of this EULA. In such event, you must destroy all copies of The Software. 
  
- COPYRIGHT
 All title, including but not limited to copyrights, in and to The Software and any copies thereof are 
owned by RMIAdmin Software. All title and intellectual property rights in and to the content which may 
be accessed through use of The Software is the property of the respective content owner and may be 
protected by applicable copyright or other intellectual property laws and treaties. This EULA grants 
you no rights to use such content. All rights not expressly granted are reserved by RMIAdmin Software. 
  
- LIMITED WARRANTY TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, RMIADMIN SOFTWARE DISCLAIMS ALL 
WARRANTIES AND CONDITIONS, EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, IMPLIED WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT, WITH REGARD TO THE 
SOFTWARE, AND THE PROVISION OF OR FAILURE TO PROVIDE SUPPORT SERVICES. RMIADMIN SOFTWARE DOES NOT WARRANT 
THAT THE SOFTWARE WILL MEET YOUR REQUIREMENTS OR THAT THE OPERATION OF THE SOFTWARE WILL BE UNINTERRUPTED 
OR ERROR FREE. THE ENTIRE RISK AS TO SATISFACTORY QUALITY, PERFORMANCE, ACCURACY, AND EFFORT IS WITH YOU, 
THE USER. 
  
- LIMITATIONS OF REMEDIES AND LIABILITY TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, IN NO EVENT 
SHALL RMIADMIN SOFTWARE BE LIABLE FOR ANY SPECIAL, INCIDENTAL, INDIRECT, CONSEQUENTIAL OR OTHER DAMAGES 
WHATSOEVER (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS OF PROFITS, BUSINESS INTERRUPTION, LOSS OF 
INFORMATION, OR ANY OTHER PECUNIARY LOSS) ARISING OUT OF THE USE OF OR INABILITY TO USE THE SOFTWARE 
PRODUCT OR THE PROVISION OF OR FAILURE TO PROVIDE SUPPORT SERVICES, EVEN IF RMIADMIN SOFTWARE HAS BEEN 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. SOME STATES AND JURISDICTIONS DO NOT ALLOW THE EXCLUSION OR 
LIMITATION OF LIABILITY, THE ABOVE LIMITATION MAY NOT APPLY TO YOU. 
  
- DISTRIBUTIONS
You are hereby licensed to make as many copies of the installation package for The Software as you wish; 
give exact copies of the original installation package for The Software to anyone; and distribute the 
original installation package for The Software in its unmodified form via electronic or other means. 
The Software must be clearly identified as an evaluation version where described. You are specifically 
prohibited from charging, or requesting donations, for any such copies, however made; and from distributing 
The Software including documentation with other products (commercial or otherwise) without prior written 
permission from RMIAdmin Software. You are also prohibited from distributing components of The Software 
other than the complete original installation package.



/*--- Copyright 2005 RMIAdmin Software ---*/
RMIAdmin is a trademark of RMIAdmin Software. This program is protected by copyright law and 
international treaties as described in the EULA . All Rights Reserved.

JDK, JRE and Java are trademarks of Sun Microsystems.

Microsoft, the Windows logo, and Windows are registered trademarks of Microsoft Corporation.

Pentium is a registered trademark of Intel Corporation.

All other brands and product names are trademarks or registered trademarks of their respective 
owners.




