package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paa.locker.business.ParcelServiceException;
import paa.locker.business.RemoteParcelService;

public class CreateLockerDialog extends JDialog{
	
	//variables
	private static final long serialVersionUID = 1L;
	private final String[] names = {"Name", "Adress", "Longitude", "Latitude", "Large Compartments", "Small Compartments"};
	private RemoteParcelService ps;
	private JFrame f;
	
	//componentes internos
	private JButton okButton;
	private JButton cancelButton;
	private JTextField[] valoresCreateLocker = new JTextField[names.length];

	public CreateLockerDialog(JFrame f, RemoteParcelService ps) {
		super(f, "Create Locker", true);
		this.ps = ps;
		this.f = f;

		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		
		JPanel panel1 = new JPanel(new GridLayout(0, 2));
		JPanel panel2 = new JPanel(new FlowLayout());
		
		for(int i = 0; i < names.length; i++) {
			panel1.add(new JLabel(names[i]));
			valoresCreateLocker[i] = new JTextField(names[i], 20);
			panel1.add(valoresCreateLocker[i]);
		}
		
		panel2.add(okButton);
		panel2.add(cancelButton);
		
		this.setLayout(new BorderLayout());
		this.add(panel1, BorderLayout.WEST);
		this.add(panel2, BorderLayout.SOUTH);
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("locker.png")).getImage());
		this.pack();
		
		addListeners(this);
		
		this.setResizable(false);
		this.setVisible(true);
		
		
	}
	
	private void addListeners(JDialog d) {
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ps.createLocker(valoresCreateLocker[0].getText(), valoresCreateLocker[1].getText(), Double.parseDouble(valoresCreateLocker[2].getText()), Double.parseDouble(valoresCreateLocker[3].getText()), Integer.parseInt(valoresCreateLocker[4].getText()), Integer.parseInt(valoresCreateLocker[5].getText()));
					LeftLateral.initializeLockerCombo();
					RightLateral.actualizarMapa();
					sucessfullMessage("El locker ha sido creado correctamente");
					d.dispose();
					} catch (NumberFormatException e2) {
						exceptionMessage("Introduzca los datos en el formato correcto. Recuerde que 'small compartments', 'large compartments' y las coordenadas deben ser numeros");
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

