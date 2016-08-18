package edu.upenn.cis455.servlet;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import edu.upenn.cis455.crawler.HelperFunctions;
import edu.upenn.cis455.storage.*;

/**
 * Servlet for retrieving channel xml from the database
 * @author cis455
 *
 */
public class ChannelFetcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChannelFetcher() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if(session==null || !HelperFunctions.isValidSession(session)){
			response.sendRedirect("xpath");
		}else{
			String userId = (String)session.getAttribute("userid");
			String channelId = request.getParameter("id");
			if(userId==null || channelId==null){
				session.invalidate();
				response.sendRedirect("xpath");
			}else{
				response.setContentType("application/xml");
				response.getWriter().write(generateDocument(channelId));
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doGet(request, response);
	}
	
	/**
	 * Generate xml document for channel id
	 * @param channelId
	 * @return
	 */
	private String generateDocument(String channelId){
		try{
			ChannelXmlDA chXmlDA = new ChannelXmlDA(DatabaseWrapper.getStore());
			ChannelDA channelDA = new ChannelDA(DatabaseWrapper.getStore());
			Channel channel = channelDA.fetchEntityFromPrimaryKey(channelId);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			if(channel!=null){
				ProcessingInstruction inst = doc.createProcessingInstruction(
						"xml-stylesheet", "type=\"text/xsl\" href=\"" + channel.getXls() + "\"");
				doc.appendChild(inst);
			}
			
			Element rootElement = doc.createElement("documentcollection");
			doc.appendChild(rootElement);
			
			ChannelXmlMap channelXml = chXmlDA.fetchEntityFromPrimaryKey(channelId);
			XmlDocumentDA xmlDA = new XmlDocumentDA(DatabaseWrapper.getStore());
			
			if(channelXml!=null){
				Set<String> xmlDocSet = channelXml.getXmlPageID();
				if(xmlDocSet!=null){
					for(String xmlDoc:xmlDocSet){
						
						XmlDocument currPage = xmlDA.fetchEntityFromPrimaryKey(xmlDoc);
						
						if(currPage!=null){
							Element currDoc = doc.createElement("document");
							Attr att1 = doc.createAttribute("crawled");
							att1.setValue(getXmlDate(currPage.getXmlLastParsed()));
							
							Attr att2 = doc.createAttribute("location");
							att2.setValue(currPage.getXmlDocUrl());
							
							currDoc.setAttributeNode(att1);
							currDoc.setAttributeNode(att2);

							Document fetchedDoc = HelperFunctions.getDocs(currPage.getXmlContent());
							int childCount=0;
							Node node= fetchedDoc.getChildNodes().item(childCount);
							
							if(node.getNodeType()!=Node.ELEMENT_NODE){
								while(node.getNodeType()!=Node.ELEMENT_NODE){
									node = fetchedDoc.getChildNodes().item(++childCount);
								}
							}
							
							currDoc.appendChild(doc.adoptNode(node));
							rootElement.appendChild(currDoc);
						}
					}
				}
			}
			
			DOMSource domSource=new DOMSource(doc);
		    StringWriter stringWriter=new StringWriter();
		    StreamResult result = new StreamResult(stringWriter);

		    TransformerFactory tFactory =TransformerFactory.newInstance();
		    Transformer transformer = tFactory.newTransformer();
		    transformer.setOutputProperty("indent","yes");

		    transformer.transform(domSource, result);        
		    return stringWriter.toString();
		}catch(ParserConfigurationException pce){
			System.out.println("ParserConfigurationException" + pce.toString());
			return null;
		}catch(TransformerException tfe){
			System.out.println("TransformerException: " + tfe.toString());
			return null;
		}
	}
	
	/**
	 * Returns formatted date for putting in channel xml
	 * @param time
	 * @return
	 */
	private String getXmlDate(long time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = sdf.format(new Date(time));
		
		return date;
	}
}
