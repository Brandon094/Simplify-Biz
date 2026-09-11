package com.mycompany.zl_solucion_integral.models;

/**
 * Clase que representa a un usuario en el sistema.
 *
 * @author ChopCode Solutions
 */
public class Usuario {

    // Atributos
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private String contraseña;
    private String rol; // Se puede manejar como String o int según el diseño de tu aplicación
    private String noCc;
    private String NIT;
    private String DIR;

    // Constructor vacío (útil para instanciar sin datos iniciales)
    public Usuario() {
    }

    // Constructor con parámetros (para inicializar con datos)
    public Usuario(int id, String nombre, String telefono, String email, String contraseña, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    // Getters y Setters para cada atributo
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNoCc() {
        return noCc;
    }

    public void setNoCc(String noCc) {
        this.noCc = noCc;
    }

    public String getNIT() {
        return NIT;
    }

    public void setNIT(String NIT) {
        this.NIT = NIT;
    }

    public String getDIR() {
        return DIR;
    }

    public void setDIR(String DIR) {
        this.DIR = DIR;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombre=" + nombre + ", telefono=" + telefono + ", email=" + email + ", contrase\u00f1a=" + contraseña + ", rol=" + rol + ", noCc=" + noCc + ", NIT=" + NIT + ", DIR=" + DIR + '}';
    }

}
