import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Login{
    
    private Scanner scan;

    public Login(){
	
	this.scan = new Scanner(System.in);
    }
    
    /*

      Process login
      
      Require the user to enter a valid password until the correct one is entered or the
      user quits.  The connection object is created within the scope of this function
      and is passed back to the calling function.      

    */
    public Connection processLogin() throws SQLException{
	
	System.out.printf("\nWelcome to Jog Wireless!\n\nPlease enter your password: ");
	
	String error = "\nInvalid password.  Please try again: ";
	String passwd = Jog.verifyInput(error);
	Connection con = null;
     
	while(true){
	    
	    try{
		con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241","mah318",passwd);
		break;
	    }
	    catch(SQLException ex){			
	    }
	    System.out.printf("%s", error);
	    passwd = Jog.verifyInput(error);
	}	    

	System.out.printf("\nSuccess!  Logging in...");
	return con;
    }
}