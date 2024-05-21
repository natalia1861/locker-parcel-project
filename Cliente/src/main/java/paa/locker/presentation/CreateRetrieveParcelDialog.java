package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import paa.locker.business.ParcelServiceException;
import paa.locker.business.RemoteParcelService;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;

public class CreateRetrieveParcelDialog extends JDialog {

	//variables
	private static final long serialVersionUID = 1L;
	
	private RemoteParcelService ps;
	private JFrame f;
	
	//componentes
	JComboBox<Locker> comboLockers = new JComboBox<Locker>();
	JComboBox<Parcel>comboParcels = new JComboBox<Parcel>();
	JButton okButton;
	JButton cancelButton;
	
	public CreateRetrieveParcelDialog(JFrame f, RemoteParcelService ps) {
		super(f, "Retrieve Parcel", true);
		this.f = f;
		this.ps = ps;
		
		initializeComboLockers();
		initializeComboParcels();
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		
		JPanel panel1 = new JPanel(new GridLayout(0, 2));
		JPanel panel2 = new JPanel(new FlowLayout());

		panel1.add(new JLabel("Locker: "));
		panel1.add(comboLockers);
		panel1.add(new JLabel("Parcel: "));
		panel1.add(comboParcels);

		panel2.add(okButton);
		panel2.add(cancelButton);

		this.add(panel1, BorderLayout.WEST);
		this.add(panel2, BorderLayout.SOUTH);

		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("retrieve.png")).getImage());
		this.pack();
		this.setMinimumSize(new Dimension(590, 125));
		
		addListeners(this);				//los listeners se añaden antes que se haga visible el dialogo!
		this.setResizable(false);
		this.setVisible(true);
	}
	
	/* InitializeComboLockers - método privado
	 *  Permite iniciar el combobox para elegir de que Locker eliminar los Parcels
	 */
	
	private void initializeComboLockers() {
		List<Locker>lockerList = ps.findAllLockers();
		for (int i = 0; i < lockerList.size(); i++) {
			comboLockers.addItem(lockerList.get(i));
		}
	}
	
	/* InitializeComboParcels - método privado
	 * Permite inicializar el combobox de Parcels según el Locker seleccionado
	 */
	private void initializeComboParcels() {
		
		Locker l = null;
		List<Parcel> parcelList = null;
		
		comboParcels = new JComboBox<Parcel> ();						//reiniciamos el combobox (comboParcels.removeAllItems();)
		
		if ((l = (Locker) comboLockers.getSelectedItem()) != null) {		//si hay algun locker seleccionado, se ponen sus Parcels en dicho combobox
			parcelList = ps.findLocker(l.getCode()).getParcels();		//si el Locker es null da error porque no se puede hacer l.getCode()
			for (int i = 0; i < parcelList.size(); i++) {
				comboParcels.addItem(parcelList.get(i));
			}
		}
		
	}

	private void addListeners(JDialog d) {
		comboLockers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					initializeComboParcels();
				} catch (ParcelServiceException ex) {
					exceptionMessage(ex.getMessage());
				}
			}
			
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Parcel p = (Parcel) comboParcels.getSelectedItem();
					ps.retrieveParcel(p.getCode());
					LeftLateral.mostrarParcels();
					RightLateral.actualizarMapa();
					sucessfullMessage("El paquete ha sido recogido correctamente");
					d.dispose();
					
					} catch (NullPointerException ex) {
						exceptionMessage("Debe seleccionar un item");
					} catch (ParcelServiceException ex) {
						exceptionMessage(ex.getMessage());
					}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
			
		});
	}
	
	private void sucessfullMessage(String msg) {
		JOptionPane.showMessageDialog(f, msg , "Information ", JOptionPane.CLOSED_OPTION);
	}
	
	private void exceptionMessage(String msg) {
		JOptionPane.showMessageDialog(f, msg , "Error ", JOptionPane.ERROR_MESSAGE);
	}
}