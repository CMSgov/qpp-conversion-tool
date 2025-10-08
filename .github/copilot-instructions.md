# Copilot Instructions (Java – qpp-conversion-tool)

> Keep it short, specific, and actionable. Use these prompts as-is or tweak slightly.  
> Project facts: Java **17**, Maven **3.9.6+**, Spring Boot REST API (`rest-api`), multipart upload controller (`QrdaControllerV2`), ZIP upload controller (`ZipController`), main app `RestApiApplication`. Docker compose: `docker-compose.test.yaml`.

---

## 1) How to “set the stage” (first message to Copilot)
Paste this at the top of your prompt when working in this repo:

```
You are assisting on a Java 17 + Spring Boot REST API (qpp-conversion-tool).
Build, deps, and tests use Maven. Keep code small, readable, and unit‑testable.
Prefer constructor injection, final fields, early returns, and clear names.
Follow existing patterns in SkeletalQrdaController and v2 controllers.
Add JavaDoc only where not obvious. Include minimal unit tests when changing logic.
```

---

## 2) Quick commands (local/dev)
```bash
# Run API via Docker (recommended for quick tests)
docker compose -f ./docker-compose.test.yaml up --build

# Run locally (Maven)
mvn -q -DskipTests package && java -jar rest-api/target/*.jar

# Convert one file (script)
./convert.sh ./qrda-files/valid-QRDA-III-latest.xml
```

**Endpoints to test**
```bash
# Single file
curl -X POST http://localhost:3000   -H 'Accept: application/json;version=2'   -F file=@./qrda-files/valid-QRDA-III-latest.xml

# Zip upload
curl -X POST http://localhost:3000   -H 'Accept: application/zip'   -F file=@./sample-files/some.zip
```

---

## 3) High‑value prompt patterns

### A) New helper class or small refactor
```
Create a small utility in package gov.cms.qpp.conversion.api.util:
- Purpose: wrap common MultipartFile null/empty checks.
- API: boolean isMissing(MultipartFile f)
- Tests: parameterized test with null and empty cases (JUnit 5).
Follow repo style. No external libs.
```

### B) Safer controller logic (minimal changes)
```
Open QrdaControllerV2.respond().
Goal: reduce branching and clarify response creation.
- Use early return on null Metadata.
- Extract "toConvertResponse(ConversionReport, Metadata)" private method.
- Keep existing behavior 1:1, same headers.
Add focused unit test for: warning passthrough and Location header set.
```

### C) Add a small unit test
```
Write a JUnit 5 test for ZipController that:
- Mocks a ZipFile with two entries (happy path).
- Verifies we return two ConvertResponse and Location uuid is set when audit returns metadata.
Use Mockito. Keep test fast and isolated.
```

### D) Guardrails for new public methods
```
When you add any public method:
- Add @Nullable/@NotNull as appropriate.
- Validate inputs early with IllegalArgumentException.
- Keep methods <= 30 LOC; extract helpers if needed.
```

### E) Docstring / README snippets
```
Generate a short JavaDoc for RestApiApplication and a README snippet that
shows how to run with Docker + curl sample. Keep it under 10 lines.
```

---

## 4) Style & constraints
- **Java 17**, Spring Boot idioms, use **constructor injection**.
- Prefer **final** for fields, **early returns**, and **small methods**.
- Keep exceptions specific; wrap checked IO with clear messages.
- Don’t introduce new frameworks without a strong reason.
- Tests: JUnit 5 + Mockito; fast, isolated, deterministic.

---

## 5) What NOT to do
- Don’t change public API behavior without a test proving parity.
- Don’t add static singletons or hidden global state.
- Don’t log sensitive data (file contents, PHI/PII).
- Don’t exceed ~150 lines per class without a reason.

---

## 6) Review checklist (use before commit)
- [ ] New/changed code has targeted unit tests.
- [ ] No behavior changes unless ticket says so.
- [ ] Names clear; methods small; no dead code.
- [ ] Errors are actionable, logs concise (no sensitive data).
- [ ] Compiles: `mvn -q -DskipTests package` passes.
- [ ] Docker path still works: `docker compose -f docker-compose.test.yaml up --build`.

---

## 7) Sample “micro‑prompts” (copy/paste)

**Improve null‑safety and readability**
```
Refactor QrdaControllerV2.respond() to:
- Null‑check file and file.getOriginalFilename() up front.
- Use try-with-resources around inputStream(file).
- Extract to private buildResponse(conversionReport, metadata).
Keep exact response semantics. Add a unit test.
```

**Small logging improvement**
```
In ZipController, log a one‑line warning when a ZipEntry fails to process,
including entry name and top exception message. No stack traces at INFO.
```

**Test first for a bug**
```
Write a failing JUnit 5 test that reproduces: empty ZIP yields 200 with empty list.
Then adjust controller to return an empty list with 200 and a warning log.
```

---

## 8) Optional: New Relic agent update (ops)
```
curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-java.zip
unzip newrelic-java.zip -d newrelic-latest
cp newrelic-latest/newrelic/newrelic.jar tools/docker/docker-artifacts/newrelic/
# Merge newrelic.yml carefully; keep license_key and app_name.
docker compose -f ./docker-compose.test.yaml up --build
```

---

## 9) One‑liner to remember
> “Small diffs, focused tests, zero surprises.”
