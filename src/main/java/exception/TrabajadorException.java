package exception;

public class TrabajadorException extends Exception {

    public static final String ERROR_EXISTE_EL_TRABAJADOR = "Trabajador actualizado en la Base de datos";
    public static final String ERROR_LISTA_TRABAJADORES_VACIA = "La lista de trabajadores vacia";


    public TrabajadorException(String message) {
        super(message);
    }
}
