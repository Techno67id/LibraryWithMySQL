import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;

public class checkDB
{
	public checkDB() {
		ArrayList<String> list = new ArrayList<String>();
		String database = "library1";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			Statement stmt = con.createStatement();
			
			DatabaseMetaData meta = con.getMetaData();
			// check if "library1" Database is there
			ResultSet rs = meta.getCatalogs();
			while (rs.next()) {
				String listofDatabases = rs.getString("TABLE_CAT");
				list.add(listofDatabases);
			}
			
			if (list.contains(database)) {
				System.out.println("Database already exists");
			} else {
				stmt.executeUpdate("CREATE DATABASE " + database);
				System.out.println("Database is created");

				sqlExec(con, "use library1");
				sqlExec(con, "create table kunden(kundennummer int, name text, klasse text, aktuellAusgelieheneBueche int,image longblob)");
				sqlExec(con, "create table buecher(idbook int, title text,author text, verlag text, isbn text, thema text, exemplare int, available int)");
				sqlExec(con, "create table ausleihen(ausleiheID int, idbook int,kundennummer int, datumAusleihe date, datumRueckgabe date)");
			}
			stmt.close();
			con.close();
			rs.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void sqlExec(Connection c, String sql) //SQL-Kommando ohne Rueckgabewert
	{
		try
		{
			Statement stmt = c.createStatement();
		    stmt.executeUpdate(sql);	    
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
