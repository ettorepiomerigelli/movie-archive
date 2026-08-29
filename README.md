# Movie Archive

Applicazione desktop Java per la gestione di una collezione personale di film.
Progetto svolto per il corso di Ingegneria del Software (traccia: "Gestione di
una collezione di film").

## Funzionalità

- Aggiunta, modifica ed eliminazione di film (titolo, regista, anno, genere,
  valutazione 0-5, stato di visione).
- Annulla/Ripeti (undo/redo) di ogni operazione di modifica dell'archivio.
- Vista tabellare con ricerca testuale (titolo/regista), filtro per genere e
  per stato di visione, ordinamento per titolo, anno o valutazione — tutti
  i criteri sono combinabili tra loro.
- Persistenza automatica su file JSON ad ogni modifica dell'archivio.

## Tecnologie

- Java 17
- Swing (GUI)
- Java NIO (persistenza JSON, implementazione dedicata senza librerie esterne)
- JUnit 5 (testing)
- Maven (build)

## Architettura

Il progetto segue il pattern **MVC**:

- **Model**: `Movie`, `MovieArchive` (package `model`)
- **View**: `MainView`, `MovieFormDialog`, `MovieTableModel` (package `view`)
- **Controller**: `ArchiveController` (package `controller`)

Pattern GoF applicati, con relativa motivazione:

| Pattern | Dove | Perché |
|---|---|---|
| **Observer** | `MovieArchive` → `ArchiveController` | Disaccoppia il Model dalla View: il Controller reagisce ai cambiamenti dell'archivio senza che il Model conosca chi lo osserva. Modello "push": l'evento porta tipo e film coinvolto. |
| **Strategy** | `SortStrategy` e implementazioni | Permette di aggiungere nuovi criteri di ordinamento senza modificare Controller o View. |
| **Composite** | `MovieFilter`, `CompositeMovieFilter` | I criteri di ricerca/filtro (genere, stato, testo) sono oggetti indipendenti combinabili in AND; aggiungere un nuovo filtro non richiede di toccare il Controller. |
| **Command** | `Command`, `CommandManager` e implementazioni | Ogni operazione CRUD è un comando eseguibile e annullabile: abilita undo/redo in modo uniforme. |
| **Factory Method** | `MovieFactory` | Centralizza creazione e validazione dei `Movie`, evitando stati incoerenti (es. anno non valido). |

Il repository (`MovieRepository`/`JsonMovieRepository`) viene iniettato nel
Controller dall'esterno (Dependency Injection) invece di essere un Singleton:
questo rende il Controller testabile con un repository finto, senza toccare
il file system (si veda `ArchiveControllerTest`).

## Struttura del progetto

```
src/main/java/com/moviearchive/
├── Main.java
├── model/            Movie, MovieArchive, Genre, ViewingStatus
├── factory/          MovieFactory
├── observer/         ArchiveObserver, ArchiveEvent
├── strategy/         SortStrategy e implementazioni
├── filter/           MovieFilter, CompositeMovieFilter e implementazioni
├── command/          Command, CommandManager e implementazioni
├── persistence/      MovieRepository, JsonMovieRepository
└── view/             MainView, MovieFormDialog, MovieTableModel
```

## Come eseguire

```bash
mvn clean package
mvn exec:java
```

oppure, da IntelliJ IDEA: importa come progetto Maven ed esegui `Main.java`.

## Test

```bash
mvn test
```
