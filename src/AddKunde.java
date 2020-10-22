import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AddKunde extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel kunde = new JPanel();//main panel to add a new Kunde

	private JTextField[] infoText = new JTextField[3];//entry field for Kunde's information
	private JLabel[] infoLabel = new JLabel[3];//label to guide user to enter Kunde's information
	private JLabel imageKunde = new JLabel();//to post Kundenbild
	private String[] infoString = { "Kundenname", "Klasse/Seminargruppe","Upload Kundenbild"};

	private JButton addKundeBtn = new JButton("Add Kunde to Database");
	private JButton addBildKundeBtn = new JButton("...");
	private int kundennummer_zaehler=1000;//start number for Kunde identification number

	//Getting Screen Size, to posting and dimensioning components on panel
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	private int windowWidth = (int)(screenSize.width*0.75); //Application take only 75% of full screen
	private ListKunde2 listkunde=new ListKunde2();//panel to display list of Kunde
	private boolean do_debug=false;
	private String imageFile="";//variable for path of file image of Kunde


	//constructor of AddKunde
	public AddKunde() {
		setLayout(new BorderLayout(0,0));

		for (int i = 0; i < infoLabel.length; i++) {
			kunde.add(infoLabel[i] = new JLabel(infoString[i]));
			infoLabel[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			kunde.add(infoText[i] = new JTextField());
		}

		infoText[2].setEnabled(false);//this field is filled-out that using FileChooser (by clicking addBildKundeBtn)
		
		kunde.setLayout(null);
		kunde.add(addKundeBtn);
		kunde.add(addBildKundeBtn);
		kunde.add(imageKunde);
		imageKunde.setBounds(10, 120, 300, 300);
		imageKunde.setOpaque(true);
		imageKunde.setBackground(new Color(100,100,100));

		infoLabel[0].setBounds(20, 20, 150, 25);
		infoLabel[1].setBounds(20, 50, 150, 25);
		infoLabel[2].setBounds(20, 80, 150, 25);

		infoText[0].setBounds(200, 20, 300, 25);
		infoText[1].setBounds(200, 50, 300, 25);
		infoText[2].setBounds(200, 80, 300, 25);
		addBildKundeBtn.setBounds(520, 80, 30, 25);
		imageKunde.setBounds(600, 20, 100, 100);

		addKundeBtn.setBounds(200, 160, 300, 25);

		kunde.setPreferredSize(new Dimension(windowWidth-290, 230));
		listkunde.setPreferredSize(new Dimension(windowWidth-290, windowHeight - 270));
		
		imageKunde.setBackground(Color.WHITE);
		imageKunde.setHorizontalAlignment(JLabel.CENTER);
		imageKunde.setVerticalAlignment(JLabel.CENTER);
		
		add(kunde,BorderLayout.PAGE_START);
		add(listkunde,BorderLayout.LINE_END);
		listkunde.clearField();//to refresh table ?this can be removed, because not needed at startup the program
		listkunde.viewListKunde();//?this can be removed, because not needed at startup the program

		addKundeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (infoText[0].getText().equals("") || infoText[1].getText().equals("")) {
					System.out.println("Cancel input fur die Kunde!");
					JOptionPane.showMessageDialog(null, "Information von Kunde nicht ausreichen!");
				} else {
				writeToDB();
				infoText[0].setText("");
				infoText[1].setText("");
				infoText[2].setText("");
				imageKunde.setIcon(null);
				listkunde.viewListKunde();
				System.out.println("New Kunde added!");
				}
			}
		});

		addBildKundeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageKunde.setIcon(null);
				String path = FileDialog("", false, "img");// if 'save' then 'true'
				imageFile = path;
				if (!path.equals("")) {
					infoText[2].setText(path);
					ImageIcon ii = new ImageIcon(path);

					int width = ii.getIconWidth();
					int height = ii.getIconHeight();

					double scaleWidth = (double) imageKunde.getWidth() / width;
					double scaleHeight = (double) imageKunde.getHeight() / height;

					System.out.println("scale Width: " + scaleWidth);
					System.out.println("scale Height: " + scaleHeight);

					double minScale = Math.min(scaleWidth, scaleHeight);

					ii.setImage(ii.getImage().getScaledInstance((int) (width * minScale), (int) (height * minScale),
							Image.SCALE_SMOOTH));

					imageKunde.setIcon(ii);
					imageKunde.setHorizontalAlignment(JLabel.CENTER);
					imageKunde.setVerticalAlignment(JLabel.CENTER);
				} else {
					infoText[2].setText("");
				}
			}
		});
	}
	
	//Getting path of file image of Kunde
	public String FileDialog(String path, boolean save, String typ) {
		
		String p="";
		int action =0;
		
		JFileChooser chooser = new JFileChooser(path);
	
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("bild datei", "jpg","jpeg","png"));
		chooser.setAcceptAllFileFilterUsed(false);// deactivated AllFile filter
		
		if(save) //speichern
		{
			deb("mode save");
			action=chooser.showSaveDialog(null);//wenn Save-Fuktion
		} else //laden
		{
			deb("mode load");
			action=chooser.showOpenDialog(null);//wenn Open-Fuktion
		}
		
		if(action==JFileChooser.APPROVE_OPTION) //wenn Zeile 21 oder 24 durchgeführt (nicht abgebrochen)...
		{
			deb("option ...");
			p=chooser.getSelectedFile().getAbsolutePath();
			deb(""+p);
		} 
		
		return p;
	}

	public void deb(String msg)
	{
		if(do_debug) {System.out.println(msg);}
	}
	
	//write new Kunde to database
	public void writeToDB() 
	{
		String[] elm = {infoText[0].getText(),infoText[1].getText(),infoText[2].getText()};
		UpdateDB updateDB = new UpdateDB();
		updateDB.setToDB(elm, "kunden", "kundennummer");
		updateDB.addToDB();
	}

	//method to clear entry field
	public void clearField() {
		imageKunde.setIcon(null);
		for (int i = 0; i < infoText.length; i++) {
			infoText[i].setText(null);
		}
		listkunde.clearField();
		listkunde.viewListKunde();
	}
}
