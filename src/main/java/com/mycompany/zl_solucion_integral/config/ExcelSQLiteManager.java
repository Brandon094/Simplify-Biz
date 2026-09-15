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



    /**
     * Lee y retorna los nombres de cabecera de la primera fila del archivo Excel dado.
     */
    public static java.util.List<String> leerCabeceras(java.io.File archivo) throws IOException {
        java.util.List<String> cabeceras = new java.util.ArrayList<>();
        try (FileInputStream fis = new FileInputStream(archivo); Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                    Cell celda = headerRow.getCell(c);
                    String val = celda != null ? obtenerValorTexto(celda) : "";
                    if (!val.trim().isEmpty()) {
                        cabeceras.add(val.trim());
                    } else {
                        cabeceras.add("Columna " + (c + 1));
                    }
                }
            }
        }
        return cabeceras;
    }

    /**
     * Lee hasta maxFilas de vista previa del archivo Excel.
     */
    public static java.util.List<Object[]> leerVistaPrevia(java.io.File archivo, int maxFilas) throws IOException {
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        try (FileInputStream fis = new FileInputStream(archivo); Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalFilas = sheet.getLastRowNum();
            for (int r = 1; r <= Math.min(totalFilas, maxFilas); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                int cols = row.getLastCellNum();
                Object[] objFila = new Object[cols];
                for (int c = 0; c < cols; c++) {
                    Cell celda = row.getCell(c);
                    objFila[c] = celda != null ? obtenerValorTexto(celda) : "";
                }
                filas.add(objFila);
            }
        }
        return filas;
    }

    /**
     * Importa productos desde Excel con mapeo dinámico de columnas y manejo defensivo de tipos.
     */
    public static ResultadoOperacion importarConMapeo(java.io.File archivo, java.util.Map<String, Integer> mapeo, String categoriaDefault) {
        Connection conn = GestorConexion.getInstancia().obtenerConexion();
        if (conn == null) {
            return ResultadoOperacion.error("No hay conexión a la base de datos.");
        }

        int creados = 0;
        int actualizados = 0;
        int omitidos = 0;

        try (FileInputStream fis = new FileInputStream(archivo); Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalFilas = sheet.getLastRowNum();

            String selectSQL = "SELECT cantidad, precio, precio_costo FROM productos WHERE codigo = ?";
            String insertSQL = "INSERT INTO productos (producto, precio, precio_costo, cantidad, codigo, categoria) VALUES (?, ?, ?, ?, ?, ?)";
            String updateSQL = "UPDATE productos SET cantidad = cantidad + ?, precio = ?, precio_costo = ?, categoria = ? WHERE codigo = ?";

            conn.setAutoCommit(false); // Transacción atómica por lote

            for (int i = 1; i <= totalFilas; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Extraer valores según mapeo (-1 indica no asignado)
                Integer idxNombre = mapeo.get("nombre");
                Integer idxCodigo = mapeo.get("codigo");
                Integer idxPrecio = mapeo.get("precio");
                Integer idxCosto = mapeo.get("precio_costo");
                Integer idxStock = mapeo.get("cantidad");
                Integer idxCat = mapeo.get("categoria");

                String nombre = (idxNombre != null && idxNombre >= 0) ? obtenerValorTexto(row.getCell(idxNombre)) : "";
                String codigo = (idxCodigo != null && idxCodigo >= 0) ? obtenerValorTexto(row.getCell(idxCodigo)) : "";
                double precio = (idxPrecio != null && idxPrecio >= 0) ? obtenerValorDoubleDefensivo(row.getCell(idxPrecio)) : 0.0;
                double costo = (idxCosto != null && idxCosto >= 0) ? obtenerValorDoubleDefensivo(row.getCell(idxCosto)) : 0.0;
                int stock = (idxStock != null && idxStock >= 0) ? (int) Math.round(obtenerValorDoubleDefensivo(row.getCell(idxStock))) : 1;
                String cat = (idxCat != null && idxCat >= 0) ? obtenerValorTexto(row.getCell(idxCat)) : "";

                if (cat.trim().isEmpty()) {
                    cat = (categoriaDefault != null && !categoriaDefault.trim().isEmpty()) ? categoriaDefault.trim() : "GENERAL";
                }

                // Autogenerar código SKU si viene vacío
                if (codigo.trim().isEmpty()) {
                    if (nombre.trim().isEmpty()) {
                        omitidos++;
                        continue; // Fila vacía
                    }
                    codigo = "SKU-IMP-" + System.currentTimeMillis() + "-" + i;
                }

                if (nombre.trim().isEmpty()) {
                    nombre = "Producto " + codigo;
                }

                // Verificar si existe el producto en SQLite
                try (PreparedStatement psSelect = conn.prepareStatement(selectSQL)) {
                    psSelect.setString(1, codigo.trim());
                    ResultSet rs = psSelect.executeQuery();

                    if (rs.next()) {
                        // Actualizar producto existente (Upsert)
                        try (PreparedStatement psUp = conn.prepareStatement(updateSQL)) {
                            psUp.setInt(1, Math.max(0, stock));
                            psUp.setDouble(2, precio > 0 ? precio : rs.getDouble("precio"));
                            psUp.setDouble(3, costo > 0 ? costo : rs.getDouble("precio_costo"));
                            psUp.setString(4, cat.toUpperCase());
                            psUp.setString(5, codigo.trim());
                            psUp.executeUpdate();
                            actualizados++;
                        }
                    } else {
                        // Insertar nuevo producto
                        try (PreparedStatement psIn = conn.prepareStatement(insertSQL)) {
                            psIn.setString(1, nombre.trim());
                            psIn.setDouble(2, precio);
                            psIn.setDouble(3, costo);
                            psIn.setInt(4, Math.max(0, stock));
                            psIn.setString(5, codigo.trim());
                            psIn.setString(6, cat.toUpperCase());
                            psIn.executeUpdate();
                            creados++;
                        }
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            String msg = String.format("Importación completada: %d creados, %d actualizados, %d omitidos.", creados, actualizados, omitidos);
            return ResultadoOperacion.ok(msg);

        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ignored) {}
            return ResultadoOperacion.error("Error durante la importación: " + e.getMessage());
        }
    }

    /**
     * Genera un archivo Excel (.xlsx) con la plantilla modelo oficial ERP+ Business.
     */
    public static ResultadoOperacion generarPlantillaModelo(java.io.File archivoDestino) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(archivoDestino)) {
            Sheet sheet = workbook.createSheet("Productos ERP+");

            // Estilos de Encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Cabeceras
            String[] cabeceras = {"CÓDIGO SKU", "NOMBRE DEL PRODUCTO", "PRECIO VENTA", "PRECIO COSTO", "CANTIDAD STOCK", "CATEGORÍA"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de Ejemplo
            Object[][] ejemplos = {
                {"SKU-001", "Filtro de Aceite Universal", 25000.0, 15000.0, 20, "LUBRICANTES"},
                {"SKU-002", "Kit Pastillas de Freno Delanteras", 85000.0, 52000.0, 10, "FRENOS"},
                {"SKU-003", "Batería 12V 100Ah Heavy Duty", 320000.0, 210000.0, 5, "ELÉCTRICO"}
            };

            for (int r = 0; r < ejemplos.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < ejemplos[r].length; c++) {
                    Cell cell = row.createCell(c);
                    if (ejemplos[r][c] instanceof Number) {
                        cell.setCellValue(((Number) ejemplos[r][c]).doubleValue());
                    } else {
                        cell.setCellValue(ejemplos[r][c].toString());
                    }
                }
            }

            for (int i = 0; i < cabeceras.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
            return ResultadoOperacion.ok("Plantilla modelo guardada exitosamente en:\n" + archivoDestino.getAbsolutePath());
        } catch (IOException e) {
            return ResultadoOperacion.error("Error al generar la plantilla: " + e.getMessage());
        }
    }

    private static String obtenerValorTexto(Cell celda) {
        if (celda == null) return "";
        switch (celda.getCellType()) {
            case STRING: return celda.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(celda)) {
                    return celda.getDateCellValue().toString();
                }
                double num = celda.getNumericCellValue();
                if (num == (long) num) {
                    return String.format("%d", (long) num);
                } else {
                    return String.format("%.2f", num);
                }
            case BOOLEAN: return String.valueOf(celda.getBooleanCellValue());
            case FORMULA:
                try { return celda.getStringCellValue(); } 
                catch (Exception e) { return String.valueOf(celda.getNumericCellValue()); }
            default: return "";
        }
    }

    private static double obtenerValorDoubleDefensivo(Cell celda) {
        if (celda == null) return 0.0;
        if (celda.getCellType() == CellType.NUMERIC) {
            return celda.getNumericCellValue();
        }
        String txt = obtenerValorTexto(celda);
        return Validaciones.limpiarFormatoMoneda(txt);
    }
}

