import java.io.*;
import java.sql.*;

class AccountCreation{

    private Connection con = null;
    private String store_id = null;
    private String customerNumber = null;
    private String city = null;
    private String name = null;
    private String address = null;
    private String state = null;



    String stateList[] = {"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU","HI","IA","ID","IL","IN","KS","KY","LA","MA","MD","ME","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", "OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VA","VI","VT","WA","WI","WV","WY"};

    public AccountCreation(Connection con, String store_id){

	this.con = con;
	this.store_id = store_id;
    }
    
    public void doWork() throws SQLException{
	
	int temp = setUpCustomer();

	if(temp != 0){
	    System.out.printf("%s", Jog.separatorString);
	    this.customerNumber = Integer.toString(temp);
	    if(createAccount()){
		
		/*
		  Now that the account has been created, the user no longer has any reason to be
		  logged into the new customer section of the system.
		 */
	    }
	    else{
		
		/*
		  Exit the system.
		*/
	    }
	}
	else{
	    return;
	}
       
    }
    
    /*
      This method handles the account creation process after creation of the customer profile.
     */
    private boolean createAccount() throws SQLException{
	
	/*
	  Ask the customer to select their bill plan
	 */

	PreparedStatement pstate = null;
	ResultSet result = null;
	CallableStatement cstate = null;

	try{
	    System.out.printf("\nTo continue the account creation process, bill plan and account type information must be obtained.\n");

	    System.out.printf("\nHere are the billing plan options:\n ");
	    
	    System.out.printf("\nPlan 1:");
	    System.out.printf("\n$0.05 per minute");
	    System.out.printf("\n$0.01 per text message");
	    System.out.printf("\n$5.00 per gigabyte of data");
	    
	    System.out.printf("\n\nPlan 2:");
	    System.out.printf("\nUnlimited minutes");
	    System.out.printf("\nUnlimited text messages");
	    System.out.printf("\nUnlimited data");
	    System.out.printf("\nTotal Monthly Cost: $60");
	    
	    System.out.printf("\n\nPlan 3:");
	    System.out.printf("\nUnlimited minutes");
	    System.out.printf("\nUnlimited text messages");
	    System.out.printf("\nUnlimited data");
	    System.out.printf("\nTotal Monthly Cost: $100");
	    
	    System.out.printf("\n\nWhich bill plan will be chosen (1, 2, or 3)?\n");
	    
	    System.out.printf("\nEnter your selection here: ");
	    
	    String error = "\nPlease enter a valid billing plan (1, 2, or 3): ";
	    String bill_plan = Jog.verifyInput(error);
	    
	    while(true){
		
		if(bill_plan.compareTo("1") == 0 || bill_plan.compareTo("2") == 0 || bill_plan.compareTo("3") == 0){
		    System.out.printf("\nBill plan %s was chosen.",bill_plan);
		    break;		
		}
		System.out.printf("%s", error);
		bill_plan = Jog.verifyInput(error);		
	    }
	    
	    /*
	      Ask the customer to select their account type
	    */
	    System.out.printf("%s",Jog.separatorString);
	    System.out.printf("\nHere are the account types: ");
	    
	    System.out.printf("\n\nIndividual Account (1): Limit of 1 Phone");
	    System.out.printf("\nFamily Account (2): Limit of 5 Phones");
	    System.out.printf("\nBusiness Account (3): Unlimited Phones");
	    
	    System.out.printf("\n\nWhich account type will be chosen (1, 2, or 3)?\n");
	    System.out.printf("\nEnter your selection here: ");
	    error = "\nPlease enter a valid account type (1, 2, or 3): ";
	    String account_type = Jog.verifyInput(error);
	    int account_type_number = 0;
	    String account_word = null;
	    while(true){
		
		if(account_type.compareTo("1") == 0){
		    System.out.printf("\nIndividual account was chosen.");
		    account_type_number = 1;
		    account_word = "Individual";
		    break;
		}	
		else if(account_type.compareTo("2") == 0){
		    System.out.printf("\nFamily account was chosen.");
		    account_type_number = 5;
		    account_word = "Family";
		    break;		
		} 
		else if(account_type.compareTo("3") == 0){
		    System.out.printf("Business plan was chosen.");	
		    account_type_number = 999;
		    account_word = "Business";
		    break;		
		}
		System.out.printf("%s", error);
		account_type = Jog.verifyInput(error);
	    }
	    
	    System.out.printf("%s", Jog.separatorString);
	    
	    
	    /*
	      
	      There needs to be a phone attached to an account for it to be created.
	      Now, we will create a new phone number for the customer and assign them a phone.
	      
	    */
	    
	    System.out.printf("\nIn order to proceed, a phone must be purchased.  This phone will be used as the primary contact phone for the account.");
	    System.out.printf("\n\nWill a phone be purchased at this time? (y or n)?");
	    System.out.printf("\n\nEnter the selection here: ");
	    error = "\nPlease enter a valid selection (y or n): ";
	    String selection = Jog.verifyInput(error);
	    
	    while(true){
		selection.toLowerCase();
		if(selection.compareTo("y") == 0){
		    break;
		}
		else if(selection.compareTo("n") == 0){
		    return false;		    		    
		}
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);	    
	    }	    
	    System.out.printf("%s",Jog.separatorString);	    
	
	    
	    /*
	      The customer and account information need to be added to the database before 
	      the phone purchase can be made.  If the phone purchase is aborted in any way
	      and the user cancels the setup process, the information will be removed 
	      from the database.	      
	    */
	    
	    String query = "INSERT into customer values(?,?,?,?,?,?)";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, this.customerNumber);
	    pstate.setString(2, this.store_id);
	    pstate.setString(3, this.name);
	    pstate.setString(4, this.address);
	    pstate.setString(5, this.city);
	    pstate.setString(6, this.state);
	    pstate.executeQuery();
	    
	    /*
	      Generate an account number
	    */
	    int temp_acc = 0;
	    String accountNumber = null;

	    while(true){
		
		temp_acc = (int)(Math.random() * 100000) + ((int)Math.random() * 10000); 
		
		accountNumber = Integer.toString(temp_acc);
		
		/*
		  Check to see if the account number has been taken.
		*/
		
		try{
		    cstate = this.con.prepareCall("{? = call checkAccountNum(?)}");
		    cstate.registerOutParameter(1, Types.INTEGER);
		    cstate.setString(2, accountNumber);
		    cstate.execute();

		    if(cstate.getInt(1) == 1){
			System.out.printf("\nThe account number for this new account is %s.", accountNumber);
			break;
		    }
		}
		catch(SQLException ex){}
	    }	    
	    
	    pstate.close();
	    
	    query = "INSERT into account values(?,?,?,?,?,?)";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, accountNumber);
	    pstate.setString(2, this.customerNumber);
	    pstate.setString(3, null);
	    pstate.setInt(4, account_type_number);
	    pstate.setInt(5, 0);
	    pstate.setString(6, bill_plan);
	    pstate.executeQuery();

	    pstate.close();

	    PhonePurchase purch = new PhonePurchase(this.con,accountNumber,store_id);
	    /*
	      If the phone purchase works without any issues, the account relation should be updated
	      to include the new phone as its primary phonenumber.
	     */
	    String phone_number = null;
	    
	    pstate.close();

	    if((phone_number = purch.doWork()) != null){
		
		query = "UPDATE account set primary_phone = ?, phones_assigned = ? where account_number = ?";
		pstate = this.con.prepareStatement(query);
		pstate.setString(1, phone_number);
		pstate.setInt(2, 1);
		pstate.setString(3, accountNumber);
		pstate.executeUpdate();
		
		
	    }
	    /*
	      If doWork returns a null value, there was an issue with the phone purchase process (or the 
	      user aborted).  The previous tuples added to the database should be deleted.
	     */
	    else{

		query = "DELETE from account where account_number = ?";
		pstate = this.con.prepareStatement(query);
		pstate.setString(1, accountNumber);
		pstate.executeUpdate();

		pstate.close();

		query = "DELETE from customer where customer_number = ?";
		pstate = this.con.prepareStatement(query);
		pstate.setString(1, this.customerNumber);
		pstate.executeUpdate();
		
		return false;
	    }
	    
	    pstate.close();
	    System.out.printf("%s", Jog.separatorString);
	    System.out.printf("\nThis account has been successfully created!  Below is the account information.\n");
	    System.out.printf("\nCustomer Number: %s", this.customerNumber);
	    System.out.printf("\nAccount Number: %s", accountNumber);
	    System.out.printf("\nAccount Type: %s", account_word);
	    System.out.printf("\nBilling Plan: %s", bill_plan);
	    System.out.printf("\nPrimary Phone: %s", phone_number);
	
	    System.out.printf("\n\nName: %s", this.name);
	    System.out.printf("\nAddress: %s", this.address);
	    System.out.printf("\nCity: %s", this.city);
	    System.out.printf("\nState: %s", this.state);
	    System.out.printf("%s", Jog.separatorString);
	    
	    String fileName = "account".concat(accountNumber).concat(".txt");
	    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	    writer.printf("Customer Number: %s", this.customerNumber);
	    writer.printf("\nAccount Number: %s", accountNumber);
            writer.printf("\nAccount Type: %s", account_word);
            writer.printf("\nBilling Plan: %s", bill_plan);
            writer.printf("\nPrimary Phone: %s", phone_number);

            writer.printf("\n\nName: %s", this.name);
            writer.printf("\nAddress: %s", this.address);
            writer.printf("\nCity: %s", this.city);
            writer.printf("\nState: %s", this.state);
	    writer.close();

	    System.out.printf("\n\nA file containing the above account information, titled %s, has been created.  Please print it for record keeping purposes.", fileName);

	    return true;

	}
	catch(SQLException ex){
	    System.out.printf("\nInternal database error.  Please try again later.");
	    return false;
	}
	catch(Exception ex){
	    System.out.printf("\nInternal error.  Please try again later.");
	    return false;

	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	    if(cstate != null){
		cstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	}       
    }
    
    /*
      First, the system asks for the user to submit his/her customer information and creates 
      a log of it in the database.
     */
    private int setUpCustomer(){

	System.out.printf("%s", Jog.separatorString);

	System.out.printf("\nThe first step of the account creation process is to create a customer profile.");
	
	/*
	  Get the customer's name
	 */
	System.out.printf("\n\nPlease enter a full name to be associated with the account (70 or less characters): ");
	
	String error = "\nPlease enter a valid name (70 or less characters): ";
	String cust_name = Jog.verifyInput(error);
	CallableStatement cstate = null;

	while(true){
	    try{
		if(cust_name.length() <= 70){
		    break;
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s", error);
	    cust_name = Jog.verifyInput(error);		
	}
	this.name = cust_name;

	/*
	  Get the customer's address
	 */
	System.out.printf("\nPlease enter a street address to be associated with this account.  It will be used to ship purchased products (40 or less characters): ");
	
	error = "\nPlease enter a valid street address (40 or less characters): ";
	String cust_addr = Jog.verifyInput(error);

	while(true){
	    try{
		if(cust_addr.length() <= 40){
		    break;
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s", error);
	    cust_addr = Jog.verifyInput(error);		   
	}
	this.address = cust_addr;
	/*
	  Get the customer's city
	*/
	System.out.printf("\nPlease enter a city to be associated with this address (20 or less characters): ");
	error = "\nPlease enter a valid city (20 or less characters): ";
	String cust_city = Jog.verifyInput(error);

	while(true){
	    try{
		if(cust_city.length() <= 20){
		    break;
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s", error);
	    cust_city = Jog.verifyInput(error);
	}
	this.city = cust_city;
	/*
	  Get the customer's state
	*/
	System.out.printf("\nPlease enter a state (abrv) to be associated with this address (2 or less characters): ");
	error = "\nPlease enter a valid city (2 or less characters): ";
	String cust_state = Jog.verifyInput(error);

	while(true){
	    try{
		if(cust_state.length() <= 2){
		    if(checkState(cust_state)){
			break;
		    }
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s", error);
	    cust_state = Jog.verifyInput(error);
	}
	this.state = cust_state;
	/*
	  Create a new tuple in the customer table for this account setup.    
	  Will be done only if the customer decides to continue the setup process.
	*/

	System.out.printf("%s", Jog.separatorString);
	System.out.printf("\nIn order to continue the account setup process, customer data must be stored. \nThis storage is permanent and the customer information will be stored with Jog.  \nWould you like to continue the account setup process?");
    
	System.out.printf("\n\nEnter the selection here (y or n): ");

	error = "\nPlease enter a valid selection: ";
	String selection = Jog.verifyInput(error);

	while(true){
	    selection.toLowerCase();
	    if(selection.compareTo("y") == 0){
		
		/*
		  Determine the customer number
		*/
		
		try{

		    cstate = this.con.prepareCall("{? = call GETCUSTOMERNUMBER()}");
		    cstate.registerOutParameter(1, Types.INTEGER);
		    cstate.execute();

		    int result = cstate.getInt(1);

		    if(result != 0){
			System.out.printf("\nThe customer number is %d.",result);
			return result;
		    }
		    else if(result == 0){
			throw new SQLException();
		    }
		}
		catch(SQLException ex){
		    ex.printStackTrace();
		    System.out.printf("\nError connecting to the Jog database. Please try again later.");
		    return 0;
		}

	    }
	    else if(selection.compareTo("n") == 0){
		
		/*
		  Return a 0, signalling the customer decided to cancel the process.
		 */
		System.out.printf("\nReturning to main account menu... ");
		return 0;
	    }
	    System.out.printf("%s", error);
	    selection = Jog.verifyInput(error);
	}
    }

    private boolean checkState(String st){
	
	st.toUpperCase();
	for(int i = 0; i < stateList.length; i++){
	    if(st.compareTo(stateList[i]) == 0){
		return true;
	    }
	}
	return false;
    }
    
}