import java.io.*;
import java.sql.*;

class Test{
    

    public static void main(String [] args) throws SQLException, java.lang.ClassNotFoundException{
	
	Class.forName("oracle.jdbc.driver.OracleDriver");

	Connection con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", "mah318", "max!95rox");

	ExistCustomerSys sys = new ExistCustomerSys();

	sys.doWork(con);
	
	con.close();
    }
}