import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListBooks extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTable table;//table to put books information in

	private DefaultTableModel tableModel = null;//cells(row and column) for table
	private JScrollPane scroll = null;//to put table
	
	private JLabel titel = new JLabel("List of Books");
	private JTextField searchText = new JTextField();//entry field for search query of a text
	private JLabel searchLabel= new JLabel("Search Book: (Titel)");
	
	private int postX=0;
	private int postY=0;
	private int initWidth=100;
	
	//constructor of listBooks
	public ListBooks() {

		String[] columnNames = { "ID Book", "Titel", "Author", "Verlag", "ISBN","Thema", "Copy", "Availability" };

		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(columnNames);

		table = new JTable();
		scroll = new JScrollPane(table);
		table.setModel(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFillsViewportHeight(true);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		scroll.getViewport().add(table);

		setLayout(null);

		add(titel);
		add(scroll);
		add(searchLabel);
		add(searchText);

		titel.setSize(100, 25);
		
		scroll.setSize(400, 200);
		scroll.setLocation(5, 5);
		searchLabel.setBounds(postX, postY, 120, 25);
		searchText.setBounds(postX+110, postY, initWidth, 25);
		
		searchText.addKeyListener(new KeyAdapter() 
		{
			public void keyReleased(KeyEvent e)
			{
				displayInfo(searchText.getText());
			}
		 });
	}
	
	//setup width of entry field of search text
	public void setWidthText(int w) 
	{
		searchText.setSize(w, 25);
	}
	
	//setup position of the panel and components
	public void setPosSize(int w,int h) 
	{
		scroll.setSize(w, h-10);
		scroll.setLocation(5, 20);
		titel.setLocation(w/2-100, 0);
		searchText.setSize(w-130, 25);
		searchLabel.setLocation(5, h+25);
		searchText.setLocation(130, h+25);
	}
	
	//To Display list of all books
	public void viewListBook()
	{
		tableModel.setRowCount(0);// Clear old viewing book list
		Connection con = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();
			
			sqlExec(con, "use library1");

			DatabaseMetaData dbm = con.getMetaData();
			// check if "employee" table is there
			ResultSet tables = dbm.getTables(null, null, "buecher", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String workTable = "buecher";// name of working table

				String sql;
				sql = "SELECT idbook, title, author, verlag,isbn,thema,exemplare,available FROM " + workTable;
				ResultSet rs = stmt.executeQuery(sql);

				// get the last idbook, preparing for next new idbook
				while (rs.next()) {
					System.out.print("ID: " + rs.getInt("idbook"));
					System.out.print(", Titel: " + rs.getString("title"));
					System.out.print(", Author: " + rs.getString("author"));
					System.out.print(", Verlag: " + rs.getString("verlag"));
					System.out.print(", ISBN: " + rs.getString("isbn"));
					System.out.print(", Thema: " + rs.getString("thema"));
					System.out.print(", Copy: " + rs.getInt("exemplare"));
					System.out.print(", Available: " + rs.getInt("available"));
					System.out.println();

					Object[] newRowData = { rs.getInt("idbook"), rs.getString("title"), rs.getString("author"),
							rs.getString("verlag"), rs.getString("isbn"), rs.getString("thema"), rs.getInt("exemplare"),
							rs.getInt("available") };
					tableModel.addRow(newRowData);

				}

				rs.close();
				stmt.close();
				con.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//SQL-Kommando ohne Rückgabewert
	public void sqlExec(Connection c, String sql) 
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

	//To Display list of books based on search query text
	public void displayInfo(String s) {
		Connection con = null;
		Statement stmt = null;
		tableModel.setRowCount(0);// Clear old viewing book list

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();

			sqlExec(con, "use library1");

			DatabaseMetaData dbm = con.getMetaData();
			// check if "buecher" table is available
			ResultSet tables = dbm.getTables(null, null, "buecher", null);

			// if table exists, do ...
			if (tables.next()) {

				// STEP 4: Execute a query
				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String sql;
				sql = "select *, title from buecher where title like '%" + s + "%'";
				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					System.out.print("ID: " + rs.getInt("idbook"));
					System.out.print(", Titel: " + rs.getString("title"));
					System.out.print(", Author: " + rs.getString("author"));
					System.out.print(", Verlag: " + rs.getString("verlag"));
					System.out.print(", ISBN: " + rs.getString("isbn"));
					System.out.print(", Thema: " + rs.getString("thema"));
					System.out.print(", Copy: " + rs.getInt("exemplare"));
					System.out.print(", Available: " + rs.getInt("available"));
					System.out.println();

					Object[] newRowData = { rs.getInt("idbook"), rs.getString("title"), rs.getString("author"),
							rs.getString("verlag"), rs.getString("isbn"), rs.getString("thema"), rs.getInt("exemplare"),
							rs.getInt("available") };
					tableModel.addRow(newRowData);
				}
				rs.close();
				stmt.close();
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
	}

	public void clearField() {
		searchText.setText("");
	}
}