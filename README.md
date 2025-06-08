# MWO_LAB_ZAL

Program do tworzenia raportów. Umożliwia generowanie raportów na podstawie danych o pracownikach i projektach.


## Dostępne opcje raportów:

1. **Report: Employee Report (raport po pracownikach)**  
   Funkcjonalności raportu:
   - lista pracowników z sumą godzin wypracowaną w projekcie.

2. **Report: Projects Report (raport po projektach)**  
   Funkcjonalności raportu:
   - lista projektów z sumą wypracowanych godzin;
   - lista pracowników uczestniczących w projekcie;
   - liczba godzin przepracowanych przez danego pracownika w projekcie;
   - procentowy udział godzinowy pracownika odniesiony do godzin z całego projektu.

3. **Report: TOP (raport po pracownikach)**  
   Funkcjonalności raportu:
   - lista "najlepszych" pracowników z sumą godzin wypracowaną w projektach.
   - możliwośc podania ilości pracowników, domyślnie przyjmowana wartość "10"



## Instrukcja generowania raportu

Aby wygenerować raport, użyj poniższych opcji w terminalu:


rodzaj raportu: -r <employees|projects|top> 
ścieżka katalogu: -p <ścieżka_katalogu> 
data od: -df <RRRR-MM-DD> 
data do: -dt <RRRR-MM-DD>"


dla raportu top, możliwość określenia ilości pracowników poprzez podanie wartości liczbowej, 
-top <liczba pracowników>
przy braku wartości domyślnie przyjmowane jest "10"



### Przykład:

Aby wygenerować raport dla pracowników, z danych znajdujących się w katalogu `./sample-data` na okres od 1 stycznia 2020 roku do 1 stycznia 2025 roku, użyj poniższego polecenia:

```
java -jar report.jar -r employees -p ../sample-data -df 2020-01-01 -dt 2025-01-01
```

Aby wygenerować raport dla najlepszych 5 pracowników, z danych znajdujących się w katalogu `../sample-data` na okres od 1 stycznia 2020 roku do 1 stycznia 2025 roku, użyj poniższego polecenia:

```
java -jar report.jar -r top -p ../sample-data -df 2020-01-01 -dt 2025-01-01 -top 5
```

Domyślnie w typie raportu top wyszukuje 10 najlepszych pracowników
```
java -jar report.jar -r top -p ../sample-data -df 2020-01-01 -dt 2025-01-01 
```


Raport 2 - projekty plus pracownicy i ilość czasu poświęconego na projekt (tylko typ raportu i ścieżka jest wymagana)
```
java -jar report.jar -r project -p ../sample-data
```

Raport bez żadnych danych
```
java -jar report.jar -r top -p ../sample-data/Projekt1/2022 -df 2028-09-09 
```

### Częste błedy:

```
-r projects -p ../sample-data -df 2000-01-01 -dt 2022/01/01
```
!Daty muszą być w formacie RRRR-MM-DD!

```
-r projects -p ../sample-data -df 2023-01-01 -dt 2022-01-01
```

!Data początkowa nie może być po dacie końcowej!



## Dodatkowe funkcjonalności
- Raporty są przygotowywane w określonych przez użytkownika datach "od" "do"
- W przypadku błędnego uzupełnienia pliku xls na poczatku raportu pojawia się szczegółowy opis błędów i ich lokalizacja. Zakres objętych błędów: "nieprawidłowy czas - w przypadku podania 0", "nieprawidłowy format podania godzin", "puste pola".
___________________________




