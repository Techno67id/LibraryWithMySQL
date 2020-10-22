import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AusleiheBuch extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel panelAusleihen = new JPanel();//main panel to add a new Ausleihen
	private JPanel panelStatusKunde = new JPanel();//panel to display status of one Kunde
	private JPanel panelStatusBuch = new JPanel();//panel to display status of a book
	private ListAusleihe listausleihe=new ListAusleihe();//panel to display all Order of Ausleihen

	private JTextField[] infoText = new JTextField[2];//entry field for Book's Ausleihen
	private JLabel[] infoLabel = new JLabel[2];//label to guide user to enter Book's Ausleihen
	private String[] infoString = { "Kundennummer", "ID Book"};

	private JButton proceedBtn = new JButton("Proceed Ausleihen");
	private JButton previewBtn = new JButton("Check Status");

	private final int MAXBUCH=5;//Maximum number of Books that can be borrowed by Kunde
	boolean proceed = true;
	private int kundeAktuellAusgelieheneBuecher=0;
	private int availableBuch;//variable for available books in Shelf/Regal
	
	JTextArea infoStatusBuch = new JTextArea();//To display Current Status of a book
	JTextArea infoStatusKunde = new JTextArea();//To display Current Status of one Kunde
	JLabel imgLab = new JLabel();//to post Kundenbild
	
	//Getting Screen Size, to posting and dimensioning components on panel
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	private int windowWidth = (int)(screenSize.width*0.75); //Application take only 75% of full screen

	private JScrollPane scrollKunde = null;//to put text of 'InfoStatusKunde'
	private JScrollPane scrollBuch = null;//to put text of 'InfoStatusBuch'
	private JLabel labelInfoBuch = new JLabel("Info Buch");
	private JLabel labelInfoKunde = new JLabel("Info Kunde");

	public AusleiheBuch() {
		setLayout(new BorderLayout(0,0));

		for (int i = 0; i < infoLabel.length; i++) {
			panelAusleihen.add(infoLabel[i] = new JLabel(infoString[i]));
			infoLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			panelAusleihen.add(infoText[i] = new JTextField(""));
		}

		panelAusleihen.add(proceedBtn);
		panelAusleihen.add(previewBtn);
		panelStatusBuch.setVisible(false);
		panelStatusKunde.setVisible(false);

		panelAusleihen.setLayout(null);
		panelStatusBuch.setLayout(new BorderLayout(5,5));
		panelStatusKunde.setLayout(new BorderLayout(5,5));

		infoLabel[0].setBounds(20, 20, 100, 25);
		infoLabel[1].setBounds(20, 50, 100, 25);

		infoText[0].setBounds(200, 20, 300, 25);
		infoText[1].setBounds(200, 50, 300, 25);

		proceedBtn.setBounds(200, 120, 300, 25);
		previewBtn.setBounds(550, 35, 150, 25);

		proceedBtn.setEnabled(false);
		
		scrollBuch = new JScrollPane(infoStatusBuch,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollKunde = new JScrollPane(infoStatusKunde,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		imgLab.setOpaque(true);
		imgLab.setBackground(Color.WHITE);
		imgLab.setHorizontalAlignment(JLabel.CENTER);
		imgLab.setVerticalAlignment(JLabel.CENTER);

		
		panelStatusBuch.add(labelInfoBuch,BorderLayout.PAGE_START);
		scrollBuch.setPreferredSize(new Dimension(250, 250));
		panelStatusBuch.add(scrollBuch,BorderLayout.CENTER);
		panelStatusKunde.add(labelInfoKunde,BorderLayout.PAGE_START);
		scrollKunde.setPreferredSize(new Dimension(250, 250));
		panelStatusKunde.add(scrollKunde,BorderLayout.LINE_START);
		panelStatusKunde.add(imgLab,BorderLayout.CENTER);
		panelStatusBuch.setVisible(false);
		panelStatusKunde.setVisible(false);
		
		panelAusleihen.setPreferredSize(new Dimension(windowWidth-250-40, 230));
		listausleihe.setPreferredSize(new Dimension(windowWidth-250-40, windowHeight - 230-80));
		
		add(panelAusleihen,BorderLayout.PAGE_START);
		add(panelStatusBuch,BorderLayout.LINE_START);
		add(panelStatusKunde,BorderLayout.CENTER);
		add(listausleihe,BorderLayout.LINE_END);
		listausleihe.viewListAusleihen();//to refresh table Ausleihen//?this can be removed, because not needed at startup the program
		listausleihe.clearField();//?this can be removed, because not needed at startup the program

		//Call getStatus : to display status of a book and member
		previewBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				infoStatusBuch.setText("");
				infoStatusKunde.setText("");
				imgLab.setText("");
				getStatus();
//				listausleihe.setVisible(false);
//				panelStatusBuch.setVisible(true);
//				panelStatusKunde.setVisible(true);
			}
		});

		//Call proceedAusleihe : to proceed Ausleihen
		proceedBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				proceedAusleihe();
				listausleihe.viewListAusleihen();
				listausleihe.setVisible(true);
				infoText[0].setText("");
				infoText[1].setText("");
				infoStatusBuch.setText("");
				infoStatusKunde.setText("");
				imgLab.setText("");
				proceedBtn.setEnabled(false);
			}
		});
}

	//to display status of a book and member
	public void getStatus() {
		String kundenNummer = infoText[0].getText();
		String buchid = infoText[1].getText();

		if (kundenNummer.equals("") || buchid.equals("")) {
			System.out.println("Cancel input fur die Ausleihenbuch!");
			JOptionPane.showMessageDialog(null, "Information von Kunde/Buch nicht gefunden!");
			proceed = false;
		} else {
			getKundenStatus(kundenNummer);
			getBuchStatus(buchid);
			System.out.println("kunde aktuellAusgelieheneBuecher: "+kundeAktuellAusgelieheneBuecher);
			System.out.println("buch available: "+availableBuch);
		}

		//proceed "true" : proceed an Order of Borrowing a book  
		if (proceed) {
			if (kundeAktuellAusgelieheneBuecher < MAXBUCH) {
				if (availableBuch>0) {
					proceedBtn.setEnabled(true);
					listausleihe.setVisible(false);
					panelStatusBuch.setVisible(true);
					panelStatusKunde.setVisible(true);
				} else {
					listausleihe.setVisible(true);
					panelStatusBuch.setVisible(false);
					panelStatusKunde.setVisible(false);
					JOptionPane.showMessageDialog(null, "Buch ausgeliehen or kein Buch/Kunde gefunden");
				}
			} else {
				listausleihe.setVisible(true);
				panelStatusBuch.setVisible(false);
				panelStatusKunde.setVisible(false);
				JOptionPane.showMessageDialog(null, "Max Buch der Kunde ausgeliehen hat ist erreicht");
			}
		}
	}

	// Method to proceed Ausleihen
	public void proceedAusleihe() {
		updateBuch();//Update datebase Buch, specially for available book is to be subtracted 1 
		updateKunde();//Update datebase Kunde, specially for Number of borrowed Book is to be added 1
		addAusleihe();// Add new Order of Ausleihen
		System.out.println("Ausleihe added!");
		listausleihe.setVisible(true);
		panelStatusBuch.setVisible(false);
		panelStatusKunde.setVisible(false);
		listausleihe.viewListAusleihen();
		proceedBtn.setEnabled(false);
	}

	//Update datebase Buch, specially for available book is to be subtracted 1 (SQL - Server)
	public void updateBuch() {
		String[] elm = {infoText[1].getText()};
		UpdateDB updateDB = new UpdateDB();
		updateDB.setToDB(elm, "buecher", "available");//reference col(control),table name, update col
		availableBuch = availableBuch - 1;
		updateDB.updateDB("idbook",availableBuch);//update col, new status
	}

	//Update datebase Kunde, specially for Number of borrowed Book is to be added 1
	public void updateKunde() {
		String[] elm = {infoText[0].getText()};
		UpdateDB updateDB = new UpdateDB();
		updateDB.setToDB(elm, "kunden", "aktuellAusgelieheneBueche");;//reference col(control),table name, update col
		kundeAktuellAusgelieheneBuecher = kundeAktuellAusgelieheneBuecher + 1;
		updateDB.updateDB("kundennummer",kundeAktuellAusgelieheneBuecher);//update col, new status
	}

	//To get Current Status of one Kunde
	public void getKundenStatus(String s) {
		int kn = Integer.valueOf(s);

		Connection con = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();
			
			sqlExec(con, "use library1");
			
			String workTable="kunden";
			
			String sql = "SELECT * FROM " + workTable;
			ResultSet rs = stmt.executeQuery(sql);
			
			File file=new File("tmp.png");
			FileOutputStream fos=new FileOutputStream(file);
			byte b[];
			Blob blob;

			String info="\n";
			while (rs.next()) {
				if (kn == rs.getInt("kundennummer")) {
					info= info+"Kundennummer: " + rs.getInt("kundennummer")+"\n";
					info= info+"Name: " + rs.getString("name")+"\n";
					info= info+"Klasse: " + rs.getString("klasse")+"\n";
					info= info+"AktuellAusgelieheneBuecher: " + rs.getInt("aktuellAusgelieheneBueche")+"\n";

					kundeAktuellAusgelieheneBuecher = rs.getInt("aktuellAusgelieheneBueche");
					info= info+"\n";
					blob=rs.getBlob("image");
					b=blob.getBytes(1,(int)blob.length());
					fos.write(b);
					
					ImageIcon ii = new ImageIcon("tmp.png");
					
					
					int width = ii.getIconWidth();
					int height = ii.getIconHeight();

					System.out.println("width: "+width);
					System.out.println("height: "+height);
					System.out.println("label.width: "+imgLab.getWidth());
					System.out.println("label.height: "+imgLab.getHeight());
					
					double scaleWidth = (double) Math.abs(imgLab.getWidth())/width; 
					double scaleHeight = (double) Math.abs(imgLab.getHeight())/height; 
					
					System.out.println("scale Width: "+scaleWidth);
					System.out.println("scale Height: "+scaleHeight);
					
					double minScale = Math.max(scaleWidth, scaleHeight)*0.5;
					
					ii.setImage(ii.getImage().getScaledInstance((int)(width*minScale), (int)(height*minScale), Image.SCALE_SMOOTH));
					
					imgLab.setIcon(ii);
					imgLab.setHorizontalAlignment(JLabel.CENTER);
					imgLab.setVerticalAlignment(JLabel.CENTER);
				}
			}
			
			infoStatusKunde.append(info);
			
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//To get Current Status of one Book/Kunde
	public String getInfo(String s) {
		int idbook = Integer.valueOf(s);
		String info="\n";

		Connection con = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();
		
			sqlExec(con, "use library1");
			
			String workTable="buecher";
			
			String sql = "SELECT * FROM " + workTable;
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				if (idbook == rs.getInt("idbook")) {
					info= info+"Buch ID: " + rs.getInt("idbook")+"\n";
					info= info+"Titel: " + rs.getString("title")+"\n";
					info= info+"Author: " + rs.getString("author")+"\n";
					info= info+"Verlag: " + rs.getString("verlag")+"\n";
					info= info+"ISBN: " + rs.getString("isbn")+"\n";
					info= info+"Thema: " + rs.getString("thema")+"\n";
					info= info+"Copy: " + rs.getInt("exemplare")+"\n";
					info= info+"Available: " + rs.getInt("available")+"\n";
					availableBuch=rs.getInt("available");
				}
			}
			
//			infoStatusBuch.append(info);
			
			rs.close();

			stmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return info;
	}

	public void getBuchStatus(String s) {
		int idbook = Integer.valueOf(s);

		Connection con = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();
		
			sqlExec(con, "use library1");
			
			String workTable="buecher";
			
			String sql = "SELECT * FROM " + workTable;
			ResultSet rs = stmt.executeQuery(sql);

			
			String info="\n";
			while (rs.next()) {
				if (idbook == rs.getInt("idbook")) {
					info= info+"Buch ID: " + rs.getInt("idbook")+"\n";
					info= info+"Titel: " + rs.getString("title")+"\n";
					info= info+"Author: " + rs.getString("author")+"\n";
					info= info+"Verlag: " + rs.getString("verlag")+"\n";
					info= info+"ISBN: " + rs.getString("isbn")+"\n";
					info= info+"Thema: " + rs.getString("thema")+"\n";
					info= info+"Copy: " + rs.getInt("exemplare")+"\n";
					info= info+"Available: " + rs.getInt("available")+"\n";
					availableBuch=rs.getInt("available");
				}
			}
			
			infoStatusBuch.append(info);
			
			rs.close();

			stmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// Add new Order of Ausleihen
	public void addAusleihe() 
	{
		String[] elm = {infoText[0].getText(),infoText[1].getText()};
		
		UpdateDB updateDB = new UpdateDB();
		updateDB.setToDB(elm, "ausleihen", "ausleiheID");
		updateDB.addToDB();
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

	public String toString(Date d) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = String.format(dateFormat.format(d));
		return date;
	}
	
	//method to clear entry field
	public void clearField() {
		infoStatusBuch.setText("");
		infoStatusKunde.setText("");
		imgLab.setText("");
		for (int i = 0; i < infoText.length; i++) {
			infoText[i].setText(null);
		}
		proceedBtn.setEnabled(false);
		listausleihe.clearField();
		listausleihe.viewListAusleihen();
	}
}
