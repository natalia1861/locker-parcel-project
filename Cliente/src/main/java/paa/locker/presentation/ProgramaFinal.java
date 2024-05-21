package paa.locker.presentation;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import paa.locker.business.RemoteParcelService;



public class ProgramaFinal extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private RemoteParcelService parcelService;
	private ParteSuperior ps;
	private LeftLateral ll;
	private RightLateral rl;
	
	public ProgramaFinal(String title) {
		super(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		parcelService = new RemoteParcelService();
		
		this.setLayout(new BorderLayout());
		
		ps = new ParteSuperior(parcelService, this);
		ll = new LeftLateral(parcelService, this);
		rl = new RightLateral(parcelService);
		
		this.add(ps, BorderLayout.NORTH);
		this.add(ll, BorderLayout.WEST);
		this.add(rl, BorderLayout.CENTER);
		
		this.pack();
		this.setVisible(true);
	}

}
