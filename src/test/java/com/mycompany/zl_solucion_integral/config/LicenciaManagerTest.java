package com.mycompany.zl_solucion_integral.config;

import com.mycompany.zl_solucion_integral.tools.GeneradorLicenciaAdmin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LicenciaManagerTest {

    @Test
    public void testGetHardwareIdNotNull() {
        String hwId = HardwareUtils.getHardwareId();
        assertNotNull(hwId, "El Hardware ID no debe ser nulo");
        assertTrue(hwId.matches("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$"),
                "El Hardware ID debe cumplir el formato XXXX-XXXX-XXXX-XXXX. Obtenido: " + hwId);
    }

    @Test
    public void testObtenerInfoLicenciaDefaultDemo() {
        LicenciaManager.InfoLicencia info = LicenciaManager.obtenerInfoLicencia();
        assertNotNull(info, "InfoLicencia no debe ser nula");
        assertNotNull(info.getHardwareId(), "El HWID en la info no debe ser nulo");
        assertTrue(info.getDiasRestantes() >= 0, "Los días de prueba deben ser >= 0");
    }

    @Test
    public void testActivarLicenciaClaveInvalida() {
        ResultadoOperacion res = LicenciaManager.activarLicencia("CLAVE_INVALIDA_999");
        assertFalse(res.esExito(), "Una clave inválida debe retornar un error");
    }

    @Test
    public void testRechazarBypassLicenciaSimulada() {
        String hwId = HardwareUtils.getHardwareId();
        String prefix = hwId.substring(0, 9);
        String tokenSimuladoInseguro = "ERPPRO-" + prefix + "-20271231-HASH123";

        ResultadoOperacion res = LicenciaManager.activarLicencia(tokenSimuladoInseguro);
        assertFalse(res.esExito(), "Un token simulado ERPPRO- ya NO debe ser aceptado por seguridad");
    }

    @Test
    public void testActivarLicenciaCriptograficaRsaValida() throws Exception {
        String hwId = HardwareUtils.getHardwareId();
        String tokenRsaValido = GeneradorLicenciaAdmin.generarTokenLicencia("Cliente Test Pro", hwId, "2099-12-31");

        ResultadoOperacion res = LicenciaManager.activarLicencia(tokenRsaValido);
        assertTrue(res.esExito(), "Una clave firmada con RSA-2048 correspondiente al HWID debe ser exitosa");

        LicenciaManager.InfoLicencia info = LicenciaManager.obtenerInfoLicencia();
        assertEquals(LicenciaManager.EstadoLicencia.PRO_ACTIVA, info.getEstado(), "El estado debe pasar a PRO_ACTIVA");
        assertEquals("Cliente Test Pro", info.getCliente(), "El cliente debe coincidir");
    }
}
