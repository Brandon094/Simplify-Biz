package com.mycompany.zl_solucion_integral.models;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;

public class Producto {

    private final ConexionDB conexion; // Variable global para la conexión
    // Atributos 
    private int id;
    private String producto;
    private double precio;
    private int cantidad;
    private int cantidadSolicitada;
    private String codigo;
    private double total;
    private String categoria;
    
    SelecionRuta rutaDB = new SelecionRuta();

    // Constructor vacío (útil para instanciar sin datos iniciales)
    public Producto() {
        this.conexion = new ConexionDB(rutaDB.cargarRutaBaseDatos()); // Asegúrate de que ConexionDB esté bien definida.
    }

    // Constructor con parámetros (para inicializar con datos, incluyendo el total)
    public Producto(int id, String producto, double precio, int cantidad, String codigo, double total, String categoria) {
        this();
        this.id = id;
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.total = total;
        this.categoria = categoria;
    }

    // Constructor sin el parametro id 
    public Producto(String producto, double precio, int cantidad, String codigo, double total) {
        this();
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.total = total;
    }

    // Constructor sin el parametro de total 
    public Producto(String producto, Double precio, int cantidad, String codigo) {
        this();
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
    }

    // Constructor con el parametro de categoria 
    public Producto(String producto, Double precio, int cantidad, String codigo, String categoria) {
        this();
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.categoria = categoria;
    }

    // Getters y Setters para cada atributo
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(int cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    @Override
    public String toString() {
        return String.format(
                "| %-5s | %-20s | %-10s | %-8s | %-10s | %-10s | %-15s |",
                "ID: " + id,
                "PRODUCTO: " + producto,
                "PRECIO: " + precio,
                "CANTIDAD: " + cantidad,
                "CODIGO: " + codigo,
                "TOTAL: " + total,
                "CATEGORIA: " + categoria
        );
    }
}
