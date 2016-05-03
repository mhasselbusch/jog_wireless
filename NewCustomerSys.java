import java.io.*;
import java.sql.*;

class NewCustomerSys{
    
    private Connection con = null;
    
    public NewCustomerSys(Connection con){
	this.con = con;
    }
    
    public void doWork(){

	/*

	  Ask the customer what they would like to do.

	  Currently, there is only support for creating a new account.
	
	*/
	System.out.printf("%s",Jog.separatorString);
	System.out.printf("\nWelcome to Jog Wireless!  Would you like to set up a new account?  \n\nPlease enter a 'y' for yes or an 'n' for no: ");
	String error = "\nPlease enter a valid selection (y or n): ";
	String selection = Jog.verifyInput(error);
	AccountCreation acc_create = null;

	while(true){
	    
	    try{
		selection.toLowerCase();
		if(selection.compareTo("y") == 0){
		    acc_create = new AccountCreation(con, "0");
		    acc_create.doWork();
		    break;
		}
		else if(selection.compareTo("n") == 0){
		    return;
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s", error);
	    selection = Jog.verifyInput(error);
	}
    }
}