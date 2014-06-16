package com.rbt.framework.util;

import java.io.*;
import java.util.HashMap;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

/**
 * XML DOM parser & node process utilities.
 */
public class DOM {
	// Constants used for JAXP 1.2
	static final String W3C_XML_SCHEMA       = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String JAXP_SCHEMA_SOURCE   = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	// setup the XML parser & transformer factory
	static private DocumentBuilder docBuilder = null;
	static private Transformer transformer = null;
	static {
		try {
			// XML Document Builder
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setCoalescing(true);
			dbf.setExpandEntityReferences(true);
			dbf.setNamespaceAware(true);
			//dbf.setValidating(true);
			//dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			//dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File(schemaSource));
			docBuilder = dbf.newDocumentBuilder();

			// XML Transformer
			TransformerFactory tff = TransformerFactory.newInstance();
			transformer = tff.newTransformer();
//			transformer.setOutputProperty(OutputKeys.ENCODING, "BIG5");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		} catch (Exception ex) {
			// ingore exception
		}
	}

	/**
	 * Get an empty document.
	 */
	public static Document newDoc() {
		return docBuilder.newDocument();
	}

	/**
	 * Get a document that loaded from XML file.
	 *
	 * @param xmlFileName	- the XML file name.
	 */
	public static Document loadDoc(String xmlFileName)
			throws IOException, SAXException{
		return docBuilder.parse(new File(xmlFileName));
	}

	/**
	 * Get a document that loaded from XML file.
	 *
	 * @param xmlFileName	- the XML file name.
	 */
	public static Document loadDoc(File xmlFile)
			throws IOException, SAXException{
		return docBuilder.parse(xmlFile);
	}

	/**
	 * Get a document that loaded from byte[].
	 *
	 * @param xmlBytes	- the byte[] of XML content.
	 */
	public static synchronized Document loadDoc(byte[] xmlBytes)
			throws IOException, SAXException {
		// set respond data to XML document

		ByteArrayInputStream byteIn = new ByteArrayInputStream(xmlBytes);
		return docBuilder.parse(byteIn);
	}

	/**
	 * Get a document that loaded from InputStream.
	 *
	 * @param xmlIS	- the InputStream of XML content.
	 */
	public static synchronized Document loadDoc(InputStream xmlIS)
			throws IOException, SAXException {
		return docBuilder.parse(xmlIS);
	}

	/**
	 * Get document byte[]
	 *
	 * @param doc	- the document to be transform.
	 * @return XML byte[] of the specific document.
	 */
	public static byte[] getDocBytes(Document doc) {
		try {
			// set document to DOM source
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(byteOut);

			// transform document to byte[]
			transformer.transform(source, result);
			return byteOut.toByteArray();

		} catch (Exception ex) {
			// ignored exception
			return null;

		}
	}

	/**
	 * 指定字符集得到byte
	 *
	 * @param doc	- the document to be transform.
	 * @return XML byte[] of the specific document.
	 */
	public static byte[] getDocBytes(Document doc, String encoding) {
		try {
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			// set document to DOM source
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(byteOut);

			// transform document to byte[]
			transformer.transform(source, result);
			return byteOut.toByteArray();

		} catch (Exception ex) {
			// ignored exception
			return null;

		} finally {
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		}
	}

	/**
	 * Get node has the given node name and attribute value.
	 *
	 * @param   doc    The document to be parsed.
	 * @param   tag    The node name to be found.
	 * @param   attr   The attribute name of node.
	 * @param   value  The attribute value of node to be found.
	 * @return  The first node found in documet, return null if not found.
	 */
	public static Node getNode(Document doc, String tag, String attr, String value) {
		// Get NodeList of Tag
		NodeList list = doc.getElementsByTagName(tag);

		// Parsing each node in the nodelist
		for(int i=0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String val = getNodeAttribute(node, attr);
			if(val.equalsIgnoreCase(value)) return node;
		}
		// Not found
		return null;
	}

	/**
	 * Get node attribute value or it's TEXT child node value which has
	 * the node name same as the attribute.
	 *
	 * @param   node  The node to be parsed.
	 * @param   attr  The attribute name of node.
	 * @return  The attribute value of the node, return null if not found.
	 */
	public static String getNodeAttribute(Node node, String attr) {
		// Check is element node
		if(node.getNodeType() != Node.ELEMENT_NODE) return null;

		// Found attribute from the node's attributes
		NamedNodeMap atts = node.getAttributes();
		for(int i=0; i < atts.getLength(); i++) {
			Node att = atts.item(i);
			if(att.getNodeName().equalsIgnoreCase(attr)) {
				return att.getNodeValue();
			}
		}

		// Found attribute from the node's TEXT children
		for(Node child=node.getFirstChild(); child != null;	child=child.getNextSibling()) {
			if(child.getNodeName().equalsIgnoreCase(attr)) {
				Node text = child.getFirstChild();
				if((text!=null) && (text.getNodeType() == Node.TEXT_NODE)) {
					return text.getNodeValue();
				}
			}
		}
		// Not found
		return null;
	}

	/**
	 * Get node attributes names and values as a HashMap
	 * name as map's key and value as map's value
	 *
	 * @param   Node node  The node to be parsed.
	 * @return  HashMap The attributes of the node
	 */
	public static HashMap getNodeAttributes(Node node) {
		HashMap attrMap = new HashMap();

		NamedNodeMap attrList = node.getAttributes();
		for (int i=0; attrList!=null&&i<attrList.getLength(); i++) {
			Node attr = attrList.item(i);
			attrMap.put(attr.getNodeName(), attr.getNodeValue());
		}

		return attrMap;
	}

	/**
	 * Get node attribute value or it's TEXT child node value which has
	 * the node name same as the attribute. If not found or empty return
	 * given default value.
	 *
	 * @param   node  The node to be parsed.
	 * @param   attr  The attribute name of node.
	 * @param   deft  The default value returned when not found or empty
	 * @return  The attribute value of the node.
	 */
	public static String getNodeAttribute(Node node, String attr, String deft) {
		// Get node Attribute value
		String val = getNodeAttribute(node, attr);

		// Check is null
		if(val == null) return deft;

		// Check is empty string
		if(val.length() == 0) return deft;

		// Return value
		return val;
	}

	/**
	 * Get node attribute value or it's TEXT child node value which has
	 * the node name same as the attribute. If not found or empty return
	 * html space.
	 *
	 * @param   node  The node to be parsed.
	 * @param   attr  The attribute name of node.
	 * @return  The attribute value of the node.
	 */
	public static String getNodeStr(Node node, String attr) {
		// return the node attribute value
		return getNodeAttribute(node, attr, "&nbsp;");
	}

	/**
	 * Get node attribute value or it's TEXT child node value which has
	 * the node name same as the attribute. If not found or empty or 0.00
	 * return html space.
	 *
	 * @param   node  The node to be parsed.
	 * @param   attr  The attribute name of node.
	 * @return  The attribute value of the node.
	 */
	public static String getNodeNum(Node node, String attr) {
		// return the node attribute value
		String val = getNodeAttribute(node, attr, "&nbsp;");

		// Check is 0.00 string
		if(val.equals("0.00")) return "&nbsp;";
		return val;
	}

	// 存放分頁數據的臨時文件
	public static boolean writeToFile(Document doc, String filename) {
		FileWriter file = null;
		try {
			file = new FileWriter(filename);
			file.write(new String(getDocBytes(doc)));
			file.close();
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}

	// 存放分頁數據的臨時文件
	public static boolean writeToFile(Document doc, String filename, String encoding) {
		FileWriter file = null;
		try {
			file = new FileWriter(filename);
			file.write(new String(getDocBytes(doc, encoding)));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
