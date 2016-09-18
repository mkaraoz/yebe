
public class Entry {
	private int entryNo;
	private String title;
	private String titleID;
	private String user;
	private String dateTime;
	private String body;
	public final String sozluk = "Ekşi Sözlük";

	public int getEntryNo() {
		return entryNo;
	}

	public void setEntryNo(int entryNo) {
		this.entryNo = entryNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleID() {
		return titleID;
	}

	public void setTitleID(String titleID) {
		this.titleID = titleID;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void print() {
		System.out.println(this.title);
		System.out.println(this.titleID);
		System.out.println(this.body);
		System.out.println(this.user);
		System.out.println(this.dateTime);
	}

}
