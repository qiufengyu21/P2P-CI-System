CSC573 Project1 P2P-CI System
Qiufeng Yu(qyu4)
Mingxuan Shi(mshi4)

Instructions to run the program:
- Server side:
	1. navigate into P2P-CI-System/src/
	2. javac *.java
	3. java Server
	
- Client side:
	1. naviage into P2P-CI-System/src/
	2. javac *.java
	3. java Client serverIP uploadPort
	4. after running, the client will prompt you for the location of the RFC files
	5. note that the RFC files have to follow a certain naming convention
	6. there are some sample RFC files in the P2P-CI-System/RFCs folder
	
- After starting up the client:
	Enter specific option number to perform different actions
	Option 1: List all available RFCs
	
	Option 2: Lookup for a specific RFC
		- Enter the RFC number you want to lookup. e.g. enter the number 2 for RFC2, don't enter "RFC2"!
		- Enter the RFC title you want to lookup.
	
	Option 3: Download an RFC
		- IMPORTANT: Perform option 1 or 2 to get information about the hosts before downloading an RFC.
		- Enter RFC number that you want to download. e.g. enter the number 6 for RFC6, don't enter "RFC6"!
		- Enter host name which you want to download from. e.g. 172.31.93.3
		- Enter this host's upload port number. e.g. 7766
	
	Option 0: Close connection with the server
	