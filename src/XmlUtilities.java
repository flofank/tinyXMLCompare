import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XmlUtilities {
	private static int match_id = 0;
	
	/**
	 * Tries to find Matching nodes in tow given XML Trees
	 * @param root_a
	 * @param root_b
	 */
	public static void matchTrees(XmlTreeNode root_a, XmlTreeNode root_b) {
		try {
			if (root_a.toString().equals(root_b.toString())) {
				matchNodes(root_a, root_b, true);
			} else {
				matchNodes(root_a, root_b, false);
			}
			matchBranches(root_a, root_b);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Finds Matching nodes in two Xml branches and marks them as matching
	 * @param branch_a
	 * @param branch_b
	 */
	public static void matchBranches(XmlTreeNode branch_a, XmlTreeNode branch_b) {
		for (XmlTreeNode n_a : branch_a.getChilds()) {
			for (XmlTreeNode n_b : branch_b.getChilds()) {
				if (!n_a.getName().equals(n_b.getName()) || n_b.hasMatch()) {
					//no match
					continue;
				} else if (n_a.toString().equals(n_b.toString())) {
					//Totally equal
					matchNodes(n_a, n_b, true);
					matchBranches(n_a, n_b);
					break;
				} else if (n_a.getAttributes().size() != 0 && n_b.getAttributes().size() != 0) { 
					String firstKey_a = (String) n_a.getAttributes().keySet().toArray()[0]; 
					String firstKey_b = (String) n_b.getAttributes().keySet().toArray()[0];
					if (firstKey_a.equals(firstKey_b) && n_a.getAttribute(firstKey_a).equals(n_b.getAttribute(firstKey_a))) {
						//Partial match
						matchNodes(n_a, n_b, false);
						matchBranches(n_a, n_b);
						break;
					}
				} 
			}
		}
	}
	
	/**
	 * Sets the Matching Properties of two Nodes found equal. 
	 * @param node_a
	 * @param node_b
	 * @param equal if nodes are totally equal
	 * Generates a unique id and assigns it to the matched nodes.
	 */
	private static void matchNodes(XmlTreeNode node_a, XmlTreeNode node_b, Boolean equal) {
		match_id++;
		node_a.setMatch_id(match_id);
		node_b.setMatch_id(match_id);	
		if (equal) {
			node_a.setEqual(true);
			node_b.setEqual(true);
		} else {
			node_a.setEqual(false);
			node_b.setEqual(false);
		}
	}
	
	/**
	 * Takes a XML Document and parses it into a XmlTree (w3c.dom -> XmlTreeNode)
	 * @param d Xml Document
	 * @return
	 * @throws Exception if some Error occurs during the parsing process
	 */
	public static XmlTreeNode buildTree(Document d) throws Exception {
		Node r = d.getFirstChild();
		XmlTreeNode root = new XmlTreeNode(r.getNodeName(), null);
		for(int i = 0; i < r.getAttributes().getLength(); i++) {
			root.addAttribute(r.getAttributes().item(i).getNodeName(), r.getAttributes().item(i).getNodeValue());
		}
		if (r.getChildNodes().getLength() < 2) {
			root.addChild(new XmlTreeNode("text", root, r.getTextContent()));
		} else {
			for (int j = 0; j < r.getChildNodes().getLength(); j++) {
				if (!r.getChildNodes().item(j).getNodeName().equals("#text")) {
					root.addChild(buildLeaf(r.getChildNodes().item(j), root));
				}
			}
		}
		return root;
	}
	/**
	 * Recursivly builds up a XmlLeaf from an Node (w3c.dom -> XmlTreeNode)
	 * @param l w3c.dom.Node
	 * @param parent the parent XmlTreeNode (null if given Node is root)
	 * @return a leaf as XmlTreeNode
	 */
	public static XmlTreeNode buildLeaf(Node l, XmlTreeNode parent) {
		XmlTreeNode leaf = new XmlTreeNode(l.getNodeName(), parent);
		//Set attributes
		for(int i = 0; i < l.getAttributes().getLength(); i++) {
			leaf.addAttribute(l.getAttributes().item(i).getNodeName(), l.getAttributes().item(i).getNodeValue());
		}
		if (l.getChildNodes().getLength() < 2) { // If Node has only text content
			leaf.setText(l.getTextContent());
		} else { //Add child leafs recursive
			for (int j = 0; j < l.getChildNodes().getLength(); j++) {
				if (!l.getChildNodes().item(j).getNodeName().equals("#text")) { //Avoid text only child-nodes
					leaf.addChild(buildLeaf(l.getChildNodes().item(j), leaf));
				}
			}
		}
		return leaf;
	}
	
	public static void printTree(XmlTreeNode root, int lev) {
//		String tabs = "";
//		for (int i = 0; i < lev; i++) {
//			tabs += "\t";
//		}
//		String opentag = tabs + "<" + root.getName();
//		for (String key : root.getAttributes().keySet()) {
//			opentag += " " + key + "=\"" + root.getAttribute(key) + "\"";
//		}
//		System.out.println(opentag + ">");
//		if (root.hasText()) {
//			System.out.println(tabs + "\t" + root.getText());
//		} else {
//			for (XmlTreeNode node : root.getChilds()) {
//				printTree(node, lev + 1);
//			}
//		}
//		System.out.println(tabs + "</" + root.getName() + ">");	
	}
	
	/**
	 * Runs recursivly trough a Tree of XmlTreeNode and finds the node with given id
	 * @param root tree-root
	 * @param id 
	 * @return node with given id if found in tree
	 */
	public static XmlTreeNode getNodeByID(XmlTreeNode root, int id) {
		if (root.getMatch_id() == id) {
			return root;
		}
		for (XmlTreeNode n : root.getChilds()) {
			XmlTreeNode node = getNodeByID(n, id);
			if (node != null) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds all nodes containing the given String or regex and marks them as SearchResult
	 * @param search Search String (or regular expression)
	 * @param regex Set to true if search string is a regex
	 * @param node xmlTree to search in
	 * @return true if one or more matches were found
	 */
	public static Boolean find(String search, Boolean regex, XmlTreeNode node) {
		Boolean found = false;
		if (regex) {
			if (node.toString().split(search).length > 1) {
				node.setSearchMatch(true);
				found = true;
			} else {
				node.setSearchMatch(false);
			}
		} else {
			if (node.toString().contains(search)) {
				node.setSearchMatch(true);
				found = true;
			} else {
				node.setSearchMatch(false);
			}
		}		
		for (XmlTreeNode child : node.getChilds()) {
			if (find(search, regex, child)) {
				found = true;
			}
		}
		return found;
	}
}
