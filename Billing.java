import java.io.*;
import java.sql.*;

class Billing{
    
    private Connection con = null;
    private String accountNumber = null;
    private String monthArray[] = {"January", "February", "March", "April", "May", "June",
				     "July", "August", "September", "October", "November", "December"};
    public Billing(Connection con, String accountNumber){
	this.con = con;
	this.accountNumber = accountNumber;
    }

    public void doWork() throws SQLException{

	/*
	  First, we need to ask the user to indicate which month they would like a 
	  bill to be generated for.
	*/
	System.out.printf("%s", Jog.separatorString);
	System.out.printf("\nWhich month would you like to generate a bill for? (1-12): ");
	String error = "\nPlease enter a valid month number (1-12): ";
	String month = Jog.verifyInput(error);
	
	while(true){
	    
	    try{
		if((Integer.parseInt(month) > 0 && Integer.parseInt(month) <= 12)){
		    break;
		}
	    }
	    catch(Exception ex){}	      
	    System.out.printf("%s", error);
	    month = Jog.verifyInput(error);
	}

	/*
	  Second, we need to ask the user which year to generate the bill for.
	*/
	 
	CallableStatement cstate = null;
	ResultSet result = null;
	String query = null;
	
	System.out.printf("\nWhich year would you like to generate the bill for? (> 2014): ");
	error = "\nPlease enter a valid year number (> 2014): ";
	String year = Jog.verifyInput(error);
	
	while(true){
	    
	    try{
		if(Integer.parseInt(year) > 2014){
		    break;
		}
	    }
	    catch(Exception ex){}
	    System.out.printf("%s",error);
	    year = Jog.verifyInput(error);
	}
	
	/*
	  Next, we will call the PL/SQL function generateBill.  
	  This will do essentially all of the
	  work in the bill generation process.  After the bill is 
	  generated, we will simply query the database for it.
	*/
	
	try{
	    
	    cstate = this.con.prepareCall("{? = call generateBill(?,?,?)}");
	    cstate.registerOutParameter(1, Types.INTEGER);
	    cstate.setString(2, this.accountNumber);
	    cstate.setString(3, month);
	    cstate.setString(4, year);
	    cstate.execute();
		
	    int result_num = cstate.getInt(1);
		
	    if(result_num == 2 || result_num == 3){
		System.err.printf("\nInvalid year/month. \nReturning to previous menu...");		  
		return;
	    }
	    else if(result_num == 4){
		System.out.printf("\nNo phones were activated for this account during the specified timeframe. As such, there is no billing information.\nReturning to previous menu...");
		return;
	    }
	    else if(result_num == 5){
		System.out.printf("\nNo bill can be generated for this month because the billing cycle is still in progress. Bills can be viewed at the end of each month.\nReturning to previous menu...");
		return;
	    }
	    else if(result_num == 0){
		throw new SQLException();
	    }
	
	    
	}
	catch(SQLException ex){
	    System.err.printf("\n\nInternal database error.  Returning to main account menu...");
	    return;
	}
	finally{
	    if(cstate != null){
		cstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	}
	
	/*
	  Now, we will query the database to get the results of the bill generation back for the
	  user to view.
	*/
	String plan = null;
	try{
	    query = "SELECT bill_plan from account where account_number = ?";
	    PreparedStatement pstate = null;
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, accountNumber);
	    result = pstate.executeQuery();
	    plan = null;
	    
	    if(result.next()){
		
		plan = result.getString("bill_plan");
		
	    }
	    else{
		throw new SQLException();
	    }
	}
	catch(SQLException ex){
	    System.err.printf("\nInternal database error.  Please try again later.");
	    return;
	}
	
	//Query the BillsPlan1 table
	if(plan.compareTo("1") == 0){
	    
	    if(this.queryBills1(month, year)){
		return;
	    }

	}
	//Query the BillsPlan2 table
	else if(plan.compareTo("2") == 0){
	    
	    if(this.queryBills2(month, year)){
		return;
	    }
	}
	//Query the BillsPlan3 table
	else if(plan.compareTo("3") == 0){

	    if(this.queryBills3(month,year)){
		return;
	    }
	}
	else{
	    System.err.printf("\nInternal database error.  Please try again later.");
	    return;
	}   
	
    }

    private boolean queryBills1(String month, String year) throws SQLException{
	
	PreparedStatement pstate = null;
	ResultSet result = null;
	double total = 0;
	int paid = 0;

	try{
	    this.con.setAutoCommit(false);
	    String query = "SELECT * from billsplan1 where account_number = ? and month = ? and year = ?";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, this.accountNumber);
	    pstate.setString(2, month);
	    pstate.setString(3, year);
	    result = pstate.executeQuery();
	    
	    /*
	      Organize the results into a table
	    */
	    
	    if(result.next()){
		System.out.printf("%s", Jog.separatorString);
		
		System.out.printf("\n\n%s, %s bill for account #%s",
				  this.monthArray[Integer.parseInt(month) - 1]
				  ,year, this.accountNumber);
		
		System.out.printf("\n\n# of Minutes");
		System.out.printf("\t# of Texts");
		System.out.printf("\t# of Gigabytes ");
		
		System.out.printf("\n%12s", result.getString("NUM_MIN"));
		System.out.printf("\t%10s", result.getString("NUM_TEXT"));
		System.out.printf("\t%14s", result.getString("NUM_GIG"));
	    
		System.out.printf("\n\nCost per Minute");
		System.out.printf("\tCost per Text");
		System.out.printf("\tCost per Gigabyte");

		System.out.printf("\n%15s", result.getString("MIN_COST"));
		System.out.printf("\t%13s", result.getString("TEXT_COST"));
		System.out.printf("\t%17s", result.getString("GIG_COST"));
		
		total = result.getInt("TOTAL");
		paid = result.getInt("PAID");

		System.out.printf("\n\nBill Total: $%.2f", total);

		System.out.printf("%s", Jog.separatorString);
	    }
	    else{
		System.err.printf("\n\nInternal database error.  Please try again later.");
	    }
	    
	    con.commit();
	}
	catch(SQLException ex){
	    System.out.printf("\n\nInternal database error.  Please try again later.");
	    return false;
	}
	catch(Exception ex){
	    System.out.printf("\n\nInternal error.  Please try again later.");
	    return false;
	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	    this.con.setAutoCommit(true);
	}
	
	/*
	  Proceed with the bill payment
	*/
	if(this.payBill(total, 1, paid, month, year)){
	    System.out.printf("\n\nThank you! Your bill has been paid!  Returning to main account menu...");
	}
	
	
	return true;
    }
    
    private boolean queryBills2(String month, String year) throws SQLException{
	PreparedStatement pstate = null;
	ResultSet result = null;
	double total = 0;
	int paid = 0;

	try{
	    this.con.setAutoCommit(false);
	    String query = "SELECT * from billsplan2 where account_number = ? and month = ? and year = ?";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, this.accountNumber);
	    pstate.setString(2, month);
	    pstate.setString(3, year);
	    result = pstate.executeQuery();
	    
	    /*
	      Organize the results into a table
	    */
	    
	    if(result.next()){
		System.out.printf("%s", Jog.separatorString);
		
		System.out.printf("\n\n%s, %s bill for account #%s",
				  this.monthArray[Integer.parseInt(month) - 1]
				  ,year, this.accountNumber);
		
		System.out.printf("\n\n# of Minutes");
		System.out.printf("\t# of Texts");
		System.out.printf("\t# of Gigabytes ");
		
		System.out.printf("\n%12s", result.getString("NUM_MIN"));
		System.out.printf("\t%10s", result.getString("NUM_TEXT"));
		System.out.printf("\t%14s", result.getString("NUM_GIGS"));
	    
		total = result.getDouble("COST_MONTH");
		paid = result.getInt("PAID");
		
		System.out.printf("\n\nBill Monthly Cost: $%.2f", total);
		System.out.printf("\n\nBill Total: $%.2f", total);

		System.out.printf("%s", Jog.separatorString);
	    }
	    else{
		System.err.printf("\n\nInternal database error.  Please try again later.");
	    }
	    
	    con.commit();
	}
	catch(SQLException ex){
	    System.out.printf("\n\nInternal database error.  Please try again later.");
	    return false;
	}
	catch(Exception ex){
	    System.out.printf("\n\nInternal error.  Please try again later.");
	    return false;
	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	    this.con.setAutoCommit(true);
	}
	/*
	  If the total is greater than 0 and the bill is unpaid, the customer
	  will be asked to pay the bill.
	*/
	/*
	  Proceed with the bill payment
	 */
		
	if(this.payBill(total, 2, paid, month, year)){
	    System.out.printf("\n\nThank you! Your bill has been paid!  Returning to main account menu...");
	}

	return true;

    }
    
    private boolean queryBills3(String month, String year) throws SQLException{
	PreparedStatement pstate = null;
	ResultSet result = null;
	double total = 0;
	int paid = 0;

	try{
	    this.con.setAutoCommit(false);
	    String query = "SELECT * from billsplan3 where account_number = ? and month = ? and year = ?";
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, this.accountNumber);
	    pstate.setString(2, month);
	    pstate.setString(3, year);
	    result = pstate.executeQuery();
	    
	    /*
	      Organize the results into a table
	    */
	    
	    if(result.next()){
		System.out.printf("%s", Jog.separatorString);
		
		System.out.printf("\n\n%s, %s bill for account #%s",
				  this.monthArray[Integer.parseInt(month) - 1]
				  ,year, this.accountNumber);
		
		System.out.printf("\n\n# of Minutes");
		System.out.printf("\t# of Texts");
		System.out.printf("\t# of Gigabytes ");
		
		System.out.printf("\n%12s", result.getString("NUM_MIN"));
		System.out.printf("\t%10s", result.getString("NUM_TEXT"));
		System.out.printf("\t%14s", result.getString("NUM_GIGS"));
	    
		total = result.getDouble("COST_MONTH");
		paid = result.getInt("PAID");
		
		System.out.printf("\n\nBill Monthly Cost: $%.2f", total);
		System.out.printf("\n\nBill Total: $%.2f", total);

		System.out.printf("%s", Jog.separatorString);
	    }
	    else{
		System.err.printf("\n\nInternal database error.  Please try again later.");
	    }
	    
	    con.commit();
	}
	catch(SQLException ex){
	    System.out.printf("\n\nInternal database error.  Please try again later.");
	    return false;
	}
	catch(Exception ex){
	    System.out.printf("\n\nInternal error.  Please try again later.");
	    return false;
	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	    this.con.setAutoCommit(true);
	}

	/*
	  Proceed with the bill payment
	 */
	
	if(this.payBill(total, 3, paid, month, year)){
	    System.out.printf("\n\nThank you! Your bill has been paid!  Returning to main account menu...");
	}

	return true;

    }


    private boolean payBill(double total, int type, int paid, String month, String year) throws SQLException{
	
	String error = null;
	String selection = null;
	String query = null;
	PreparedStatement pstate = null;
	
	/*
	  If the total is 0, do not pay the bill.  Else, see if the it is already paid.  If so,
	  exit.  If not, proceed with payment.
	*/
	if(total > 0){
	    
	    if(paid == 0){
		if(type == 1){			
		    query = "UPDATE billsplan1 set paid = 1 where account_number = ? and month = ? and year = ?";
		    }
		else if(type == 2){
		    query = "UPDATE billsplan2 set paid = 1 where account_number = ? and month = ? and year = ?";
		}
		else if(type == 3){
		    query = "UPDATE billsplan3 set paid = 1 where account_number = ? and month = ? and year = ?";
		}
		else{
		    
		}
	    }
	    else{
		System.out.printf("\nThis bill is already paid.  Returning to main account menu...");
		return false;
	    }
	}
	else{
	    System.out.printf("\nThis bill is $0.  Returning to main account menu...");
	    return false;
	}
	try{	    	
   	    
	    /*
	      Ask if the user wants to pay their bill.
	    */
	    System.out.printf("\n\nWould you like to pay the bill of $%.2f now? (y or n)", total);
	    System.out.printf("\n\nEnter the selection here: ");
	    error = "\nPlease enter a valid selection (y or n): ";
	    selection = Jog.verifyInput(error);
	    
	    while(true){
		
		if((selection = selection.toLowerCase()).compareTo("y") == 0){
		    break;
		    
		}
		if((selection = selection.toLowerCase()).compareTo("n") == 0){
		    return false;
		}
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);
	    }
	    
	    /*
	      Get the user's credit card number.
	    */

	    System.out.printf("\nEnter a credit card number: ");
	    error = "\nInvalid credit card number.  Please try again: ";
	    String cc_num = Jog.verifyInput(error);
	    int times = 1;

	    //3 attempts before the system shuts down.

	    while(true){
		if(Jog.check_cc_num(cc_num)){
		    break;
		}
		if(times == 3){
		    System.out.printf("\n\n3 incorrect attempts.  Returning to main menu.\n");
		    if(this.con != null){
			con.close();
		    }
		    return false;
		}
		times++;
		System.out.printf("%s", error);
		cc_num = Jog.verifyInput(error);
	    }
	    
	    /*
	      Execute the change to the database indicating the bill has been paid.
	    */

	    pstate = this.con.prepareStatement(query);	    
	    pstate.setString(1, this.accountNumber);
	    pstate.setString(2, month);
	    pstate.setString(3, year);

	    int ans = pstate.executeUpdate();

	    con.commit();

	    if(ans == 1){
		return true;
	    }
	    else{
		throw new SQLException();
	    }
	}
	catch(SQLException ex){
	    System.err.printf("\n\nInternal database error.  Please try again later.");
	    return false;
	}
	catch(Exception ex){
	    System.err.printf("\n\nInternal error.  Please try again later.");
	    return false;
	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	}
    }
}