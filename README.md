# jog_wireless

Written by: Maximilian Hasselbusch

Last Updated: 4 Jan 2017

##    Table of Contents   ##

1. File Structure and File Descriptions
2. Interface Descriptions
3. Recommended Tests
4. Changes to ER Design
5. Important Information and Assumptions
6. Resources Used

##1. File Structure &  File Descriptions##

#######################
##   File Structure  ##
#######################

	jog_wireless/
		META-INF/
		oracle/
		src/
			AccountCreation.java
			Billing.java
			CustomerSys.java
			EmployeeSys.java
			ExistCustomerSys.java
			Jog.java
			Login.java
			Makefile
			META-INF/
			NewCustomerSys.java
			oracle/
			PhonePurchase.java
			Restock.java
			functionDefinitions.sql
			tabledefinitions.sql
			triggerDefinitions.sql
		ER Diagram.pdf
		Makefile
		Manifest.txt
		README.md
		ojdbc6.jar
	
## Source Code File Descriptions ##

	src/AccountCreation.java - used to set up new jog accounts*Billing.java - used to process bills and request customer payment

	src/Billing.java - used to process billing requests from customer and employees
	
	src/CustomerSys.java - the main system for customers.  It branches off to NewCustomerSys and ExistCustomerSys
	
	src/EmployeeSys.java - the main system for employees.  It directs employees off to individual interfaces

	src/ExistCustomerSys.java - contains all interfaces for existing customers

	src/Jog.java - main program for the software package.  Contains the main method.

	src/Login.java - Process login requests.  Create the connection to the database.

	src/NewCustomerSys.java - Interface for new customers.  Only allows account creation.

	src/PhonePurchase.java - used to process phone purchases.
		
	src/Restock.java - used to process restock requests
	
	src/functionDefinitions.sql - definitions for all database procedures and functions
	
	src/tabledefinitions.sql - database schema
	
	src/triggerDefinitions.sql - defintions of database triggers

**It is also important to note that whenever a new account is created, whether it’s in the employee or customer side of the Jog software, a new file will be created in the top level directory.  This file functions as a receipt for the customer to keep for their records.  Therefore, there will be more files in the file structure as the program is tested.  

**In addition to the above files, there are 25 functions and triggers stored in the database that augment the design and complement the Java code.  

##2. Interface Descriptions ##

The Jog wireless software package includes two separate interface categories (one for employees and one for customers) divided into many more for specific interfaces.

#######################
##Customer Interfaces##
#######################

Jog’s software supports interfaces to new and existing customers.

The first thing the software asks for is a password.  This is the password for the oracle database.  There is no password functionality built into the relational design.  Make sure the right password is used.

================================
==Existing Customer Interfaces==
================================

Existing customers are asked to enter their account numbers in order to log in.  From here,
account specific tasks can be accomplished.

**Interface 1: Viewing and Paying Bills

Customers who log into the Jog system remotely (online) have the option to view and pay their bills.  These customers can generate a bill for any month since the first time a phone was activated and added to the account.  This will be at account creation time because Jog requires all accounts to have at least one phone activated and attached.  When the bill month/year is requested, the system displays the bill.  If it is unpaid, the system will ask the user if they want to pay it.  If so, a credit card number will be requested.  The Luhn algorithm is implemented within the system to ensure that a credit card number is valid.  If the number is valid, the database will be updated indicating the bill has been paid.  Users do have the option to not pay their bill.  Jog assumes its customers will pay on time at a later date if they do not pay their bill when requesting it.  

**Interface 2: Purchase and Add Phone to Account

Jog only offers two phones at this time: the Apple iPhone 6s and Samsung Galaxy S7.  The system gives the user to option to select either one and proceed with the purchase.  A random phone number and MEID is then generated and displayed to the user.  The number and MEID are compared against ones that are already in the database to ensure their uniqueness.  To proceed, a credit card number is requested.  Once again, the Luhn algorithm is implemented.  If the credit card number is valid, the system will tell the user the phone is being shipped to the address on file for that user.

==========================
==New Customer Interface==
==========================

**Interface 3: Set up a new Account

New Jog customers can setup new accounts online.  They will be asked to enter a Name, Address, City, and State.  The only thing restricting these is the number of characters (in order to fit them in the database).  Billing plan and account types (Individual, Family, Business) are then displayed and the user is asked to select an option for each.  	

Because Jog requires all new accounts have a phone attached, the system then transfers control over to the phone purchasing interface.  If the phone purchase process is successful, changes to the database will be committed and the account will be officially set up.  If it is aborted (via in-system commands, not ctrl-c), no changes to the database will be made and the account creation process will be canceled.  Jog will then log the customer out because new customers only have one option within the system (to create an account) and there is nothing else for them to do.  If an account was created, they can then log into the existing customer system.

After a successful account creation, a file titled account<account_number>.text will be created in the directory that the jar file is in.  In practice, the customer would then print the file to keep for their records.

#######################
##Employee Interfaces##
#######################

Employees who log in will be asked to enter the store id of the Jog store they are working at.  This ID will be used within the different interfaces available.

**Interface 4: Process a restock request

Jog employees can request a restock from the Jog online store.  If the user selects this option, the current stock of phones at their store will be displayed for reference.  They can then choose the phone they’d like to restock.  Because Jog stores can only keep an inventory less than 999, the system will only allow the user to order a number of phones that will not push that limit.  If the number of phones is valid, the request will be processed and the phones will be “shipped”.

**Interface 5: Process a new phone purchase request (for an in-store existing customer)

The process is the same as it is for customers purchasing phones except for the fact that the phone purchase will be recorded under the store that the employee is logged in under (because the employee is doing the purchase and activation for a customer).

**Interface 6: Process a bill payment request (for an in-store existing customer)

Jog allows its customers to pay bills in-store.  The bill generation and payment process is the same as it is for online customers except for the fact that an employee would be going through the interface, not a customer.

**Interface 7: Set up a new Account for a new in-store customer

Employees can create accounts for in-store customers.  The process is the same for employees as it is for online customers.  The created file would be printed by the employee and given to the customer so they can keep it for their records.


############################
##            4.          ##
##  Important Information ##
##     and Assumptions    ##
##                        ##
############################


#########################
##Important Information##
#########################

There are many things to take note of throughout testing and running the software:

1. A Makefile is included in the directory containing Java source code.  Run make clean prior to compilation

2. Account creation generates a file that will be added to the directory the software resides in.

3. The Luhn algorithm is used to verify credit card numbers.  This algorithm is in the public domain
and the source for it is included in the resources section below.  Credit card numbers are NOT stored.

4. This software has 7 different interfaces (some for customers, others for employees).  Code is reused throughout the interfaces for the purpose of  brevity and efficiency.  It was not necessary to create many different .java files that accomplish 99% of the same things.  The files themselves account for the differences between customer and employee users.

5. This software attempts to account for unexpected kill and stop signals (ctrl-c) by only committing updates and insertions to the database at the end of the interfaces.  There may be times when kill or stop signals mess up transaction concurrency, but throughout all tests there were no outstanding transactions that prevented future use of the software.

6.  It is incredibly difficult to replicate phone call data.  Because of this, many of the calls stored in Jog’s data base took place within the span of one or two days.  All of the interfaces still work without a hitch, however.  It is just important to mention specifics such as this.

7. Do not run make cleanjar in the top level directory unless you want to recompile the jar file.  The instructions on how to do so are in section 1 of the README.

#########################
##     Assumptions     ##
#########################

1. Because Jog has limited technology, bills are not generated automatically and sent to customers.  Jog relies on its customers to generate and pay their bills on time.  They can do this in-store or online.

2. Customers are customers of individual stores as well as of Jog at large.  This is reflected in the ER and relational designs.

3. Customer billing plans are simply the method in which bill totals are calculated.  Because of this, there are no tables in the database containing bill plan information.  Each account has a bill plan attribute in the database corresponding to which one the customer chose.  The PL/SQL functions and Java code conduct proper calculations based upon which number is stored (1, 2, or 3).

4. Jog employees and users (and managers for that matter) do NOT have access to individual tables and tuples stored in the database.  All changes to the database are made through the Jog software.  The software is designed to account for invalid data stored in tuples and typically exits when errors are detected (sometimes new input is requested).

5. The software is designed assuming users will not send stop and kill signals.  As mentioned above, the system generally handles these situations well.  In the case a signal is sent at an inopportune time, transactions may be left open.  The only location in the software where this will cause an issue is during account creation.  If the user cancels the process during the phone purchase (after account setup), the account will be left without a primary phone.  This will persist until a new phone is purchased under the same account.  That new phone’s number will then be added to the account relation as the primary phone number.

6. If Jog were to start selling a new type of phone (other than the Apple iPhone 6s or Samsung Galaxy S7), customers can purchase it without changes being made to the software.  In this case, it is assumed the Jog DBA would manually enter the new phone inventory information.

7. The data stored in Jog’s database is limited in comparison to what would be expected from an actual phone company. Most of the usage data is for May 2015 and the majority of phones (and accounts for that matter) were set up in March of 2015.  This was done to simply the data generation process.  The purpose of the project is to create functional interfaces to interact with a database, not to generate or manipulate fully believable data.

8. The Jog system is implemented in such a way that customers are able to have multiple accounts.  But, you can only log into accounts.  So, you can create multiple accounts under the same exact name, address, etc, but you can only log into one of them at a time.  The customer numbers will be different for record keeping purchases (because technically they are different account creation transactions but are still linked to the same person).  

##  5. Resources Used     ##

1. All data was generated using the free website mockaroo.com.  Data was generated in excel spreadsheet format and inserted into the database through SQL developer.

2. The Luhn Algorithm is implemented in two places in the software: at phone purchase time and when bills are paid for.  This algorithm is in the public domain and free for use wherever.  The implementation of the the algorithm used is described here: https://en.wikipedia.org/wiki/Luhn_algorithm.
