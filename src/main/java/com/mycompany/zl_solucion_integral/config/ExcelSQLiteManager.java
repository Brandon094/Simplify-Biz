package com.mycompany.zl_solucion_integral.config;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * La clase ExcelSQLiteManager proporciona métodos para importar datos de un
 * archivo Excel a una base de datos SQLite.
 */
public class ExcelSQLiteManager {

    public static void importarExcel(String rutaExcel, String nombreTabla) {
        Connection conn = GestorConexion.getInstancia().obtenerConexion();

        try {
            if (conn != null) {
                System.out.println("Conexión a SQLite establecida.");

                try (FileInputStream fis = new FileInputStream(rutaExcel); Workbook workbook = new XSSFWorkbook(fis)) {
                    // Recorrer todas las hojas del archivo Excel
                    for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                        Sheet sheet = workbook.getSheetAt(sheetIndex);
                        System.out.println("Procesando hoja: " + sheet.getSheetName());

                        // Crear la tabla si no existe
                        if (sheetIndex == 0) { // Solo se genera la tabla una vez
                            String createTableSQL = generarSQLCreacionTabla(sheet, nombreTabla);
                            try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                                stmt.executeUpdate();
                                System.out.println("Tabla verificada/creada correctamente: " + nombreTabla);
                            }
                        }

                        // Importar los datos de la hoja
                        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Empezar en la segunda fila (i = 1)
                            Row row = sheet.getRow(i);

                            if (row == null) {
                                continue; // Si la fila está vacía, saltar a la siguiente
                            }

                            // Leer los datos de las columnas
                            String nombreProducto = obtenerValorCelda(row.getCell(0)); // Asumimos que la segunda columna es el nombre del producto
                            double precio = obtenerValorNumerico(row.getCell(1)); // Asumimos que la tercera columna es el precio
                            double cantidad = obtenerValorNumerico(row.getCell(2)); // Asumimos que la cuarta columna es la cantidad
                            String codigoProducto = obtenerValorCelda(row.getCell(3)); // Asumimos que la primera columna es el código
                            String categoria = obtenerValorCelda(row.getCell(4)); // Asumimos que la quinta columna es la categoría

                            // Verificar si el producto ya existe
                            String selectSQL = "SELECT cantidad FROM " + nombreTabla + " WHERE codigo = ?";
                            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                                selectStmt.setString(1, codigoProducto);
                                ResultSet rs = selectStmt.executeQuery();

                                if (rs.next()) {
                                    // Actualizar el producto si ya existe
                                    double cantidadExistente = rs.getDouble("cantidad");
                                    String updateSQL = "UPDATE " + nombreTabla + " SET cantidad = ?, categoria = ?, precio = ? WHERE codigo = ?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                                        updateStmt.setDouble(1, cantidadExistente + cantidad);
                                        updateStmt.setString(2, categoria);
                                        updateStmt.setDouble(3, precio);
                                        updateStmt.setString(4, codigoProducto);
                                        updateStmt.executeUpdate();
                                    }
                                } else {
                                    // Insertar el producto si no existe
                                    String insertSQL = "INSERT INTO " + nombreTabla + " (producto, precio, cantidad, codigo, categoria) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                                        insertStmt.setString(1, nombreProducto);
                                        insertStmt.setDouble(2, precio);
                                        insertStmt.setDouble(3, cantidad);
                                        insertStmt.setString(4, codigoProducto);
                                        insertStmt.setString(5, categoria);
                                        insertStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Datos importados correctamente.");
                }
            } else {
                System.out.println("Error al establecer la conexión a la base de datos.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error general: " + e.getMessage());
        }
    }

    // Método para exportar datos desde una tabla SQLite a un archivo Excel
    public static void exportarDatosAExcel(String nombreTabla, String rutaExcel) {
        Connection conn = GestorConexion.getInstancia().obtenerConexion();
        FileOutputStream fos = null;

        try {
            if (conn != null) {
                System.out.println("Conexión a SQLite establecida.");

                // Crear un libro de trabajo y una hoja de Excel
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(nombreTabla);

                // Ejecutar una consulta para obtener los datos de la tabla sin la columna id
                String query = "SELECT producto, precio, cantidad, codigo, categoria FROM " + nombreTabla; // Omitir 'id'
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // Obtener los metadatos de las columnas
                int columnCount = rs.getMetaData().getColumnCount();

                // Crear la fila de encabezado en la primera fila del Excel
                Row headerRow = sheet.createRow(0);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = headerRow.createCell(i - 1);
                    cell.setCellValue(rs.getMetaData().getColumnName(i));
                }

                // Escribir los datos en las filas siguientes
                int rowIndex = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowIndex++);
                    for (int i = 1; i <= columnCount; i++) {
                        Cell cell = row.createCell(i - 1);
                        cell.setCellValue(rs.getString(i));
                    }
                }

                // Escribir el libro de Excel en el archivo
                fos = new FileOutputStream(rutaExcel);
                workbook.write(fos);

                System.out.println("Datos exportados correctamente a " + rutaExcel);

                // Cerrar recursos
                rs.close();
                stmt.close();
                workbook.close();
            } else {
                System.out.println("Error al establecer la conexión a la base de datos.");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Genera la sentencia SQL para crear una tabla en SQLite con el mismo
     * esquema que la primera fila de un archivo Excel.
     *
     * @param sheet La hoja del archivo Excel que contiene los datos.
     * @param nombreTabla Nombre de la tabla que se creará en SQLite.
     * @return La sentencia SQL para crear la tabla.
     */
    private static String generarSQLCreacionTabla(Sheet sheet, String nombreTabla) {
        Row headerRow = sheet.getRow(0);
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(nombreTabla).append(" (");

        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
            String columnName = headerRow.getCell(j).getStringCellValue().trim();
            // Ajusta la columna de categoría para que acepte NULL o tenga un valor predeterminado
            if (columnName.equalsIgnoreCase("categoria")) {
                sb.append(columnName).append(" TEXT DEFAULT 'Sin Categoría'"); // Establece valor por defecto
            } else {
                sb.append(columnName).append(" TEXT");
            }

            if (j < headerRow.getLastCellNum() - 1) {
                sb.append(", ");
            }
        }
        sb.append(");");
        return sb.toString();
    }

    private static String obtenerValorCelda(Cell celda) {
        if (celda == null) {
            return "";
        }
        return celda.getCellType() == CellType.STRING ? celda.getStringCellValue().trim() : "";
    }

    private static double obtenerValorNumerico(Cell celda) {
        if (celda == null) {
            return 0.0;
        }
        return celda.getCellType() == CellType.NUMERIC ? celda.getNumericCellValue() : 0.0;
    }

}
