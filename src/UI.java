import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;


//Comment
public class UI extends JFrame implements DropTargetListener {	
	private Document xml_a, xml_b;
	private XmlTreeNode root_a, root_b;
	
	private DropTarget drop_a, drop_b;
	private XmlTreeCellRenderer renderer = new XmlTreeCellRenderer();
	
	//Main UI
	private JSplitPane splitPane;
	private JScrollPane scrollPane_a, scrollPane_b;
	private JTree tree_a, tree_b;
	//Top Interface
	private JPanel pn_top;
	private JPanel pn_paths;
	private JLabel lbl_path_a, lbl_path_b;
	private JButton btn_file_a, btn_file_b;
	private JTextField txtSuche;
	//MenuBar
	private JMenuBar menuBar;
	private JMenu mnFile, mnAction, mnOptions, mnInformation;
	private JMenuItem mntmCompare, mntmSearch;
	private JRadioButtonMenuItem mnrd_AutoExp;
	private JMenu mnView;
	private JMenuItem mntmCollapseAll;
	private JMenuItem mntmExpandAll;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		UI window = new UI();
		try {
			if (args.length == 2) {
				window.loadFile(new File(args[0]), true);
				window.loadFile(new File(args[1]), false);
			}
		} catch (Exception e) {
			System.out.println("File paths not valid");
		}		
	}

	/**
	 * Create the application.
	 */
	public UI() {
		super();
		initMenu();		
		initUI();
		setVisible(true);
		splitPane.setDividerLocation(0.5); //Has to stand after setVisible
	}
	/**
	 * Initialize the MenuBar
	 */
	private void initMenu() {
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		//Menus
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		mnAction = new JMenu("Action");
		menuBar.add(mnAction);
		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmCollapseAll = new JMenuItem("Collapse All");
		mntmCollapseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root_a.collapseAll(tree_a);
				root_b.collapseAll(tree_b);
			}
		});
		mnView.add(mntmCollapseAll);
		
		mntmExpandAll = new JMenuItem("Expand All");
		mntmExpandAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				root_a.expandAll(tree_a);
				root_b.expandAll(tree_b);
			}
		});
		mnView.add(mntmExpandAll);
		mnInformation = new JMenu("Information");
		menuBar.add(mnInformation);
		//Actions
		mntmCompare = new JMenuItem("Compare");
		mntmCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				compare();
			}
		});
		mnAction.add(mntmCompare);
		mntmSearch = new JMenuItem("Search");
		mnAction.add(mntmSearch);
		//Options		
		mnrd_AutoExp = new JRadioButtonMenuItem("Automatic Expand");
		mnOptions.add(mnrd_AutoExp);		
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initUI() {		
		//Initialize JFrame
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {System.out.println("Setting look an feel failed");}
		this.setTitle("tinyXMLCompare");
		this.setBounds(100, 100, 900, 740);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 5));
		
		initTrees();
		initTopInterface();
	}
	
	/**
	 * Initialize the trees
	 */
	private void initTrees() {
		//Panes
		splitPane = new JSplitPane();
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		scrollPane_a = new JScrollPane();
		scrollPane_b = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_a);
		splitPane.setRightComponent(scrollPane_b);
		
		//Trees
		tree_a = new JTree(new DefaultTreeModel(new XmlTreeNode("EmptyXml", null)));
		tree_a.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tree_a.setCellRenderer(renderer);
		tree_a.setFocusable(true);
		scrollPane_a.setViewportView(tree_a);
		tree_b = new JTree(new DefaultTreeModel(new XmlTreeNode("EmptyXml", null)));
		tree_b.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tree_b.setCellRenderer(renderer);	
		scrollPane_b.setViewportView(tree_b);
		
		addTreeListeners();
	}
	/**
	 * Add action Listeners to trees
	 */
	private void addTreeListeners() {
		//Expand an Collapse Listeners
		tree_a.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				int id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id > 0) {
					XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
					if (b != null) {
						tree_b.collapsePath(b.getPath());
					}
				}
			}
			public void treeExpanded(TreeExpansionEvent event) {
				int id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id > 0) {
					XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
					if (b != null) {
						tree_b.expandPath(b.getPath());
					}
				}
			}
		});
		tree_b.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				int id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id > 0) {
					XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
					if (a != null) {
						tree_a.collapsePath(a.getPath());
					}
				}
			}
			public void treeExpanded(TreeExpansionEvent event) {
				int id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id > 0) {
					XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
					if (a != null) {
						tree_a.expandPath(a.getPath());
					}
				}
			}
		});
		
		//Select Listeners
		tree_a.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				try {
					XmlTreeNode node = (XmlTreeNode) tree_a.getLastSelectedPathComponent();
					int id = node.getMatch_id();
					if (id > 0) {
						XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
						if (b != null) {
							tree_b.setSelectionPath(b.getPath());
						}
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		});
		tree_b.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				try {
					XmlTreeNode node = (XmlTreeNode) tree_b.getLastSelectedPathComponent();
					int id = node.getMatch_id();
					if (id > 0) {
						XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
						if (a != null) {
							tree_a.setSelectionPath(a.getPath());
						}
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		});
		
		//Drop Targets
		tree_a.setDragEnabled(true);
		tree_b.setDragEnabled(true);
		drop_a = new DropTarget(tree_a, this);
		drop_b = new DropTarget(tree_b, this);
	}
	/**
	 * Initialize the Top Interface
	 */
	private void initTopInterface() {
		pn_top = new JPanel();
		this.getContentPane().add(pn_top, BorderLayout.NORTH);
		pn_top.setLayout(new BorderLayout(5, 0));
		//File Loading Buttons
		btn_file_a = new JButton("");
		btn_file_a.setIcon(new ImageIcon(UI.class.getResource("/icons/open_16.png")));
		btn_file_a.setToolTipText("Load XML Left");
		btn_file_a.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseFile(true);
			}
		});
		pn_top.add(btn_file_a, BorderLayout.WEST);
		btn_file_b = new JButton("");
		btn_file_b.setIcon(new ImageIcon(UI.class.getResource("/icons/open_16.png")));
		btn_file_b.setToolTipText("Load XML Right");
		btn_file_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(false);
			}
		});
		pn_top.add(btn_file_b, BorderLayout.EAST);
		
		//Path Labels and Search Field
		pn_paths = new JPanel();
		pn_paths.setLayout(new GridLayout(0, 3, 0, 0));
		pn_top.add(pn_paths, BorderLayout.CENTER);
		lbl_path_a = new JLabel("Path a");
		lbl_path_a.setBackground(Color.WHITE);
		pn_paths.add(lbl_path_a);
		txtSuche = new JTextField();
		txtSuche.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (txtSuche.getText().equals("")) {
						txtSuche.setText("Suche ...");
						renderer.setSearching(false);
					} else {
						renderer.setSearching(true);
						if (XmlUtilities.find(txtSuche.getText(), false, root_a) | XmlUtilities.find(txtSuche.getText(), false, root_b)) {
							root_a.collapseAll(tree_a);
							root_b.collapseAll(tree_b);
							root_a.expandSearchMatches(tree_a);
							root_b.expandSearchMatches(tree_b);						
						}
					}
					repaint();
				}
			}
		});
		txtSuche.setText("Suche ...");
		pn_paths.add(txtSuche);
		lbl_path_b = new JLabel("Path b");
		lbl_path_b.setBackground(Color.WHITE);
		lbl_path_b.setHorizontalAlignment(SwingConstants.RIGHT);
		pn_paths.add(lbl_path_b);
	}
	
	/**
	 * Show FileChooseDialog and set chosen File to loadFile method
	 * @param left
	 */
	private void chooseFile(boolean left) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a xml");
		fc.showOpenDialog(this);
		File f = fc.getSelectedFile();
		loadFile(f, left);		
	}
	/**
	 * Load given File into given tree
	 * @param f File to load 
	 * @param left if left tree is target
	 */
	private void loadFile(File f, boolean left) {
		try {
			renderer.setCompared(false);
			if (left) {
				lbl_path_a.setText(f.getAbsolutePath());
				xml_a = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
				root_a = XmlUtilities.buildTree(xml_a);
				tree_a.setModel(new DefaultTreeModel(root_a));
				this.repaint();
				
			} else {
				lbl_path_b.setText(f.getAbsolutePath());
				xml_b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
				root_b = XmlUtilities.buildTree(xml_b);
				tree_b.setModel(new DefaultTreeModel(root_b));
				this.repaint();
			}
		} catch (Exception e) {
			System.out.println("Reading file failed for path: " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	private void compare() {
		renderer.setCompared(true);
		XmlUtilities.matchTrees(root_a, root_b);
		this.repaint();
	}
	

	/**
	 * Drag and Drop Interface implementations
	 */
	@Override
	public void dragEnter(DropTargetDragEvent arg0) {}
	public void dragExit(DropTargetEvent arg0) {}
	public void dragOver(DropTargetDragEvent arg0) {}
	public void dropActionChanged(DropTargetDragEvent arg0) {}
	public void drop(DropTargetDropEvent e) {
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				List files = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() == 1) {
					if (e.getDropTargetContext().getDropTarget() == drop_a) {
						loadFile((File) files.get(0), true);
					} else if (e.getDropTargetContext().getDropTarget() == drop_b) {
						loadFile((File) files.get(0), false);
					}
				} else if (files.size() > 1) {
					loadFile((File) files.get(0), true);
					loadFile((File) files.get(1), false);
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
