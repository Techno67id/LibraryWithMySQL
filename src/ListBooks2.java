import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListBooks2 extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;//table to put books information

	DefaultTableModel tableModel = null;//cells(row and column) for table
	JScrollPane scroll = null;//to put table
	
	private JLabel titel = new JLabel("List of Books");
	private JTextField searchText = new JTextField();//entry field for search query of a text
	private JLabel searchLabel= new JLabel("Search Book: (Titel)");

	GridBagConstraints c = new GridBagConstraints();

	//Getting Screen Size, to posting and dimensioning components on panel
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// get information of screen size 
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen

	//constructor of ListBooks2
	public ListBooks2() {
		String[] columnNames = { "ID Book", "Titel", "Author", "Verlag", "ISBN","Thema", "Copy", "Availability" };

		setLayout(new GridBagLayout());
        setLayout(new GridBagLayout());
 
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(columnNames);

		table = new JTable();
		scroll = new JScrollPane(table);
		table.setModel(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFillsViewportHeight(true);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		setPos();//to configure components on the panel
		
		searchText.addKeyListener(new KeyAdapter() 
		{
			public void keyReleased(KeyEvent e)
			{
				displayInfo(searchText.getText());
			}
		 });
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
			// check if "buecher" table is there
			ResultSet tables = dbm.getTables(null, null, "buecher", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String workTable = "buecher";// name of working table

				String sql;
				sql = "SELECT idbook, title, author, verlag,isbn,thema,exemplare,available FROM " + workTable;
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
	
	//To Display list of books based on search query text 'title'
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
			// check if "buecher" table is there
			ResultSet tables = dbm.getTables(null, null, "buecher", null);

			// if table exists, do ...
			if (tables.next()) {

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
	
	//to configure components on the panel
	public void setPos()
	{
        c.weightx = 1.0;

        scroll.getViewport().add(table);
	    c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 6;
		c.gridheight = 1; 
		c.ipady = 0;//reset
		c.gridwidth = 1; 
		c.gridx = 2;
		c.gridy = 0;// first row
		add(titel, c);
		
		c.gridwidth = 6;
		c.ipady = windowHeight-370;//make this component tall
		c.gridx = 0;
		c.gridy = 1;// second row
		add(scroll, c);

		c.ipady = 0;//reset
		c.weightx = 0.07;
		c.gridx = 0;
 		c.gridwidth = 2;
		c.gridy = 2;// third row
		add(searchLabel, c);

		c.gridx = 2;
 		c.gridwidth = 3;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridy = 2;// third row
		add(searchText, c);
	}
	
	public void clearField() {
		searchText.setText("");
	}
}