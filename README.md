# VideoGameRentalApp

## Introducció

Aquest projecte consisteix en una aplicació d'escriptori per gestionar el lloguer de videojocs. L'he desenvolupat com a pràctica del cicle formatiu de grau superior de Desenvolupament d'Aplicacions Multiplataforma (DAM), al CIFP Pau Casesnoves.

L'objectiu principal és practicar la programació amb Java i aprendre a crear una interfície gràfica utilitzant Java Swing.

## Què pot fer l'aplicació?

L'aplicació té tres apartats principals:

- **Lloguers:** permet registrar nous lloguers, consultar els lloguers existents i fer les devolucions.
- **Usuaris:** permet afegir i eliminar usuaris.
- **Videojocs:** permet afegir i eliminar videojocs i consultar l'estoc disponible de cada plataforma.

Quan es registra un lloguer, l'estoc del videojoc es redueix automàticament. Quan es fa una devolució, l'estoc es torna a incrementar.

També hi ha un control dels lloguers vençuts, que es mostren en vermell, mentre que els lloguers retornats es mostren en verd.

## Tecnologies utilitzades

Per desenvolupar el projecte he utilitzat:

- **Java:** llenguatge principal de programació.
- **Java Swing:** per crear les finestres, pestanyes, taules, botons i formularis.
- **NetBeans:** entorn de desenvolupament utilitzat.
- **Git i GitHub:** per controlar les versions i guardar el codi del projecte.

## Organització del codi

He intentat separar el codi en diferents paquets per tenir el projecte més ordenat:

- `app`: conté el punt d'entrada de l'aplicació.
- `model`: conté les classes principals, com ara User, Game, Rental i Platform.
- `controller`: gestiona les operacions i les regles del programa.
- `view`: conté la interfície gràfica.
- `resources`: conté els textos de la interfície.

També he utilitzat un fitxer `Bundle_ca.properties` amb ResourceBundle. Això permet tenir els textos de la interfície separats del codi Java i facilita que en el futur es puguin afegir altres idiomes.

A més, he creat una petita biblioteca pròpia anomenada `ValidationAPI`, que s'utilitza per validar els noms dels usuaris i comprovar que comencin amb una lletra majúscula.

## Plataformes disponibles

Els videojocs poden tenir estoc independent per a les següents plataformes:

- PlayStation 5
- Nintendo Switch 2
- Xbox
- PC

## Dades inicials de prova

Quan s'inicia l'aplicació es carreguen algunes dades per poder provar les funcionalitats sense haver d'introduir-ho tot manualment.

Hi ha dos usuaris inicials (Marc i Laura), dos videojocs (Spider-Man 2 i Forza Horizon 5) i dos lloguers actius.

També es carrega estoc inicial per a cada plataforma i es descompta automàticament la quantitat corresponent als lloguers existents.

## Execució del projecte

Per executar l'aplicació:

1. Obrir el projecte amb NetBeans.
2. Comprovar que la biblioteca `ValidationAPI` està afegida com a dependència.
3. Executar el projecte `VideoGameRentalApp` mitjançant F6.

## Objectiu de la pràctica

Amb aquesta pràctica he pogut treballar amb classes i objectes, col·leccions, enumeracions, dates, validacions, separació de responsabilitats i creació d'interfícies gràfiques amb Java Swing.

## Autor

*Thierno Mamadou Bah Diallo*