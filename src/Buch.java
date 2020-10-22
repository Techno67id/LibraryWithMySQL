
public class Buch {
	private String idbook="";
	private String title="";
	private String author="";
	private String verlag="";
	private String isbn="";
	private String thema="";
	private String exemplare="0";
	private String available="0";

	public Buch() {
		
	}

	public Buch(String idbook,String title,String author,String verlag,String isbn,String thema,String exemplare,String available) {
		setIdbook(idbook);
		setTitle(title);
		setAuthor(author);
		setVerlag(verlag);
		setIsbn(isbn);
		setThema(thema);
		setExemplare(exemplare);
		setAvailable(available);
	}

	public String getIdbook() {
		return idbook;
	}

	public void setIdbook(String idbook) {
		this.idbook = idbook;
	}

	public String getTitle(String title) {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor(String author) {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getVerlag(String verlag) {
		return verlag;
	}
	
	public void setVerlag(String verlag) {
		this.verlag = verlag;
	}
	
	public String getIsbn(String isbn) {
		return isbn;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String getThema(String thema) {
		return thema;
	}
	
	public void setThema(String thema) {
		this.thema = thema;
	}
	
	public String getExemplare(String exemplare) {
		return exemplare;
	}
	
	public void setExemplare(String exemplare) {
		this.exemplare = exemplare;
	}
	
	public String getAvailable(String available) {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}
	
}
