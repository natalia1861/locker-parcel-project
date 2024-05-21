package paa.locker.presentation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import paa.locker.business.RemoteParcelService;

public class ParteSuperior extends JPanel{
	//items de la clase
	private RemoteParcelService ps;
	private JFrame f;
	
	//Botones generales
	private JButton lockerButton;
	private JButton deliverButton;
	private JButton retrieveButton;
	private JPanel panelBotones;

	//Menus generales
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;
	
	//Menus internos
	private JMenuItem lockerMenu;
	private JMenuItem deliverMenu;
	private JMenuItem retrieveMenu;
	private JMenuItem quitMenu;
	private JMenuItem aboutMenu;
	
	private static final long serialVersionUID = 1L;
	public ParteSuperior(RemoteParcelService parcelService, JFrame f) {
		this.ps = parcelService;
		this.f = f;
		
		//items
		
		crearGUI();
		addListeners();
	}
	
	
	private void addListeners() {
		lockerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateLockerDialog(f, ps);
				
			}
			
		});
		
		deliverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateDeliveryDialog(f, ps);
			}
			
		});
		
		retrieveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateRetrieveParcelDialog(f, ps);
			}
		});
		
		lockerMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateLockerDialog(f, ps);
			}
		});
		
		
		deliverMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateDeliveryDialog(f, ps);
			}
		});
		
		retrieveMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateRetrieveParcelDialog(f, ps);
			}
		});
		
		aboutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addHelpActionPane();
			}
		});
		
		quitMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
	

	private void addHelpActionPane() {
		
		JPanel d = new JPanel();
		d.setLayout(new GridLayout(0, 1));
		d.add(new JLabel("Program Name: Parcel Service"));
		d.add(new JLabel("Author: Natalia Agüero"));
		d.add(new JLabel("Version: 1"));
		
		JOptionPane.showMessageDialog(f, d , "About ", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	private void crearGUI() {
		String[] menuNames = {"File", "Help"};
		menuBar = crearMenu(menuNames);
		
		String[] buttonPath = {"locker.png", "deliver.png", "retrieve.png"};
		panelBotones = crearPanelBotones(buttonPath);
		
		this.setLayout(new BorderLayout());
		this.add(menuBar, BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.WEST);
		
	}
	private JMenuBar crearMenu (String[] items) {
		JMenuBar barrademenu = new JMenuBar();
		
		fileMenu = new JMenu(items[0]);
		helpMenu = new JMenu(items[1]);
		
		lockerMenu = new JMenuItem("New Locker");
		deliverMenu = new JMenuItem("Deliver Parcel");
		retrieveMenu = new JMenuItem("Retrieve Parcel");
		aboutMenu = new JMenuItem("About");
		quitMenu = new JMenuItem("Quit");
		
		fileMenu.add(lockerMenu);
		fileMenu.add(deliverMenu);
		fileMenu.add(retrieveMenu);
		fileMenu.add(quitMenu);
		
		helpMenu.add(aboutMenu);
		
		barrademenu.add(fileMenu);
		barrademenu.add(helpMenu);
		
		return barrademenu;
	}


	private JPanel crearPanelBotones(String[] images) {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		lockerButton = crearBoton(images[0]);
		deliverButton = crearBoton(images[1]);
		retrieveButton = crearBoton(images[2]);
		
		jp.add(lockerButton);
		jp.add(deliverButton);
		jp.add(retrieveButton);
		
		return jp;
		
	}
	
	private JButton crearBoton(String img) {
		return new JButton(new ImageIcon(getClass().getClassLoader().getResource(img)));
	}

	/*
	private void seeDirectory() {
		 File f = new File("");
		 System.out.println(f.getAbsolutePath());
	}
	*/
}
