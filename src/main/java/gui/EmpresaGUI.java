
package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import dialogs.*;
//import dialogs.ModificaDialog;
//import dialogs.VerDialog;
import exception.TrabajadorException;
import ficheros.FicheroDatos;
import modelo.Empresa;
import modelo.Trabajador;

import static dao.AccesoTrabajador.insertarListaTrabajadores;
import static dao.AccesoTrabajador.obtenerTrabajadoresBaseDatos;
import static ficheros.FicheroDatos.*;


/**
 *
 * @author Achraf Ait
 *
 */
public class EmpresaGUI extends JFrame implements ActionListener {
    String rutaArchivoDat = "src\\main\\resources\\ficheroDatos\\empresa.dat";
    String rutaFotosPrograma = "src/main/resources/images";
    List<Trabajador> trabaj = obtenerTrabajadoresBaseDatos();

    Empresa empresa;
    JButton altaTrabajador;
    JButton bajaTrabajador;
    JButton modificaTrabajador;
    JButton buscaTrabajador;
    JButton listarTrabajadores;
    JButton salir;

    public EmpresaGUI() {
        super("Gestión de personal");

        // Carga los trabajadores leidos de un fichero a memoria
//        ArrayList<Trabajador> trabajDAT = FicheroDatos.obtenerTrabajadores(rutaArchivoDat);

        empresa = new Empresa(trabaj);

        // Tamaño JFrame
        setSize(800, 750);
        // Cerrar al salir
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));
        setLocationRelativeTo(null);
        // Creación de los botones y se añaden al JFrame
        // Se añade una imagen para cada botón
        altaTrabajador = new JButton("Añadir Trabajador");
        altaTrabajador.addActionListener(this);
        altaTrabajador.setIcon(new ImageIcon(rutaFotosPrograma + "/addUser.png"));
        add(altaTrabajador);

        bajaTrabajador = new JButton("Borrar Trabajador");
        bajaTrabajador.addActionListener(this);
        bajaTrabajador.setIcon(new ImageIcon(rutaFotosPrograma + "/removeUser.png"));
        add(bajaTrabajador);

        modificaTrabajador = new JButton("Modificar Trabajador");
        modificaTrabajador.addActionListener(this);
        modificaTrabajador.setIcon(new ImageIcon(rutaFotosPrograma + "/editUser.png"));
        add(modificaTrabajador);

        buscaTrabajador = new JButton("Buscar Trabajador");
        buscaTrabajador.addActionListener(this);
        buscaTrabajador.setIcon(new ImageIcon(rutaFotosPrograma + "/searchUser.png"));
        add(buscaTrabajador);

        listarTrabajadores = new JButton("Listar Trabajadores");
        listarTrabajadores.addActionListener(this);
        listarTrabajadores.setIcon(new ImageIcon(rutaFotosPrograma + "/list.png"));
        add(listarTrabajadores);

        salir = new JButton("Salir");
        salir.addActionListener(this);
        salir.setIcon(new ImageIcon(rutaFotosPrograma + "/exit.png"));
        add(salir);

        // Visible
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Para responder a los clicks del usuario en cada botón (ActionEvent)
        // nuestra clase hace de oyente de eventos por eso implementa ActionListener
        // e implementa el método actionPerformed() pasando como parámetro un
        // ActionEvent.
        if (e.getSource() == altaTrabajador) {
            new AltaDialog(empresa);
        } else if (e.getSource() == bajaTrabajador) {
            new BajaDialog(empresa);
        } else if (e.getSource() == modificaTrabajador) {
            new ModificaDialog(empresa);
        } else if (e.getSource() == buscaTrabajador) {
            new VerDialog(empresa);
        } else if (e.getSource() == listarTrabajadores) {
            new ListarDialog(empresa);
        }

        // Cuando se sale se vuelca a fichero.
        else if (e.getSource() == salir) {
            FicheroDatos.escribirTrabajadores(rutaArchivoDat, empresa.getTrabajadores());
//            try {
//                insertarListaTrabajadores(trabaj);
//            } catch (TrabajadorException ex) {
//				System.out.println(ex.getMessage());;
//            }
            System.exit(0);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new EmpresaGUI();
    }

}
