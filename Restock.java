import java.io.*;
import java.sql.*;

class Restock{

    private Connection con = null;
    private String store_id = null;

    public Restock(Connection con, String store_id){
	this.con = con;
	this.store_id = store_id;
    }
    
    public void doWork() throws SQLException{


    }

}