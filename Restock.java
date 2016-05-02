import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

class Restock{

    private Connection con = null;
    private String store_id = null;

    public Restock(Connection con, String store_id){
	this.con = con;
	this.store_id = store_id;
    }
    
    public void doWork() throws SQLException{
	
	if(processRestock()){
	    System.out.printf("\nRestock request successfully processed.  Your order will be shipped to the store shortly");
	}
	else{
	    System.out.printf("\n\nError processing restock request.  Please try again later");
	
	}
	return;
    }

    /*
      Prints out the stock of phones at a store
     */
    private boolean processRestock() throws SQLException{

	String query = "SELECT manufacturer, model, quantity FROM inventory where STORE_ID = ?";
	PreparedStatement pstate = null;
	ResultSet result = null;
	List<String> phoneModels = new ArrayList<>();
	List<String> phoneManus = new ArrayList<>();
	List<String> phoneQuant = new ArrayList<>();
	int i = 0;
	try{
	    pstate = this.con.prepareStatement(query);
	    pstate.setString(1, this.store_id);
	    result = pstate.executeQuery();
	    System.out.printf("%s", Jog.separatorString);
	    System.out.printf("\n\nThe current stock of phones at store #%s:\n", this.store_id);
	    System.out.printf("\n%15s\t%15s\t%15s", "Manufacturer", "Model", "Quantity");

	    while(result.next()){

		System.out.printf("\n\n%15s\t%15s\t%15s", result.getString("manufacturer"), result.getString("model"), result.getString("quantity"));

		phoneModels.add(result.getString("model"));
		phoneManus.add(result.getString("manufacturer"));
		phoneQuant.add(result.getString("quantity"));
			       
		i++;
				
	    }

	    System.out.printf("\n\nWhich phone would you like to submit a restock request for?");

	    for(int y = 0; y < i; y++){
		System.out.printf("\nTo restock the %s %s, enter a %d.", phoneManus.get(y), phoneModels.get(y), y);
		

	    }
	    System.out.printf("\n\nEnter the selection here: ");
	    
	    String error = "\nPlease enter a valid selection (one of the numbers listed above): ";
	    String selection = Jog.verifyInput(error);
	    int select_Int = 0;
	    String model_selection = null;
	    String manu_selection = null;
	    
	    while(true){

		if(selection.length() == 1){
		    
		    if(Character.isDigit(selection.charAt(0))){
			
			select_Int = Character.getNumericValue(selection.charAt(0));
		    
			if(select_Int < phoneModels.size()){

			    model_selection = phoneModels.get(select_Int);
			    manu_selection = phoneManus.get(select_Int);
			    break;
		    
			}
		    }
		}
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);
	    }
	    int selectionQuantity = Integer.parseInt(phoneQuant.get(select_Int));
	    int maxQuantity = 999 - selectionQuantity;
	    System.out.printf("%s", Jog.separatorString);

	    /*
	      Now, get the quantity of the restock. It must be less than or equal to 999 - the current quantity
	    */
	    
	    
	    System.out.printf("\nYou have selected to restock the %s %s.", manu_selection, model_selection);

	    System.out.printf("\n\nHow many phones would you like to order (%d or less)? ", maxQuantity);
	    
	    error = "\nPlease enter a valid selection (less than the limit mentioned above): ";
	    selection = Jog.verifyInput(error);
	    int selection_to_int = 0;

	    while(true){

		try{

		    selection_to_int = Integer.parseInt(selection);
		
		    if(selection_to_int <= maxQuantity){
			break;
		    }
		}
		catch(NumberFormatException ex){}		   
		
		System.out.printf("%s", error);
		selection = Jog.verifyInput(error);
		
	    }

	    /*
	      
	      Send the phone model, manufacturer and quanity to the Jog database for restocking.

	     */
	    CallableStatement cstate = null;
	    cstate = this.con.prepareCall("{? = call restockRequest(?,?,?,?)}");;
	    cstate.registerOutParameter(1, Types.INTEGER);
	    cstate.setString(2, store_id);
	    cstate.setString(3, model_selection);
	    cstate.setString(4, manu_selection);
	    cstate.setInt(5, selection_to_int);
	    cstate.execute();

	    if(cstate.getInt(1) == 1){
		return true;
	    }
	    else{
		return false;
	    }
	}
	catch(SQLException ex){
	    System.out.printf("\n\nError connecting to Jog database.  Please try again later");
	    ex.printStackTrace();
	    return false;
	}
	finally{
	    if(pstate != null){
		pstate.close();
	    }
	    if(result != null){
		result.close();
	    }
	}
    }

}