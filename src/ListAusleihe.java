import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListAusleihe extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTable table;

	DefaultTableModel tableModel = null;//cells(row and column) for table
	JScrollPane scroll = null;//to put table

	private JLabel titel = new JLabel("Liste Ausleihen Buch");
	private JTextField searchText = new JTextField();//entry field for search query of a text
	private JLabel searchLabel= new JLabel("Kundennummer");

	GridBagConstraints c = new GridBagConstraints();
	
	//Getting Screen Size, to posting and dimensioning components on panel
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// get information of screen size 
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	
	public ListAusleihe() {
		String[] columnNames = { "AusleiheID", "ID Book", "Kundennummer", "Datum Ausleihe","Datum Ruckgabe"};

		setLayout(new GridBagLayout());
        setLayout(new GridBagLayout());
 		
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(columnNames);

		//to format column 'return date'/'ruckgabedatum' with color
		// remaining days<=1 -> RED
		// 1<remaining days<=3 -> YELLOW
		// remaining days>3 -> GREEN
		
		table = new JTable(tableModel) {
				public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int columnIndex) {
					JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);

					String date= ""+getValueAt(rowIndex, 4);
					System.out.println("Date: "+date);

					long rest = getRestDay(date);
					System.out.println("Rest: "+rest);
					
					component.setBackground(Color.WHITE);//reset to be "WHITE" if there are already used (RED/YELLOW/GREEN)
					if (columnIndex == 4 && rest<=1) 
					{
						component.setBackground(Color.RED);
					} 
					else if (columnIndex == 4 && rest<=3 && rest>1)
					{
						component.setBackground(Color.YELLOW);
					}
					else if (columnIndex == 4 && rest>3)
					{
						component.setBackground(Color.GREEN);
					}

					return component;
				}
			};

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

	// to display all Order of Borrowing Book
	public void viewListAusleihen() 
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
			// check if "ausleihen" table is there
			ResultSet tables = dbm.getTables(null, null, "ausleihen", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String workTable = "ausleihen";// name of working table

				String sql;
				sql = "SELECT * FROM " + workTable;
				ResultSet rs = stmt.executeQuery(sql);
				
				while (rs.next()) {
					System.out.print("AusleiheID: " + rs.getInt("ausleiheID"));
					System.out.print(", ID Book: " + rs.getInt("idbook"));
					System.out.print(", Kundennummer: " + rs.getInt("kundennummer"));
					System.out.print(", Datum Ausleihe: " + rs.getDate("datumAusleihe"));
					System.out.print(", Datum Ruckgabe: " + rs.getDate("datumRueckgabe"));
					System.out.println();
					
					Object[] newRowData = { rs.getInt("ausleiheID"), rs.getInt("idbook"), rs.getInt("kundennummer"),
							rs.getDate("datumAusleihe"),rs.getDate("datumRueckgabe")};
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

	//To Display list of Borrowing books based on search query text 'kunndennumer'
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
			// check if "employee" table is there
			ResultSet tables = dbm.getTables(null, null, "ausleihen", null);

			// if table exists, do ...
			if (tables.next()) {

				System.out.println("Creating statement...");
				stmt = con.createStatement();

				String sql;
				sql = "select *, kundennummer from ausleihen where kundennummer like '%" + s + "%'";
				ResultSet rs = stmt.executeQuery(sql);

				// get the last idbook, preparing for next new idbook
				while (rs.next()) {
					System.out.print("AusleiheID: " + rs.getInt("ausleiheID"));
					System.out.print(", ID Book: " + rs.getInt("idbook"));
					System.out.print(", Kundennummer: " + rs.getInt("kundennummer"));
					System.out.print(", Datum Ausleihe: " + rs.getDate("datumAusleihe"));
					System.out.print(", Datum Ruckgabe: " + rs.getDate("datumRueckgabe"));
					System.out.println();

					Object[] newRowData = { rs.getInt("ausleiheID"), rs.getInt("idbook"), rs.getInt("kundennummer"),
							rs.getDate("datumAusleihe"), rs.getDate("datumRueckgabe") };
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

	//to get remaining days to return of the book
	public long getRestDay(String date) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date returnDate = null;
		try {
			returnDate = format1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("return date: " + returnDate);
		Date currentDate = new Date();

		long diffInMillies = returnDate.getTime() - currentDate.getTime();
		return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	
	public void clearField() {
		searchText.setText("");
	}
}