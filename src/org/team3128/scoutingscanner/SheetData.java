package org.team3128.scoutingscanner;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SheetData {
	public int teamNumber;
	
	private HashMap<String, Object> responses = new HashMap<String, Object>();
	
	public int getData(String key)
	{
		int data = 0;
		if (responses.get(key) instanceof Boolean)
		{
			boolean resp = (boolean) responses.get(key);
			data = (resp) ? 1 : 0;
		}
		else
		{
			data = (int) responses.get(key);
		}
		return data;
	}
	
	public SheetData(int teamNumber, Document sheetDataXML)
	{
		this.teamNumber = teamNumber;
		
		NodeList questionList = sheetDataXML.getElementsByTagName("question");
		
		for (int i = 0; i < questionList.getLength(); i++)
		{
			Node questionNode = questionList.item(i);

			if (questionNode.getNodeType() == Node.ELEMENT_NODE) {

				Element questionElement = (Element) questionNode;
				String questionName = questionElement.getAttribute("question").toString();
					
				System.out.println(questionName);
				if (questionElement.getElementsByTagName("value").getLength() > 0)
				{
					String firstResponse = questionElement.getElementsByTagName("value").item(0).getAttributes().item(0).getNodeValue().toString();
					
					if (questionElement.getAttribute("multiple").toString() == "true")
					{
						int count = questionElement.getElementsByTagName("value").getLength();
						int result = 0;
						for (int j = 0; j < count; j++)
						{
							String response = questionElement.getElementsByTagName("value").item(j).getAttributes().item(0).getNodeValue().toString();
							result += Integer.parseInt(response);
						}
						responses.put(questionName, result);
						
					}
					else if (!isInteger(firstResponse))
					{
						boolean response = false;
						if (firstResponse.equals("Y"))
						{
							response = true;
						}
						responses.put(questionName, response);
					}
					else
					{
						int result = Integer.parseInt(firstResponse);
						responses.put(questionName, result);
					}
				}
				else 
				{
					responses.put(questionName, 0);
				}
			}
		}
	}
	
	public String toString(Document xmlDoc)
	{
		String output = "";
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(xmlDoc), new StreamResult(writer));
			output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }

	    return true;
	}
}
