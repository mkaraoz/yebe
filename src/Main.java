import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class Main {

	private final String inputFile = "/home/mk/Desktop/2004.txt";
	private final String outputFile = "/home/mk/Desktop/2004.xml";

	public static void main(String[] args) {
		new Main().start();
	}

	private void start() {
		List<Integer> entryNumbers = getEntryNumbers();
		List<Entry> entryList = getEntriesFromEksi(entryNumbers);
		boolean isSaved = saveEntriesToXmlFile(entryList);
		if (isSaved) {
			System.out.println("Entriler " + outputFile + " dosyasına kaydedildi.");
		} else
			System.out.println("Çeşitli aksaklıklar sonucu işlem tamamlanamadı.");

	}

	private List<Integer> getEntryNumbers() {
		List<Integer> entryNumbers = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

			String line = br.readLine();
			while (line != null) {
				if (line.trim().equals(""))
				{ 
					line = br.readLine();
				} 
				else {
					int indexOfNumberSign = line.indexOf('#');
					int indexOfCloseBracket = line.indexOf(')');
					line = line.substring(indexOfNumberSign + 1, indexOfCloseBracket).trim();
					System.out.println(line);
					entryNumbers.add(Integer.valueOf(line));
					line = br.readLine(); 
				} 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// for (int i : entryNumbers)
		// System.out.println(i);

		return entryNumbers;
	}

	private List<Entry> getEntriesFromEksi(List<Integer> entryNumbers) {

		List<Entry> entryList = new ArrayList<>();
		for (int entryNumber : entryNumbers) {
			String url = "https://eksisozluk.com/entry/" + entryNumber;
			Entry e = new Entry();
			Source source;
			try {
				source = new Source(new URL(url));

				// body
				Element ul_id = source.getElementById("entry-list");
				if (ul_id == null)
					continue;

				Element divContent = ul_id.getAllElementsByClass("content").get(0);
				String body = divContent.getContent().toString();
				if (!body.startsWith("http")) {
					body = body.replaceAll("href=\"/\\?q=", "href=\"https://eksisozluk.com/?q=");
					body = body.replaceAll("href=\"/entry/", "href=\"https://eksisozluk.com/entry/");
				}
				e.setBody(body);

				// user
				String user = ul_id.getAllElements().get(1).getAttributeValue("data-author");
				e.setUser(user);

				// entry number
				String entryNo = ul_id.getAllElements().get(1).getAttributeValue("data-id");
				e.setEntryNo(Integer.parseInt(entryNo));

				// date time
				Element element = ul_id.getAllElementsByClass("entry-date").get(0);
				String entry_date = element.getRenderer().toString();
				e.setDateTime(entry_date);

				// title
				element = source.getElementById("title");
				String title = element.getAttributeValue("data-title");
				e.setTitle(title);

				// title id
				String title_id = element.getAttributeValue("data-id");
				e.setTitleID(title_id);

				System.out.println("Kaydedildi: " + entryNumber);

				entryList.add(e);
			} catch (java.io.FileNotFoundException ex) {
				System.out.println("Bu entry silinmiş: #" + entryNumber);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return entryList;
	}

	private boolean saveEntriesToXmlFile(List<Entry> entryList) {
		try {
			File f = new File(outputFile);
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.newDocument();

			// Root element - use full name it may interfere with
			// jericho.Element
			org.w3c.dom.Element root = doc.createElement("EntryList");
			doc.appendChild(root);

			org.w3c.dom.Element ele = null;
			org.w3c.dom.Element entryElement = null;

			for (Entry e : entryList) {
				entryElement = doc.createElement("Entry");

				ele = doc.createElement("Sozluk");
				ele.appendChild(doc.createTextNode(e.sozluk));
				entryElement.appendChild(ele);

				ele = doc.createElement("EntryNo");
				ele.appendChild(doc.createTextNode(String.valueOf(e.getEntryNo())));
				entryElement.appendChild(ele);

				ele = doc.createElement("Title");
				ele.appendChild(doc.createTextNode(e.getTitle()));
				entryElement.appendChild(ele);

				ele = doc.createElement("Title_Id");
				ele.appendChild(doc.createTextNode(e.getTitleID()));
				entryElement.appendChild(ele);

				ele = doc.createElement("Body");
				ele.appendChild(doc.createTextNode(e.getBody()));
				entryElement.appendChild(ele);

				ele = doc.createElement("User");
				ele.appendChild(doc.createTextNode(e.getUser()));
				entryElement.appendChild(ele);

				ele = doc.createElement("Date_Time");
				ele.appendChild(doc.createTextNode(e.getDateTime()));
				entryElement.appendChild(ele);

				root.appendChild(entryElement);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
