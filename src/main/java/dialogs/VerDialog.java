package dialogs;

import dao.AccesoTrabajador;
import modelo.Empresa;
import modelo.Trabajador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Array;
import java.util.List;

import static dao.AccesoTrabajador.insertarTrabajador;

public class VerDialog extends JDialog implements ActionListener, ItemListener {

    Empresa empresa;
    JTable tabla;
    JButton cerrar;
    JButton buscar;
    JPanel pBotones;

    JLabel etiquetaIdentificador;
    JTextField areaOpcion;
    JPanel pIdentificador;
    String opcion = "";

    JLabel etiquetaPuesto;
    JComboBox comboPuesto;
    String puesto = "";

    public VerDialog(Empresa empresa) {
        this.empresa = empresa;

        etiquetaPuesto = new JLabel("Filtrar por");
        add(etiquetaPuesto);
        // lista desplegable
        comboPuesto = new JComboBox();
        comboPuesto.addItem("DNI");
        comboPuesto.addItem("Nombre");
        comboPuesto.addItem("Apellidos");
        comboPuesto.addItem("Direccion");
        comboPuesto.addItem("Telefono");
        comboPuesto.addItem("Puesto");

        comboPuesto.addItemListener(this);
        add(comboPuesto);

        // una fila por JPanel
        pIdentificador = new JPanel();

        // Se crean los elementos y se añaden
//        etiquetaIdentificador = new JLabel("Identificador");
        areaOpcion = new JTextField(15);

        // Se añaden al JPanel
//        pIdentificador.add(etiquetaIdentificador);
        pIdentificador.add(areaOpcion);

        add(pIdentificador);

        pBotones = new JPanel();

        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotones.add(buscar);

        add(pBotones);

        // Impide cambiar tamaño ventana
        setResizable(false);
        // titulo del dialog
        setTitle("Buscado Trabajadores");
        // tamaño
        setSize(750, 700);
        setLayout(new FlowLayout());
        // colocacion en el centro de la pantalla
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
        if (e.getSource() == buscar) {
            try {

                opcion = areaOpcion.getText();

                if (puesto.equals("DNI")) {
                   List<Trabajador> listaNueva = AccesoTrabajador.obtenerListaTrabajadoresPorDni(opcion);

                    this.empresa.setTrabajadores(listaNueva);

                }

            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }

        } else if (e.getSource() == cerrar) {
            dispose();
        }
    }

//    public boolean comprobarErrores() {
//        if (id < 1) {
//            JOptionPane.showMessageDialog(null, "El ID debe ser un numero entero positivo", "Error",
//                    JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//        return true;
//    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        puesto = comboPuesto.getSelectedItem().toString();
    }
}
