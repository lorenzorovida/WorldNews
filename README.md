# World-News

Questa applicazione sarà un client per poter visualizzare dati recuperati da [News API](https://newsapi.org)

## Esercitazione 3 (24 ottobre)

In questa esercitazione abbiamo creato il progetto World News da zero. In particolare, è stato importato un foglio di stile da [questo sito](https://material-foundation.github.io/material-theme-builder/) e successivamente è stata creata la pagina `PickACountryActivity`

È stata modificata l'Activity di partenza da `MainActivity` a `PickACountryActivity` modificando il file `manifests/AndroidManifest.xml`

`PickACountryActivity` utilizza un ConstraintLayout per mostrare una `TextView` e un `LinearLayout`, all'interno del quale sono presenti due `CountryCard`.

La `CountryCard` è definita nel file `res/layout/country_card.xml` ed è stata definita a partire dalle linee guida presenti [in questo link](https://github.com/material-components/material-components-android/blob/master/docs/components/Card.md)

## Esercitazione 4 (31 ottobre)

In questa esercitazione abbiamo concluso la pagina `PickACountryActivity` e creato le pagine `PickCategoriesActivity` e `LoginActivity`

Le tre pagine serviranno per fare il login, e successivamente scegliere una nazione di riferimento e una serie di categorie.


## Esercitazione 5 (7 novembre)

In questa esercitazione abbiamo introdotto gli `Intent` come oggetti che permettono la comunicazione fra elementi dell'applicazione (`Intent` espliciti) e, più in generale, del sistema operativo (`Intent` impliciti). Inoltre abbiamo introdotto il `Navigation Graph` per la gestione dei `Fragment`. 

N.B. La versione del codice non è esattamente quella con cui si è conclusa l'esercitazione, in particolare `PickACountryActivity` e `PickCategoriesActivity` sono state trasformate in `Fragment`

## Esercitazione 6 (21 novembre)

## Esercitazione 7 (28 novembre)

In questa esercitazione abbiamo introdotto la `RecyclerView` come alternativa efficient alla `ListView`. Successivamente, abbiamo visto come poter fare il parsing di un oggetto JSON generato da una chiamata API al sito NewsAPI. Abbiamo mostrato i risultati del JSON all'interno della RecyclerView utilizzando il relativo adapter e delle card generate a partire da `card_news.xml`. Infine, abbiamo associato un database alla classe `Article` e abbiamo utilizzato il DB per aggiungere degli articoli ai preferiti. 
N.B. il codice ha delle piccole modifiche rispetto alla versione scritta ad esercitazione, vedremo queste modifiche settimana prossima

# Esercitazione 8 (5 dicembre)
