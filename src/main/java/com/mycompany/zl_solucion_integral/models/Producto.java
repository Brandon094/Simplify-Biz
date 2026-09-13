package com.mycompany.zl_solucion_integral.models;

public class Producto {

    private int id;
    private String producto;
    private double precio;
    private double precioCosto;
    private int cantidad;
    private int cantidadSolicitada;
    private String codigo;
    private double total;
    private String categoria;

    public Producto() {
    }

    public Producto(int id, String producto, double precio, double precioCosto, int cantidad, String codigo, double total, String categoria) {
        this.id = id;
        this.producto = producto;
        this.precio = precio;
        this.precioCosto = precioCosto;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.total = total;
        this.categoria = categoria;
    }

    public Producto(int id, String producto, double precio, int cantidad, String codigo, double total, String categoria) {
        this(id, producto, precio, 0.0, cantidad, codigo, total, categoria);
    }

    public Producto(String producto, double precio, int cantidad, String codigo, double total) {
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.total = total;
    }

    public Producto(String producto, Double precio, int cantidad, String codigo) {
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
    }

    public Producto(String producto, Double precio, int cantidad, String codigo, String categoria) {
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigo = codigo;
        this.categoria = categoria;
    }

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

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
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
