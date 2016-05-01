import java.io.*;
import java.sql.*;
import java.util.Scanner;

class Jog {
    
    public static Connection con;
    private static Scanner scan = new Scanner(System.in);
    
    //String used to separate section for improved readability
    public static String separatorString = "\n\n===========================================\n";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
	
	String username = "mah318";
	
	try{
	   Class.forName("oracle.jdbc.driver.OracleDriver");	
	   
	   /*
	     Process the user's login by creating a 
	     login object and getting a connection to the database from it.
	   */
	   
	   Login userLogin = new Login();
	   con = userLogin.processLogin(username);
	   
	   /*
	     
	     Determine which system to send the user to.  

	     There are two options for systems.  Jog employee or jog customer.

	   */

	   System.out.printf("\n\nEnter a 1 to log into the Jog customer interface");
	   System.out.printf("\nEnter a 2 to log into the Jog employee interface");
	   
	   System.out.printf("\n\nEnter your selection here: ");
	   
	   String error = "\nPlease enter a valid selection (1 or 2): ";
	   String selection = verifyInput(error);
	  
	   while(true){
	       if(selection.compareTo("1") == 0){
		   System.out.printf("Processing customer login request...");
		   //process Jog customer login request
		   CustomerSys sys = new CustomerSys();
		   sys.doWork(con);
		   break;
	       }
	       else if(selection.compareTo("2") == 0){
		   System.out.printf("Processing employee login request...");
		   //process Jog employee login request
		   break;
	       }
	       System.out.printf("%s", error);
	       selection = verifyInput(error);
	   }
	  
	   con.close();
	   System.out.printf("\nConnection closing...\n");

	}
	
	catch(ClassNotFoundException ex){
	    System.err.println("\nInternal Error.  Proper drivers could not be loaded.  Please be sure all files are present.");
	    System.exit(0);
	    
	}
	catch(SQLException ex){
	    System.err.println("\nConnection could not be established.  Try again later");	  
	    
	    System.exit(0);
	}
	catch(Exception ex){
	    System.err.println("\nInternal Error.  Please be sure all files are present and loaded.");
	    System.exit(0);
	}
	
    }

    public static String verifyInput(String error){	
	
	Boolean valid = true;
	String input = null;

	while(valid){	    
	    try{
		if(scan.hasNextLine()){
		    if(!(input = scan.nextLine()).isEmpty()){
			break;		      
		    }
		}		
	    }
	    catch(Exception ex){	
	    }
	    System.out.printf("%s", error);    
	}
	return input;
    }
    
    /*
      Use a customer's account number to get their address
     */
    public static ResultSet getAddressByAcc(String accountNumber) throws SQLException{
	
	PreparedStatement pstate = null;
	try{

	    String query = "SELECT address, city, state FROM customer WHERE customer_number = (select customer_number from account where account_number = ?)";
	    pstate = con.prepareStatement(query);
	    pstate.setString(1,accountNumber);
	    return pstate.executeQuery();
    
	}
	catch(SQLException ex){
	    
	    System.out.printf("\n\nCannot reach the database at this time.  Please try again later");
	    ex.printStackTrace();
	    pstate.close();
	    return null;

	}
	
    }
    
    /*
      Implement the Luhn algorithm to check if a credit card number is valid
     */
    public static boolean check_cc_num(String cc_num){
	
	int sum = 0;
	boolean alt = false;
	try{
	    for (int i = cc_num.length() - 1; i >= 0; i--){
		int n = Integer.parseInt(cc_num.substring(i,i+1));
		
		if(alt){
		    n *= 2;
		    
		if(n > 9){
		    
		    n = (n % 10) + 1;
		}
		}
		sum += n;
		alt = !alt;
	    }
	    return (sum % 10 == 0);
	}
	catch(Exception ex){
	    return false;
	}
    }
}