<img src="mediaticon.png" width=250 height=250 align="left"></img>

# Mediaticon-sviluppatori
Progetto Mediaticon - Repository privata programmatori
<br></br>
[![tag](https://img.shields.io/badge/languages-C%23%20%7c%20Java%20%7c%20python-orange.svg)](https://github.com/VisualLaser10New)
[![tag](https://img.shields.io/badge/testing--on-Windows-green.svg)](https://github.com/Giova-Bell)

please follow the [workflow guide](#workflow) to avoid any conflicts in the various commits and to make the workflow smoother

# Git help
   * `git clone <link>` copy all the repository in the current path

   * `git checkout <branch-name>` move to branch
   * `git checkout -b <branch-name>` create a new branch

   * `git branch -D <branch-name>` delete branch

   * `code .` open editor

   * `git status` check status of branch
   * `git add -A` add all changes
   * `git commit -m "comment"` commit all changes with comment

   * `git push` #push all changes
   * `git push origin <branch-name>` push all changes in branch

   * `git pull` update all
   * `git pull origin <branch-name>` update a branch of the local repository (from another branch)
   
   * `git push -f origin <branch to copy>:<branch to paste>` make a branch equal to another

## <a name="workflow"></a> Workflow
> 1) make a new branch to work on the feature
> 2) make a pull request to merge the branch
> 3) wait for testing (or if u do it yourself for the ok of the contributors)
> 4) if at the time u make the pull request a branch has already been merged, update your branch with the new changes
> 5) if the case above has happened return to point 4.
> 6) merge once u have the approval of all the contributors

# Ideas for documentation
## Mermaid
```mermaid
classDiagram
  class Abstraction {
    <<abstract>>
  }
  RefinedAbstaction1 --|> Abstraction
  RefinedAbstaction2 --|> Abstraction
  class Implementation {
    <<abstract>>
    +implementation()
  }
  Abstraction o-- Implementation : uses
  Implementation <|-- ConcreteImplementation1
  Implementation <|-- ConcreteImplementation2
  ConcreteImplementation1: +implementation()
  ConcreteImplementation2: +implementation()
```

# Cose veramente utili (non come quanto scritto sopra, ovviamente VL scherza)
## Mediaticon DB
### Informazioni
Mediaticon DB è il gestore del database che mediaticon app utilizzerà per prelevare le informazione dei contenuti.
Il software serve principalmente per effettuare i download dei file csv e successivamente aggiornare il database con quanto appena scaricato.
Inoltre sono presenti metodi sia per la gestione della guida tv sia per l'implementazione della lista di contenuti selezionati dall'utente.
### Classi & Utilizzi
#### Download
La Classe download contiene i metodi utili per l'acquisizione dei file "CSV", i quali contengono i contenuti cinematografici.
In aggiunta, sono presenti costrutti adibiti al controllo delle eccezioni e all'"Error Correction". Pertanto il programma è in grado di evitare errori nella scrittura del database, i quali, essendo i titoli ordinati per 'data di uscita', corromperebbero l'intero sistema.
## Mediaticon APP
### Informazioni
### Finestre
#### Login
#### MainWindow
#### Description Window
#### MyListWindow
## Mediaticon Server
[V2 Description & Guide](https://github.com/cristiancrazy/Mediaticon-sviluppatori/blob/6963698901a5a1cb5bb3f337466f44f958cd8925/Mediaticon%20Server%20V2/README.md)
## Mediaticon Dependency installers
