package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import paa.locker.business.ParcelServiceException;
import paa.locker.business.RemoteParcelService;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;

public class LeftLateral extends JPanel{

	private static final long serialVersionUID = 1L;
	private static RemoteParcelService ps;
	private JFrame f;
	
	private static JComboBox<Locker> comboLockers = new JComboBox<Locker>();
	private static JList<String> listaParcels;
	
	public LeftLateral(RemoteParcelService ps, JFrame f) {
		this.f = f;
		LeftLateral.ps = ps;
		this.setLayout(new BorderLayout());
		
		initializeLockerCombo();
		mostrarParcelsINI();
		
		this.add(comboLockers, BorderLayout.NORTH);
		this.add(listaParcels);
		this.setVisible(true);
		
		addListeners();
	
	}
	
	/* Método público. Lo utilizan varias clases
	 * Método que permite mostrar los Parcels de un Locker según la selección del combobox
	 * Se utiliza internamente, o cuando se hace alguna modificacion en los datos, como crear un Parcel o recogerlo
	 */
	
	public static void mostrarParcels() {
		
		Locker l = (Locker) comboLockers.getSelectedItem();
		List<Parcel> parcelList = ps.findLocker(l.getCode()).getParcels();
		Vector<String> v = new Vector<String>();
		
		for(Parcel p: parcelList) {
			v.add(p.toString());
		}
		
		listaParcels.setListData(v);
	}
	
	/* Metodo publico
	 * Metodo que permite cambiar los items del combobox.
	 * Lo utiliza la clase internamente, y tambien otras para cuando se crea un nuevo Locker, este aparezca en esta lista
	 */ 
	public static void initializeLockerCombo() {
		List<Locker> lockerList = ps.findAllLockers();
			comboLockers.removeAllItems();
			for (int i = 0; i < lockerList.size(); i++) {
				comboLockers.addItem(lockerList.get(i));
			}
			comboLockers.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray), "Lockers"));
	}
	/* Metodo interno
	 * Muestra todos los paquetes iniciales sin tener en cuenta el locker seleccionado del combobox (solo se usa una vez)
	 */
	private void mostrarParcelsINI() {
		
		List<Locker> lockerList = ps.findAllLockers();
		List<Parcel> parcelList = null;
		Vector<String> v = new Vector<String>();
		
		for (Locker l : lockerList) {
			parcelList = l.getParcels();
			//parcelList.forEach(x -> v.add(x.toString())); otra forma de hacerlo
			for(Parcel p: parcelList) {
				v.add(p.toString());
			}
		}
		
		listaParcels = new JList<String> (v);
		listaParcels.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray), "Parcels"));
	}
	
	private void addListeners() {
		comboLockers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarParcels();
				} catch (NullPointerException ex) {
					//nada, este evento saltara al modificar el combo por crear un locker (cuando sea llamado por otra clase)
				} catch(ParcelServiceException ex) {
					exceptionMessage(ex.getMessage());
				}
			}
			
		});
	}
	
	private void exceptionMessage(String msg) {
		JOptionPane.showMessageDialog(f, msg , "Error ", JOptionPane.ERROR_MESSAGE);
	}

}
