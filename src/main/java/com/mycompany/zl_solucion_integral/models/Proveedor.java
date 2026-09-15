package com.mycompany.zl_solucion_integral.models;

public class Proveedor {
    private int id;
    private String nombre;
    private String nit;
    private String telefono;
    private String email;
    private String direccion;

    public Proveedor() {}

    public Proveedor(int id, String nombre, String nit, String telefono, String email, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public Proveedor(String nombre, String nit, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
