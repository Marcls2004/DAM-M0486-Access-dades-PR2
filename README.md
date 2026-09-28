# MP0486-RA1 - PR1.2 Serialització d'objectes #

[![Java CI with Maven](https://github.com/jpala4-ieti/DAM-M0486-RA1-PR1.2-Practica-Punt-Partida-26-27/actions/workflows/maven.yml/badge.svg)](https://github.com/jpala4-ieti/DAM-M0486-RA1-PR1.2-Practica-Punt-Partida-26-27/actions/workflows/maven.yml)

Punt de partida de la pràctica PR1.2: dades primitives amb `DataOutputStream`/`DataInputStream`, serialització d'objectes amb `ObjectOutputStream`/`ObjectInputStream`, fitxers CSV i accés aleatori amb `RandomAccessFile`.

### Instruccions ###

Cada exercici té una classe de partida al paquet `com.project` amb els mètodes que criden els tests marcats amb `// *************** CODI PRÀCTICA **********************/`. **No canvieu els noms de les classes ni les signatures d'aquests mètodes**: si ho feu, els tests no compilaran.

| Exercici | Classe de partida | Fitxer de dades |
|---|---|---|
| 0 | `PR120mainPersonesHashmap` | `data/PR120persones.dat` |
| 1 | `PR121mainEscriu`, `PR121mainLlegeix` | `data/PR121HashMapData.ser` |
| 2 | `PR122main` | `data/PR122persones.dat` |
| 3 | `PR123mainTreballadors` | `data/PR123treballadors.csv` |
| 4 | `PR124main` | `data/PR124estudiants.dat` |

Classes de suport (ja fetes, no cal modificar-les):

```
    excepcions/IOFitxerExcepcio.java          Excepció pròpia que han de llançar els mètodes d'E/S
    objectes/PR121hashmap.java                Classe serialitzable de l'exercici 1
    objectes/PR122persona.java                Classe serialitzable de l'exercici 2
    utilitats/UtilsCSV.java                   Utilitats per llegir/escriure/modificar CSV (exercici 3)
    utilitats/UTF8Utils.java                  Truncar noms en UTF-8 sense tallar caràcters (exercici 4)
    exemples/RandomAccessFilesVideojocsManager.java   Exemple complet de RandomAccessFile (base de l'exercici 4)
```

Punts que convé tenir clars abans de començar (els tests els comproven):

* **Exercici 0**: obriu el fitxer amb `FileInputStream`/`FileOutputStream` (el test comprova que la causa de l'`IOFitxerExcepcio` és una `FileNotFoundException`). Un `HashMap` no té mida fixa: escriviu primer el nombre d'entrades i després cada parella nom/edat.
* **Exercici 2**: per detectar "Fitxer no trobat" captureu `FileNotFoundException` abans que `IOException`.
* **Exercici 3**: un Id inexistent o una columna no vàlida han de provocar `IllegalArgumentException`; `UtilsCSV.escriure` propaga la `IOException` perquè la pugueu embolcallar en una `IOFitxerExcepcio`.
* **Exercici 4**: el límit del nom és de **40 bytes UTF-8** (no de caràcters). `afegirEstudiantFitxer` mai no rebutja un nom llarg: el trunca amb `UTF8Utils.truncar`. Consultar o actualitzar sobre un fitxer inexistent mostra "No s'ha trobat..." sense llançar excepció, i un registre duplicat no s'afegeix.

El repositori inclou un `.gitignore` que exclou `target/` i els fitxers `.dat`/`.ser` generats a `data/` (no cal pujar-los: es regeneren executant els programes).

Tots els programes escriuen i llegeixen dins de la carpeta `data/` del projecte:

```java
String camiBase = System.getProperty("user.dir") + "/data/";
```

### Compilació i funcionament ###

Cal el 'Maven' per compilar el projecte
```bash
mvn clean
mvn compile
mvn clean compile test package
```

Per executar una classe amb `main` a Windows
```bash
.\run.ps1 com.project.PR120mainPersonesHashmap
```

Per executar una classe amb `main` a Linux/macOS
```bash
./run.sh com.project.PR120mainPersonesHashmap
```

Per executar sense usar script propi, directament amb maven:
```bash
mvn compile exec:java -PrunMain "-Dexec.mainClass=com.project.PR124main"
```

### Execució de tests ###
```bash
# Executar TOTS els tests
mvn test
# Executar només els tests d'un exercici
mvn test -Dtest=PR120mainPersonesHashmapTest
mvn test -Dtest=PR121mainTest
mvn test -Dtest=PR122mainTest
mvn test -Dtest=PR123mainTreballadorsTest
mvn test -Dtest=PR124mainTest
# Tots els tests que comencin amb "PR12"
mvn test -Dtest="PR12*"
```

El repositori inclou un workflow de GitHub Actions (`.github/workflows/maven.yml`) que executa els tests a cada push: en el lliurament final el semàfor ha de quedar en verd.

### Entrega ###

* Repositori Git privat, compartit amb l'usuari `jpala4-ieti`.
* Subdirectori `doc/` amb el fitxer `memoria.pdf` (l'enunciat en PDF amb el nom i l'enllaç al repositori emplenats).
* A Moodle només s'ha de lliurar l'URL del repositori.

### Visual Studio Code: resseteig de l'entorn de programació Java ###

Si Visual Studio Code no es comporta com esperem i hem provat a solucionar-ho sense èxit podem provar aquestes dues solucions:

* Recarregar la Finestra: Obre la Paleta de Comandes (**Ctrl+Maj+P**), escriu Developer: "**Reload Window**" i prem Enter.

* Netejar l'Espai de Treball: Si recarregar no funciona, obre de nou la Paleta de Comandes (**Ctrl+Maj+P**), escriu "**Java: Clean Java Language Server Workspace**" i prem Enter. Se't demanarà que recarreguis i tornis a escanejar el projecte.
