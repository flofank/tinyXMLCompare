import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class XmlTreeNode implements TreeNode {
	private String name;
	private String text = null;
	private Hashtable<String,String> attributes = new Hashtable<String, String>();
	private XmlTreeNode parent;
	private ArrayList<XmlTreeNode> children = new ArrayList<XmlTreeNode>();
	
	//Matching properties
	private int match_id;		//Two matching nodes from compared xmls are given the same id (Maybe ids could be inherited)
	private boolean equal;	//True if matching node from other xml is all the same 
	private boolean searchMatch;

	public XmlTreeNode(String name, String text, Hashtable<String,String> attributes, XmlTreeNode parent, ArrayList<XmlTreeNode> children) {
		this.name = name;
		this.text = text;
		this.attributes = attributes;
		this.parent = parent;
		this.children = children;
	}
	
	public XmlTreeNode(String name, XmlTreeNode parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public XmlTreeNode(String name, XmlTreeNode parent, String text) {
		this.name = name;
		this.parent = parent;
		this.text = text;
	}
	
	@Override
	public Enumeration children() {
		return Collections.enumeration(children);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		return children.get(arg0);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return children.indexOf(arg0);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		if (children.size() > 0) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		String out = "<" + name;
		for (String key : attributes.keySet()) {
			out += " " + key + "=\"" + attributes.get(key) + "\"";
		}
		out += ">";
		//out += " - id: " + getMatch_id();
		if (text != null ) {
			out += text + "</" + name + ">";
		} 
		return out;
		
	}
	
	

	public int getMatch_id() {
		return match_id;
	}
	public void setMatch_id(int match_id) {
		this.match_id = match_id;
	}
	public boolean hasMatch() {
		if (this.match_id > 0) {
			return true;
		}
		return false;
	}
	public boolean isEqual() {
		if (equal) {
			for (XmlTreeNode c : children) {
				if (!c.isEqual()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	public void setEqual(boolean equal) {
		this.equal = equal;
	}
	public TreePath getPath() {
		ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
		nodes.add(this);
		TreeNode node = this.getParent();
		while (node != null) {
			nodes.add(0, node);
			node = node.getParent();
		}
		return new TreePath(nodes.toArray());
	}
	public void expandSearchMatches(JTree tree) {
		if (searchMatch) {
			tree.expandPath(parent.getPath());
		}
		for (XmlTreeNode child : children) {
			child.expandSearchMatches(tree);
		}
	}
	public void collapseAll(JTree tree) {
		for (XmlTreeNode child : children) {
			child.collapseAll(tree);
		}
		if (parent != null) {
			tree.collapsePath(getPath());
		}		
	}
	public void expandAll(JTree tree) {
		for (XmlTreeNode child : children) {
			child.expandAll(tree);
		}
		if (children.size() == 0) {
			tree.expandPath(parent.getPath());
		}	
	}
	/**
	 * Getters and Setters from here on
	 */
	
	public boolean isSearchMatch() {
		return searchMatch;
	}
	public void setSearchMatch(boolean searchMatch) {
		this.searchMatch = searchMatch;
	}
	
	public String getName() {
		return name;
	}
	
	public Hashtable<String,String> getAttributes() {
		return attributes;
	}	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public void addChild(XmlTreeNode child) {
		children.add(child);
	}
	public ArrayList<XmlTreeNode> getChilds() {
		return children;
	}
	public int countChilds() {
		return children.size();
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean hasText() {
		return text != null;
	}
	
	

}
