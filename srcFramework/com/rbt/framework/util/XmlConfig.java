/*
 * @(#)XmlConfig.java
 *
 * Copyright (c) 2004 HiTRUST Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2004/07/23, Jackie Yang
 *   1) First release
 *
 */
package com.rbt.framework.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.rbt.framework.util.DOM;

/**
 * Application Environment for global variables
 *
 * @author  Jackie Yang
 * @version 1.00, 2004/07/23
 */
public class XmlConfig {
	// XML DOM for keep application configuations
	private Document theDoc = null;
	private Node theNode = null;

	/**
	 * Constructs with the specified file name of XML.
	 * @param   fname  XML file name
	 */
	public XmlConfig(String fname) throws Exception {
		this.theDoc = DOM.loadDoc(fname);
	}

	/**
	 * Set tag node for get attribute value.
	 *
	 * @param   tag      the specified documet tag.
	 * @param   tagName  the specified documet tag's name.
	 * @return  The node found in documet, return null if not found.
	 */
	public Node setTagNode(String tag, String tagName) {
		// Get the tag node with the tag name
		this.theNode = DOM.getNode(this.theDoc, tag, "name", tagName);
		return this.theNode;
	}

	/**
	 * Get node attribute value in String.
	 *
	 * @param   attrib   the attribute of the node to be get.
	 * @return  the value of the specify attribute.
	 */
	public String getString(String attrib) {
		// Return the attribute value of specify attrib
		if(this.theNode == null) return null;
		return DOM.getNodeAttribute(this.theNode, attrib);
	}

	/**
	 * Get node attribute value in Int.
	 *
	 * @param   attrib   the attribute of the node to be get.
	 * @return  the value of the specify attribute.
	 */
	public int getInt(String attrib) {
		// Get the attribute of the node
		String value = this.getString(attrib);
		if(value == null) return 0;

		// Return the attribute value of specify attrib
		return Integer.parseInt(value);
	}

	/**
	 * Get node attribute value in Long.
	 *
	 * @param   attrib   the attribute of the node to be get.
	 * @return  the value of the specify attribute.
	 */
	public long getLong(String attrib) {
		// Get the attribute of the node
		String value = this.getString(attrib);
		if(value == null) return 0;

		// Return the attribute value of specify attrib
		return Long.parseLong(value);
	}

	/**
	 * Get node attribute value in Double.
	 *
	 * @param   attrib   the attribute of the node to be get.
	 * @return  the value of the specify attribute.
	 */
	public double getDouble(String attrib) {
		// Get the attribute of the node
		String value = this.getString(attrib);
		if(value == null) return 0;

		// Return the attribute value of specify attrib
		return Double.parseDouble(value);
	}

	/**
	 * Get tag node attribute's value.
	 *
	 * @param   tag      the specified documet tag.
	 * @param   tagName  the specified documet tag's name.
	 * @param   attrib   the attribute of the node to be get.
	 * @return  the value of the specify attribute.
	 */
	public String getTagNodeAttribute(String tag, String tagName, String attrib) {
		// Get the tag node with the tag name
		Node node = DOM.getNode(this.theDoc, tag, "name", tagName);
		if(node == null) return null;

		// Return the attribute value of specify attrib
		return DOM.getNodeAttribute(node, attrib);
	}

}
