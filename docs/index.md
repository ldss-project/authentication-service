---
title: Homepage
layout: default
nav_order: 1
---

# Authentication Service
{: .no_toc}

## Contenuti
{: .no_toc}

- TOC
  {:toc}

---

## Descrizione

L'**Authentication Service** è un servizio che gestisce la registrazione e l'autenticazione
degli utenti all'interno di un'applicazione.

Il servizio espone un contratto di tipo _REST_, disponibile al seguente
[link](/swagger-apis/authentication-service/latest/rest).

## Implementazione

L'implementazione dell' **Authentication Service** è descritta dal seguente diagramma delle classi
UML.

![Authentication Service Class Diagram](/authentication-service/resources/images/authentication-service.png)

Come si può vedere dal diagramma, l'implementazione del servizio dipende dal framework
[HexArc](https://github.com/ldss-project/hexarc).

In particolare, il servizio definisce due componenti principali:
- `AuthenticationPort`: definisce le funzionalità del servizio.
- `AuthenticationHttpAdapter`: espone alcune delle funzionalità dell'`AuthenticationPort` attraverso un
  contratto di tipo _REST_.

Le funzionalità definite dall'`AuthenticationPort` sono le seguenti:
- `registerUser`: registra un nuovo utente nel servizio;
- `loginUser`: autentica un utente nel servizio, assegnandogli un nuovo token;
- `revokeToken`: revoca il token di un utente e quindi la sua autenticazione nel servizio;
- `getUserInformation`: restituisce le informazioni relative a un utente del servizio;
- `updatePassword`: aggiorna la password di un utente del servizio;
- `validateToken`: rimuove tutte le statistiche di un utente del servizio.

Tali funzionalità sono definite nei termini dei concetti del dominio del servizio.
In particolare, i modelli relativi a tali concetti sono i seguenti:
- `User`: modella un utente del servizio;
- `Token`: modella il token assegnato a un utente autenticato del servizio;
- `UserSession`: modella la sessione di un utente autenticato del servizio.

L'implementazione dell'`AuthenticationPort` è modellata dall'`AuthenticationModel`.
L'`AuthenticationModel` gestisce la persistenza dei dati nel servizio attraverso una
`PersistentCollection` e per comunicare con la `PersistentCollection` utilizza il
linguaggio delle query `MongoDBQueryLanguage`. Quindi, implementa tutte le funzionalità
dell'`AuthenticationPort` attraverso delle opportune query.

L'`AuthenticationHttpAdapter` e l'`AuthenticationModel` possono generare delle eccezioni,
modellate dalla classe `AuthenticationServiceException`. In particolare, l'utente che
utilizza il servizio potrebbe essere notificato delle seguenti `AuthenticationServiceException`s:
- `IncorrectPasswordException`: indica all'utente che la password da lui specificata per autenticarsi
  non è corretta;
- `MalformedInputException`: indica all'utente che l'input specificato per una certa
  funzionalità da lui richiesta non è corretto;
- `TokenExpiredException`: indica all'utente che la funzionalità da lui richiesta necessita
  di essere autenticati, ma il suo token è scaduto;
- `UsernameAlreadyTakenException`: indica all'utente che il nome utente da lui specificato per
  registrarsi all'applicazione è già stato riservato da un altro utente;
- `UserNotAuthorizedException`: indica all'utente che la funzionalità da lui richiesta necessita
  di essere autenticati, ma il suo token non esiste o non gli fornisce i permessi necessari;
- `UserNotFoundException`: indica all'utente che il nome utente da lui specificato non è
  associato a nessun dato nel sistema.

## Verifica

Per verificare il sistema, è stata creata una suite di test manuali su
[Postman](https://www.postman.com/), in modo da accertarsi che tutte le funzionalità
esposte dal contratto _REST_ del servizio producessero i risultati attesi.

In futuro, si dovrà creare degli _unit test_ equivalenti, ma automatici. Per fare ciò,
sarà necessario approfondire come creare un database [MongoDB](https://www.mongodb.com)
di tipo _in-memory_ in [Scala](https://scala-lang.org/).

## Esecuzione

Per eseguire il sistema è disponibile un jar al seguente
[link](https://github.com/ldss-project/authentication-service/releases).

Per eseguire il jar è sufficiente utilizzare il seguente comando:
```shell
java -jar authentication-service-<version>.jar \
--mongodb-connection MONGODB_CONNECTION_STRING
```

In particolare, il jar permette di specificare i seguenti argomenti a linea di comando:
- `--mongodb-connection MONGODB_CONNECTION_STRING`: obbligatorio. Permette di specificare
  la stringa (`MONGODB_CONNECTION_STRING`) per connettersi all'istanza di
  [MongoDB](https://www.mongodb.com) che sarà utilizzata dal servizio per memorizzare i propri
  dati.
- `--mongodb-database DATABASE_NAME`: opzionale. Permette di indicare il nome del database (`DATABASE_NAME`)
  all'interno dell'istanza di [MongoDB](https://www.mongodb.com) specificata in cui il servizio memorizzerà i
  propri dati. Default: `authentication`.
- `--mongodb-collection COLLECTION_NAME`: opzionale. Permette di indicare il nome della collezione
  (`COLLECTION_NAME`) all'interno del database [MongoDB](https://www.mongodb.com) specificato in cui il
  servizio memorizzerà i propri dati. Default: `users`.
- `--http-host HOST`: opzionale. Permette di indicare il nome dell'host (`HOST`) su cui sarà esposto il
  contratto _REST_ del servizio. Default: `localhost`.
- `--http-port PORT`: opzionale. Permette di indicare la porta dell'host (`PORT`) su cui sarà esposto il
  contratto _REST_ del servizio. Default: `8080`.
- `--allowed-origins ORIGIN_1;ORIGIN_2;...;`: opzionale. Permette di indicare una lista dei siti web che
  saranno autorizzati a comunicare con il servizio. Tale lista consiste in una sequenza di URL separati
  da `;`. Default: _nessun sito web autorizzato_.

In alternativa, un'immagine per eseguire il jar è stata pubblicata anche su [Docker](https://www.docker.com/).
Per eseguire il servizio tramite [Docker](https://www.docker.com/) è sufficiente utilizzare il seguente comando:
```shell
docker run -it jahrim/io.github.jahrim.chess.authentication-service:<version> \
--mongodb-connection MONGODB_CONNECTION_STRING
```