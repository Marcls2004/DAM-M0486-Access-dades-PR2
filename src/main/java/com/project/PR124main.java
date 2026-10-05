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
        // 1. Ens col·loquem al byte 0 (l'inici absolut del fitxer)
        raf.seek(0); 
        
        // 2. Bucle que s'executa mentre el punter no arribi al final del fitxer (raf.length())
        while (raf.getFilePointer() < raf.length()) {
            
            // Guardem la posició exacta on comença AQUEST estudiant en concret
            long posicioActual = raf.getFilePointer(); 
            
            // Llegim el número de registre (això mou automàticament el punter 4 bytes endavant)
            int registreActual = raf.readInt(); 
            
            // Si el número coincideix amb el que busquem, objectiu aconseguit!
            if (registreActual == registreBuscat) {
                return posicioActual; // Retornem la posició on començava l'estudiant
            }
            
            // SI NO COINCIDEIX: Saltem (skipBytes) els 40 bytes del nom + els 4 bytes de la nota.
            // Així el punter es col·loca directament a l'inici del SEGUENT estudiant.
            raf.skipBytes(NAME_MAX_BYTES + GRADE_SIZE);
        }
        
        // Si el bucle acaba i no hem trobat res, retornem -1
        return -1; 
    }


    // Operacions amb fitxers (són els mètodes que criden els tests: no en canviïs la signatura)

    // Mètode que manipula el fitxer i llista tots els estudiants.
    // Si el fitxer no existeix o és buit ha de mostrar "No hi ha estudiants registrats."
    public void llistarEstudiantsFitxer() throws IOException {
        // *************** CODI PRÀCTICA **********************/
        
        //Comprovem si el fitxer existeix i si té contingut
        File fitxer = new File(filePath);
        if (!fitxer.exists() || fitxer.length() == 0) {
            // Si el fitxer no existeix o està buit, mostrem el missatge corresponent
            System.out.println("No hi ha estudiants registrats.");
            return;
        }else{

            // Si el fitxer existeix i té contingut, llistem els estudiants
            try (RandomAccessFile raf = new RandomAccessFile(fitxer, "rw")) { 
                raf.seek(0); // Comencem des del principi del fitxer
                
                // Iterem sobre tots els registres del fitxer fins al final
                while (raf.getFilePointer() < raf.length()) {
                    int registre = raf.readInt(); // Llegim el número de registre
                    String nom = llegirNom(raf); // Llegim el nom
                    float nota = raf.readFloat(); // Llegim la nota

                    // Mostrem la informació de l'estudiant
                    System.out.println("Registre: " + registre + ", Nom: " + nom + ", Nota: " + nota);
                }
            }
        }
    }

    // Mètode que manipula el fitxer i afegeix l'estudiant al final.
    // - Mai no rebutja un nom llarg: el trunca a NAME_MAX_BYTES (vegeu escriureNom).
    // - Si el número de registre ja existeix no afegeix res i mostra
    //   "Ja existeix un estudiant amb registre: " + registre
   public void afegirEstudiantFitxer(int registre, String nom, float nota) throws IOException {
        File fitxer = new File(filePath);
        
        // 1. Seguretat: Si la carpeta "/data/" no existeix, la creem perquè no doni un error
        File carpeta = fitxer.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        // 2. Obrim el fitxer. Si no existeix, el Java el crearà buit en aquest moment
        try (RandomAccessFile raf = new RandomAccessFile(fitxer, "rw")) { 
            
            // 3. Busquem si aquest número de registre ja està agafat
            long posicio = trobarPosicioRegistre(raf, registre);
            
            // Si trobarPosicioRegistre NO torna -1, vol dir que l'estudiant ja existeix!
            if (posicio != -1) {
                System.out.println("Ja existeix un estudiant amb registre: " + registre);
                return; // Bloquegem l'escriptura i sortim
            }
            
            // 4. Si el registre està lliure, anem al final absolut del fitxer per escriure-hi
            raf.seek(raf.length()); 
            
            // 5. Escrivim les dades en ordre exacte (48 bytes en total)
            raf.writeInt(registre); // Escriu 4 bytes
            escriureNom(raf, nom);   // Escriu exactament 40 bytes (gràcies a la teva funció d'omplir amb zeros)
            raf.writeFloat(nota);   // Escriu 4 bytes
            
            System.out.println("Estudiant afegit correctament.");
        }
    }


    // Mètode que manipula el fitxer i consulta la nota d'un estudiant.
    // Si no es troba (o el fitxer encara no existeix) mostra
    // "No s'ha trobat l'estudiant amb registre: " + registre, sense llançar cap excepció.
    public void consultarNotaFitxer(int registre) throws IOException {
        File fitxer = new File(filePath);
        
        // 1. Control d'errors: Si el fitxer no existeix, no cal ni obrir-lo
        if (!fitxer.exists() || fitxer.length() == 0) {
            System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            return;
        }

        // 2. Obrim en mode només lectura ("r")
        try (RandomAccessFile raf = new RandomAccessFile(fitxer, "r")) { 
            
            // 3. Busquem a on està l'estudiant
            long posicio = trobarPosicioRegistre(raf, registre);
            
            // 4. CORRECCIÓ: Si és -1, és que realment NO existeix
            if (posicio == -1) { 
                System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            } else {
                // 5. Si existeix, saltem (seek) directament a la seva posició inicial
                raf.seek(posicio);
                
                // 6. Llegim els 48 bytes correlatius per recuperar la informació
                int id = raf.readInt();
                String nom = llegirNom(raf);
                float nota = raf.readFloat();
                
                // 7. Mostrem el resultat demanat per l'enunciat
                System.out.println("Registre: " + id + ", Nom: " + nom + ", Nota: " + nota);
            }
        }
    }


    // Mètode que manipula el fitxer i actualitza la nota d'un estudiant (sobreescriu només els 4 bytes de la nota).
    // Si no es troba (o el fitxer encara no existeix) mostra el mateix missatge de "No s'ha trobat...".
    public void actualitzarNotaFitxer(int registre, float novaNota) throws IOException {
        File fitxer = new File(filePath);
        
        // 1. Control inicial: Si no hi ha fitxer, l'estudiant no existeix
        if (!fitxer.exists() || fitxer.length() == 0) {
            System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            return;
        }

        // 2. Obrim en mode lectura/escriptura ("rw") perquè hem de modificar dades
        try (RandomAccessFile raf = new RandomAccessFile(fitxer, "rw")) { 
            
            // 3. Busquem on comença el registre d'aquest estudiant
            long posicioInici = trobarPosicioRegistre(raf, registre);
            
            if (posicioInici == -1) {
                // Si no el trobem, mostrem el missatge d'error
                System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            } else {
                // 4. L'ACCÉS DIRECTE: Movem el punter directament a la nota d'aquest estudiant.
                // Sumem: Posició on comença l'estudiant + 44 bytes de desplaçament (GRADE_POS)
                raf.seek(posicioInici + GRADE_POS);
                
                // 5. Escrivim la nova nota a sobre de la vella (ocupa exactament els mateixos 4 bytes)
                raf.writeFloat(novaNota); 
                
                System.out.println("Nota actualitzada correctament.");
            }
        }
    }


    // Funcions auxiliars per a la lectura i escriptura del nom amb UTF-8
    // Llegeix exactament NAME_MAX_BYTES bytes (readFully) i els converteix a String, eliminant el farciment.
    private String llegirNom(RandomAccessFile raf) throws IOException {
        // *************** CODI PRÀCTICA **********************/
        // Creem un buffer de bytes amb la mida exacta de NAME_MAX_BYTES
        byte[] buffer = new byte[NAME_MAX_BYTES];
        // Llegim exactament NAME_MAX_BYTES bytes del fitxer
        raf.readFully(buffer);
        
        // Convertim els bytes a String utilitzant UTF-8 i eliminem els caràcters de farciment (zeros) al final
        return new String(buffer, StandardCharsets.UTF_8).trim();
    }

    // Escriu sempre exactament NAME_MAX_BYTES bytes: trunca amb UTF8Utils.truncar si cal
    // (sense partir cap caràcter) i omple amb bytes a zero fins a NAME_MAX_BYTES.
    private void escriureNom(RandomAccessFile raf, String nom) throws IOException {
        // *************** CODI PRÀCTICA **********************/

        // Convertim el nom a bytes UTF-8
        byte[] bytesOriginals = nom.getBytes(StandardCharsets.UTF_8);
        // Truncar correctament a NAME_MAX_BYTES sense tallar caràcters
        byte[] bytesTruncats = UTF8Utils.truncar(bytesOriginals, NAME_MAX_BYTES);
        // Escriure els bytes truncats al fitxer
        raf.write(bytesTruncats);

        // Omplir amb zeros fins a NAME_MAX_BYTES si cal
        for (int i = bytesTruncats.length; i < NAME_MAX_BYTES; i++) {
            raf.writeByte(0); // Omplim amb zeros fins a NAME_MAX_BYTES
        }
    }
}
