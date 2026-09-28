package com.project;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.project.utilitats.UTF8Utils;

public class PR124main {

    // Constants que defineixen l'estructura d'un registre (longitud fixa: 48 bytes)
    private static final int ID_SIZE = 4;          // Número de registre: 4 bytes (int)
    private static final int NAME_MAX_BYTES = 40;  // Nom: 40 bytes reservats en UTF-8 (límit de BYTES, no de caràcters)
    private static final int GRADE_SIZE = 4;       // Nota: 4 bytes (float)
    private static final int RECORD_SIZE = ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE; // 48 bytes

    // Posicions dels camps dins el registre
    private static final int NAME_POS = ID_SIZE;                  // El nom comença just després del número de registre
    private static final int GRADE_POS = NAME_POS + NAME_MAX_BYTES; // La nota comença després del nom

    // Atribut per al path del fitxer
    private String filePath;

    private Scanner scanner = new Scanner(System.in);

    // Constructor per inicialitzar el path del fitxer
    public PR124main() {
        this.filePath = System.getProperty("user.dir") + "/data/PR124estudiants.dat"; // Valor per defecte
    }

    // Getter per al filePath
    public String getFilePath() {
        return filePath;
    }

    // Setter per al filePath
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        PR124main gestor = new PR124main();
        boolean sortir = false;

        while (!sortir) {
            try {
                gestor.mostrarMenu();
                int opcio = gestor.getOpcioMenu();

                switch (opcio) {
                    case 1 -> gestor.llistarEstudiants();
                    case 2 -> gestor.afegirEstudiant();
                    case 3 -> gestor.consultarNota();
                    case 4 -> gestor.actualitzarNota();
                    case 5 -> sortir = true;
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Error en la manipulació del fitxer: " + e.getMessage());
            }
        }
    }

    // Mostrar menú d'opcions
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió d'Estudiants");
        System.out.println("1. Llistar estudiants");
        System.out.println("2. Afegir nou estudiant");
        System.out.println("3. Consultar nota d'un estudiant");
        System.out.println("4. Actualitzar nota d'un estudiant");
        System.out.println("5. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    // Obtenir la selecció del menú
    private int getOpcioMenu() {
        return Integer.parseInt(scanner.nextLine());
    }

    // Mètode per llistar tots els estudiants
    public void llistarEstudiants() throws IOException {
        llistarEstudiantsFitxer();
    }

    // Mètode per afegir un nou estudiant
    public void afegirEstudiant() throws IOException {
        int registre = demanarRegistre();
        String nom = demanarNom();
        float nota = demanarNota();
        afegirEstudiantFitxer(registre, nom, nota);
    }

    // Mètode per consultar la nota
    public void consultarNota() throws IOException {
        int registre = demanarRegistre();
        consultarNotaFitxer(registre);
    }

    // Mètode per actualitzar la nota
    public void actualitzarNota() throws IOException {
        int registre = demanarRegistre();
        float novaNota = demanarNota();
        actualitzarNotaFitxer(registre, novaNota);
    }

    // Funcions per obtenir input de l'usuari (validació de la capa interactiva)
    private int demanarRegistre() {
        System.out.print("Introdueix el número de registre (enter positiu): ");
        int registre = Integer.parseInt(scanner.nextLine());
        if (registre <= 0) {
            throw new IllegalArgumentException("El número de registre ha de ser positiu.");
        }
        return registre;
    }

    private String demanarNom() {
        System.out.print("Introdueix el nom (màxim " + NAME_MAX_BYTES + " bytes en UTF-8; si és més llarg es truncarà): ");
        return scanner.nextLine();
    }

    private float demanarNota() {
        System.out.print("Introdueix la nota (valor entre 0 i 10): ");
        float nota = Float.parseFloat(scanner.nextLine());
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("La nota ha de ser un valor entre 0 i 10.");
        }
        return nota;
    }

    // Mètode per trobar la posició (en bytes, des de l'inici del fitxer) del registre
    // d'un estudiant segons el número de registre. Retorna -1 si no es troba.
    private long trobarPosicioRegistre(RandomAccessFile raf, int registreBuscat) throws IOException {
        // *************** CODI PRÀCTICA **********************/
        return 0; // Substitueix pel teu
    }

    // Operacions amb fitxers (són els mètodes que criden els tests: no en canviïs la signatura)

    // Mètode que manipula el fitxer i llista tots els estudiants.
    // Si el fitxer no existeix o és buit ha de mostrar "No hi ha estudiants registrats."
    public void llistarEstudiantsFitxer() throws IOException {
        // *************** CODI PRÀCTICA **********************/
    }

    // Mètode que manipula el fitxer i afegeix l'estudiant al final.
    // - Mai no rebutja un nom llarg: el trunca a NAME_MAX_BYTES (vegeu escriureNom).
    // - Si el número de registre ja existeix no afegeix res i mostra
    //   "Ja existeix un estudiant amb registre: " + registre
    public void afegirEstudiantFitxer(int registre, String nom, float nota) throws IOException {
        // *************** CODI PRÀCTICA **********************/
    }

    // Mètode que manipula el fitxer i consulta la nota d'un estudiant.
    // Si no es troba (o el fitxer encara no existeix) mostra
    // "No s'ha trobat l'estudiant amb registre: " + registre, sense llançar cap excepció.
    public void consultarNotaFitxer(int registre) throws IOException {
        // *************** CODI PRÀCTICA **********************/
    }

    // Mètode que manipula el fitxer i actualitza la nota d'un estudiant (sobreescriu només els 4 bytes de la nota).
    // Si no es troba (o el fitxer encara no existeix) mostra el mateix missatge de "No s'ha trobat...".
    public void actualitzarNotaFitxer(int registre, float novaNota) throws IOException {
        // *************** CODI PRÀCTICA **********************/
    }

    // Funcions auxiliars per a la lectura i escriptura del nom amb UTF-8
    // Llegeix exactament NAME_MAX_BYTES bytes (readFully) i els converteix a String, eliminant el farciment.
    private String llegirNom(RandomAccessFile raf) throws IOException {
        // *************** CODI PRÀCTICA **********************/
        return "<nom>"; // Substitueix pel teu
    }

    // Escriu sempre exactament NAME_MAX_BYTES bytes: trunca amb UTF8Utils.truncar si cal
    // (sense partir cap caràcter) i omple amb bytes a zero fins a NAME_MAX_BYTES.
    private void escriureNom(RandomAccessFile raf, String nom) throws IOException {
        // *************** CODI PRÀCTICA **********************/
    }
}
