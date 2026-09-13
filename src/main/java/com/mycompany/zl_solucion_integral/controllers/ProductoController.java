package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ProductoController {

    private final Logger logger = Logger.getLogger(ProductoController.class.getName());

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    public ResultadoOperacion agregarOActualizarProductoSiExiste(Producto producto) {
        if (producto != null && producto.getCategoria() != null) {
            guardarCategoriaSiNoExiste(producto.getCategoria());
        }
        String sqlSelect = "SELECT cantidad FROM productos WHERE codigo = ?";
        String sqlUpdate = "UPDATE productos SET cantidad = ?, precio = ?, precio_costo = ? WHERE codigo = ?";
        String sqlInsert = "INSERT INTO productos (producto, precio, precio_costo, cantidad, codigo, categoria) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = conn();
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setString(1, producto.getCodigo());
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                int nuevaCantidad = rs.getInt("cantidad") + producto.getCantidad();
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, nuevaCantidad);
                    pstmtUpdate.setDouble(2, producto.getPrecio());
                    pstmtUpdate.setDouble(3, producto.getPrecioCosto());
                    pstmtUpdate.setString(4, producto.getCodigo());
                    pstmtUpdate.executeUpdate();
                    return ResultadoOperacion.ok(UIMessages.MSG_STOCK_ACTUALIZADO);
                }
            }
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, producto.getProducto());
                pstmtInsert.setDouble(2, producto.getPrecio());
                pstmtInsert.setDouble(3, producto.getPrecioCosto());
                pstmtInsert.setInt(4, producto.getCantidad());
                pstmtInsert.setString(5, producto.getCodigo());
                pstmtInsert.setString(6, producto.getCategoria());
                pstmtInsert.executeUpdate();
                return ResultadoOperacion.ok(UIMessages.MSG_PRODUCTO_GUARDADO);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al agregar o actualizar producto", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public ResultadoOperacion modificarProducto(Producto producto) {
        String sqlCheck = "SELECT COUNT(*) FROM productos WHERE codigo = ? AND id != ?";
        String sqlUpdate = "UPDATE productos SET producto = ?, precio = ?, precio_costo = ?, cantidad = ?, codigo = ?, categoria = ? WHERE id = ?";

        Connection conn = conn();
        try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
            pstmtCheck.setString(1, producto.getCodigo());
            pstmtCheck.setInt(2, producto.getId());
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return ResultadoOperacion.error(UIMessages.MSG_PRODUCTO_CODIGO_DUPLICADO);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar código duplicado", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }

        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setString(1, producto.getProducto());
            pstmtUpdate.setDouble(2, producto.getPrecio());
            pstmtUpdate.setDouble(3, producto.getPrecioCosto());
            pstmtUpdate.setInt(4, producto.getCantidad());
            pstmtUpdate.setString(5, producto.getCodigo());
            pstmtUpdate.setString(6, producto.getCategoria());
            pstmtUpdate.setInt(7, producto.getId());
            pstmtUpdate.executeUpdate();
            return ResultadoOperacion.ok(UIMessages.MSG_PRODUCTO_MODIFICADO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al modificar producto", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public ResultadoOperacion eliminarProducto(int idProducto) {
        if (idProducto == -1) {
            return ResultadoOperacion.error(UIMessages.MSG_SELECCIONE_PRODUCTO);
        }
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return ResultadoOperacion.ok(UIMessages.MSG_PRODUCTO_ELIMINADO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_PRODUCTO_NO_ELIMINADO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar el producto", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public ResultadoOperacion eliminarCantidadProducto(int idProducto, int cantidadAEliminar) {
        if (idProducto == -1) {
            return ResultadoOperacion.error(UIMessages.MSG_SELECCIONE_PRODUCTO);
        }
        String sqlSelect = "SELECT cantidad FROM productos WHERE id = ?";
        String sqlUpdate = "UPDATE productos SET cantidad = ? WHERE id = ?";

        Connection conn = conn();
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, idProducto);
            ResultSet rs = pstmtSelect.executeQuery();
            if (!rs.next()) {
                return ResultadoOperacion.error(UIMessages.MSG_SELECCIONE_PRODUCTO);
            }
            int cantidadActual = rs.getInt("cantidad");
            if (cantidadAEliminar > cantidadActual) {
                return ResultadoOperacion.error(UIMessages.MSG_STOCK_INSUFICIENTE);
            }
            int nuevaCantidad = cantidadActual - cantidadAEliminar;
            if (nuevaCantidad == 0) {
                return eliminarProducto(idProducto);
            }
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, nuevaCantidad);
                pstmtUpdate.setInt(2, idProducto);
                pstmtUpdate.executeUpdate();
                return ResultadoOperacion.ok(UIMessages.MSG_STOCK_ACTUALIZADO);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar cantidad del producto", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public void mostrarProductos(final JTable tablaProductos) {
        final DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id");
        modelo.addColumn("Producto");
        modelo.addColumn("Precio");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Código");
        modelo.addColumn("Categoría");
        tablaProductos.setModel(modelo);

        tablaProductos.getColumnModel().getColumn(0).setMinWidth(45);
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaProductos.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaProductos.getColumnModel().getColumn(1).setMinWidth(250);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaProductos.getColumnModel().getColumn(1).setMaxWidth(350);
        tablaProductos.getColumnModel().getColumn(2).setMinWidth(70);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(2).setMaxWidth(130);
        tablaProductos.getColumnModel().getColumn(3).setMinWidth(50);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(3).setMaxWidth(100);
        tablaProductos.getColumnModel().getColumn(4).setMinWidth(50);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(4).setMaxWidth(150);

        final String sql = "SELECT * FROM productos";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("producto"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("codigo"),
                    rs.getString("categoria")
                });
            }
            tablaProductos.setModel(modelo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar productos", e);
        }
    }

    public void mostrarProductosPorCategoria(javax.swing.JTable tabla, String categoria) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        String query = "SELECT * FROM productos";
        if (!categoria.equals("Todas")) {
            query += " WHERE categoria = ?";
        }

        try (PreparedStatement stmt = conn().prepareStatement(query)) {
            if (!categoria.equals("Todas")) {
                stmt.setString(1, categoria);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("producto"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("codigo"),
                    rs.getString("categoria")
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al filtrar productos por categoría", e);
        }
    }

    public int contarRegistros(String categoria) {
        String query = "SELECT COUNT(*) AS total_registros FROM productos";
        if (!categoria.equals("Todas")) {
            query += " WHERE categoria = ?";
        }
        int total = 0;
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            if (!categoria.equals("Todas")) {
                pstmt.setString(1, categoria);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total_registros");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al contar productos", e);
        }
        return total;
    }

    public int obtenerIdProductoSeleccionado(final JTable tabla) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            return -1;
        }
        Integer id = Validaciones.parseEntero(tabla.getValueAt(filaSeleccionada, 0).toString());
        return id == null ? -1 : id;
    }

    public Producto obtenerProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("producto"),
                        rs.getDouble("precio"),
                        rs.getDouble("precio_costo"),
                        rs.getInt("cantidad"),
                        rs.getString("codigo"),
                        rs.getDouble("precio") * rs.getInt("cantidad"),
                        rs.getString("categoria")
                );
                return p;
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener producto por ID", e);
            return null;
        }
    }

    public double obtenerInversionTotalInventario() {
        String sql = "SELECT SUM(precio_costo * cantidad) FROM productos";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener la inversión total del inventario", e);
        }
        return 0.0;
    }

    public Producto buscarProductoPorCodigo(String codigoProducto) {
        Producto producto = null;
        final String sql = "SELECT * FROM productos WHERE codigo = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, codigoProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double precio = rs.getDouble("precio");
                    double precioCosto = rs.getDouble("precio_costo");
                    int cantidad = rs.getInt("cantidad");
                    producto = new Producto(
                            rs.getInt("id"),
                            rs.getString("producto"),
                            precio,
                            precioCosto,
                            cantidad,
                            rs.getString("codigo"),
                            precio * cantidad,
                            rs.getString("categoria")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar producto por código", e);
        }
        return producto;
    }

    public Producto buscarProductoPorNombre(String nombreProducto) {
        Producto producto = null;
        final String sql = "SELECT * FROM productos WHERE producto = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, nombreProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double precio = rs.getDouble("precio");
                    double precioCosto = rs.getDouble("precio_costo");
                    int cantidad = rs.getInt("cantidad");
                    producto = new Producto(
                            rs.getInt("id"),
                            rs.getString("producto"),
                            precio,
                            precioCosto,
                            cantidad,
                            rs.getString("codigo"),
                            precio * cantidad,
                            rs.getString("categoria")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar producto por nombre", e);
        }
        return producto;
    }

    public java.util.List<Producto> buscarProductosSugeridos(String query) {
        java.util.List<Producto> lista = new java.util.ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return lista;
        }
        String sql = "SELECT * FROM productos WHERE LOWER(codigo) LIKE LOWER(?) OR LOWER(producto) LIKE LOWER(?) LIMIT 10";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            String term = "%" + query.trim() + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    double precioCosto = rs.getDouble("precio_costo");
                    int cantidad = rs.getInt("cantidad");
                    lista.add(new Producto(
                            rs.getInt("id"),
                            rs.getString("producto"),
                            precio,
                            precioCosto,
                            cantidad,
                            rs.getString("codigo"),
                            precio * cantidad,
                            rs.getString("categoria")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar sugerencias de productos", e);
        }
        return lista;
    }

    public void actualizarCantidadProducto(String codigoProducto, int nuevaCantidad) {
        String query = "UPDATE productos SET cantidad = ? WHERE codigo = ?";
        try (PreparedStatement stmt = conn().prepareStatement(query)) {
            stmt.setInt(1, nuevaCantidad);
            stmt.setString(2, codigoProducto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar la cantidad del producto", e);
            throw new RuntimeException("Error al actualizar la cantidad del producto en inventario");
        }
    }

    public java.util.Map<String, Double> obtenerDistribucionCategorias() {
        java.util.Map<String, Double> distribucion = new java.util.HashMap<>();
        String sql = "SELECT categoria, COUNT(*) as total FROM productos GROUP BY categoria";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String cat = rs.getString("categoria");
                if (cat == null || cat.isEmpty()) {
                    cat = "Sin Categoría";
                }
                distribucion.put(cat, rs.getDouble("total"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener distribución de categorías", e);
        }
        return distribucion;
    }

    public int obtenerCantidadStockCritico(int limite) {
        String sql = "SELECT COUNT(*) FROM productos WHERE cantidad <= ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener stock crítico", e);
        }
        return 0;
    }

    public java.util.List<String> obtenerCategorias() {
        java.util.List<String> categorias = new java.util.ArrayList<>();
        String sql = "SELECT nombre FROM categorias ORDER BY nombre ASC";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String nom = rs.getString("nombre");
                if (nom != null && !nom.trim().isEmpty()) {
                    categorias.add(nom.trim().toUpperCase());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener categorías", e);
        }
        if (categorias.isEmpty()) {
            categorias.add("GENERAL");
        }
        return categorias;
    }

    public void guardarCategoriaSiNoExiste(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return;
        }
        String catFormateada = categoria.trim().toUpperCase();
        String sql = "INSERT OR IGNORE INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, catFormateada);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al guardar categoría en base de datos", e);
        }
    }
}
