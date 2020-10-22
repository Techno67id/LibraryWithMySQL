import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListKunde2 extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;//table to put Kunde information

	DefaultTableModel tableModel = null;
	JScrollPane scroll = null;//to put table

	private JLabel titel = new JLabel("List of Members");
	private JTextField searchText = new JTextField();
	private JLabel searchLabel= new JLabel("Name of Member");

	GridBagConstraints c = new GridBagConstraints();

	//Getting Screen Size, to posting and dimensioning components on panel
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// get information of screen size 
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	
	//constructor of ListKunde2
	public ListKunde2() {
		String[] columnNames = { "Kundennummer", "Name", "Klasse", "AktuellAusgelieheneBuecher"};

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

	//To Display list of all Kunden
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

	//To Display list of books based on search query text 'name'
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
	    
	public void setPos() 
	{
		c.weightx = 1.0;

		scroll.getViewport().add(table);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 6;
		c.gridheight = 1; 
		c.ipady = 0;// reset
		c.gridwidth = 1; 
		c.gridx = 2;
		c.gridy = 0;// first row
		add(titel, c);

		c.gridwidth = 6;
		c.ipady = windowHeight - 370;// make this component tall
		c.gridx = 0;
		c.gridy = 1;// second row
		add(scroll, c);

		c.ipady = 0;// reset
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