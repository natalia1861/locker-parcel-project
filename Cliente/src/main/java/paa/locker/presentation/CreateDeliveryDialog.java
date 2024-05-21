package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paa.locker.business.ParcelServiceException;
import paa.locker.business.RemoteParcelService;
import paa.locker.model.Locker;

public class CreateDeliveryDialog extends JDialog {

	//variables
	private static final long serialVersionUID = 1L;
	String[] names = {"Addressee (number) ", "Weight (kg) ", "Arrival Day: yyyy-mm-dd "};
	String[] etiquetas = {"Addressee", "kg", "2013-08-12"};
	private JFrame f;
	private RemoteParcelService ps;
	
	//private boolean done = false;
	
	//componentes
	
	JComboBox<Locker> comboLockers = new JComboBox<Locker>();
	JTextField[] valoresDeliverParcel = new JTextField[names.length];
	JButton okButton;
	JButton cancelButton;
	
	public CreateDeliveryDialog(JFrame f, RemoteParcelService ps) {
		super(f, "Deliver Parcel", true);
		this.f = f;
		this.ps = ps;

		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		
		JPanel panel1 = new JPanel(new GridLayout(0, 2));
		JPanel panel2 = new JPanel(new FlowLayout());

		
		for(int i = 0; i  < names.length; i++) {
			panel1.add(new JLabel(names[i]));
			valoresDeliverParcel[i] = new JTextField(etiquetas[i], 20);
			panel1.add(valoresDeliverParcel[i]);
		}
		
		initializeComboLockers();
		
		panel1.add(new JLabel("Lockers"));
		panel1.add(comboLockers);
		
		panel2.add(okButton);
		panel2.add(cancelButton);
		
		this.add(panel1, BorderLayout.NORTH);
		this.add(panel2, BorderLayout.SOUTH);
		
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("deliver.png")).getImage());
		addListeners(this);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	private void addListeners(JDialog d) {
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Long code = ((Locker) comboLockers.getSelectedItem()).getCode();
					ps.deliverParcel(code, Integer.parseInt(valoresDeliverParcel[0].getText()) , Float.parseFloat(valoresDeliverParcel[1].getText()), LocalDate.parse((valoresDeliverParcel[2].getText())));
					sucessfullMessage("El paquete ha sido reservado correctamente");
					RightLateral.actualizarCombo();
					LeftLateral.mostrarParcels();
					d.dispose();
					} catch (NullPointerException | NumberFormatException | DateTimeParseException ex) {
						exceptionMessage("Introduzca el formato de datos adecuado");
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
	
	private void initializeComboLockers() {
		List<Locker>lockerList = ps.findAllLockers();
		
		for (int i = 0; i < lockerList.size(); i++) {
			comboLockers.addItem(lockerList.get(i));
		}
	}
}
