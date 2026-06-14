# Pacman Game

Projekt przedstawia prostą grę typu Pacman napisaną w języku Java z użyciem biblioteki Swing. Gracz porusza się po labiryncie, zbiera punkty, unika duchów i może zdobywać dodatkowe ulepszenia.

## Funkcje programu

* generowanie losowego labiryntu,
* sterowanie graczem za pomocą klawiatury,
* przeciwnicy poruszający się w osobnych wątkach,
* system punktów i żyć,
* zbieranie kropek na planszy,
* ulepszenia dla gracza, np. dodatkowe życie, większa prędkość, nieśmiertelność lub mnożnik punktów,
* animacja Pacmana,
* menu główne,
* tabela najlepszych wyników zapisywana do pliku `highscores.ser`.

## Sterowanie

Gracz może poruszać się za pomocą:

* strzałek na klawiaturze,
* klawiszy `W`, `A`, `S`, `D`.

## Struktura projektu

Projekt jest podzielony na pakiety:

* `controller` – obsługa logiki gry i komunikacji między widokiem a modelem,
* `model` – klasy odpowiedzialne za dane i mechanikę gry,
* `view` – klasy odpowiedzialne za wygląd aplikacji,
* `resources` – pliki graficzne używane w menu i oknach gry.

Główna klasa uruchamiająca program to:

```java
PacmanGame
```

## Wymagania

* Java JDK 23 lub nowsza,
* IntelliJ IDEA lub inne środowisko obsługujące projekty Java,
* biblioteka Swing, która jest częścią standardowej Javy.

## Uruchomienie

Najprościej uruchomić projekt w IntelliJ IDEA:

1. Otwórz folder projektu.
2. Upewnij się, że folder `src` jest oznaczony jako Sources Root.
3. Uruchom klasę `PacmanGame`.
4. W menu gry wybierz `New Game` i podaj rozmiar planszy.

## Zasady gry

Celem gry jest zebranie jak największej liczby punktów. Gracz zdobywa punkty za zbieranie kropek na planszy. Należy unikać duchów, ponieważ kontakt z nimi powoduje utratę życia. Gra kończy się, gdy gracz straci wszystkie życia. Po zakończeniu gry można zapisać swój wynik w tabeli najlepszych wyników.

## Autor

https://github.com/vladosvvv
