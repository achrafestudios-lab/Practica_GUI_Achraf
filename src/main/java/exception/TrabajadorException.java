package exception;

public class TrabajadorException extends Exception {

    public static final String ERROR_NO_ACUALIZO_NADA = "No hubo ninguna actualiccacion en la base de datos";
    public static final String ERROR_EXISTE_EL_TRABAJADOR = "Trabajador actualizado en la Base de datos";
    public static final String ERROR_LISTA_TRABAJADORES_VACIA = "La lista de trabajadores vacia";
    public static final String ERROR_NO_EXISTE_TRABAJADOR_PARA_ACTUALIZAR = "No se puede actualizar un trabajador inexistente";

    public TrabajadorException(String message) {
        super(message);
    }
}
