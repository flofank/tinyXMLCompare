import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


public class XmlTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = -2254522696802482663L;
	private boolean compared = false;
	private boolean searching = false;

	public XmlTreeCellRenderer() {
		super();
		setOpenIcon(null);
		setClosedIcon(null);
		setLeafIcon(null);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree arg0, Object arg1, boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {
		Component comp = super.getTreeCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		XmlTreeNode node = (XmlTreeNode) arg1;
		setBackgroundNonSelectionColor(Color.WHITE);
		if (searching && node.isSearchMatch()) {
			setBackgroundNonSelectionColor(new Color(100, 255, 100));
		} else if (compared) {
			if (node.hasMatch()) {
				if (!node.isEqual()) {
					setBackgroundNonSelectionColor(new Color(255, 240, 128));
				}
			} else {
				setBackgroundNonSelectionColor(new Color(255,128,128));
			}
		}
		return comp;
	}

	public boolean isSearching() {
		return searching;
	}

	public void setSearching(boolean searching) {
		this.searching = searching;
	}

	public boolean isCompared() {
		return compared;
	}

	public void setCompared(boolean compared) {
		this.compared = compared;
	}
	
	
}
