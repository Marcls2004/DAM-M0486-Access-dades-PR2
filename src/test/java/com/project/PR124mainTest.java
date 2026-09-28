package com.project;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PR124mainTest {

    @TempDir
    File tempDir;

    private PR124main gestor;

    @BeforeEach
    void setUp() {
        gestor = new PR124main();
        gestor.setFilePath(new File(tempDir, "PR124estudiants.dat").getAbsolutePath());
    }

    @Test
    void testAfegirIConsultarEstudiant() throws IOException {
        // Afegir un estudiant
        gestor.afegirEstudiantFitxer(1, "Estudiant Test", 8.5f);
        
        // Consultar l'estudiant afegit
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(1));
    }

    @Test
    void testLlistarEstudiants() throws IOException {
        // Afegir alguns estudiants
        gestor.afegirEstudiantFitxer(1, "Joan", 7.0f);
        gestor.afegirEstudiantFitxer(2, "Marta", 9.0f);

        // Capturar la sortida estàndard (System.out)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Llistar estudiants
            gestor.llistarEstudiantsFitxer();
        } finally {
            // Restaurar la sortida estàndard
            System.setOut(originalOut);
        }

        // Obtenir la sortida capturada com a String
        String output = outputStream.toString();

        // Verificar que la sortida conté els estudiants esperats
        assertTrue(output.contains("Registre: 1, Nom: Joan, Nota: 7.0"));
        assertTrue(output.contains("Registre: 2, Nom: Marta, Nota: 9.0"));
    }

    @Test
    void testConsultarEstudiantNoExistent() throws IOException {
        // Intentar consultar un estudiant que no existeix
        gestor.afegirEstudiantFitxer(1, "Joan", 7.0f);
        gestor.afegirEstudiantFitxer(2, "Marta", 9.0f);
        
        // Consultar un registre no existent
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(3));
    }

    @Test
    void testActualitzarNotaEstudiant() throws IOException {
        // Afegir un estudiant i actualitzar la seva nota
        gestor.afegirEstudiantFitxer(1, "Anna", 6.0f);
        gestor.actualitzarNotaFitxer(1, 9.5f);
        
        // Consultar per verificar l'actualització
        assertDoesNotThrow(() -> gestor.consultarNotaFitxer(1));
    }
    
    @Test
    void testMidaRegistreCorrecte() throws IOException {
        // Verificar que la mida de cada registre sigui correcta (48 bytes)
        gestor.afegirEstudiantFitxer(1, "Test", 5.0f);
        File file = new File(gestor.getFilePath());
        assertEquals(48, file.length());  // Verifica la mida del fitxer: 4 (int) + 40 (nom) + 4 (float)
    }

    @Test
    void testTruncamentNom() throws IOException {
        // Afegim un estudiant amb un nom molt llarg
        gestor.afegirEstudiantFitxer(1, "UnNomRealmentMoltLlargQueSuperaEls40Bytes", 7.5f);
    
        // Capturar la sortida
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }
    
        String output = outputStream.toString();
        // Assegurar que el nom ha estat truncat a una longitud màxima raonable
        assertTrue(output.contains("Registre: 1, Nom: UnNomRealmentMoltLlargQueS"));
    }
 
    @Test
    void testLecturaMultiplesRegistres() throws IOException {
        for (int i = 1; i <= 100; i++) {
            gestor.afegirEstudiantFitxer(i, "Estudiant" + i, i % 10);
        }
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }
    
        String output = outputStream.toString();
        for (int i = 1; i <= 100; i++) {
            assertTrue(output.contains("Registre: " + i));
        }
    }

    @Test
    void testActualitzarRegistreNoExistent() throws IOException {
        // Intentem actualitzar un estudiant que no existeix
        gestor.actualitzarNotaFitxer(999, 5.0f);
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    
        try {
            gestor.consultarNotaFitxer(999);
        } finally {
            System.setOut(originalOut);
        }
    
        String output = outputStream.toString();
        assertTrue(output.contains("No s'ha trobat l'estudiant amb registre: 999"));
    }

    @Test
    void testTruncamentNoPartCaracterMultibyte() throws IOException {
        // 39 bytes ASCII + 'é' (2 bytes) = 41 bytes: cal truncar just abans de la 'é',
        // el registre ha de continuar ocupant 48 bytes i el nom no ha de contenir cap caràcter trencat
        String nom = "abcdefghijklmnopqrstuvwxyzabcdefghijklm" + "é";
        gestor.afegirEstudiantFitxer(1, nom, 5.0f);
        gestor.afegirEstudiantFitxer(2, "Segon", 6.0f);

        File file = new File(gestor.getFilePath());
        assertEquals(2 * 48, file.length());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Registre: 1, Nom: abcdefghijklmnopqrstuvwxyzabcdefghijklm, Nota: 5.0"));
        assertTrue(output.contains("Registre: 2, Nom: Segon, Nota: 6.0"));
        assertFalse(output.contains("\uFFFD"));  // cap caràcter de substitució (UTF-8 trencat)
    }

    @Test
    void testLlistarSenseFitxer() throws IOException {
        // Sense fitxer: llistar ha d'informar que no hi ha estudiants i no ha de llançar excepció
        assertFalse(new File(gestor.getFilePath()).exists());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(outputStream.toString().contains("No hi ha estudiants registrats."));
    }

    @Test
    void testConsultarIActualitzarSenseFitxer() throws IOException {
        // Sense fitxer: consultar i actualitzar han de mostrar "No s'ha trobat..." i no llançar excepció
        assertFalse(new File(gestor.getFilePath()).exists());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            gestor.consultarNotaFitxer(7);
            gestor.actualitzarNotaFitxer(7, 5.0f);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("No s'ha trobat l'estudiant amb registre: 7"));
        assertFalse(output.contains("Nota actualitzada correctament."));
    }

    @Test
    void testAfegirRegistreDuplicat() throws IOException {
        // Un número de registre repetit no s'ha d'afegir
        gestor.afegirEstudiantFitxer(1, "Anna", 6.0f);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            gestor.afegirEstudiantFitxer(1, "Anna repetida", 9.0f);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(outputStream.toString().contains("Ja existeix un estudiant amb registre: 1"));
        assertEquals(48, new File(gestor.getFilePath()).length());  // només un registre
    }

    @Test
    void testNomsAmbAccentsICaractersEspecials() throws IOException {
        gestor.afegirEstudiantFitxer(1, "José García", 8.5f);
        gestor.afegirEstudiantFitxer(2, "Renée O'Connor", 7.5f);
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }
    
        String output = outputStream.toString();
        assertTrue(output.contains("José García"));
        assertTrue(output.contains("Renée O'Connor"));
    }

    @Test
    void testNomsAmbCaractersXinesos() throws IOException {
        gestor.afegirEstudiantFitxer(1, "张伟", 9.0f);  // Nom comú en xinès
        gestor.afegirEstudiantFitxer(2, "王芳", 8.0f);  // Un altre nom comú en xinès
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    
        try {
            gestor.llistarEstudiantsFitxer();
        } finally {
            System.setOut(originalOut);
        }
    
        String output = outputStream.toString();
        assertTrue(output.contains("张伟"));
        assertTrue(output.contains("王芳"));
    }
}
