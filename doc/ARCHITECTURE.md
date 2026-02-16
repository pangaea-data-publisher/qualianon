# QualiAnon Architecture

## Scope and intent
This document summarizes the architecture of QualiAnon, a JavaFX desktop application for anonymizing text data. It describes the main components, data model, persistence formats, and key workflows, based on the current codebase.

**Reference convention**: Document references use line numbers (not paragraphs) for consistent, stable citation in exports and search results.

## System context
- **User**: Interacts via a JavaFX desktop UI.
- **Local file system**: All projects, documents, and exports are stored locally.
- **External libraries**: JavaFX UI, Apache POI for DOCX/XLSX, Jackson XML for XML.

## Entry points
- `org.qualiservice.qualianon.Launcher`: JVM entry point for the fat JAR.
- `org.qualiservice.qualianon.Main`: JavaFX `Application`, opens the welcome window.
- JavaFX views are loaded from `src/main/resources/*.fxml`.

## High-level component map
**UI layer (JavaFX)**
- Package: `org.qualiservice.qualianon.gui`.
- Controllers under `gui.components` orchestrate UI flows and bind to model classes.
- FXML views in `src/main/resources` (e.g., `main.fxml`, `welcome.fxml`).

**Domain/model layer**
- Package: `org.qualiservice.qualianon.model`.
- Captures domain concepts like projects, documents, categories, replacements, exports, and commands.

**Persistence and IO**
- Package: `org.qualiservice.qualianon.files`.
- Reads/writes DOCX, XLSX, XML; handles backups, file utilities, and XML serialization.

**Import/conversion**
- Packages: `org.qualiservice.qualianon.listimport`, `org.qualiservice.qualianon.conversion`.
- Imports external coding lists and converts coding schemes.

**Audit/logging**
- Package: `org.qualiservice.qualianon.audit`.
- Composite logger integrates file log, UI log, and alert log.

**Utility**
- Package: `org.qualiservice.qualianon.utility`.
- Properties, preferences, string utilities, and listener support.

## Core domain model
**Project**
- `model.project.Project` is the central aggregate.
- Holds `ReplacementCollection`, list of `AnonymizedFile`, `Categories`, and `ExportList`.
- Manages project directory structure and all load/save operations.

**Documents**
- `model.project.AnonymizedFile` represents a DOCX document plus marker storage.
- Uses `model.text.IndexedText` and marker storage for in-document annotations.

**Replacements and categories**
- `model.project.ReplacementCollection` manages `Replacement` entities.
- `model.categories.*` handles category schemes and coding lists.

**Exports**
- `model.exports.Export` and `ExportList` manage export configurations.
- Exported DOCX uses anonymization profiles.

**Commands**
- `model.commands.*` encapsulates user actions for undo/redo.
- `CommandList` and `CommandRunner` drive command execution in the UI.

## Domain/model layer detail
This section expands the `org.qualiservice.qualianon.model` packages and how they work together.

### Concept diagrams (PlantUML)
- Domain overview: `doc/domain-overview.puml`
- Text + markers flow: `doc/text-markers-flow.puml`
- Export flow: `doc/export-flow.puml`

### `model.project` (project aggregate and persistence boundaries)
- **`Project`** is the root aggregate. It owns category data, replacement data, export configurations, and the list of `AnonymizedFile` documents.
- **`ProjectConfigurator`** defines and validates the on-disk project structure (anonymized docs, identification, categories, exports, trash).
- **`AnonymizedFile`** wraps a DOCX plus marker storage; it loads, saves, exports, deletes/restores via trash, and tracks modification state.
- **`ReplacementCollection`** is an observable list of `Replacement` entities with a `BooleanProperty` for modified state.
- **`SearchParams`** and **`SearchResult`** carry search criteria/results across documents.

### `model.text` (document text and markers)
- **`IndexedText`** holds the document text, line index, and in-memory markers (`MarkerRuntime`). It supports:
  - Anonymized render (`toAnonymized`) and export render (`toExport`).
  - Search over text with case/whole-word/unmarked filters.
  - Conversion to/from persisted marker storage (`MarkerStorage`).
- **`MarkerRuntime`** represents a live marker with position range and replacement metadata.
- **`MarkerStorage`** is the serializable form persisted to XLSX per document.
- **`LineIndex`, `Coords`, `LineBreaker`** support line mapping, navigation, and formatting.

### `model.categories` (classification schemes and coding lists)
- **`Categories`** is the root container for `CategoryScheme` entities and coding lists.
- **`CategoryScheme`** defines labels, list usage, and selection behavior (paired with `SelectionStyle`).
- **`CodingList`** loads external controlled lists and supports lookups.
- **`LabelScheme`** and **`CategoryListScheme`** model labels and list-backed categories.

### `model.anonymization` (export anonymization profiles)
- **`AnonymizationProfile`** defines how replacements are rendered in exports per category.
- **`CategoryProfile`** and **`LabelProfile`** store per-category and per-label rules (e.g., original vs. replacement formatting).

### `model.commands` (undo/redo actions)
- **`Command`** is the base interface; each concrete command encapsulates a user action and its inverse.
- **`CommandList`** maintains a pointer-based history stack and exposes undo/redo enablement details.
- Commands cover document/marker changes, replacement edits, list imports, and export configuration edits.

### `model.exports` (export definitions)
- **`Export`** owns a named export directory and `AnonymizationProfile`, saving the profile to XML and exporting each document.
- **`ExportList`** loads, stores, and deletes export definitions within the project.

### `model.properties` (observable values)
- **`BooleanProperty`** and **`StringProperty`** are lightweight observable values used for modification flags and UI bindings.

## Persistence formats and locations
**Project directory structure**
- Project root (chosen by user)
  - `anonymized/`: source documents (`*.docx`)
  - `identification/`: identifiers and marker storage
    - `replacements.xlsx`
    - `<document>_markers.xlsx`
  - `categories/`: category definitions and imported coding lists
    - `categories.xml`
    - coding list files referenced by category list schemes (e.g., `*.xlsx`)
  - `exports/`: export definitions and exported files
    - `<export name>/profile.xml`
    - `<export name>/<document> [export].docx`
  - `trash/`: deleted files and exports (renamed with UUIDs)
  - `backup/`: timestamped backup folders containing prior versions
  - `auditfile.txt`: audit log for project actions

**File format details**
- `categories.xml`
  - Root element `categories` with repeated `category` entries.
  - Each category stores name, color, labels (label name + optional choices), and optional category list config (list file name + selection style).
- `replacements.xlsx`
  - One sheet per category scheme (sheet name = category name).
  - Header row: `ID` + each label name.
  - Data rows: replacement UUID + label values for that category.
- `<document>_markers.xlsx`
  - Single sheet with header: `Marker ID`, `Replacement ID`, `Original`, `Note`.
  - Rows store UUIDs for marker/replacement plus original text and optional note.
- `<document>.docx`
  - Stored as plain text paragraphs; each line is a paragraph in the DOCX.
  - Line numbers are used for references in search/export (not paragraph indices).
- `exports/<export name>/profile.xml`
  - Serialized anonymization profile with category profiles, label enablement flags, and per-category options (e.g., original/counting enabled).
- `auditfile.txt`
  - Plain-text audit log written by the composite logger.
- `backup/<timestamp>/...`
  - On save, prior versions are moved to a timestamped folder (pattern `yyyy_MM_dd-HH_mm_ss`).

## Key workflows
**1) Start application**
- `Launcher` -> `Main` -> `WelcomeController`.
- User opens or creates a project, which constructs `Project` and calls `open()`.

**2) Open/create project**
- `Project.open()` loads categories, exports, replacements, and documents.
- Directory structure is created if missing.

**3) Import document**
- `Project.importDocument()` uses `AnonymizedFile.importFile()`.
- DOCX text is normalized (`LineBreaker`), indexed, and persisted as DOCX + marker XLSX.

**4) Editing and anonymization**
- UI controllers manipulate document text and markers.
- Changes are tracked via observable properties and update listeners.

**5) Save all**
- `Project.saveAllDocuments()` saves categories XML, replacements XLSX, and modified DOCX/XLSX.

**6) Export**
- `AnonymizedFile.export()` writes an anonymized DOCX according to the selected export profile.
- `Project.exportProject()` creates a ZIP with anonymized and identification folders.

## UI structure (high level)
- `MainController` manages the main window and tabs.
- Sub-components for categories, replacements, documents, exports, and search live under `gui.components.*`.
- JavaFX FXML defines the layout and is bound to controllers.

## External dependencies (from pom.xml)
- JavaFX (controls, fxml, graphics).
- Apache POI (DOCX/XLSX read/write).
- Jackson XML.
- ControlsFX and RichTextFX for UI components.

## Build and packaging
- Maven build with Java 17 (`maven-compiler-plugin`).
- `javafx-maven-plugin` builds a JavaFX runtime image.
- `maven-shade-plugin` builds a fat JAR with `Launcher` as the main class.

## Extension points
- Add new import formats via `listimport` subpackages.
- Add new conversions via `conversion` classes.
- Add new commands for UI actions under `model.commands`.

## Observability and error handling
- Logging goes through `audit.CompositeLogger` to file, UI, and alerts.
- UI uses `StandardDialogs` for confirmations and progress dialogs.

## Notable constraints
- The application is single-user and local-file-system based.
- Most operations assume file system access and DOCX/XLSX support via Apache POI.
