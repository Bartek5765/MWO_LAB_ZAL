# MWO_LAB_ZAL
Program do generowania raportów aktywności pracowników. 

**Funkcojalności programów zawierają przygotowanie raportów:**
**Report:Employee Report **(raport po pracownikach) - podaje:
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
