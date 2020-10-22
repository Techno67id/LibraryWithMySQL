import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddBuch extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel buch = new JPanel();//main panel to add a new book 
	private JTextField[] infoText = new JTextField[6];//entry field for Book's information 
	private JLabel[] infoLabel = new JLabel[6];//label to guide user to enter Book's information
	private int copy=0;//variable for number of books
	private int available=0;//variable for available books in Shelf/Regal

	private String[] infoString = { "Titel", "Autor", "Verlag", "ISBN","Thema","Number of Copy"};

	private JButton addBookBtn = new JButton("Add Book to Database");
	private int id_zaehler = 100;//start number for Book identification number

	//Getting Screen Size, to posting and dimensioning components on panel
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// get information of screen size 
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	private int windowWidth = (int)(screenSize.width*0.75); //Application take only 75% of full screen
	private ListBooks2 listbook=new ListBooks2();//panel to display available book

	//constructor of AddBuch
	public AddBuch() {
		setLayout(new BorderLayout(0,0));
		System.out.println("#window height: "+windowHeight);
		System.out.println("#window width: "+windowWidth);
		
		for (int i = 0; i < infoLabel.length; i++) {
			buch.add(infoLabel[i] = new JLabel(infoString[i]));
			infoLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			buch.add(infoText[i] = new JTextField());
		}

		buch.setLayout(null);
		buch.add(addBookBtn);

		infoLabel[0].setBounds(20, 20, 100, 25);
		infoLabel[1].setBounds(20, 50, 100, 25);
		infoLabel[2].setBounds(20, 80, 100, 25);
		infoLabel[3].setBounds(20, 110, 100, 25);
		infoLabel[4].setBounds(20, 140, 100, 25);
		infoLabel[5].setBounds(20, 170, 100, 25);

		infoText[0].setBounds(200, 20, 300, 25);
		infoText[1].setBounds(200, 50, 300, 25);
		infoText[2].setBounds(200, 80, 300, 25);
		infoText[3].setBounds(200, 110, 300, 25);
		infoText[4].setBounds(200, 140, 300, 25);
		infoText[5].setBounds(200, 170, 300, 25);
		addBookBtn.setBounds(550, 95, 170, 25);
		
		buch.setPreferredSize(new Dimension(windowWidth-290, 230));
		listbook.setPreferredSize(new Dimension(windowWidth-290, windowHeight - 250));
		
		add(buch,BorderLayout.PAGE_START);
		add(listbook,BorderLayout.LINE_END);
		listbook.clearField();//to refresh table ?this can be removed, because not needed at startup the program
		listbook.viewListBook();//?this can be removed, because not needed at startup the program

		addBookBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean isComplete = true;
				for (int i = 0; i < infoText.length; i++) {
					if (infoText[i].getText().equals(""))
					{isComplete=false;}
				}
				if (isComplete) 
				{
					writeToDB();
					
					clearField();//after write to database, field is cleared
					listbook.clearField();
					listbook.viewListBook();
					System.out.println("New Book added!");
				} else
				{
					System.out.println("Cancel input for Book!");
					JOptionPane.showMessageDialog(null, "Information von Book nicht ausreichen!");
				}
			}
		});
	}

	//method to clear entry field
	public void clearField() {
		for (int i = 0; i < infoText.length; i++) {
			infoText[i].setText(null);
		}
		listbook.clearField();
		listbook.viewListBook();
	}

	//write new book to database
	public void writeToDB()
	{
		String[] elm = {infoText[0].getText(),infoText[1].getText(),infoText[2].getText(),
				infoText[3].getText(),infoText[4].getText(),infoText[5].getText()
		};
		
		UpdateDB updateDB = new UpdateDB();
		updateDB.setToDB(elm, "buecher", "idbook");
		updateDB.addToDB();
	}

}
