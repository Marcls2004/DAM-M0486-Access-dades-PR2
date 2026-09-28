package com.project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.project.excepcions.IOFitxerExcepcio;

public class PR120mainPersonesHashmap {
    private static String filePath = System.getProperty("user.dir") + "/data/PR120persones.dat";

    public static void main(String[] args) {
        HashMap<String, Integer> persones = new HashMap<>();
        persones.put("Anna", 25);
        persones.put("Bernat", 30);
        persones.put("Carla", 22);
        persones.put("David", 35);
        persones.put("Elena", 28);

        try {
            escriurePersones(persones);
            llegirPersones();
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error en treballar amb el fitxer: " + e.getMessage());
        }
    }

    // Getter per a filePath
    public static String getFilePath() {
        return filePath;
    }

    // Setter per a filePath
    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }

    // Mètode per escriure les persones al fitxer
    public static void escriurePersones(HashMap<String, Integer> persones) throws IOFitxerExcepcio {
       // *************** CODI PRÀCTICA **********************/
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getFilePath()))) {
            dos.writeInt(persones.size());                    // 1) quants elements hi ha
            for (Map.Entry<String, Integer> e : persones.entrySet()) {
                dos.writeUTF(e.getKey());                     // 2) nom (String, format UTF)
                dos.writeInt(e.getValue());                   // 3) edat (int, 4 bytes)
            }
        }
        catch (Exception e){
                throw new IOFitxerExcepcio("Error al escriure al fitxer: " + e.getMessage());
            }
    }

    // Mètode per llegir les persones des del fitxer
    public static void llegirPersones() throws IOFitxerExcepcio {
        // *************** CODI PRÀCTICA **********************/

        try (DataInputStream dis = new DataInputStream(new FileInputStream(getFilePath()))) {
            int n = dis.readInt();                            // 1) comptador
            for (int i = 0; i < n; i++) {
                String nom = dis.readUTF();                   // 2) nom
                int edat = dis.readInt();                     // 3) edat
                System.out.println(nom + ": " + edat + " anys");
            }
        }catch (Exception e){
            throw new IOFitxerExcepcio("Error al llegir del fitxer: " + e.getMessage());
        }
    }
}
