package dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import dao.AccesoTrabajador;
import exception.TrabajadorException;
import ficheros.FicheroJSON;
import ficheros.FiecheroCSV;
import modelo.Trabajador;
import validacion.Validacion;
import static dao.AccesoTrabajador.insertarTrabajador;
import static utilidades.Utilidades.crearComboPuestos;

// Diálogo para dar de alta un nuevo trabajador con formulario completo
public class AltaDialog extends JDialog implements ActionListener, ItemListener {

    // --- Etiquetas y campos de texto del formulario ---
    JLabel etiquetaDni;
    JTextField areaDni;
    JLabel etiquetaNombre;
    JTextField areaNombre;
    JLabel etiquetaApellidos;
    JTextField areaApellidos;
    JLabel etiquetaDireccion;
    JTextField areaDireccion;
    JLabel etiquetaTelefono;
    JTextField areaTelefono;
    JLabel etiquetaPuesto;
    JComboBox comboPuesto;

    // --- Botones de acción ---
    JButton aceptar;
    JButton cancelar;
    JButton importar;

    // --- Variables para almacenar los datos del formulario ---
    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";

    // --- Paneles contenedores de cada campo ---
    JPanel pDni;
    JPanel pNombre;
    JPanel pApellidos;
    JPanel pDireccion;
    JPanel pTelefono;
    JPanel pPuesto;
    JPanel pBotones;
    JPanel botoneImportar;

    // Constructor: configura la ventana y crea todos los componentes del formulario
    public AltaDialog() {
        setResizable(false);
        setTitle("Alta Trabajador");
        setSize(300, 350);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // Crea los paneles para agrupar cada campo del formulario
        pDni = new JPanel();
        pNombre = new JPanel();
        pApellidos = new JPanel();
        pDireccion = new JPanel();
        pTelefono = new JPanel();
        pPuesto = new JPanel();
        pBotones = new JPanel();
        botoneImportar = new JPanel();

        // Campo DNI: etiqueta + campo de texto
        etiquetaDni = new JLabel("DNI                 ");
        areaDni = new JTextField(15);
        pDni.add(etiquetaDni);
        pDni.add(areaDni);

        // Campo Nombre
        etiquetaNombre = new JLabel("Nombre         ");
        areaNombre = new JTextField(15);
        pNombre.add(etiquetaNombre);
        pNombre.add(areaNombre);

        // Campo Apellidos
        etiquetaApellidos = new JLabel("Apellidos      ");
        areaApellidos = new JTextField(15);
        pApellidos.add(etiquetaApellidos);
        pApellidos.add(areaApellidos);

        // Campo Dirección
        etiquetaDireccion = new JLabel("Dirección      ");
        areaDireccion = new JTextField(15);
        pDireccion.add(etiquetaDireccion);
        pDireccion.add(areaDireccion);

        // Campo Teléfono
        etiquetaTelefono = new JLabel("Teléfono       ");
        areaTelefono = new JTextField(15);
        pTelefono.add(etiquetaTelefono);
        pTelefono.add(areaTelefono);

        // Campo Puesto: etiqueta + combo desplegable con los puestos disponibles
        etiquetaPuesto = new JLabel("Puesto             ");
        pPuesto.add(etiquetaPuesto);
        comboPuesto = crearComboPuestos();
        comboPuesto.addItemListener(this);
        pPuesto.add(comboPuesto);

        // Añade los paneles de los campos al diálogo en orden
        add(pDni);
        add(pNombre);
        add(pApellidos);
        add(pDireccion);
        add(pTelefono);
        add(pPuesto);

        // Botón Aceptar: valida y guarda el trabajador
        aceptar = new JButton("Aceptar");
        aceptar.addActionListener(this);
        pBotones.add(aceptar);

        // Botón Cancelar: cierra el diálogo sin guardar
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        pBotones.add(cancelar);

        // Botón Importar: importa trabajadores desde un archivo CSV o JSON
        importar = new JButton("Importar");
        importar.addActionListener(this);
        botoneImportar.add(importar);

        // Panel contenedor que apila los botones principales y el de importar verticalmente
        JPanel pContenedorBotones = new JPanel();
        pContenedorBotones.setLayout(new BoxLayout(pContenedorBotones, BoxLayout.Y_AXIS));
        pContenedorBotones.add(pBotones);
        pContenedorBotones.add(botoneImportar);
        add(pContenedorBotones);

        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // Actualiza el puesto cuando el usuario selecciona una opción del desplegable
    @Override
    public void itemStateChanged(ItemEvent e) {
        puesto = (String) comboPuesto.getSelectedItem();
    }

    // Gestiona las acciones de los botones: aceptar, importar y cancelar
    @Override
    public void actionPerformed(ActionEvent e) {

        // --- Lógica del botón Aceptar: validar campos y guardar en BD ---
        if (e.getSource() == aceptar) {
            try {
                dni = areaDni.getText();
                nombre = areaNombre.getText();
                apellidos = areaApellidos.getText();
                direccion = areaDireccion.getText();
                telefono = areaTelefono.getText();

                // Valida todos los campos antes de insertar
                if (Validacion.comprobarErroresAlta(areaDni, areaNombre, areaApellidos, areaDireccion, areaTelefono, comboPuesto, dni, nombre, apellidos, direccion, telefono, puesto)) {
                    Trabajador t = new Trabajador(dni, nombre, apellidos, direccion, telefono, puesto);
                    int insertar = insertarTrabajador(t);

                    // 1 = insertado nuevo, 2 = actualizado existente, otro = error
                    if (insertar == 1) {
                        JOptionPane.showMessageDialog(null, "Nuevo trabajador insertado correctamente", "Alerta", JOptionPane.WARNING_MESSAGE);
                    } else if (insertar == 2) {
                        JOptionPane.showMessageDialog(null, "Trabajador ya existente, datos modificados correctamente", "Alerta", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "El ID del trabajador que quiere introducir ya existe y no has echo modificaciones", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    dispose();
                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        // --- Lógica del botón Importar: seleccionar archivo e importar trabajadores ---
        } else if (e.getSource() == importar) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(".csv .json", "csv", "json"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                String nombre = f.getAbsolutePath();
                List<Trabajador> importados;

                // Detecta el formato por la extensión y llama al método correspondiente
                if (nombre.endsWith(".json")) {
                    importados = FicheroJSON.importarFicheroJSON(nombre);
                } else {
                    importados = FiecheroCSV.importarFicheroTrabajadoresCSV(nombre);
                }

                try {
                    AccesoTrabajador.insertarListaTrabajadores(importados);
                    JOptionPane.showMessageDialog(this,
                            "Importados: " + importados.size() + " trabajadores",
                            "Completado", JOptionPane.INFORMATION_MESSAGE);
                } catch (TrabajadorException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            dispose();

        // --- Lógica del botón Cancelar: cerrar el diálogo ---
        } else if (e.getSource() == cancelar) {
            dispose();
        }
    }
}
