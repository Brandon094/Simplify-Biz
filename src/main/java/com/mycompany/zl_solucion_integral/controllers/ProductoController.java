package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.models.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador para gestionar las operaciones con productos.
 * <p>
 * Esta clase proporciona métodos para agregar, modificar, eliminar y mostrar
 * productos en la base de datos. También incluye métodos para manejar la
 * cantidad de productos y obtener detalles de productos específicos.
 * </p>
 * Utiliza una instancia de `ConexionDB` para interactuar con la base de datos.
 *
 * @author Dazac
 */
public class ProductoController {

    private final ConexionDB conexion = new ConexionDB();
    private final Logger logger = Logger.getLogger(ProductoController.class.getName());

    /**
     * Método para agregar un producto nuevo o actualizar su cantidad si ya
     * existe en la base de datos.
     *
     * Este método primero verifica si un producto con el mismo código ya está
     * presente en la base de datos. Si el producto ya existe, actualiza su
     * cantidad sumando la cantidad actual con la cantidad nueva. Si el producto
     * no existe, lo inserta como un nuevo registro.
     *
     * @param producto El objeto de tipo Producto que contiene la información
     * del producto a agregar o actualizar.
     */
    public void agregarOActualizarProductoSiExiste(Producto producto) {
        // Consulta SQL para verificar si el producto ya existe en la base de datos
        String sqlSelect = "SELECT cantidad FROM productos WHERE codigo = ?";

        // Consulta SQL para actualizar la cantidad de un producto existente
        String sqlUpdate = "UPDATE productos SET cantidad = ? WHERE codigo = ?";

        // Consulta SQL para insertar un nuevo producto si no existe en la base de datos
        String sqlInsert = "INSERT INTO productos (producto, precio, cantidad, codigo, total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexion.establecerConexion()) {

            // Verificar si el producto ya existe en la base de datos mediante su código
            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                // Asignar el código del producto en la consulta
                pstmtSelect.setString(1, producto.getCodigo());
                // Ejecutar la consulta para verificar si el producto existe
                ResultSet rs = pstmtSelect.executeQuery();

                if (rs.next()) {
                    // Si el producto ya existe, obtener la cantidad actual
                    int cantidadActual = rs.getInt("cantidad");

                    // Calcular la nueva cantidad sumando la actual con la cantidad que se va a agregar
                    int nuevaCantidad = cantidadActual + producto.getCantidad();

                    // Preparar la consulta para actualizar la cantidad del producto existente
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setInt(1, nuevaCantidad);  // Establecer la nueva cantidad
                        pstmtUpdate.setString(2, producto.getCodigo());  // Establecer el código del producto

                        pstmtUpdate.executeUpdate();  // Ejecutar la actualización en la base de datos
                        JOptionPane.showMessageDialog(null, "Stock actualizado exitosamente.");
                    }
                } else {
                    // Si el producto no existe, preparar la consulta para insertar un nuevo producto
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                        pstmtInsert.setString(1, producto.getProducto());  // Establecer el nombre del producto
                        pstmtInsert.setDouble(2, producto.getPrecio());  // Establecer el precio del producto
                        pstmtInsert.setInt(3, producto.getCantidad());  // Establecer la cantidad del producto
                        pstmtInsert.setString(4, producto.getCodigo());  // Establecer el código del producto
                        pstmtInsert.setDouble(5, producto.getTotal());  // Establecer el total del producto

                        pstmtInsert.executeUpdate();  // Ejecutar la inserción en la base de datos
                        JOptionPane.showMessageDialog(null, "Producto guardado exitosamente.");
                    }
                }
            }
        } catch (Exception e) {
            // Manejo de errores: registrar el error y mostrar un mensaje al usuario
            logger.log(Level.SEVERE, "Error al agregar o actualizar producto", e);
            JOptionPane.showMessageDialog(null, "Error al procesar producto: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
    }

    /**
     * Método para modificar un producto existente en la base de datos.
     *
     * Este método actualiza los datos de un producto existente en la base de
     * datos. Antes de la actualización, valida que no haya otro producto con el
     * mismo código, excepto el producto que se está modificando. Si no hay
     * duplicados, procede con la actualización.
     *
     * @param producto El objeto Producto que contiene los datos actualizados.
     */
    public void modificarProducto(Producto producto) {
        // Consulta SQL para verificar si existe un producto con el mismo código pero distinto ID
        String sqlCheck = "SELECT COUNT(*) FROM productos WHERE codigo = ? AND id != ?";

        // Consulta SQL para actualizar los datos del producto en la base de datos
        String sqlUpdate = "UPDATE productos SET producto = ?, precio = ?, cantidad = ?, codigo = ? WHERE id = ?";

        try (Connection conn = conexion.establecerConexion()) {

            // Verificar si el código del producto ya está registrado en otro producto con diferente ID
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                // Establece el código del producto en la consulta
                pstmtCheck.setString(1, producto.getCodigo());
                // Excluye el producto actual de la verificación
                pstmtCheck.setInt(2, producto.getId());

                ResultSet rs = pstmtCheck.executeQuery();
                // Si se encuentra un duplicado, muestra un mensaje de advertencia y detiene la ejecución
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "El código del producto ya está registrado en otro producto.");
                    return;  // Finaliza el método si se encuentra un duplicado
                }
            }

            // Si no hay duplicados, procede a actualizar el producto
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setString(1, producto.getProducto());  // Establece el nuevo nombre del producto
                pstmtUpdate.setDouble(2, producto.getPrecio());  // Establece el nuevo precio del producto
                pstmtUpdate.setInt(3, producto.getCantidad());  // Establece la nueva cantidad del producto
                pstmtUpdate.setString(4, producto.getCodigo());  // Establece el nuevo código del producto
                pstmtUpdate.setInt(5, producto.getId());  // Establece el ID del producto que se va a actualizar

                // Ejecutar la actualización
                pstmtUpdate.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto modificado exitosamente.");
            }
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra durante el proceso de modificación
            logger.log(Level.SEVERE, "Error al modificar producto", e);
            JOptionPane.showMessageDialog(null, "Error al modificar producto: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
    }

    /**
     * Método para eliminar un producto de la base de datos.
     *
     * Este método elimina un producto identificado por su ID de la base de
     * datos. Antes de proceder con la eliminación, se asegura de que se haya
     * seleccionado un producto y solicita una confirmación al usuario.
     *
     * @param idProducto ID del producto a eliminar.
     * @param tbProductos Tabla de productos que se actualizará después de la
     * eliminación.
     */
    public void eliminarProducto(int idProducto, JTable tbProductos) {
        // Verificar si se ha seleccionado un producto
        if (idProducto == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un producto de la tabla",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;  // Si no se selecciona un producto, se detiene el proceso
        }

        // Mostrar un cuadro de confirmación antes de eliminar el producto
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar este producto?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        // Si el usuario no confirma la eliminación, el proceso se detiene
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        // Consulta SQL para eliminar el producto
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conn = conexion.establecerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);  // Asignar el ID del producto a eliminar en la consulta

            // Ejecutar la consulta de eliminación
            int rowsAffected = pstmt.executeUpdate();

            // Verificar si la eliminación fue exitosa
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Producto eliminado exitosamente");
                mostrarProductos(tbProductos);  // Actualizar la tabla de productos
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el producto",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Manejo de errores en caso de que ocurra un problema durante la eliminación
            JOptionPane.showMessageDialog(null, "Error al eliminar el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar la conexión a la base de datos
            conexion.cerrarConexion();
        }
    }

    /**
     * Método para eliminar una cantidad parcial de un producto en la base de
     * datos.
     *
     * Este método permite reducir una cantidad específica de unidades de un
     * producto en stock. Si la cantidad en stock llega a cero, se elimina el
     * producto completo. Antes de proceder, se confirma la acción con el
     * usuario.
     *
     * @param idProducto ID del producto cuyo stock será reducido.
     * @param cantidadAEliminar Cantidad de unidades a eliminar del stock.
     * @param tbProductos Tabla de productos que se actualizará después de la
     * modificación.
     */
    public void eliminarCantidadProducto(int idProducto, int cantidadAEliminar, JTable tbProductos) {
        // Verificar si se ha seleccionado un producto
        if (idProducto == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un producto de la tabla", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;  // Detener el proceso si no se selecciona un producto
        }

        // Mostrar cuadro de confirmación para la eliminación parcial de stock
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar "
                + cantidadAEliminar + " unidades de este producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        // Si el usuario no confirma, detener el proceso
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Consultas SQL
        String sqlSelect = "SELECT cantidad FROM productos WHERE id = ?";
        String sqlUpdate = "UPDATE productos SET cantidad = ? WHERE id = ?";

        try (Connection conn = conexion.establecerConexion(); PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {

            // Buscar la cantidad actual del producto
            pstmtSelect.setInt(1, idProducto);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                int cantidadActual = rs.getInt("cantidad");

                // Verificar si la cantidad a eliminar es válida (no excede la cantidad en stock)
                if (cantidadAEliminar <= cantidadActual) {
                    int nuevaCantidad = cantidadActual - cantidadAEliminar;

                    // Si la nueva cantidad es cero, eliminar el producto completo
                    if (nuevaCantidad == 0) {
                        // Usar método existente para eliminar el producto
                        eliminarProducto(idProducto, tbProductos);
                    } else {
                        // Si la cantidad es mayor a cero, actualizar el stock
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                            pstmtUpdate.setInt(1, nuevaCantidad);  // Actualizar la cantidad
                            pstmtUpdate.setInt(2, idProducto);

                            pstmtUpdate.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Stock actualizado exitosamente.");

                            // Actualizar la tabla de productos
                            mostrarProductos(tbProductos);
                        }
                    }
                } else {
                    // Error si la cantidad a eliminar es mayor a la disponible en stock
                    JOptionPane.showMessageDialog(null, "No se puede eliminar más cantidad de la disponible en stock.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            // Manejo de errores en la eliminación parcial de stock
            JOptionPane.showMessageDialog(null, "Error al eliminar cantidad del producto: "
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar la conexión a la base de datos
            conexion.cerrarConexion();
        }
    }

    /**
     * Método para mostrar todos los productos en una tabla.
     *
     * Este método realiza una consulta a la base de datos para obtener todos
     * los productos almacenados y los muestra en una tabla gráfica (`JTable`).
     * Se actualiza la tabla con las columnas de "Id", "Producto", "Precio",
     * "Cantidad", "Código" y "Precio Total".
     *
     * @param tablaProductos La tabla (`JTable`) donde se mostrarán los
     * productos.
     */
    public void mostrarProductos(final JTable tablaProductos) {
        // Crear un nuevo modelo de tabla
        final DefaultTableModel modelo = new DefaultTableModel();

        // Definir las columnas del modelo
        modelo.addColumn("Id");
        modelo.addColumn("Producto");
        modelo.addColumn("Precio");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Código");
        modelo.addColumn("Precio Total");

        // Establecer el modelo de tabla vacío antes de cargar los datos
        tablaProductos.setModel(modelo);

        // Consulta SQL para obtener todos los productos
        final String sql = "SELECT * FROM productos";

        try (Connection conn = conexion.establecerConexion(); // Conexión a la base de datos
                 Statement st = conn.createStatement(); // Crear un Statement
                 ResultSet rs = st.executeQuery(sql)) {  // Ejecutar la consulta SQL y obtener el resultado

            // Recorrer el ResultSet para agregar los productos al modelo de la tabla
            while (rs.next()) {
                double precio = rs.getDouble("precio");  // Obtener el precio del producto
                int cantidad = rs.getInt("cantidad");  // Obtener la cantidad en stock
                double precioTotal = precio * cantidad;  // Calcular el precio total (precio * cantidad)

                // Agregar una nueva fila al modelo con los datos del producto
                modelo.addRow(new Object[]{
                    rs.getInt("id"), // ID del producto
                    rs.getString("producto"), // Nombre del producto
                    precio, // Precio unitario
                    cantidad, // Cantidad en stock
                    rs.getString("codigo"), // Código del producto
                    precioTotal // Precio total
                });
            }
            // Asignar el modelo a la tabla para mostrar los productos
            tablaProductos.setModel(modelo);
        } catch (Exception e) {
            // Manejo de errores si ocurre un problema al obtener los productos
            logger.log(Level.SEVERE, "Error al mostrar productos", e);
            JOptionPane.showMessageDialog(null, "Error al mostrar productos: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
    }

    /**
     * Método para obtener el ID del producto seleccionado en una tabla.
     *
     * Este método devuelve el ID del producto seleccionado en la tabla
     * (`JTable`). Si no hay una fila seleccionada, devuelve `-1` para indicar
     * que no se ha seleccionado ningún producto.
     *
     * @param tabla La tabla (`JTable`) de donde se obtiene el producto
     * seleccionado.
     * @return El ID del producto seleccionado o `-1` si no se ha seleccionado
     * ninguna fila.
     */
    public int obtenerIdProductoSeleccionado(final JTable tabla) {
        // Obtener el índice de la fila seleccionada
        int filaSeleccionada = tabla.getSelectedRow();

        // Verificar si hay una fila seleccionada
        if (filaSeleccionada != -1) {
            // Obtener el valor de la primera columna (ID del producto) y convertirlo a entero
            return Integer.parseInt(tabla.getValueAt(filaSeleccionada, 0).toString());
        } else {
            // No se ha seleccionado ninguna fila, devolver -1
            return -1;
        }
    }

    /**
     * Método para obtener un producto desde la base de datos usando su ID.
     *
     * Este método consulta la base de datos para obtener los detalles de un
     * producto dado su ID. Si el producto se encuentra, se devuelve un objeto
     * `Producto` con la información del producto. Si no se encuentra el
     * producto o ocurre un error, se muestra un mensaje de error y se devuelve
     * `null`.
     *
     * @param id El ID del producto que se desea obtener.
     * @return Un objeto `Producto` con los datos del producto si se encuentra,
     * o `null` si el producto no se encuentra o si ocurre un error.
     */
    public Producto obtenerProductoPorId(int id) {
        // Consulta SQL para seleccionar el producto por su ID
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (Connection conn = conexion.establecerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Establecer el ID del producto en la consulta
            pstmt.setInt(1, id);

            // Ejecutar la consulta y obtener el resultado
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crear un nuevo objeto Producto con los datos obtenidos
                return new Producto(
                        rs.getInt("id"), // ID del producto
                        rs.getString("producto"), // Nombre del producto
                        rs.getDouble("precio"), // Precio del producto
                        rs.getInt("cantidad"), // Cantidad disponible
                        rs.getString("codigo"), // Código del producto
                        rs.getDouble("precio") * rs.getInt("cantidad") // Calcular el total (precio * cantidad)
                );
            } else {
                // Si no se encuentra el producto, mostrar un mensaje de error
                JOptionPane.showMessageDialog(null, "Producto no encontrado", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            // Manejo de excepciones y registro del error
            logger.log(Level.SEVERE, "Error al obtener producto por ID", e);
            JOptionPane.showMessageDialog(null, "Error al obtener producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            conexion.cerrarConexion();
        }
    }

    /*
     Método para buscar por código
     */
    public Producto buscarProductoPorCodigo(String codigoProducto) {
        Producto producto = null;
        final String sql = "SELECT * FROM productos WHERE codigo = ?";

        try (Connection conn = conexion.establecerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto(
                            rs.getString("producto"),
                            rs.getDouble("precio"),
                            rs.getInt("cantidad"),
                            rs.getString("codigo"),
                            rs.getDouble("total")
                    );
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar producto por código", e);
            JOptionPane.showMessageDialog(null, "Error al buscar el producto: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
        return producto;
    }

    /*
     Método para buscar por nombre
     */
    public Producto buscarProductoPorNombre(String nombreProducto) {
        Producto producto = null;
        final String sql = "SELECT * FROM productos WHERE producto = ?";

        try (Connection conn = conexion.establecerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto(
                            rs.getString("producto"),
                            rs.getDouble("precio"),
                            rs.getInt("cantidad"),
                            rs.getString("codigo"),
                            rs.getDouble("total")
                    );
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar producto por nombre", e);
            JOptionPane.showMessageDialog(null, "Error al buscar el producto: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
        return producto;
    }
}
