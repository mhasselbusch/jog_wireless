import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;

class PhonePurchase{
    
    private Connection con;
    private String accountNumber;
    private int number_limit;
    private int phones_assigned;
    private String store_id;
    private String new_phone_number;

    public PhonePurchase(Connection con, String accountNumber, String store_id){
	this.con = con;
	this.accountNumber = accountNumber;
	this.store_id = store_id;
    }
    
    public String doWork() throws SQLException{
	
	//First make sure the account can purchase a phone
	if(this.confirmAccount()){
	    
	    System.out.printf("%s", Jog.separatorString);
	    
	    System.out.printf("\n\nThis account has been cleared to buy a new phone!");
	    
	    //Proceed with the phone purchase.
	    if(buyPhone()){
	    
		System.out.printf("\n\nReturning to previous menu...");
		return new_phone_number;
	    }
	    else{
		System.out.printf("\n\nReturning to previous menu...");
		return null;
	    }
	}
	else{
	    
	    System.out.printf("\n\nThis account is unable to purchase a new phone at this time.");
	    System.out.printf("\nThis account has a limit of %d phone(s) and  currently has %d phone(s) assigned.\n",this.number_limit,this.phones_assigned);
	    return null;
	}

    }
    
    //The method will be used to confirm that more phones can be purchased for an account.
    private Boolean confirmAccount() throws SQLException{
	
	CallableStatement cstate = null;

	try{
	    
	    //Get the number of assigned phones
	    cstate = this.con.prepareCall("{? = call getAssigned(?)}");
	    cstate.registerOutParameter(1, Types.INTEGER);
	    cstate.setString(2, this.accountNumber);
	    cstate.execute();
	    
	    this.phones_assigned = cstate.getInt(1);
	    
	    //Get the limit of phone assignments
	    cstate = this.con.prepareCall("{? = call getLimit(?)}");
	    cstate.registerOutParameter(1, Types.INTEGER);
	    cstate.setString(2, this.accountNumber);
	    cstate.execute();

	    this.number_limit = cstate.getInt(1);
	    
	    //Determine if you can purchase a phone
	    cstate = this.con.prepareCall("{? = call confirmAvailablePhones(?)}");
	    cstate.registerOutParameter(1,Types.INTEGER);
	    cstate.setString(2, this.accountNumber);
	    cstate.execute();
	    
	    if(cstate.getInt(1) == 1){
		return true;
	    }
	    else{
		return false;
	    }
	}
	catch(SQLException ex){
	    System.err.printf("\n\nInternal database error.  Please try again.");
	    return false;
	}
	finally{
	    if(cstate != null){
		cstate.close();
	    }
	}
    }

    //Make a phone purchase
    private Boolean buyPhone() throws SQLException{
	
	String query = "SELECT manufacturer, model FROM inventory where STORE_ID = ?";
	Statement statement = null;
	ResultSet result = null;
	CallableStatement cstate = null;
	PreparedStatement pstate = null;

	try{
	
	   /*
	     Get the names and brands of phones that Jog currently offers through its online store.
  	   */
    
	    pstate = con.prepareStatement(query);
	    pstate.setString(1, this.store_id);
	    result = pstate.executeQuery();
	 
	    System.out.printf("\n\nWhich phone will be purchased?");
	    
	    int i = 0;
	    
	    List<String> phoneModels = new ArrayList<>();
	    List<String> phoneManus = new ArrayList<>();
	   
	    /*
	      Determine which phone the user wants to purchase.
	    */
	    
	    while(result.next()){
		
		System.out.printf("\nTo purchase a(n) %s %s, enter a %d.", result.getString("manufacturer"), result.getString("model"), i);
		
		phoneModels.add(result.getString("model"));
		phoneManus.add(result.getString("manufacturer"));

		i++;
		
	    }
	    System.out.printf("\n\nEnter the selection here: ");	    
	    
	    String error = "\nPlease enter a valid selection (one of the numbers listed above): ";
	    String selection = Jog.verifyInput(error);
	    int select_Int = 0;
	    String model_Selection = null;
	    String manu_Selection = null;

	    while(true){
		
		if(selection.length() == 1){
		    
		    if(Character.isDigit(selection.charAt(0))){
			
			select_Int = Character.getNumericValue(selection.charAt(0));
			
			if(select_Int < phoneModels.size()){
			    
			    model_Selection = phoneModels.get(select_Int);
			    manu_Selection = phoneManus.get(select_Int);
			    break;
			}		       
		    }
		}
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);
	    }
	    
	    System.out.printf("%s", Jog.separatorString);
	    /*
	      Ask the user once more if they are sure they want to proceed with the phone purchase.
	      If they no longer want to, control is returned to the main account menu.
	    */

	    System.out.printf("\nYou selected the %s %s.", manu_Selection, model_Selection);	    
	    System.out.printf("\n\nDo you want to proceed with this purchase?  \nEnter a 'y' for yes or an 'n' for no.  \nIf you enter an 'n' you will be returned to the main  menu.");
	    System.out.printf("\n\nEnter the selection here: ");

	    error = "\nPlease enter a valid selection (y or n): ";
	    selection = Jog.verifyInput(error);
	    
	    while(true){
		
		if((selection = selection.toLowerCase()).compareTo("y") == 0){
		    break;
		}
		else if((selection = selection.toLowerCase()).compareTo("n") == 0){
		    if(statement != null){
			statement.close();
		    }
		    if(result != null){
			result.close();
		    }
		    return false;		    		    
		}
		System.out.printf("%s",error);
		selection = Jog.verifyInput(error);		
	    }

	    
	    /*
	      Complete the purchase using the account holder's information
	    */
	    
	    /*
	      First, randomly generate the phone number that will be used.  
	      It will be compared against the rest of the phone numbers to ensure that it is valid.
	    */
	    
	    StringBuilder phone_num_build = new StringBuilder(10);
	    StringBuilder meid_build = new StringBuilder(14);
	    String phone_num = null;
	    String meid = null;	   
	    
	    while(true){
		int num = (int)(Math.random() * 10);
		
		phone_num_build.append(Integer.toString(num));       
		
		for(i = 0; i < 9; i++){
		    num = (int)(Math.random() * 10);	
		    phone_num_build.append(Integer.toString(num));			    
		}			       
		
		phone_num = phone_num_build.toString();
		
		System.out.printf("%s", Jog.separatorString);

		/*
		  Call the PL/SQL function checkPhoneNum to compare the generated number 
		  against all others.
		*/
		
		cstate = this.con.prepareCall("{? = call checkPhoneNum(?)}");
		cstate.registerOutParameter(1, Types.INTEGER);
		cstate.setString(2, phone_num);
		cstate.execute();
		
		if(cstate.getInt(1) == 1){
		    
		    System.out.printf("\nThe phone number for this new phone is %s.", phone_num);
		    break;
		}
	    }
	    
	    
	    /*
	      Second, randomly generate the meid that will be used.  
	      It will be compared against the rest of the meids to ensure that it is valid.
	    */
	    
	    while(true){
		int num = (int)(Math.random()*10);
		
		meid_build.append(Integer.toString(num));
		
		for(i = 0; i < 13; i++){
		    num = (int)(Math.random() * 10);
		    meid_build.append(Integer.toString(num));
		    
		}
		
		/*
		  Call the PL/SQL function checkMEID to compare the generated MEID against all others.
		 */
		meid = meid_build.toString();
		
		cstate = this.con.prepareCall("{? = call checkMEID(?)}");
		cstate.registerOutParameter(1, Types.INTEGER);
		cstate.setString(2, phone_num);
		cstate.execute();

		if(cstate.getInt(1) == 1){
		    
		    System.out.printf("\n\nThe MEID for this new phone is %s.", meid);
		    break;
		}
		
	    }

	    System.out.printf("\n\nDo you want to proceed with the purchase of this phone?");
	    System.out.printf("\nHere is its information: ");
	    System.out.printf("\n\tManufacturer: %s", manu_Selection);
	    System.out.printf("\n\tModel: %s", model_Selection);
	    System.out.printf("\n\tPhone Number: %s", phone_num);
	    System.out.printf("\n\tMEID: %s", meid);

	    System.out.printf("\n\nEnter the selection here (y or n): ");

	    error = "\nPlease enter a valid selection (y or n): ";
	    selection = Jog.verifyInput(error);
	    
	    while(true){
		
		if((selection = selection.toLowerCase()).compareTo("y") == 0){
		    break;
		}
		else if((selection = selection.toLowerCase()).compareTo("n") == 0){
		    if(statement != null){
			statement.close();
		    }
		    if(result != null){
			result.close();
		    }
		    return false;		    		    
		}
		System.out.printf("%s",error);
		selection = Jog.verifyInput(error);		
	    }
	    
	    System.out.printf("%s", Jog.separatorString);

	    /*
	      
	      First, we should update the soldPhones relation to include the newly purchased phone

	     */
	    
	    //Autocommit will be turned off until all updates to database are ready
	    con.setAutoCommit(false);

	    //Today's date needs to be obtained 
	    
	    cstate = this.con.prepareCall ("{? = call getDate}");
	    cstate.registerOutParameter(1, java.sql.Types.VARCHAR);
	    cstate.execute();
	    
	    String date_string = cstate.getString(1);;
	    
	    //Update the soldPhones relation
	    query = "insert into soldphones values (?, ?, ?, ?, ?)";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, store_id);
	    pstate.setString(2, meid);
	    pstate.setString(3, model_Selection);
	    pstate.setString(4, manu_Selection);
	    pstate.setString(5, date_string);
	    pstate.executeUpdate();
	    
	    
	    /*
	      Next, we will update the phonenumber relation in the database to include the newly
	      generated phonenumber.  It will also be assigned to the account number of the 
	      current user.
	    */
	    query = "insert into phonenumber values (?,?,?)";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1,this.accountNumber);
	    pstate.setString(2,phone_num);
	    pstate.setString(3,meid);
	    pstate.executeUpdate();
	    
	    /*
	      Get the customer's address to tell them where the phone is being sent
	      if the purchase is being made online.
	    */
	    String address = null;
	    String state = null;
	    String city = null;

	    if(store_id.compareTo("0") == 0){
		result = Jog.getAddressByAcc(this.accountNumber);
		
		if(result.next()){
		    
		    address = result.getString("address");
		    state = result.getString("state");
		    city = result.getString("city");
		}
		else{
		    
		    System.out.printf("\n\nPlease make sure an address is on file with Jog for this account.  Please edit the account information and attempt the purchase again.");
		    return false;
		}
	    }
	    
	    //Get a credit card number from the user to charge
	    System.out.printf("\n\nPlease enter a credit card number: ");
	    error = "\nInvalid credit card number.  Please try again: ";
	    String cc_num = Jog.verifyInput(error);
	    int times = 1;
	    //3 attempts before the system shuts down.
	    while(true){
		if(Jog.check_cc_num(cc_num)){
		    break;
		}
		if(times == 3){
		    System.out.printf("\n\n3 incorrect attempts.");
		    if(statement != null){
			statement.close();
		    }
		    if(result != null){
			result.close();
		    }
		    if(cstate != null){
			cstate.close();
		    }
		    if(pstate != null){
			pstate.close();
		    }
		    return false;
		}
		times++;
		System.out.printf("%s", error);
		cc_num = Jog.verifyInput(error);
	    }
	    
	    //Commit all changes to the database and inform the user that their purchase went through.
	    System.out.printf("%s", Jog.separatorString);
	    con.commit();
	    
	    /*
	      If this is the first phone being purchased for an account,
	      update the account relation.
	     */
	    	    
	    cstate = this.con.prepareCall("{? = call checkPhoneNull(?)}");
	    cstate.registerOutParameter(1, Types.INTEGER);
	    cstate.setString(2, this.accountNumber);
	    cstate.execute();

	    int result_null = cstate.getInt(1);

	    if(result_null == 1){
		System.out.printf("\n\nThis is the first phone being purchased under this account.  It will be added as the primary phone for this account.");
		query = "UPDATE account set primary_phone = ?, phones_assigned = ? where account_number = ?";
		pstate = this.con.prepareStatement(query);
		pstate.setString(1, phone_num);
		pstate.setInt(2, 1);
		pstate.setString(3, accountNumber);
		pstate.executeUpdate();
	    
	    }
	    if(store_id.compareTo("0") == 0){

		System.out.printf("\nThis phone has been purchased!  \nYour credit card, number %s, has been billed the price of the phone.  \nYour phone should arrive at %s, %s %s in 3-4 business days.", cc_num, address, city, state);
	    }
	    else{
		System.out.printf("\nPurchase complete!  The customer's phone is activated and ready for use.");
	    }
	    con.setAutoCommit(true);
	    this.new_phone_number = phone_num;
	    return true;		    
	}
	catch(SQLException ex){
	    System.err.printf("Trouble reaching Jog database.  Please try again later.");
	    ex.printStackTrace();
	    return false;
	}
	finally{
	    if(statement != null){
		statement.close();
	    }
	    if(result != null){
		result.close();
	    }
	    if(cstate != null){
		cstate.close();
	    }
	    if(pstate != null){
		pstate.close();
	    }
	}	
    }
}