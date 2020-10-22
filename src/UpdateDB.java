import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

public class UpdateDB {
	
	private String[] elm;
	private Connection con=null;
	private Statement st=null;
	private String workTable;
	private String selectCol;
	private Integer zahler=0;
	
	public UpdateDB() {
		
	}

	public void setToDB(String[] elm,String workTable, String selectCol) {
		this.elm = elm;//input text from user, addbook[6] etc
		this.workTable=workTable;
		this.selectCol=selectCol;
	}
	
	public void updateDB(String referCol,int num)
	{
		Connection con = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			stmt = con.createStatement();
			sqlExec(con, "use library1");
			
			String query = "update " + workTable + " set " + selectCol + " = ? where " + referCol + " = ?";
			PreparedStatement pst = con.prepareStatement(query);
			int refId = Integer.parseInt(elm[0]);

			pst.setInt(1, num);
			pst.setInt(2, refId);
			pst.executeUpdate();
			System.out.println(workTable+" database updated!");

			stmt.close();
			pst.close();
			con.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	public void addToDB()
	{
		con = null;
		st = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307", "root2", "root2");
			st = con.createStatement();
		
			sqlExec(con, "use library1");
			
			//String workTable=workTable;
			
			String sql = "SELECT "+selectCol+" FROM " + workTable;
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				zahler=rs.getInt(selectCol);
			}
			zahler++;
			
			if (selectCol == "idbook") {
				
				if(zahler==1) {
					zahler= 101;
				}
				String titel,autor,verlag,isbn,thema;
				int available;

				titel = elm[0];
				autor = elm[1];
				verlag = elm[2];
				isbn = elm[3];
				thema = elm[4];
				available = Integer.parseInt(elm[5]);
				sql = "insert into buecher (idbook,title,author,verlag,isbn,thema,exemplare,available) "
						+ "values ("+zahler+ ",'" + titel + "','" + autor
						+ "','" + verlag + "','" + isbn + "','" + thema + "',"+available+","+available+")";
	
				sqlExec(con, sql);
			}
			
			if (selectCol == "ausleiheID") {
				if(zahler==1) {
					zahler= 10001;
				}

				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				String datumAusleihe=dateFormat2.format(today);
				
				String datumRueckgabe=ruckgabe();//to get return date of the book

				String kundennummer = ""+elm[0];
				int idbook=Integer.parseInt(elm[1]);
				sql = "insert into ausleihen (ausleiheID,idbook,kundennummer,datumAusleihe,datumRueckgabe) "
						+ "values ("+zahler+ ",'" + idbook + "','" + kundennummer
						+ "','" + datumAusleihe + "','" + datumRueckgabe+"')";
				sqlExec(con, sql);
			}

			if (selectCol == "kundennummer") {
				
				if(zahler==1) {
					zahler= 1001;
				}

				String name;
				String klasse;

				name = elm[0];
				klasse = elm[1];
				String imageFile = elm[2];

				ResizeImage resizedImage= new ResizeImage(imageFile);
	
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(resizedImage.getBufferedImage(),"jpeg",os);
				InputStream fis = new ByteArrayInputStream(os.toByteArray());
				
				PreparedStatement ps=con.prepareStatement("insert into kunden (kundennummer,name,klasse,aktuellAusgelieheneBueche,image) values(?,?,?,?,?)"); 
				ps.setInt(1,zahler);
				ps.setString(2,name);
				ps.setString(3,klasse);
				ps.setInt(4,0);
				ps.setBinaryStream(5,fis,10240000);
				ps.executeUpdate();
			}
			
			st.close();
			con.close();
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

	public String ruckgabe() {

		Date currentDate = new Date();

		// convert date to calendar
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		// manipulate date
		c.add(Calendar.DATE, 30);

		// convert calendar to date
		Date ruckgabe = c.getTime();

		String datumx = toString(ruckgabe);
		return datumx;
	}

	public String toString(Date d) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = String.format(dateFormat.format(d));
		return date;
	}

}
