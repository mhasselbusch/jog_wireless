import java.io.*;
import java.sql.*;

class EmployeeSys {
    
    private Connection con = null;

    public EmployeeSys(Connection con){
	this.con = con;
    }

    public void doWork() throws SQLException{
	/*
	  
	  First, we need to determine which store the user is logging in 
	  from.  This will be done by asking the user for the store's ID.
	  
	*/
	
	System.out.printf("\n\nPlease enter the ID of the Jog store you are logging in from: ");
	String error = "\nThis is not a Jog Store ID. Please enter a valid one: ";
	String store_id = Jog.verifyInput(error);
	CallableStatement cstate = null;
	while(true){	
	    try{	    
		if((store_id.length() == 5)){
		    cstate = this.con.prepareCall("{? = call verifyStore(?)}");
		    cstate.registerOutParameter(1, Types.INTEGER);
		    cstate.setString(2, store_id);
		    cstate.execute();
		    
		    if(cstate.getInt(1) == 1){
			break;
		    }
		}
		System.out.printf("%s", error);
		store_id = Jog.verifyInput(error);		
	    }
	    catch(SQLException ex){
		System.err.printf("\nInternal database error.  Please log in again.");	    
		this.con.close();
		if(cstate != null){
		    cstate.close();
		}
		System.exit(0);
	    }
	    catch(Exception ex){
		System.out.printf("%s", error);
	    }
	    finally{
		if(cstate != null){
		    cstate.close();
		}
	    }
		    
	}
	
	System.out.printf("%s", Jog.separatorString);
	System.out.printf("\n\nYou are now logged in under Jog Store ID #%s", store_id);
	
	/*	
	  Now that we have the store id, we can ask the employee what they would like to do.
	*/
	Restock rstock = null;
	PhonePurchase purch = null;
	Billing bill = null;
	String selection = null;
	boolean finish = false;
	AccountCreation acc = null;

	System.out.printf("\n\nWhat would you like to do today?");
	while(true){

	    System.out.printf("\nTo process a restock request, enter a '1'.");
	    System.out.printf("\nTo process a new phone purchase request, enter a '2'.");
	    System.out.printf("\nTo process a bill payment request, enter a '3'.");
	    System.out.printf("\nTo create a new account for a customer, enter a '4'.");
	    System.out.printf("\nTo quit, enter 'quit'.");
	    
	    System.out.printf("\n\nEnter your selection here: ");
	    error = "\nPlease enter a valid selection: ";
	    selection = Jog.verifyInput(error);

	    while(true){

		/*
		  If the employee enters a 1, the inventory of the store will be printed
		  and the employee can request a restock from the online store.
		 */
		if(selection.compareTo("1") == 0){
		    
		    rstock = new Restock(con, store_id);
		    rstock.doWork();
		    break;
		}
		/*
		  If the employee enters a 2 or 3, the system will first ask for the account number of the customer.
		  Then, the system will start the phone purchase or billing process.
		 */
		else if(selection.compareTo("2") == 0 || selection.compareTo("3") == 0){
		    
		    String accountNumber = null;
		    String innerErr = null;
		    finish = false;

		    while(true){
			
			System.out.printf("\nPlease enter the customer's account number (5 digit number): ");
			innerErr = "\nPlease enter a valid account number (5 digits):";
			accountNumber = Jog.verifyInput(error);
			
			while(true){

			    if((accountNumber.length() == 5)){
				
				try{
				    cstate = con.prepareCall("{? = call verifyAccount(?)");
				    cstate.registerOutParameter(1, Types.INTEGER);
				    cstate.setString(2, accountNumber);
				    cstate.execute();
				    
				    if(cstate.getInt(1) == 1){
					
					if(selection.compareTo("2") == 0){
					    //Process purchase phone request
					    purch = new PhonePurchase(con, accountNumber, store_id);
					    purch.doWork();
					    finish = true;
					    break;
					}	
					else if(selection.compareTo("3") == 0){
					    //Process a bill payment request
					    bill = new Billing(con, accountNumber);
					    bill.doWork();
					    finish = true;
					    break;
					}
				    }				    
				}
				catch(SQLException ex){
				    System.err.printf("\nInternal database error.  Please log in again.");
				    if(cstate != null){
					cstate.close();
				    }
				    if(con != null){
					con.close();
				    }
				    System.exit(0);
				}
				finally{
				    if(cstate != null){
					cstate.close();
				    }
				}
			    }
			    System.out.printf("%s", error);
			    accountNumber = Jog.verifyInput(error);
			
			}
			if(finish){
			    break;
			}
		    }
		}
		else if(selection.compareTo("4") == 0){
		    
		    acc = new AccountCreation(this.con, store_id);
		    acc.doWork();
		    break;
		    
		}
		/*
		  Exit the system if the employee enters 'quit'.
		 */
		else{
		    selection = selection.toLowerCase();
		    if(selection.compareTo("quit") == 0){
			System.out.printf("\nLogging out of the system...\n");
			if(con != null){
			    con.close();
			}
			if(cstate != null){
			    cstate.close();
			}
			System.exit(0);		    
		    }
		}
		if(!finish){
		    System.out.printf("%s", error);
		    selection = Jog.verifyInput(error);	    
		}
		if(finish){
		    break;
		}
	    }
	    System.out.printf(Jog.separatorString);
	}
    }
}   