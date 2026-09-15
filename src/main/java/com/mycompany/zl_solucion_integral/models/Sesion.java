package com.mycompany.zl_solucion_integral.models;

public class Sesion {

    public static String usuarioLogueado;
    public static String rolLogueado = "1";

    public static String getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static void setUsuarioLogueado(String usuario) {
        usuarioLogueado = usuario;
    }

    public static String getRolLogueado() {
        return rolLogueado;
    }

    public static void setRolLogueado(String rol) {
        rolLogueado = rol;
    }
}
