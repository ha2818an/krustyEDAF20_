

import krusty.Database;

public class DatabaseTest {
	public static void main(String args[]) { 
	  Database db = new Database(); 
	  db.connect(); 
	  System.out.println(db.getCustomers(null, null)); 
	} 
  }