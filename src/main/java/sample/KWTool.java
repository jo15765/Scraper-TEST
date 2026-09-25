package sample;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KWTool {

    public KWTool(){ }

    public ArrayList<String> getListKeyword(String prefix, String keywords){
        ArrayList<String> ret = new ArrayList<>();
        if (prefix != null){
            String[] arrPref = prefix.split(",");
            String[] arrKeyword = keywords.split("\n");
            for (String arr2 : arrKeyword) {
                if (arrPref.length > 0){
                    for (String arr1 : arrPref) {
                        ret.add(arr1.trim() + " " + arr2.trim());
                    }
                }else{
                    ret.add(arr2.trim());
                }
            }
        }
        return ret;
    }

    public ArrayList<String> fetchKeywords(String keyword, String lang) throws ParserConfigurationException, SAXException, IOException{
        ArrayList<String> list = new ArrayList<>();
        String url = "http://suggestqueries.google.com/complete/search?client=toolbar&q=" + keyword + "&hl=" + lang;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("suggestion");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                list.add(eElement.getAttribute("data"));
            }
        }
        return list;
    }
}
