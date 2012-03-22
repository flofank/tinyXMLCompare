import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class InformationDialog extends JDialog{
	private static final long serialVersionUID = -9061081389547704985L;

	public InformationDialog(JFrame owner) {
		super(owner);
		setTitle("tinyXMLCompare - Application Informations");
		setBounds(200, 200, 545, 398);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("tinyXMLCompare");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setBounds(10, 11, 145, 14);
		getContentPane().add(lblNewLabel);
		
		JTextPane txtpnEineApplikationDer = new JTextPane();
		txtpnEineApplikationDer.setEnabled(false);
		txtpnEineApplikationDer.setEditable(false);
		txtpnEineApplikationDer.setText("Eine Applikation der Schweizerischen Post zum Vergleich von XML-Dateien\r\n\r\nGrundlegende Bedienungshinweise:\r\n\r\n1. Farbcode:\r\n\t- Weiss sind alle Tags welche beim Vergleich im beiden Dokumente identisch (Auch \r\n\tbez\u00FCglich ihrer Child-Tags) gefunden wurden.\r\n\t- Gelb-Orange sind alle Tags welche entweder exakt \u00FCbereinstimmen aber verschiedene \r\n\tChild-Tags haben oder nicht komplett aber zumindest in Tag-Name und erstem Attribut \r\n\t\u00FCbereinstimmen.\r\n\t- Rot sind alle Tags zu welchen im anderen Dokument kein zumindest in Tag-Name und \r\n\terstem Attribut \u00FCbereinstimmendes Tag gefunden wurde.\r\n\t- Gr\u00FCn markiert sind Suchresultate.\r\n\r\n2. Suche:\r\n\t- Gesucht werden kann entweder nach gleichen Zeichenketten (Case-Sensitive) oder \r\n\tnach Regular Expressions.\r\n\t- Zwischen den beiden M\u00F6glichkeiten kann im Options Menu gewechselt werden.\r\n\t- Die Suche wird nach Eingabe des Suchstrings mit Enter gestartet.\r\n");
		txtpnEineApplikationDer.setBounds(10, 36, 506, 280);
		getContentPane().add(txtpnEineApplikationDer);
		
		JButton btnSchliessen = new JButton("Schliessen");
		btnSchliessen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		btnSchliessen.setBounds(430, 328, 89, 23);
		getContentPane().add(btnSchliessen);
	}
	
	private void close() {
		super.dispose();
	}
}
