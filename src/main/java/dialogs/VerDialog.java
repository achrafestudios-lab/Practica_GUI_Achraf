package dialogs;

import modelo.Empresa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerDialog extends JDialog implements ActionListener {

    Empresa empresa;
    JTable tabla;
    JButton cerrar;
    JButton buscar;
    JPanel pBotones;

    JLabel etiquetaIdentificador;
    JTextField areaIdentificador;
    JPanel pIdentificador;


    public VerDialog(Empresa empresa) {
        this.empresa = empresa;

        // una fila por JPanel
        pIdentificador = new JPanel();

        // Se crean los elementos y se añaden
        etiquetaIdentificador = new JLabel("Identificador");
        areaIdentificador = new JTextField(15);
        // Se añaden al JPanel
        pIdentificador.add(etiquetaIdentificador);
        pIdentificador.add(areaIdentificador);

        add(pIdentificador);

        pBotones = new JPanel();
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotones.add(buscar);

        add(pBotones);

//		aceptar = new JButton("Aceptar");
//		aceptar.addActionListener(this);
//		pBotones.add(aceptar);
//
//		cancelar = new JButton("Cancelar");
//		cancelar.addActionListener(this);
//		pBotones.add(cancelar);
//
//		add(pBotones);


        // Impide cambiar tamaño ventana
        setResizable(false);
        // t�tulo del dialog
        setTitle("Buscado Trabajadores");
        // tama�o
        setSize(750, 700);
        setLayout(new FlowLayout());
        // colocaci�n en el centro de la pantalla
        setLocationRelativeTo(null);

        // Crea un JTable, cada fila será un trabajador
        String[] columnas = { "Identificador", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto" };
        String[][] datos = empresa.listarTrabajadores();
        tabla = new JTable(datos, columnas);
        // Mete la tabla en un JCrollPane
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 600));
        add(jsp);

        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        add(cerrar);

        setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cerrar) {
            dispose();
        }
    }
}
