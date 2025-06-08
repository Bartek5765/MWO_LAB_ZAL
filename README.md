# MWO_LAB_ZAL


Program do tworzenia raportów. Umożliwia generowanie raportów na podstawie danych o pracownikach i projektach.

## Dostępne opcje raportów:

1. **Report:Employee Report (raport po pracownikach)**
      - listę pracowników z sumą godzin wypracowaną w projekcie.
        
2. **Report:Projects Report (raport po projektach**
   - listę projektów z sumą wypracowanych godzin;
   - listę pracowników uczestniczących w projekcie;
   - liczbę godzin przepracowanych przez danego pracownika w projekcie;
   - procentowy udział godzinowy pracownika odniesiony do godzin z całego projektu.

## Instrukcja generowania raportu

Aby wygenerować raport, użyj poniższych opcji w terminalu:


rodzaj raportu: -r <employees|projects> 
ścieżka katalogu: -p <ścieżka_katalogu> 
data od: -df <RRRR-MM-DD> 
data do: -dt <RRRR-MM-DD>"



### Przykład:

Aby wygenerować raport dla pracowników, z danych znajdujących się w katalogu `./sample-data` na okres od 1 stycznia 2020 roku do 1 stycznia 2025 roku, użyj poniższego polecenia:


-r employees -p ./sample-data -df 2020-01-01 -dt 2025-01-01



## Wymagania


- Odpowiednia struktura katalogów i plików wejściowych
___________________________



Program do generowania raportów aktywności pracowników. 

Funkcojalności programów zawierają przygotowanie raportów:
**Report:Employee Report (raport po pracownikach) - podaje:**
   - listę pracowników z sumą godzin wypracowaną w projekcie.

**Report:Projects Report** (raport po projektach) - podaje:
   - listę projektów z sumą wypracowanych godzin;
   - listę pracowników uczestniczących w projekcie;
   - liczbę godzin przepracowanych przez danego pracownika w projekcie;
   - procentowy udział godzinowy pracownika odniesiony do godzin z całego projektu.

Raporty są przygotowywane w określonych przez użytkownika datach "od" "do"

1. Konsolowa obsługa inputa od klienta -> przykładowy input  java -jar raport.jar path -r [employees, projects] -d [date]
/h (help - przydatne komendy)

2. Drukowanie dwóch typów raportów w konsoli
   - RAPORT Z PRACOWNIKAMI
     - imie nazwisko --- liczba godzin
   - RAPORT Z PROJEKTAMI
       - nazwa projektu --- liczba godzin
       - rozbudowa: pracownik --- liczba godzin
3. Obsługa błędów --> Wystąpił błąd
4. Date from to
---KONIEC ---
Podsumowanie co możemy zrobić jutro
