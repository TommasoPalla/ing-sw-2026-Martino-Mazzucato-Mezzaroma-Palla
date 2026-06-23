# Mesos Game Board
<img src="https://www.craniocreations.it/storage/media/products/490/2297/responsive-images/Mesos_sx_SITO___optimized_600_600.webp?raw=true" width="350" align="right" />

## About
Mesos Board Game is the final project of the **"Software Engineering"** course (2025/2026).

**Professor:** San Pietro Pierluigi

### The Team

* [Paolo Martino](https://github.com/PaoloMartino)
* [Lorenzo Mazzucato](https://github.com/lorenzomazzucato)
* [Davide Mezzaroma](https://github.com/DavideMezzaroma)
* [Tommaso Palla](https://github.com/TommasoPalla)

## Table of Contents

* [Specifications](#specifications)
* [Implemented Functionalities](#implemented-functionalities)
* [Javadocs](#javadocs)
* [How to compile](#how-to-compile)
* [How to run from JARs](#how-to-run-from-jars)
* [How to run from IDE (IntelliJ IDEA)](#how-to-run-from-ide-intellij-idea)
* [More](#more)

## Game's screenshots

### Login Screen
<img src="https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/blob/master/readme%20assets/Mesos_login.png?raw=true" alt="Mesos Login Screen" width="800px">

### Waiting Lobby
<img src="https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/blob/master/readme%20assets/Mesos_lobby.png?raw=true" alt="Mesos Lobby Screen" width="800px">

### Main Game Board
<img src="https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/blob/master/readme%20assets/Mesos_mainBoard.png?raw=true" alt="Mesos Main Board Screen" width="800px">


## Specifications

The project consists of a Java version of the board game *Mesos*, made by Cranio Creations.

The full board game details can be found [here][boardgame-publisher-link].

The final version includes:
* working game implementation, compliant with the game's rules;
* [final Model's UML diagram][finalUML-link], created by us to explain (at a high level) how the model operates
* [socket(TCP) protocol documentation][protocolDoc-link]. We chose 3 methods and described how these get handled in the network;
* source code of the [implementation][main-link];
* source code of [unit tests][tests-link];

### Implemented Functionalities
<table>
<tr><td>

| Functionality | Status |
|:--------------|:--------------:|
| Basic rules    | :white_check_mark: |
| Complete rules | :white_check_mark: |
| [Socket + RMI][networking-link] | :white_check_mark: |
| [CLI][cli-link] | :white_check_mark: |
| [GUI][gui-link] | :white_check_mark: |
| Database | :white_check_mark: |
| Multiple games | :white_check_mark: |
| Persistence | :x: |
| Resilience | :x: |

</td></tr>
</table>

### Test coverage
<img src="https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/blob/master/readme%20assets/Test_coverage.png?raw=true" alt="Test Coverage">

## Javadocs

Each method and class in this project has a Javadoc associated to it.

We wrote it to ensure the best readability of the code

## How to compile

Build dependencies:
* JDK 23
* maven

1. Clone this repo by downloading the ```.zip``` file or by using the command ```git clone``` on your terminal
2. In the root directory of the project run this command to clean the environment and generate the jars.
```
mvn clean package
```
3. Two compiled jars (```PSP11-server.jar``` and ```PSP11-client.jar```) can be found in the newly generated folder `\target`, and can be run as described below.

## How to run from JARs
### System requirements
* Java 23 or later

You can either run the JARs contained in the target directory, which has been previously built with Maven (see the [Compile](#how-to-compile) section), or run the JARs in the [deliverables/jars][deliverablesJars-link]. You can also just download the jars to run them from whatever folder you want on your pc, without the need to clone the repo.

Now open a terminal on your pc and navigate to the chosen folder.

### Server
1. Run this command
```java -jar PSP11-server.jar```
2. If you want, you can configure your server to connect a MySQL Database of choice to it. You will need to enter the Database URL, the USERNAME and the PASSWORD (Note: The Database structure has to be the same as the one found in the [Database dump][mesos_db_dump-link], otherwise it will not work properly).

### Client
1. Run this command
```java -jar PSP11-client.jar```
2. Select the type of connection you want to use (Socket or RMI)
3. Set server IP and ports for Socket and RMI connections
4. Select the type of User Interface you want to use to play the game (TUI or GUI).

## How to run from IDE (IntelliJ IDEA)
### Server
* Run ServerApp
* If you want, you can configure your server to connect a MySQL Database of choice to it. You will need to enter the Database URL, the USERNAME and the PASSWORD (Note: The Database structure has to be the same as the one found in the [Database dump][mesos_db_dump-link], otherwise it will not work properly).
### Client
* Run ClientApp
* Set server IP and ports for Socket and RMI connections
* Select the type of connection you want to use (Socket or RMI)
* Select the type of User Interface you want to use to play the game (TUI or GUI).

Mind that to play the game you will need at least:
* One Server running (with or without a connected Database)
* 2 Clients (using CLI or GUI).

## More

### Software used

**Intellij IDEA** - main IDE

**Git** - Version control

**Scenebuilder** - GUI design

**DrawIO/Mermaid** - UML and sequence diagrams

<!-- Links of the document -->
[boardgame-publisher-link]: https://www.craniocreations.it/en/product/mesos
[finalUML-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/blob/master/deliverables/final/uml/UML_Mesos.png
[protocolDoc-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/deliverables/final/uml
[deliverablesJars-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/deliverables/final/jars
[mesos_db_dump-link]:https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/deliverables/final/dump/mesos_db_dump.sql
[main-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/src/main/java/it/polimi/ingsw
[tests-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/src/test/java/it/polimi/ingsw

[networking-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/src/main/java/it/polimi/ingsw/Networking
[cli-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/src/main/java/it/polimi/ingsw/View/TUIView
[gui-link]: https://github.com/TommasoPalla/ing-sw-2026-Martino-Mazzucato-Mezzaroma-Palla/tree/master/src/main/java/it/polimi/ingsw/View/GUIView
