import java.io.*;
import java.sql.*;
import java.util.Scanner;

class CustomerSys {

    private Connection con = null;

    public CustomerSys(Connection con){
	this.con = con;
    }

    public void doWork() throws SQLException{
	
	
	//Determine what kind of customer the user is
	
	System.out.printf("%s", Jog.separatorString);
	System.out.printf("\nAre you an existing customer?  If so, enter a '1'.");
	System.out.printf("\nAre you a new customer?  If so, enter a '2'.");
	
	System.out.printf("\n\nEnter your selection here: ");
	
	String error = "\nPlease enter a valid selection (1 or 2): ";
	String input = Jog.verifyInput(error);

	while(true){
	    if(input.compareTo("1") == 0){
		//process existing customer request
		ExistCustomerSys sys = new ExistCustomerSys(con);
		sys.doWork();
		break;
	    }
	    if(input.compareTo("2") == 0){
		//process new customer request
		NewCustomerSys sys = new NewCustomerSys(con);
		sys.doWork();
		break;
	    }
	    System.out.printf("%s", error);
	    input = Jog.verifyInput(error);
	}
	return;
    }
}