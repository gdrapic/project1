package xmlCompareUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLComparePrettyPrint
{
    static PrintStream console = null;

    public static void main(String[] str) throws ParserConfigurationException, SAXException, IOException, Exception
    {
    	if(str == null || str.length < 5){
    	
    		System.out.println("Uasage:\n java "
    				+ "-DrecordIdExpr=//collection//Agreements//External_Id/text() "
    				+ "-DrecordExpr=//Agreements[External_Id='[ID]'] "
    				+ "-jar xmlCompareUtil.jar "
    				+ "/path/baselineFile.xml "
    				+ "/path/currentReleaseFile.xml "
    				+ "/path/Field_Difference_Report.txt "
    				+ "/path/Diff_Sumary.txt "
    				+ "/path/Id.txt "
    				+ "" 
    			);
    		System.exit(0);
    	}
    	
    	String fileOneAbsPath = str[0];// "test1.xml";
        String fileTwoAbsPath = str[1];//"test2.xml";
        
        File resultFile = new File(str[2]);//new File("Field_Difference_Report.txt");
        File summaryFile = new File(str[3]);//new File("Diff_Sumary.txt");
        File idFile = new File(str[4]);//new File("Id.txt");
        
//        System.setProperty("recordIdExpr","//collection//Agreements//External_Id/text()");
//        System.setProperty("recordExpr","//Agreements[External_Id='[ID]']");
//        
        String recordIdExpr = System.getProperty("recordIdExpr");
        String recordExpr = System.getProperty("recordExpr");
    	
        XMLComparePrettyPrint.compareNodes(
        		fileOneAbsPath, 
        		fileTwoAbsPath, 
        		resultFile, 
        		summaryFile, 
        		idFile,
        		recordIdExpr,
        		recordExpr
        		);

    }

    @SuppressWarnings("unused")
    static void compareNodes(
    				String fileOneAbsPath, 
    				String fileTwoAbsPath, 
    				File resultFile, 
    				File summaryFile, 
    				File IdFile,
    				String recordIdExpr,
    				String recordExpr
    				) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        PrintStream sf = new PrintStream(summaryFile);
        System.setOut(sf);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc1 = builder.parse(fileOneAbsPath);
        Document doc2 = builder.parse(fileTwoAbsPath);

        // Document doc1 = builder.parse("test2.xml");
        // Document doc2 = builder.parse("test1.xml");

        XPath xPath = XPathFactory.newInstance().newXPath();
        
        
        String idNodeValue = recordIdExpr; // "//collection//Agreements//External_Id/text()";

        NodeList idNodeListOne = (NodeList) xPath.compile(idNodeValue).evaluate(doc1, XPathConstants.NODESET);
        NodeList idNodeListTwo = (NodeList) xPath.compile(idNodeValue).evaluate(doc2, XPathConstants.NODESET);

        Collection<String> arrListOne = new ArrayList<String>();
        Collection<String> arrListTwo = new ArrayList<String>();

        for (int a = 0; a < idNodeListOne.getLength(); a++)
        {
            arrListOne.add(idNodeListOne.item(a).getNodeValue());
        }

        for (int b = 0; b < idNodeListTwo.getLength(); b++)
        {
            arrListTwo.add(idNodeListTwo.item(b).getNodeValue());
        }

        System.out.println("Total No of Records in Base Line = " + idNodeListOne.getLength());
        System.out.println("Total No of Records in Current Release = " + idNodeListTwo.getLength());
        int dd = ((idNodeListTwo.getLength() - idNodeListOne.getLength()) < 0) ? 0 : idNodeListTwo.getLength() - idNodeListOne.getLength();
        System.out.println("Total No of Extra records found in Current Release = " + dd);

        Collection<String> oneExistNotTwo = new ArrayList<String>(arrListOne);
        Collection<String> twoExistNotOne = new ArrayList<String>(arrListTwo);
        Collection<String> matchList = new ArrayList<String>(arrListOne);

        oneExistNotTwo.removeAll(arrListTwo);
        twoExistNotOne.removeAll(arrListOne);
        matchList.retainAll(arrListTwo);

        System.out.println("Total No of Base Line Records missing from Current Release = " + oneExistNotTwo.size());
        System.out.println("Total No of Extra Records in Current Release XML = " + twoExistNotOne.size());
        System.out.println("Total No of matched Ids in both Base Line and Current Release = " + matchList.size());
        
        PrintStream pd = new PrintStream(IdFile);
        System.setOut(pd);

        for (String id : oneExistNotTwo)
        {
            System.out.println("Record Id of Base Line Records missing from Current Release XML = " + id);
        }
        for (String id : twoExistNotOne)
        {
            System.out.println("Record Id of Extra Records in Current Release XML = " + id);
        }

        idNodeListOne = null;
        idNodeListTwo = null;
        arrListOne.clear();
        arrListTwo.clear();

        XPathExpression expr = null;
        int matched = 0;
        int unmatched = 0;

        PrintStream ps = new PrintStream(resultFile);
        System.setOut(ps);
        for (String id : matchList)
        {// "//Agreements[External_Id='[ID]']"
//        	recordExpr = recordExpr.replaceAll("\\[ID\\]", id);
//        	System.err.println(recordExpr);
            expr = xPath.compile(recordExpr.replaceAll("\\[ID\\]", id));
            Node nodeOne = (Node) expr.evaluate(doc1, XPathConstants.NODE);
            Node nodeTwo = (Node) expr.evaluate(doc2, XPathConstants.NODE);

            if (DOMUtils.compare(id, nodeOne, nodeTwo))
            {
                matched++;
            }
            else
            {
                unmatched++;
            }

        }

        System.setOut(sf);
        System.out.println("Total No of Matched Records = " + matched);
        System.out.println("Total No of UnMatched Reords = " + unmatched);

    }

    private static void sendEmail(String from, String[] to, String subject, File summaryFile, File attachment) throws IOException
    {
        Properties props = System.getProperties();
        // String host = propertiesAccessor.getProperty("mail.smtp.host");
        // String user = propertiesAccessor.getProperty("mail.smtp.user");

        String host = System.getProperty("mail.smtp.host");
        String user = System.getProperty("mail.smtp.user");

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", user);
        // props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "25");
        // props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try
        {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++)
            {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++)
            {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart attachPart = new MimeBodyPart();
            MimeBodyPart summaryPart = new MimeBodyPart();

            attachPart.attachFile(attachment);
            summaryPart.attachFile(summaryFile);

            multipart.addBodyPart(attachPart);
            multipart.addBodyPart(summaryPart);

            message.setContent(multipart);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae)
        {
            ae.printStackTrace();
        }
        catch (MessagingException me)
        {
            me.printStackTrace();
        }
    }
}
