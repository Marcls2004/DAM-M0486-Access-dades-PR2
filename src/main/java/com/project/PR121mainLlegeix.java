package com.project;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.project.excepcions.IOFitxerExcepcio;
import com.project.objectes.PR121hashmap;

public class PR121mainLlegeix {
    private static String filePath = System.getProperty("user.dir") + "/data/PR121HashMapData.ser";

    public static void main(String[] args) {
        try {
            PR121hashmap hashMap = deserialitzarHashMap();
            hashMap.getPersones().forEach((nom, edat) -> System.out.println(nom + ": " + edat + " anys"));
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error al llegir l'arxiu: " + e.getMessage());
        }
    }

    public static PR121hashmap deserialitzarHashMap() throws IOFitxerExcepcio {
        // *************** CODI PRÀCTICA **********************/

        try (FileInputStream is = new FileInputStream(getFilePath()); 
            ObjectInputStream ois = new ObjectInputStream(is)) {
                
           PR121hashmap hashMap = (PR121hashmap) ois.readObject();
            return hashMap; // Substitueix pel teu
            
        }catch (IOException e) {
            throw new IOFitxerExcepcio("Error en deserialitzar l'objecte HashMap", e);
        }
        catch (ClassNotFoundException e) {
            throw new IOFitxerExcepcio("Error en deserialitzar l'objecte HashMap", e);
        }

    }

    // Getter
    public static String getFilePath() {
        return filePath;
    }

    // Setter
    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }    
}