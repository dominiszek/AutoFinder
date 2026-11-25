ANARCHIA AUTOFINDER — źródła moda
Minecraft Java 1.21.11 / Fabric / Java 21

FUNKCJE
- Klawisz 6 otwiera/zamyka listę wykrytych graczy.
- Wykrywa graczy w promieniu 64 bloków, których widoczne wyposażenie zawiera:
  * Elytrę w slocie klatki piersiowej LUB
  * przynajmniej jeden element netheritowej zbroi.
- Do każdego wykrytego gracza wysyła komendę /tpa NICK co około 2 sekundy.
- Gdy gracz przestanie spełniać warunek lub wyjdzie poza zasięg, znika z listy.

WAŻNE OGRANICZENIA
- Klient Minecrafta zwykle nie zna całego ekwipunku innych graczy. Mod sprawdza wyłącznie
  wyposażenie synchronizowane przez serwer (np. zbroję/elytrę założoną na postaci).
- Nie wykrywa netheritowych narzędzi schowanych w ekwipunku.
- Serwer może ograniczać /tpa, ignorować kolejne zaproszenia albo karać za spam.
  Używaj automatyzacji zgodnie z zasadami Anarchia.gg.
- Projekt jest paczką źródłową; nie zawiera gotowego, przetestowanego pliku JAR.

JAK ZBUDOWAĆ JAR
1. Zainstaluj JDK 21 i Gradle.
2. Otwórz terminal w folderze projektu.
3. Uruchom:
   gradle build
4. Gotowy JAR powinien pojawić się w:
   build/libs/anarchia-autofinder-1.0.0.jar
5. Włóż JAR do folderu .minecraft/mods razem z Fabric API zgodnym z 1.21.11.

Jeśli kompilacja zgłosi błąd API, wklej cały komunikat błędu — można wtedy dopasować
źródła do dokładnej wersji Fabric Loader/Fabric API z Twojego launchera.
