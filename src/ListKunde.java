import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListKunde extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTable table;//table to put Kunde information in

	private DefaultTableModel tableModel = null;//cells(row and column) for table
	private JScrollPane scroll = null;//to put table

	private JLabel titel = new JLabel("List of Members");
	private JTextField searchText = new JTextField();//entry field for search query of a text
	private JLabel searchLabel= new JLabel("Name of Member");
	
	private int postX=0;
	private int postY=0;

	//constructor of ListKunde
	public ListKunde() {
		String[] columnNames = { "Kundennummer", "Name", "Klasse", "AktuellAusgelieheneBuecher"};

		//DefaultTableModel model2 = new DefaultTableModel();
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(columnNames);

		table = new JTable();
		table.setModel(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFillsViewportHeight(true);

		scroll = new JScrollPane(table);
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
		searchText.setBounds(postX+110, postY, 150, 25);

		searchText.addKeyListener(new KeyAdapter() 
		{
			public void keyReleased(KeyEvent e)
			{
				displayInfo(searchText.getText());
			}
		 });
	}
	
	public void setWidthText(int w) 
	{
		searchText.setSize(w, 25);
	}
	
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
	public void viewListKunde()
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
			// check if "kunden" table is there
			ResultSet tables = dbm.getTables(null, null, "kunden", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String workTable = "kunden";// name of working table

				String sql;
				sql = "SELECT kundennummer, name, klasse, aktuellAusgelieheneBueche FROM " + workTable;
				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					System.out.print("Kundennummer: " + rs.getInt("kundennummer"));
					System.out.print(", Name: " + rs.getString("name"));
					System.out.print(", Klasse: " + rs.getString("klasse"));
					System.out.print(", AktuellAusgelieheneBuecher: " + rs.getInt("aktuellAusgelieheneBueche"));
					System.out.println();
					
					Object[] newRowData = { rs.getInt("kundennummer"), rs.getString("name"), rs.getString("klasse"),
							rs.getInt("aktuellAusgelieheneBueche")};
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

	//To Display list of member based on search query text 'name'
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
			// check if "kunden" table is there
			ResultSet tables = dbm.getTables(null, null, "kunden", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String sql;
				sql = "select *, name from kunden where name like '%" + s + "%'";
				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					System.out.print("kundennummer: " + rs.getInt("kundennummer"));
					System.out.print(", Name: " + rs.getString("name"));
					System.out.print(", Klasse: " + rs.getString("klasse"));
					System.out.print(", AktuellAusgelieheneBueche: " + rs.getString("aktuellAusgelieheneBueche"));
					System.out.println();

					Object[] newRowData = { rs.getInt("kundennummer"), rs.getString("name"), rs.getString("klasse"),
							rs.getInt("aktuellAusgelieheneBueche") };
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