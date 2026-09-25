# Scraper-TEST

Desktop **JavaFX** app for a lead-research workflow: expand a **seed keyword** with Google suggestions, scrape **domains** from search-engine result pages, then query **Snov.io** for email counts and addresses.

Repository: [github.com/jo15765/Scraper-TEST](https://github.com/jo15765/Scraper-TEST)

![Java](https://img.shields.io/badge/Java-8+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-11-00758F)
![Maven](https://img.shields.io/badge/build-Maven-C71A36)

---

## What this project does

The app is a **multi-screen wizard** (FXML + Controllers). Window title on launch: **Get Keywords**.

| Step | Screen | Class / FXML | What it does |
|------|--------|--------------|--------------|
| 1 | **Get Keywords** | `KeywordGrabber` / `KeywordGrabber.fxml` | Fetches related keywords from Google Suggest (`suggestqueries.google.com`) |
| 2 | **Search domains** | `Controller` / `sample.fxml` | Runs a search term against Google, Yahoo, AOL, and Ask; extracts **domain names** from result links (Jsoup) |
| 3 | **Snov.io lookup** | `Snovio` / `Snovio.fxml` | For selected domains, calls Snov.io API for email counts and/or email lists |
| 4 | **Results** | `SnovioDisplay` / `SnovioDisplay.fxml` | Shows aggregated Snov.io output |

Navigation is handled in `Main.java` via `ScreenController`.

---

## Requirements

### Software

- **JDK 8+** (project targets Java 8 in `pom.xml`)
- **JavaFX 11+** (declared in Maven; required at runtime)
- **Apache Maven 3.6+** (recommended)
- **NetBeans** (optional — repo includes `Scraper.iml` and `nb-configuration.xml`)

### Network and accounts

- Outbound HTTPS to:
  - Google Suggest and search pages
  - Yahoo, AOL, Ask search pages
  - **Snov.io API** (`api.snov.io`)
- A valid **Snov.io API** client ID and secret (see configuration below)

### Maven dependencies (from `pom.xml`)

| Library | Use |
|---------|-----|
| [Jsoup](https://jsoup.org/) 1.13.1 | HTML parsing for search results |
| [ControlsFX](https://github.com/controlsfx/controlsfx) 8.40.11 | `CheckListView` UI |
| Gson, org.json | Snov.io JSON responses |
| Apache Commons Text | String utilities in API client |
| OpenJFX 11 (BOM) | JavaFX UI |

JAR copies also live under `lib/` for IDE builds.

---

## Project structure

```text
Scraper-TEST/
├── pom.xml
├── Scraper.iml
├── nb-configuration.xml
├── lib/                          # Local JARs (Jsoup, ControlsFX)
├── src/main/java/sample/
│   ├── Main.java                 # JavaFX Application entry + screen routing
│   ├── ScreenController.java     # Switches between loaded panes
│   ├── KeywordGrabber.java       # Step 1 — keywords
│   ├── KWTool.java               # Google Suggest XML fetch/parse
│   ├── Controller.java           # Step 2 — search engines → domains
│   ├── Snovio.java               # Step 3 — Snov.io UI actions
│   ├── Snovio_API.java           # Snov.io HTTP client
│   └── SnovioDisplay.java        # Step 4 — show results
└── src/main/resources/
    ├── KeywordGrabber.fxml
    ├── sample.fxml
    ├── Snovio.fxml
    └── SnovioDisplay.fxml
```

---

## Configuration (before you run)

### 1. Snov.io API credentials

Edit `src/main/java/sample/Snovio_API.java` and set your own values:

```java
private final static String API_USER_ID = "your-client-id";
private final static String API_SECRET = "your-client-secret";
```

Do **not** commit live secrets to a public repository. Rotate any keys that were ever pushed to GitHub.

Get credentials from your [Snov.io](https://snov.io/) account API settings.

### 2. JavaFX runtime

If `mvn` fails with missing JavaFX modules, install a JDK that includes JavaFX or add the JavaFX Maven plugin / dependencies for your OS. OpenJFX 11 is listed in `pom.xml` as a BOM import.

---

## Build step-by-step

### Option A — Maven (command line)

1. Clone the repository:

   ```bash
   git clone https://github.com/jo15765/Scraper-TEST.git
   cd Scraper-TEST
   ```

2. Compile:

   ```bash
   mvn clean compile
   ```

3. Run the main class (JavaFX classpath may require extra flags on your JDK):

   ```bash
   mvn exec:java -Dexec.mainClass="sample.Main"
   ```

   If exec plugin is not configured, run from your IDE or add `javafx-maven-plugin` to `pom.xml`.

### Option B — NetBeans / IntelliJ

1. Open the project (`Scraper.iml` or import Maven project).
2. Ensure `lib/*.jar` and Maven dependencies resolve.
3. Set main class to **`sample.Main`**.
4. Run.

---

## Usage step-by-step (application flow)

### Step 1 — Get Keywords

1. Start the app (`sample.Main`).
2. On **Get Keywords**, enter a **single** seed keyword (one line only).
3. Click **Get Keywords**.
4. The app calls Google Suggest (`KWTool.fetchKeywords`) and expands related terms.
5. Use the checklist to review suggestions; duplicates are removed automatically.
6. **Select exactly one** keyword in the list, then proceed to the next screen (Home / continue action in UI).

If you select more than one keyword, the app shows a warning: *Multi-Select Not Allowed.*

### Step 2 — Get Domains From Search Results

1. The chosen keyword is loaded into the search field.
2. Enable one or more providers: **Google**, **Yahoo**, **AOL**, **Ask** (all enabled by default when arriving from Step 1).
3. Click the action to **query search engines** (`QuerySearchEngines`).
4. The app:
   - Builds search URLs with your keyword (spaces → `+`)
   - Fetches result pages with Jsoup (user-agent + delays between requests)
   - Extracts domains via regex in `Controller.getDomainName`
   - Filters out common aggregator domains (Google, Yahoo, Yelp, etc.)
5. Domains appear in a **CheckListView**.
6. Optional actions:
   - **Remove duplicates**
   - **Check all** / **Uncheck all**
   - **Save CSV** — writes **checked** domains to `~/Desktop/{searchTerm}.csv`
7. Select domains to enrich, then continue to **Snov.io** (`LoadNewScene`).

### Step 3 — Get Data From Domain (Snov.io)

1. Selected domains from Step 2 are listed.
2. Choose options:
   - **Email count** per domain
   - **Email addresses** (domain search, personal type, limit 10)
3. Run the Snov.io action (`HitSnovioAPI`).
4. The app calls `Snovio_API` (OAuth client credentials, then REST endpoints).

### Step 4 — View results

1. **Snovio Display** shows email counts and/or address lines returned from the API.
2. Use **Back** / **Home** navigation to return to earlier screens as needed.

---

## How scraping works (technical notes)

- **Keywords:** HTTP GET to  
  `http://suggestqueries.google.com/complete/search?client=toolbar&q={keyword}&hl=en`  
  XML parsed for `<suggestion data="..."/>`.
- **Search engines:** One page of results per enabled provider (configurable loop in `getDataFromGoogle`); ~4 second delay between requests.
- **Domains:** Parsed from `href` attributes; not full browser automation.

Search engines may block automated requests, change HTML layout, or rate-limit IPs. Failures often show as empty domain lists or IO errors in the console.

---

## Troubleshooting

| Symptom | Things to check |
|---------|------------------|
| Empty domain list | Provider HTML changed; IP blocked; try one engine at a time |
| Snov.io errors | API keys, token endpoint, account limits |
| JavaFX won't start | JavaFX modules on classpath; use JDK/FX bundle or OpenJFX |
| CSV not saved | At least one domain checked; write permission on Desktop |
| Keyword step does nothing | Empty input; multiple lines in keyword field |

---

## Legal and ethical use

- Respect **terms of service** for Google, Yahoo, AOL, Ask, and Snov.io.
- Use only for data you are **allowed** to collect and store (CAN-SPAM, GDPR, etc.).
- This repository is a **TEST** project; rate limits and markup breakage are expected in production without maintenance.

---

## Maintenance suggestions

- Move Snov.io credentials to environment variables or a local config file excluded from git.
- Add `.gitignore` for `target/`, `out/`, and IDE files.
- Do not rely on committed `target/` build output — rebuild with Maven locally.
- Update Jsoup selectors when search providers change their HTML.

---

## License

Add a `LICENSE` file when you choose a license (for example MIT). Until then, default copyright applies.

---

## Author

**jo15765** — [Scraper-TEST on GitHub](https://github.com/jo15765/Scraper-TEST)
