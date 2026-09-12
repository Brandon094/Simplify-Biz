package com.mycompany.zl_solucion_integral.config;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ValidacionesTest {

    @Test
    void testValidarNoVacio() {
        assertTrue(Validaciones.validarNoVacio("Texto"));
        assertFalse(Validaciones.validarNoVacio(""));
        assertFalse(Validaciones.validarNoVacio("   "));
        assertFalse(Validaciones.validarNoVacio((String) null));
    }

    @Test
    void testValidarEmail() {
        assertTrue(Validaciones.validarEmail("test@example.com"));
        assertTrue(Validaciones.validarEmail("usuario.demo@dominio.co"));
        assertFalse(Validaciones.validarEmail("invalido@"));
        assertFalse(Validaciones.validarEmail("sin_arroba.com"));
        assertFalse(Validaciones.validarEmail(null));
    }

    @Test
    void testValidarTelefono() {
        assertTrue(Validaciones.validarTelefono("3001234567"));
        assertTrue(Validaciones.validarTelefono("7654321"));
        assertFalse(Validaciones.validarTelefono("12345")); // Menos de 7
        assertFalse(Validaciones.validarTelefono("12345678901")); // Más de 10
        assertFalse(Validaciones.validarTelefono("abc1234567"));
    }

    @Test
    void testParseEntero() {
        assertEquals(42, Validaciones.parseEntero("42"));
        assertEquals(-5, Validaciones.parseEntero("-5"));
        assertNull(Validaciones.parseEntero("abc"));
        assertNull(Validaciones.parseEntero(null));
        assertNull(Validaciones.parseEntero(""));
    }

    @Test
    void testParseEnteroPositivo() {
        assertEquals(10, Validaciones.parseEnteroPositivo("10"));
        assertNull(Validaciones.parseEnteroPositivo("0"));
        assertNull(Validaciones.parseEnteroPositivo("-3"));
        assertNull(Validaciones.parseEnteroPositivo("xyz"));
    }

    @Test
    void testParseDecimalNoNegativo() {
        assertEquals(15.5, Validaciones.parseDecimalNoNegativo("15.5"));
        assertEquals(0.0, Validaciones.parseDecimalNoNegativo("0"));
        assertEquals(25.99, Validaciones.parseDecimalNoNegativo("25,99")); // Con coma
        assertNull(Validaciones.parseDecimalNoNegativo("-1.5"));
        assertNull(Validaciones.parseDecimalNoNegativo("no_numero"));
    }

    @Test
    void testValidarDescuento() {
        assertTrue(Validaciones.validarDescuento("0"));
        assertTrue(Validaciones.validarDescuento("50"));
        assertTrue(Validaciones.validarDescuento("100"));
        assertTrue(Validaciones.validarDescuento("")); // Vacío es interpretado como 0
        assertFalse(Validaciones.validarDescuento("-5"));
        assertFalse(Validaciones.validarDescuento("105"));
        assertFalse(Validaciones.validarDescuento("texto"));
    }

    @Test
    void testValidarCantidad() {
        assertTrue(Validaciones.validarCantidad("1"));
        assertTrue(Validaciones.validarCantidad("100"));
        assertFalse(Validaciones.validarCantidad("0"));
        assertFalse(Validaciones.validarCantidad("-2"));
        assertFalse(Validaciones.validarCantidad("abc"));
    }

    @Test
    void testParseFecha() {
        Date fecha = Validaciones.parseFecha("12/09/2026");
        assertNotNull(fecha);

        assertNull(Validaciones.parseFecha("31/02/2026")); // Fecha inválida
        assertNull(Validaciones.parseFecha("2026-09-12")); // Formato incorrecto
        assertNull(Validaciones.parseFecha("invalid"));
        assertNull(Validaciones.parseFecha(null));
    }
}
