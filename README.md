# 📱 Android App

Android aplikace postavená na architektuře **MVI**, s využitím **single-activity přístupu** a **Conductor** pro řízení obrazovek. Aplikace podporuje **offline režim** a staví na reaktivním a modulárním návrhu.

## 🔧 Tech Stack

- **Single-Activity Architecture** – veškerá navigace a UI řízena z jediné aktivity
- **MVI Architecture** – unidirectional data flow pro predikovatelné chování
- **[Conductor](https://github.com/bluelinelabs/Conductor)** – lightweight framework pro správu navigace a lifecycle
- **[RxKotlin](https://github.com/ReactiveX/RxKotlin)** – reaktivní programování postavené na RxJava
- **[RxRelay](https://github.com/JakeWharton/RxRelay)** – řízení stavů a událostí
- **[Retrofit](https://square.github.io/retrofit/)** – HTTP klient pro REST API
- **[Moshi](https://github.com/square/moshi)** – JSON (de)serializace
- **[SQLDelight](https://cashapp.github.io/sqldelight/)** – typově bezpečná práce s databází přes SQL

👉 Kompletní seznam knihoven najdeš v souboru [`libs.versions.toml`](./gradle/libs.versions.toml)

## ⚙️ Build Info

- **Target SDK:** 36
- **Java version:** 17
- **Kotlin version:** 1.8.22
  > Vybraná z důvodu rychlejších buildů oproti novějším verzím

## ✈️ Offline podpora

Aplikace je plně funkční i bez připojení k internetu. Všechna důležitá data jsou ukládána lokálně pomocí SQLDelight

## 🔐  API klíče a Schování API klíčů

Pokud bychom chtěli částečně schovat API a API klíč, mohli bychom je přesunout do souboru `local.properties`.

Aby se ale tento sample projekt testoval jednodušeji, přidal jsem tyto klíče rovnou do souboru [`net/Constants.kt`](core/src/main/java/com/zj/core/csastest/net/Constants.kt), kde jsou viditelně dostupné.

Původně by se hodnoty mohly nacházet například takto v souboru `local.properties`:

```
API_KEY=xxxxxxxxxxx
CSAS_SANDBOX_API=https://webapi.developers.erstegroup.com/api/xxxxxxxxxxx
CSAS_PROD_API=https://www.csas.cz/xxxxxxxxxxx
```

## 🧹 Normalizace dat & formátování

- Některé názvy obsahovaly nechtěné mezery na začátku – byly normalizovány pomocí `.trim()`.
  > Pravděpodobně to byl záměr :) 
- Pro zobrazení částek byl použit **DecimalFormatter**:
    - Hodnoty jako `1234.5` se zobrazují jako `1 234,5 Kč`
    - Hodnoty `0,00` se zobrazují jednoduše jako `0` (bez desetinné čárky)

---

## Testování databáze v paměti (in-memory)

Pro testování SQLDelight databáze používám in-memory přístup. V tomto projektu je ukázka dostupná v zde:

[`core/src/test/java/com/zj/core/DatabaseTest`](core/src/test/java/com/zj/core/DatabaseTest.kt)

## Paging

> ⚠️ Paging mi nefungoval. Zkoušel jsem několik kombinací, ale nepodařilo se mi ho rozběhat. Možná jde o nechtěnou "feature" sandboxu?