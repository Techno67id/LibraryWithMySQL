import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Menu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panelMainMenu = new JPanel();//panel for main control
	private JButton addMemberBtn = new JButton("Add New Member");
	private JButton addBookBtn = new JButton("Add New Book");
	private JButton ausleiheBtn = new JButton("Borrow");
	private JButton viewBookBtn = new JButton("List all Books");
	private JButton viewKundeBtn = new JButton("List all Member");
	private JButton exitBtn = new JButton("Exit");
	private AddBuch addbuch = new AddBuch();// panel "Add new Book"
	private AddKunde addkunde = new AddKunde();// panel "Add new Member"
	private ListKunde listkunde = new ListKunde();// panel "List of Member"
	private ListBooks listbook = new ListBooks();// panel "List of Books"
	private AusleiheBuch ausleihe = new AusleiheBuch();// panel "Borrow Books"
	private JPanel panelBackground = new JPanel();//panel for main control
	private JLabel imgLab = new JLabel();//to post Background image of the Application

	//variables for optimize Main Menu
	private int hBtn = 50; // dimension of height of Button 
	private int wBtn = 200; // // dimension of width of Button
	private int zBtn = 10;// distance between Button
	private int leftPos = 20 ;// left margin of Button in Panel
	private int topBtn = 20; //distance of top-Button to the Edge

	//Getting Screen Size, to dimensioning size of Windows of Application
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//get screen size of the user's computer
	private int windowHeight = (int)(screenSize.height*0.75); //Application take only 75% of full screen
	private int windowWidth = (int)(screenSize.width*0.75); //Application take only 75% of full screen

	private int centerX=0;//as initial X-position of windows  
	private int centerY=0;//as initial Y-position of windows
	
	public Menu() {
		super();
		setTitle("University Library V1.0");
		optimizeWindow();//Optimizing windows base on Screen Size of User's Computer
		centerFrame();
		new checkDB();//check if database is available/already exist
		
		setLayout(null);
		setSize(windowWidth, windowHeight);
		setResizable(false);//windows of the Application can not resizable

		panelMainMenu.setLayout(null);
		panelMainMenu.add(addBookBtn);
		panelMainMenu.add(addMemberBtn);
		panelMainMenu.add(ausleiheBtn);
		panelMainMenu.add(viewBookBtn);
		panelMainMenu.add(viewKundeBtn);
		panelMainMenu.add(exitBtn);
		panelBackground.setLayout(null);
		panelBackground.setSize(windowWidth-panelMainMenu.getWidth()-30, windowHeight-40);
		panelBackground.setLocation(panelMainMenu.getWidth()+10, 0);
		panelBackground.setAlignmentX(CENTER_ALIGNMENT);
		panelBackground.setAlignmentY(CENTER_ALIGNMENT);
		
		imgLab.setBounds(0,0,panelBackground.getWidth(), panelBackground.getHeight());
		imgLab.setBackground(Color.WHITE);
		imgLab.setHorizontalAlignment(JLabel.CENTER);
		imgLab.setVerticalAlignment(JLabel.CENTER);
		
		backgroundImage();//get Background Image, need when this program start up
		panelBackground.add(imgLab);

		add(addbuch);
		add(listbook);
		add(addkunde);
		add(listkunde);
		add(ausleihe);
		add(panelMainMenu);
		add(panelBackground);
		
		addbuch.setVisible(false);
		listbook.setVisible(false);
		addkunde.setVisible(false);
		listkunde.setVisible(false);
		panelBackground.setVisible(true);
		ausleihe.setVisible(false);

		panelMainMenu.setSize(250, (windowHeight-40));
		panelMainMenu.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createEtchedBorder(), "Main Menu"));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addbuch.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createEtchedBorder(), "Add new Book"));

		addkunde.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createEtchedBorder(), "Add new Member"));

		ausleihe.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createEtchedBorder(), "Borrow Books"));

        setLocation(centerX, centerY);
		setVisible(true);
		
		//Call Menu : Add new Book
		addBookBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Menu Add Book!");
				addbuch.setSize(windowWidth-panelMainMenu.getWidth()-30, windowHeight-40);
				addbuch.setLocation(panelMainMenu.getWidth()+10, 0);

				panelBackground.setVisible(false);
				addkunde.setVisible(false);
				listkunde.setVisible(false);
				listkunde.clearField();
				ausleihe.setVisible(false);
				addbuch.setVisible(true);
				listbook.setVisible(false);
				listbook.clearField();
				addbuch.clearField();
			}
		});

		//Call Menu : Add new Member
		addMemberBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Menu Add New Member!");

				addkunde.setSize(windowWidth-panelMainMenu.getWidth()-30, windowHeight-40);
				addkunde.setLocation(panelMainMenu.getWidth()+10, 0);

				panelBackground.setVisible(false);
				addbuch.setVisible(false);
				listbook.setVisible(false);
				listbook.clearField();
				ausleihe.setVisible(false);
				addkunde.setVisible(true);
				listkunde.setVisible(false);
				listkunde.clearField();
				addkunde.clearField();
			}
		});
		
		//Call Menu : Ausleihen
		ausleiheBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Menu Borrow a Book!");
				ausleihe.setSize(windowWidth-panelMainMenu.getWidth()-30, windowHeight-40);
				ausleihe.setLocation(panelMainMenu.getWidth()+10, 0);

				panelBackground.setVisible(false);
				addbuch.setVisible(false);
				listbook.setVisible(false);
				listbook.clearField();
				addkunde.setVisible(false);
				listkunde.setVisible(false);
				listkunde.clearField();
				ausleihe.setVisible(true);
				ausleihe.clearField();
			}
		});
		
		//To Display list of books
		viewBookBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("View Book!");

				listbook.setSize(windowWidth-panelMainMenu.getWidth()-40, (windowHeight-20));
				listbook.setLocation(panelMainMenu.getWidth()+10, 10);
				listbook.setWidthText(windowWidth-panelMainMenu.getWidth()-150);
				
				int w= windowWidth-panelMainMenu.getWidth()-45;
				int h= (windowHeight-20)-90;
				listbook.setPosSize(w,h);

				panelBackground.setVisible(false);
				addkunde.setVisible(false);
				listkunde.setVisible(false);
				listkunde.clearField();
				addbuch.setVisible(false);
				listbook.setVisible(true);
				ausleihe.setVisible(false);
				listbook.viewListBook();
			}
		});
		
		//To Display All available books
		viewKundeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("View Member!");
				listkunde.setSize(windowWidth-panelMainMenu.getWidth()-40, (windowHeight-20));//230 from addbuch
				listkunde.setLocation(panelMainMenu.getWidth()+10, 10);
				listkunde.setWidthText(windowWidth-panelMainMenu.getWidth()-150);
				
				int w= windowWidth-panelMainMenu.getWidth()-45;
				int h= (windowHeight-20)-90;
				listkunde.setPosSize(w,h);

				panelBackground.setVisible(false);
				addbuch.setVisible(false);
				listbook.setVisible(false);
				listbook.clearField();
				listkunde.setVisible(true);
				addkunde.setVisible(false);
				ausleihe.setVisible(false);
				listkunde.viewListKunde();
			}
		});
		
		//exit from this Application
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
	}
	
	//set Background Image when program Start up
	public void backgroundImage()
	{
		ImageIcon ii = new ImageIcon("Java.jpg");
		
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
		
		double minScale = Math.min(scaleWidth, scaleHeight);
		
		ii.setImage(ii.getImage().getScaledInstance((int)(width*minScale), (int)(height*minScale), Image.SCALE_SMOOTH));
		
		imgLab.setIcon(ii);
		imgLab.setHorizontalAlignment(JLabel.CENTER);
		imgLab.setVerticalAlignment(JLabel.CENTER);
	}
	
	//setup size of windows, main panel (including buttons) and panel entry-field (panel "add new boks", etc) 
	public void optimizeWindow() {
		/*
		 * there are 7 (Buttons) and 5 (gaps/dGap) + 1 Special Gap to Exit-Button + 1 Gap for Top Margin and 1 Gap for Bottom Margin
		 *  1 Special Gap to Exit-Button equivalent to 3xdGap
		 *  1 Gap for Top Margin and 1 Gap for Bottom Margin has 2xdGap for each 
		 *  then all 5dGap + 3dGap + 2x(2dGap) = 12dGap
		 *  7hBtn + 12dGap = windowHeight, where 1 hBtn is equivalent to 4xdGap
		 *  7x4dGap +12dGap = 40dGap = windowHeight;
		 */
		
		int dGap=windowHeight/40;
		hBtn = 3*dGap; //implement 3 instead of 4(initial assumption)
		zBtn = dGap;
		topBtn = 2*dGap;
		
		//set dimension and location for Main Menu 
		addBookBtn.setBounds(leftPos, topBtn, wBtn, hBtn);
		addMemberBtn.setBounds(leftPos, (topBtn+1*hBtn+1*zBtn), wBtn, hBtn);
		ausleiheBtn.setBounds(leftPos, (topBtn+2*hBtn+2*zBtn), wBtn, hBtn);
		viewBookBtn.setBounds(leftPos, (topBtn+3*hBtn+3*zBtn), wBtn, hBtn);
		viewKundeBtn.setBounds(leftPos, (topBtn+4*hBtn+4*zBtn), wBtn, hBtn);
		exitBtn.setBounds(leftPos, (topBtn+7*hBtn+8*zBtn), wBtn, hBtn);
	}
	
	//to post window at the middle of the screen
	public void centerFrame() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();

		int dx = centerPoint.x - windowWidth / 2;
		int dy = centerPoint.y - windowHeight / 2;
		centerX=dx;
		centerY=dy;
	}
}
