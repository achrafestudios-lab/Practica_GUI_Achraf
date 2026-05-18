package dialogs;

import exception.BDException;
import modelo.Empresa;
import utilidades.Utilidades;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import static utilidades.Utilidades.*;
import static validacion.Validacion.realizarBusqueda;

// Diálogo para visualizar trabajadores en una tabla de solo lectura con filtro de búsqueda
public class VerDialog extends JDialog implements ActionListener, ItemListener {
    JButton buscar;
    JButton cancelar;
    JPanel pBotonesArriba;
    JPanel pBotones;
    JTable tabla;
    JTextField busqueda;
    JComboBox comboFiltro;
    String[][] datos;
    DefaultTableModel modelo;
    Empresa empresa;

    // Constructor: monta la ventana con filtro de búsqueda, tabla de solo lectura y botón de cierre
    public VerDialog(Empresa empresa) {
        this.empresa = empresa;
        setResizable(false);
        setTitle("Buscar Trabajadores");
        setSize(750, 700);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // --- Panel superior: filtro de búsqueda ---
        pBotonesArriba = new JPanel();
        comboFiltro = crearComboFiltro();
        pBotonesArriba.add(comboFiltro);
        busqueda = new JTextField(15);
        pBotonesArriba.add(busqueda);
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotonesArriba.add(buscar);
        add(pBotonesArriba);

        // --- Tabla de solo lectura con todos los trabajadores ---
        datos = empresa.listarTrabajadores();
        modelo = new DefaultTableModel(datos, COLUMNAS_TRABAJADORES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda editable
            }
        };
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(30);
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 560));
        add(jsp);

        // --- Panel inferior: botón de cierre ---
        pBotones = new JPanel();
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        pBotones.add(cancelar);
        add(pBotones);

        Utilidades.ajustarAnchoColumnas(tabla);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // Gestiona las acciones: buscar trabajadores o cerrar el diálogo
    @Override
    public void actionPerformed(ActionEvent e) {

        // --- Lógica del botón Buscar: filtra la tabla según el criterio ---
        if (e.getSource() == buscar) {
            String[][] resultado = realizarBusqueda(busqueda, comboFiltro, empresa, modelo, tabla, this);
            if (resultado != null) {
                datos = resultado;
            }

        // --- Lógica del botón Cancelar: cierra el diálogo ---
        } else if (e.getSource() == cancelar) {
            dispose();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
    }
}
