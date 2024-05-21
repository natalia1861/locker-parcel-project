package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import paa.locker.business.RemoteParcelService;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;
import paa.locker.util.LockerMap;

public class RightLateral extends JPanel{
	private static final long serialVersionUID = 3L;
	
	private static RemoteParcelService ps;
	private static LockerMap map;
	private static JComboBox<LocalDate> comboFechas = new JComboBox<LocalDate>();

	
	public RightLateral(RemoteParcelService ps) {
		RightLateral.ps = ps;
		map = new LockerMap(200, 200, ps);
		
		this.setLayout(new BorderLayout());
		
		crearGUI();
		actualizarMapa();
		addListeners();
		
		this.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue), "Locker Map"));
		
		this.add(comboFechas, BorderLayout.NORTH);
		this.add(map, BorderLayout.CENTER);

	}
	
	/* Actualizar combo - método publico y estático
	 * Unicamente se actualiza cuando se crea un Parcel (al inicio de la clase no se llama)
	 * 
	 */
	public static void actualizarCombo() {
		List<Locker> lista = ps.findAllLockers();
		List<Parcel> parcels;
		Set<LocalDate> dates = new HashSet<LocalDate>();
		
		comboFechas.removeAllItems();
		
		for(Locker l: lista) {
			parcels = l.getParcels();
			parcels.forEach(x -> dates.add(x.getArrivalDate()));
		}
		
		for(LocalDate s : dates) {
			comboFechas.addItem(s);
		}
	}
	/* ActualizarMapa - método publico y estático
	 * Se utiliza para actualizar los lockers en el mapa cuando se crea uno nuevo, se crea un Parcel
	 * o en el inicio de la clase
	 * 
	 * Cuidado! si el combobox de las fechas esta vacío (no hay ningún Parcel) puede saltar un nullPointerException
	 */
	public static void actualizarMapa() {
		
		LocalDate date;

		if ((date = (LocalDate) comboFechas.getSelectedItem()) == null) {		//SI NO HAY NINGUN PARCEL CREADO
			date = LocalDate.now();
		}
		
		map.showAvailability(date);

	}
	
	private void crearGUI() {
		actualizarCombo();
	}
	
	private void addListeners() {
		comboFechas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				actualizarMapa();
				} catch (NullPointerException ex) {
					//nada, esta excepcion se produce al añadir las fechas de nuevo
					//que se detecta como un action event (lo haran saltar otras clases, cuando crean un parcel)
				}
			}
			
		});
	}
}
