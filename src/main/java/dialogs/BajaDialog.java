/**
 * 
 */
package dialogs;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import modelo.Empresa;

import static dao.AccesoTrabajador.eliminarTrabajadorId;

/**
 * 
 * @author usuario
 *
 */
public class BajaDialog extends JDialog implements ActionListener, ItemListener {

	JButton aceptar;
	JButton cancelar;

	JPanel panelBotones;
	JLabel texto;

	JLabel identificador;
	JTextField areaIdentificador;
	int id = 0;
	JPanel panel;



	JPanel pIdentificador;


	Empresa empresa;

	public BajaDialog(Empresa empresa) {
		this.empresa = empresa;

		setResizable(false);
		// titulo del dialog
		setTitle("Baja Trabajador");
		setSize(300, 200);
		setLayout(new FlowLayout());
		setLocationRelativeTo(null);

		texto = new JLabel("<html>Introduzca el ID del trabajador<br> que desea dar de baja<br><br></html>");
		add(texto);

		panel = new JPanel();
		panelBotones = new JPanel();
		add(panel);
		add(panelBotones);



		identificador = new JLabel("Identificador");
		areaIdentificador = new JTextField(15);
		panel.add(identificador);
		panel.add(areaIdentificador);

		aceptar = new JButton("Aceptar");
		aceptar.addActionListener(this);
		panelBotones.add(aceptar);

		cancelar = new JButton("Cancelar");
		cancelar.addActionListener(this);
		panelBotones.add(cancelar);
		// Visible
		setVisible(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == aceptar) {
			int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea dar de baja el trabajador?", "Borrar",
					JOptionPane.YES_NO_OPTION);
			switch (respuesta) {
				case JOptionPane.YES_OPTION:
				try {
					id = Integer.parseInt(areaIdentificador.getText());

					if (comprobarErrores()) {
						// Operaciones en caso afirmativo
						if (eliminarTrabajadorId(id)) {
							JOptionPane.showMessageDialog(this, "El trabajador se ha eliminado correctamente");
						} else {
							JOptionPane.showMessageDialog(null, "El trabajador no se encuentra en la a base de datos", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}

					break;
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "El ID debe ser un numero entero positivo > 0", "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			case JOptionPane.NO_OPTION:
				// Operaciones en caso negativo
				break;
			}
		} else if (e.getSource() == cancelar) {
			dispose();
		}

	}

	public boolean comprobarErrores() {
		if (id < 1) {
			JOptionPane.showMessageDialog(null, "El ID debe ser un numero entero positivo", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}


	@Override
	public void itemStateChanged(ItemEvent e) {

	}
}
