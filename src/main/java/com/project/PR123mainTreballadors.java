package com.project;

import com.project.excepcions.IOFitxerExcepcio;
import com.project.utilitats.UtilsCSV;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Scanner;

public class PR123mainTreballadors {
    private String filePath = System.getProperty("user.dir") + "/data/PR123treballadors.csv";
    private Scanner scanner = new Scanner(System.in);

    // Getters i setters per a filePath
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void iniciar() {
        boolean sortir = false;

        while (!sortir) {
            try {
                // Mostrar menú
                mostrarMenu();

                // Llegir opció de l'usuari
                int opcio = Integer.parseInt(scanner.nextLine());

                switch (opcio) {
                    case 1 -> mostrarTreballadors();
                    case 2 -> modificarTreballadorInteractiu();
                    case 3 -> {
                        System.out.println("Sortint...");
                        sortir = true;
                    }
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IllegalArgumentException e) {
                // Id inexistent o columna no vàlida (la llança modificarTreballador)
                System.out.println("Error: " + e.getMessage());
            } catch (IOFitxerExcepcio e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // Mètode que mostra el menú
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió de Treballadors");
        System.out.println("1. Mostra tots els treballadors");
        System.out.println("2. Modificar dades d'un treballador");
        System.out.println("3. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    // Mètode per mostrar els treballadors llegint el fitxer CSV
    public void mostrarTreballadors() throws IOFitxerExcepcio {
        // *************** CODI PRÀCTICA **********************/
        
        try {
            List<String> treballadorsCSV = llegirFitxerCSV(); // se hace una lista para poder mostrarlo
            for (String treballador : treballadorsCSV) {
                System.out.println(treballador); //se recorre la lista y se imprime cada trabajador
            }
        } catch (IOFitxerExcepcio e) {
            throw new IOFitxerExcepcio("Error en llegir el fitxer: " + filePath, e);
        }

    }

    // Mètode per modificar un treballador (interactiu)
    public void modificarTreballadorInteractiu() throws IOFitxerExcepcio {
        // Demanar l'ID del treballador
        System.out.print("\nIntrodueix l'ID del treballador que vols modificar: ");
        String id = scanner.nextLine();

        // Demanar quina dada vols modificar
        System.out.print("Quina dada vols modificar (Nom, Cognom, Departament, Salari)? ");
        String columna = scanner.nextLine();

        // Demanar el nou valor
        System.out.print("Introdueix el nou valor per a " + columna + ": ");
        String nouValor = scanner.nextLine();

        // Modificar treballador
        modificarTreballador(id, columna, nouValor);
    }

    // Mètode que modifica treballador (per a tests i usuaris) llegint i escrivint sobre disc.
    // - Si l'Id no existeix o la columna no és vàlida ha de llançar IllegalArgumentException
    //   amb un missatge descriptiu (el menú la captura).
    // - Si hi ha problemes amb el fitxer ha de llançar IOFitxerExcepcio.
    public void modificarTreballador(String id, String columna, String nouValor) throws IOFitxerExcepcio {
        // *************** CODI PRÀCTICA **********************/

        int columnaArray = 0;
        boolean trobatParametre = false;
        boolean trobatTreballador = false;


        try{
            
            List<String> treballadorsCSV = llegirFitxerCSV(); //se lee el archivo y se guarda en una lista

            String cabeceraString = treballadorsCSV.get(0);

            String[] cabecera = cabeceraString.split(","); //se separa la cabecera en un array

            for (int j = 0; j < cabecera.length; j++) {
                if (cabecera[j].equals(columna)) {
                    //se guarda la posición de la columna en el array
                    columnaArray = j;
                    trobatParametre = true;
                    break;
                }
            }
            //si no se ha encontrado la columna, se lanza una excepción
            if (!trobatParametre) {
                throw new IllegalArgumentException("Columna no vàlida");
            }

            for (int i = 0; i < treballadorsCSV.size(); i++) {
                //se recorre la lista de trabajadores
                if (i != 0){
                    String[] treballador = treballadorsCSV.get(i).split(","); //se separa el trabajador en un array
                    if (treballador[0].equals(id)){
                        //si se encuentra el trabajador con el id introducido, se modifica el valor de la columna correspondiente
                        treballador[columnaArray] = nouValor; //se modifica el valor de la columna correspondiente
                        String treballadorModificat = String.join(",", treballador); //se une el array en un string
                        treballadorsCSV.set(i, treballadorModificat); //se guarda el trabajador modificado en la lista
                        trobatTreballador = true; //se indica que se ha encontrado el trabajador
                        break;
                    }
                }
            }

            //si no se ha encontrado el trabajador con el id introducido, se lanza una excepción
            if (!trobatTreballador){
                throw new IllegalArgumentException("Id inexistent");
            }

            escriureFitxerCSV(treballadorsCSV); //se escribe el archivo con los cambios realizados
            
        }catch (IOFitxerExcepcio e) {
            throw new IOFitxerExcepcio("Error en llegir el fitxer: " + filePath, e);
        }

    }

    // Encapsulació de llegir el fitxer CSV
    private List<String> llegirFitxerCSV() throws IOFitxerExcepcio {
        List<String> treballadorsCSV = UtilsCSV.llegir(filePath);
        if (treballadorsCSV == null) {
            throw new IOFitxerExcepcio("Error en llegir el fitxer: " + filePath);
        }
        return treballadorsCSV;
    }

    // Encapsulació d'escriure el fitxer CSV
    private void escriureFitxerCSV(List<String> treballadorsCSV) throws IOFitxerExcepcio {
        try {
            UtilsCSV.escriure(filePath, treballadorsCSV);
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en escriure el fitxer: " + filePath, e);
        }
    }

    // Mètode main
    public static void main(String[] args) {
        PR123mainTreballadors programa = new PR123mainTreballadors();
        programa.iniciar();
    }
}
