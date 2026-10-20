# Android Grade Calculator (Kalkulator Ocen i Średniej)

Aplikacja mobilna na platformę Android służąca do rejestrowania danych studenta, dynamicznego definiowania liczby ocenianych przedmiotów, wprowadzania ocen za pomocą grup przycisków radiowych oraz wyliczania średniej ważonej/arytmetycznej wraz z kwalifikacją zaliczenia semestralnego.

---

## Spis treści
1. [Funkcjonalności](#funkcjonalności)
2. [Architektura i technologie](#architektura-i-technologie)
3. [Struktura modułów](#struktura-modułów)
4. [Przepływ danych i logika biznesowa](#przepływ-danych-i-logika-biznesowa)
5. [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
6. [Instrukcja użytkowania](#instrukcja-użytkowania)
7. [Uwagi techniczne i optymalizacja](#uwagi-techniczne-i-optymalizacja)

---

## Funkcjonalności

### 1. Formularz danych studenta (`MainActivity`)
* **Pola danych osobowych**: Wprowadzanie imienia, nazwiska oraz deklarowanej liczby przedmiotów/ocen.
* **Walidacja w czasie rzeczywistym (`TextWatcher` & `FocusChange`)**:
  * Blokada przejścia przy pustych polach tekstowych z prezentacją dymków błędów (`setError`).
  * Weryfikacja zakresu: liczba ocen musi mieścić się w ściśle określonym przedziale od **5 do 15**.
  * Dynamiczne sterowanie widocznością przycisku przejścia (`btnMarks` staje się dostępny dopiero po spełnieniu wszystkich kryteriów walidacji).
* **Prezentacja wyniku i decyzji**:
  * Formatowane wyświetlanie obliczonej średniej arytmetycznej z dokładnością do dwóch miejsc po przecinku (`Średnia wyników: X.XX`).
  * Warunkowa prezentacja przycisków akcji po powrocie:
    * Średnia $\ge 3.0$: Przycisk zaliczenia (`btnGood`) z komunikatem gratulacyjnym.
    * Średnia $< 3.0$ (i $> 0$): Przycisk braku zaliczenia (`btnNotGood`) z komunikatem o warunku.
* **Przetrwanie cyklu życia i obrotu ekranu**:
  * Pełne zachowanie wprowadzonych wartości tekstowych, wyliczonej średniej oraz widoczności elementów interfejsu w `onSaveInstanceState` i `onRestoreInstanceState`.

### 2. Panel wyboru ocen (`MainActivity2` & `Adapter`)
* **Dynamiczna lista przedmiotów**: Pobieranie nazw przedmiotów z tablicy zasobów `R.array.wszystkieOceny` i ograniczanie ich do zadeklarowanej liczby za pomocą `Arrays.copyOfRange`.
* **Interaktywny wiersz ocen (`RadioGroup`)**:
  * Skala ocen: **2**, **3**, **4**, **5**.
  * Izolacja stanu zaznaczeń – unikanie konfliktów przy recyklingu widoków w `RecyclerView` poprzez czyszczenie i ponowne podpinanie `OnCheckedChangeListener`.
* **Automatyczna weryfikacja kompletności**:
  * Przycisk zatwierdzenia ocen (`btn2`) jest domyślnie ukryty (`View.GONE`).
  * Pojawia się automatycznie dopiero w momencie, gdy **każdy** z wyświetlonych przedmiotów ma przypisaną ocenę.
* **Zachowanie stanu zaznaczeń**:
  * Zapis tablicy wybranych ocen oraz pozycji przewinięcia `RecyclerView` przy obrocie urządzenia.

---

## Architektura i technologie

* **Język programowania**: Java
* **Platforma**: Android SDK (AndroidX, AppCompat, Material Design)
* **Wzorce architektoniczne i komponenty**:
  * **Nowoczesny Activity Result API**: Zastąpienie przestarzałego `startActivityForResult` dedykowanym rejestratorem `registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ...)`.
  * **RecyclerView + Custom Adapter + ViewHolder**: Wydajne renderowanie złożonych wierszy zagnieżdżających grupy przycisków radiowych (`RadioGroup`).
  * **Kolejkowanie stanu widoków**: Zastosowanie `Parcelable` do utrwalania stanu przewinięcia `LayoutManagera`.

---

## Struktura modułów

```text
com.example.nazwaprojektu1/
├── MainActivity.java     # Formularz wejściowy, walidacja pól, prezentacja średniej i komunikatów
├── MainActivity2.java    # Ekran listy przedmiotów, sprawdzanie kompletności ocen, kalkulacja średniej
└── Adapter.java          # Adapter RecyclerView zarządzający stanem RadioGroup w każdym wierszu
```

---

## Przepływ danych i logika biznesowa

1. Użytkownik podaje: Imię, Nazwisko, Liczbę ocen $N \in [5, 15]$.
2. Kliknięcie `btnMarks` przesyła dane za pomocą `Intent` do `MainActivity2`.
3. `MainActivity2` pobiera pierwsze $N$ pozycji z zasobów `R.array.wszystkieOceny` i buduje listę.
4. Po zaznaczeniu kompletu $N$ ocen aktywuje się przycisk `btn2`.
5. Obliczenie średniej:
   $$\bar{X} = \frac{\sum_{i=1}^{N} \text{ocena}_i}{N}$$
6. Zwrócenie danych do `MainActivity` za pomocą intencji wynikowej (`setResult(RESULT_OK)`).

---

## Instalacja i uruchomienie

1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/TomaszMarek/Android-GradeCalculator
   ```
2. Otwórz projekt w środowisku **Android Studio**.
3. Upewnij się, że plik zasobów `res/values/strings.xml` zawiera tablicę `wszystkieOceny` z co najmniej 15 pozycjami.
4. Zsynchronizuj pliki Gradle (`Sync Project with Gradle Files`).
5. Uruchom aplikację na emulatorze lub urządzeniu fizycznym (`Shift + F10`).

---

## Instrukcja użytkowania

1. Wypełnij pola **Imię** oraz **Nazwisko**.
2. W polu **Liczba ocen** wprowadź cyfrę z zakresu 5–15.
3. Kliknij przycisk przejścia do ocen.
4. W widoku listy zaznacz ocenę (2, 3, 4 lub 5) dla każdego przedmiotu.
5. Po zaznaczeniu wszystkich pozycji kliknij przycisk zatwierdzenia na dole ekranu.
6. Odczytaj uzyskaną średnią oraz wynik zaliczenia na ekranie głównym.
