package com.mycompany.zl_solucion_integral.models;

public class DetalleCompra {
    private int id;
    private int compraId;
    private String producto;
    private String codigo;
    private int cantidad;
    private double precioCosto;
    private double subtotal;

    public DetalleCompra() {}

    public DetalleCompra(int id, int compraId, String producto, String codigo, int cantidad, double precioCosto, double subtotal) {
        this.id = id;
        this.compraId = compraId;
        this.producto = producto;
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.precioCosto = precioCosto;
        this.subtotal = subtotal;
    }

    public DetalleCompra(String producto, String codigo, int cantidad, double precioCosto) {
        this.producto = producto;
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.precioCosto = precioCosto;
        this.subtotal = cantidad * precioCosto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompraId() { return compraId; }
    public void setCompraId(int compraId) { this.compraId = compraId; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
