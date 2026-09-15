package com.mycompany.zl_solucion_integral.models;

import java.util.List;

public class Compra {
    private int id;
    private Integer proveedorId;
    private String proveedorNombre;
    private String proveedorNit;
    private String numFactura;
    private String usuarioRegistro;
    private String fecha;
    private double total;
    private List<DetalleCompra> detalles;

    public Compra() {}

    public Compra(int id, Integer proveedorId, String proveedorNombre, String proveedorNit, String numFactura, String usuarioRegistro, String fecha, double total, List<DetalleCompra> detalles) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorNit = proveedorNit;
        this.numFactura = numFactura;
        this.usuarioRegistro = usuarioRegistro;
        this.fecha = fecha;
        this.total = total;
        this.detalles = detalles;
    }

    public Compra(Integer proveedorId, String proveedorNombre, String proveedorNit, String numFactura, String usuarioRegistro, String fecha, double total) {
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorNit = proveedorNit;
        this.numFactura = numFactura;
        this.usuarioRegistro = usuarioRegistro;
        this.fecha = fecha;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }

    public String getProveedorNit() { return proveedorNit; }
    public void setProveedorNit(String proveedorNit) { this.proveedorNit = proveedorNit; }

    public String getNumFactura() { return numFactura; }
    public void setNumFactura(String numFactura) { this.numFactura = numFactura; }

    public String getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(String usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }
}
