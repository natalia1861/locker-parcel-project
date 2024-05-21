package paa.locker.presentation;

import java.time.LocalDate;

import javax.swing.SwingUtilities;

import paa.locker.business.RemoteParcelService;
import paa.locker.model.Locker;

public class pruebas {
	private static RemoteParcelService ps = new RemoteParcelService();
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//crearBase();
				new ProgramaFinal("Locker Manager");
			}
		});

	}
	
	private static void crearBase() {
		Locker l1 = ps.createLocker("Locker1", "Vallecas", 0, 0, 5, 3);
		Locker l2 = ps.createLocker("Locker2", "Madrid", 3, 5, 5, 2);
		ps.deliverParcel(l1.getCode(),12, 0.5f, LocalDate.now().minusDays(2));
		ps.deliverParcel(l2.getCode(),15, 0.5f, LocalDate.now());
		ps.deliverParcel(l2.getCode(),15, 0.5f, LocalDate.now().plusDays(10));
		ps.deliverParcel(l2.getCode(),15, 5f, LocalDate.now().plusDays(10));
	}

}
