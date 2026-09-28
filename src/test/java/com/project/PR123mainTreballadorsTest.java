package com.project;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.project.utilitats.UtilsCSV;
import com.project.excepcions.IOFitxerExcepcio;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PR123mainTreballadorsTest {

    @TempDir
    File directoriTemporal;

    private File fitxerTemporal;
    private PR123mainTreballadors gestorTreballadors;

    // Contingut inicial del CSV (el mateix que data/PR123treballadors.csv)
    private static final String CONTINGUT_INICIAL =
            "Id,Nom,Cognom,Departament,Salari\n" +
            "123,Nicolás,Rana,2,1000.00\n" +
            "435,Xavi,Gil,2,1800.50\n" +
            "876,Daniel,Ramos,6,700.30\n" +
            "285,Pedro,Drake,4,2500.00\n" +
            "224,Joan,Potter,6,1000.00\n";

    @BeforeEach
    void setUp() throws IOException {
        fitxerTemporal = new File(directoriTemporal, "PR123treballadors.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxerTemporal))) {
            writer.write(CONTINGUT_INICIAL);
        }
        gestorTreballadors = new PR123mainTreballadors();
        gestorTreballadors.setFilePath(fitxerTemporal.getAbsolutePath());
    }

    @Test
    void testModificacioTreballador() throws IOFitxerExcepcio {
        // Modificar el salari del treballador amb Id 123
        gestorTreballadors.modificarTreballador("123", "Salari", "1200.00");

        // Tornar a llegir el fitxer modificat
        List<String> treballadorsDespres = UtilsCSV.llegir(fitxerTemporal.getAbsolutePath());
        assertNotNull(treballadorsDespres);

        // Comprovar que el fitxer modificat conté el nou salari
        int numLinia = UtilsCSV.obtenirNumLinia(treballadorsDespres, "Id", "123");
        String[] dadesModificades = UtilsCSV.obtenirArrayLinia(treballadorsDespres.get(numLinia));
        assertEquals("1200.00", dadesModificades[4]);

        // La resta de línies no han canviat
        assertEquals(6, treballadorsDespres.size());
        assertEquals("Id,Nom,Cognom,Departament,Salari", treballadorsDespres.get(0));
        assertTrue(treballadorsDespres.contains("435,Xavi,Gil,2,1800.50"));
        assertTrue(treballadorsDespres.contains("224,Joan,Potter,6,1000.00"));
    }

    @Test
    void testModificacioIdInexistent() {
        // Un Id que no existeix ha de provocar IllegalArgumentException i no modificar el fitxer
        assertThrows(IllegalArgumentException.class, () ->
                gestorTreballadors.modificarTreballador("999", "Salari", "1.00"));

        List<String> treballadorsDespres = UtilsCSV.llegir(fitxerTemporal.getAbsolutePath());
        assertNotNull(treballadorsDespres);
        assertEquals(6, treballadorsDespres.size());
        assertTrue(treballadorsDespres.contains("123,Nicolás,Rana,2,1000.00"));
    }

    @Test
    void testModificacioColumnaInvalida() {
        // Una columna que no existeix al CSV ha de provocar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                gestorTreballadors.modificarTreballador("123", "Edat", "40"));
    }

    @Test
    void testFitxerInexistent() {
        // Si el fitxer no existeix, els mètodes han de llançar IOFitxerExcepcio
        File fitxerInexistent = new File(directoriTemporal, "noexisteix.csv");
        assertFalse(fitxerInexistent.exists());
        gestorTreballadors.setFilePath(fitxerInexistent.getAbsolutePath());

        assertThrows(IOFitxerExcepcio.class, () -> gestorTreballadors.mostrarTreballadors());
        assertThrows(IOFitxerExcepcio.class, () ->
                gestorTreballadors.modificarTreballador("123", "Salari", "1200.00"));
    }
}
