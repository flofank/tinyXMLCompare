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
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;


//Comment
public class UI extends JFrame implements DropTargetListener {
	private Document xml_a, xml_b;
	private XmlTreeNode root_a, root_b;
	private JTree tree_a, tree_b;
	private JLabel lbl_path_a, lbl_path_b;
	private JScrollPane scrollPane_a, scrollPane_b;
	private DropTarget drop_a, drop_b;
	private XmlTreeCellRenderer renderer = new XmlTreeCellRenderer();
	private JPanel pn_top;
	private JPanel pn_center;
	private JPanel pn_openfile_a;
	private JPanel pn_openfile_b;
	private JPanel pn_compare;
	private JPanel pn_paths;
	private JPanel pn_path_a;
	private JPanel pn_path_b;
	private JSplitPane splitPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnAction;
	private JMenu mnOptions;
	private JMenu mnInformation;
	private JMenuItem mntmCompare;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		UI window = new UI();
		if (args.length == 2) {
			window.loadFile(args[0], true);
			window.loadFile(args[1], false);
		}
	}

	/**
	 * Create the application.
	 */
	public UI() {
		super();
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mnAction = new JMenu("Action");
		menuBar.add(mnAction);
		
		mntmCompare = new JMenuItem("Compare");
		mntmCompare.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				compare();
			}
		});
		mnAction.add(mntmCompare);
		
		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		mnInformation = new JMenu("Information");
		menuBar.add(mnInformation);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {System.out.println("Setting look an feel failed");}
		setTitle("tinyXMLCompare");
		setBounds(100, 100, 900, 740);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		pn_top = new JPanel();
		getContentPane().add(pn_top, BorderLayout.NORTH);
		pn_top.setLayout(new BorderLayout(0, 0));
		
		pn_openfile_a = new JPanel();
		pn_top.add(pn_openfile_a, BorderLayout.WEST);
		pn_openfile_a.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//Buttons
		JButton btn_file_a = new JButton("Open File");
		pn_openfile_a.add(btn_file_a);
		btn_file_a.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseFile(true);
			}
		});
		
		pn_openfile_b = new JPanel();
		pn_top.add(pn_openfile_b, BorderLayout.EAST);
		pn_openfile_b.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JButton btn_file_b = new JButton("Open File");
		pn_openfile_b.add(btn_file_b);
		btn_file_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile(false);
			}
		});
		
		pn_compare = new JPanel();
		pn_top.add(pn_compare, BorderLayout.CENTER);
		
		pn_paths = new JPanel();
		pn_top.add(pn_paths, BorderLayout.SOUTH);
		pn_paths.setLayout(new GridLayout(0, 2, 0, 0));
		
		pn_path_a = new JPanel();
		FlowLayout fl_pn_path_a = (FlowLayout) pn_path_a.getLayout();
		fl_pn_path_a.setAlignment(FlowLayout.LEFT);
		pn_paths.add(pn_path_a);
		//Labels
		lbl_path_a = new JLabel("Path a");
		pn_path_a.add(lbl_path_a);
		lbl_path_a.setBackground(Color.WHITE);
		
		pn_path_b = new JPanel();
		FlowLayout fl_pn_path_b = (FlowLayout) pn_path_b.getLayout();
		fl_pn_path_b.setAlignment(FlowLayout.RIGHT);
		pn_paths.add(pn_path_b);
		lbl_path_b = new JLabel("Path b");
		pn_path_b.add(lbl_path_b);
		lbl_path_b.setBackground(Color.WHITE);
		lbl_path_b.setHorizontalAlignment(SwingConstants.RIGHT);
		
		splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);
		//JScrollPanes
		scrollPane_a = new JScrollPane();
		scrollPane_b = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_a);
		splitPane.setRightComponent(scrollPane_b);
		System.out.println(splitPane.getWidth());
		//Trees
		tree_a = new JTree(new DefaultTreeModel(new XmlTreeNode("EmptyXml", null)));
		tree_a.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				String id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id != null) {
					XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
					if (b != null) {
						tree_b.collapsePath(b.getPath());
					}
				}
			}
			public void treeExpanded(TreeExpansionEvent event) {
				String id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id != null) {
					XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
					if (b != null) {
						tree_b.expandPath(b.getPath());
					}
				}
			}
		});
		tree_a.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				try {
					String id = ((XmlTreeNode) tree_a.getLastSelectedPathComponent()).getMatch_id();
					if (id != null) {
						XmlTreeNode b = XmlUtilities.getNodeByID(root_b, id);
						if (b != null) {
							tree_b.setSelectionPath(b.getPath());
						}
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		});
		tree_a.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tree_a.setCellRenderer(renderer);
		tree_a.setFocusable(true);
		scrollPane_a.setViewportView(tree_a);
		tree_a.setDragEnabled(true);
		drop_a = new DropTarget(tree_a, this);
		tree_b = new JTree(new DefaultTreeModel(new XmlTreeNode("EmptyXml", null)));
		tree_b.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tree_b.setCellRenderer(renderer);
		tree_b.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				String id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id != null) {
					XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
					if (a != null) {
						tree_a.collapsePath(a.getPath());
					}
				}
			}
			public void treeExpanded(TreeExpansionEvent event) {
				String id = ((XmlTreeNode)  event.getPath().getLastPathComponent()).getMatch_id();
				if (id != null) {
					XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
					if (a != null) {
						tree_a.expandPath(a.getPath());
					}
				}
			}
		});
		tree_b.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				try {
					String id = ((XmlTreeNode) tree_b.getLastSelectedPathComponent()).getMatch_id();
					if (id != null) {
						XmlTreeNode a = XmlUtilities.getNodeByID(root_a, id);
						if (a != null) {
							tree_a.setSelectionPath(a.getPath());
						}
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		});
		scrollPane_b.setViewportView(tree_b);
		setVisible(true);
		splitPane.setDividerLocation(0.5);
		tree_b.setDragEnabled(true);
		drop_b = new DropTarget(tree_b, this);
	}
	
	private void chooseFile(boolean left) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a xml");
		fc.showOpenDialog(this);
		File f = fc.getSelectedFile();
		loadFile(f.getAbsolutePath(), left);		
	}
	
	private void loadFile(String path, boolean left) {
		try {
			renderer.setCompared(false);
			if (left) {
				lbl_path_a.setText(path);
				xml_a = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
				root_a = XmlUtilities.buildTree(xml_a);
				tree_a.setModel(new DefaultTreeModel(root_a));
				this.repaint();
				
			} else {
				lbl_path_b.setText(path);
				xml_b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
				root_b = XmlUtilities.buildTree(xml_b);
				tree_b.setModel(new DefaultTreeModel(root_b));
				this.repaint();
			}
		} catch (Exception e) {
			System.out.println("Reading file failed for path: " + path);
			e.printStackTrace();
		}
	}
	
	private void compare() {
		renderer.setCompared(true);
		XmlUtilities.matchTrees(root_a, root_b);
		this.repaint();
	}
	

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
						loadFile(((File) files.get(0)).getAbsolutePath(), true);
					} else if (e.getDropTargetContext().getDropTarget() == drop_b) {
						loadFile(((File) files.get(0)).getAbsolutePath(), false);
					}
				} else if (files.size() > 1) {
					loadFile(((File) files.get(0)).getAbsolutePath(), true);
					loadFile(((File) files.get(1)).getAbsolutePath(), false);
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
