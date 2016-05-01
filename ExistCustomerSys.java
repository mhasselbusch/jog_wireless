import java.io.*;
import java.sql.*;
import java.util.Scanner;

class ExistCustomerSys{

    public ExistCustomerSys(){}

    public void doWork(Connection con) throws SQLException{
	
	/*
	  Get the account number of the existing customer
	*/
	System.out.printf("%s", Jog.separatorString);
	System.out.printf("\nPlease enter your account number (5 digit number): ");

	String error = "\nPlease enter a valid account number (5 digits): ";
	String accountNumber = Jog.verifyInput(error);
	CallableStatement cstate = null;
	
	while(true){
	    
	    if((accountNumber.length() == 5)){
		
		try{
		    //prepare a callablestatement to call the verifyaccount function in the
		    //database.  It returns a 1 if the account was found.
		    
		    cstate = con.prepareCall("{? = call verifyAccount(?)}");
		    cstate.registerOutParameter(1, Types.INTEGER);
		    cstate.setString(2, accountNumber);
		    cstate.execute();
		    
		    if(cstate.getInt(1) == 1){
			break;
		    }
     		}
		catch(SQLException ex){
		    System.err.printf("\nInternal database error.  Please log in again.");
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

	/*
	  At this point, the account number has been verified and the user is "logged in"
	  Now, the system will welcome the new user!
	*/
	
	PreparedStatement statement = null;
	ResultSet result = null;

	System.out.printf(Jog.separatorString);
	try{
	    String query = "SELECT name from customer, (select customer_number from account where account_number = ?) A where customer.customer_number = A.customer_number";

	    statement = con.prepareStatement(query);
	    statement.setString(1, accountNumber);

	    result = statement.executeQuery();
	    
	    if(result.next()){
		
		System.out.printf("\n\nWelcome %s!", result.getString("name"));

	    }
	    else{
		System.out.printf("\nWelcome valued customer!");
	    }

	    
	}
	catch(SQLException ex){
	    System.out.printf("\nWelcome valued customer!");
	    
	}
	finally {
	    if(statement != null){
		statement.close();
	    }
	    if(result != null){
		result.close();
	    }

	}
	

	
	/*
	  Now that the user's account number has been verified and they hav been greeted, 
	  the system will prompt them for a command
	  
	  This user will be able to do as much as they want inside the system until they 
	  specifically tell it to exit.

	*/
	
	
	String selection = null;
	System.out.printf("\n\nWhat would you like to do today?");
	while(true){
	    
       	    System.out.printf("\nTo view your bills, enter a '1'.");
	    System.out.printf("\nTo purchase and add a new phone to your account, enter a '2'.");
	    System.out.printf("\nTo view your usage logs, enter a '3'.");
	    System.out.printf("\nTo quit, enter 'quit'");
	    
	    System.out.printf("\n\nEnter your selection here: ");
	    
	    error = "\nPlease enter a valid selection: ";
	    selection = Jog.verifyInput(error);
	
	    while(true){
	
		if(selection.compareTo("1") == 0){
		    
		    Billing bill = new Billing(con, accountNumber);
		    bill.doWork();
		    break;
		    
		}
		else if(selection.compareTo("2") == 0){
		    //process phone purchase request
		    PhonePurchase purch = new PhonePurchase(con, accountNumber, "0");
		    purch.doWork();
		    break;
		    
		}
		else if(selection.compareTo("3") == 0){
		    //process usage request
		    break;
		}
		else{
		    selection.toLowerCase();
		    if(selection.compareTo("quit") == 0){
			System.out.printf("\nGoodbye!  Have a nice day.\n");
			try{
			    con.close();
			    System.out.printf("\nConnection closing...\n");
			}
			catch(SQLException ex){
			    System.err.printf("Error closing the connection.  Exiting without closing connection...");
			}
			System.exit(0);
		    }
		}
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);
	    }
	    System.out.printf(Jog.separatorString);
	}
	    
	
    }
}


		       