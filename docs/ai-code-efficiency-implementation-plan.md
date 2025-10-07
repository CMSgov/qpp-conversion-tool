# AI Code Efficacy Spike — Complexity & Runtime

> Repo: **qpp-conversion-tool** (Java 17 / Maven)  
> Goal: Assess how AI code assistance (e.g., GitHub Copilot) affects **cyclomatic complexity, lines of code,** and **runtime** without mixing the two KRs.

---

## Scope & Guardrails

- **Isolation:** Work only on throwaway branches. No merges to main or release branches.
- **Two independent KRs & branches:**
    - `feature/QPPXX-XXXX-what-if-code-complexity`
    - `feature/QPPXX-XXXX-what-if-code-runtime`
- **AI-only changes:** All code edits are AI-driven; the developer only curates prompts and commits.
- **Repeatability:** Baselines and measurements must be reproducible on the same hardware, same dataset, same JVM flags.

---

## Inputs Collected

- **AI Model Used:** Copilot (record version/build if available)
- **Complexity:** SonarCloud (M = E − N + 2P)
- **Runtime:**
    - **Unit Test Runtime:** Maven Surefire/Failsafe wall-clock times
    - **Code Execution Time:** Inline logging (start/end ms)
    - **API Response Time:** New Relic, and local curl timing loop (see Appendix)
- **Lines Of Code (LOC):** Git diff / cloc / Sonar LOC

---

## What We Report

- **% Complexity Change** = (After − Before) / Before × 100
- **% LOC Change** = (After − Before) / Before × 100
- **% Runtime Change** = (After − Before) / Before × 100

> *Positive % for runtime means slower. Negative % means faster.*

---

## Baseline → Experiment → Compare (per KR)

### 1) Complexity KR (branch: `feature/...-code-complexity`)

**Baseline**
- Identify top complex files/methods from SonarCloud (cognitive/cyclomatic, code smells, duplication).
- Record current metrics:
    - Per-file cyclomatic complexity (Sonar)
    - LOC (Sonar/Git)
    - Key findings (nested conditionals, long methods, switch pyramids, feature envy, etc.)

**AI-Driven Changes**
- Apply prompts (see **Prompt Library**) method-by-method.
- Keep unit tests passing.

**Compare**
- Re-run Sonar scan on the branch.
- Capture **Before vs After** per file/method.
- Note any functional/behavioral changes (should be none).

### 2) Runtime KR (branch: `feature/...-code-runtime`)

**Baseline**
- Choose 1–3 target paths (e.g., heavy parsers/validators, hot endpoints, heavy SQLs).
- Record current metrics:
    - Unit test wall-clock time (affected suite/classes)
    - Local execution timings (start/end logs)
    - API p95/p99 from New Relic (if applicable)
    - Local curl loop average (Appendix)

**AI-Driven Changes**
- Apply runtime prompts (see **Prompt Library**).
- Keep correctness via tests.

**Compare**
- Rerun the exact same test set, same dataset, same JVM opts.
- Capture percent change and note any CPU/memory shifts (if you profile).

---

## Success Criteria

- **Complexity KR:** Net reduction in cyclomatic complexity in targeted files with tests still passing; LOC reduced or neutral; readability improved (qualitative).
- **Runtime KR:** Statistically meaningful reduction in wall-clock times (p95, p99 where relevant) with stable resource usage and no correctness regressions.

---

## Measurement Commands & Artifacts (Java/Maven)

### SonarCloud (example)
```bash
# From branch
mvn -T1C -DskipTests -Dspotbugs.skip=true -Dcheckstyle.skip=true clean verify
mvn sonar:sonar \
  -Dsonar.projectKey=qpp-conversion-tool \
  -Dsonar.organization=<your-org> \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=$SONAR_TOKEN
```
- Capture **project dashboard URL** and **per-file complexity** screenshots/exports.

### Unit Tests — wall clock
```bash
# Baseline
mvn -T1C -DfailIfNoTests=false -DskipITs=true -DforkCount=1 -DreuseForks=false test | tee baseline.test.log
# After change
mvn -T1C -DfailIfNoTests=false -DskipITs=true -DforkCount=1 -DreuseForks=false test | tee after.test.log
```
- Parse times from Surefire reports and `baseline.test.log` vs `after.test.log`.

### Local Execution — code timer
Add minimal timing around hot paths (remove after the spike):
```java
long t0 = System.nanoTime();
// invoke hot method
long t1 = System.nanoTime();
System.out.println("hotMethod ns=" + (t1 - t0));
```
Aggregate N runs; compute avg/median.

### API Timing — local loop (Appendix script)
- Run the **same** URL, headers, and N.
- Compare avg in **ms** before vs after.

---

## Data Capture Templates

### A) Complexity Table (per file/method)
| File | Method | Cyclomatic (Before) | Cyclomatic (After) | Δ% | LOC (B) | LOC (A) | Δ% | Notes |
|---|---|---:|---:|---:|---:|---:|---:|---|

### B) Runtime Table (per suite/endpoint)
| Target | Metric | Baseline | After | Δ% | Runs (N) | Env Notes |
|---|---|---:|---:|---:|---:|---|
| Unit Test: `XyzTest` | Wall clock (s) | 45.2 | 36.1 | -20.2% | 3 | Mac M1, JDK 17.0.x |
| API: `/v1/foo` p95 | ms | 420 | 310 | -26.2% | NR window 24h | Same dataset |

> Keep raw logs, diffs, Sonar links, and commit SHAs in `/docs/spike-artifacts/`.

---

## Prompt Library (copy/paste)

### Reduce Cyclomatic Complexity / LOC
- “Refactor this function to reduce cyclomatic complexity and improve readability while keeping behavior identical. Propose small, safe extractions and guard clauses.”
- “Simplify the following code by reducing conditional branches; prefer early-returns and clear naming.”
- “Provide a more efficient algorithm that achieves the same result in fewer lines of code without changing outputs.”
- “Split this method into cohesive private helpers; remove duplication; keep exceptions/messages intact.”
- “Convert nested `if/else` or `switch` pyramids into strategy/polymorphism where appropriate.”

### Reduce Runtime
- “Rewrite this code to minimize response time and reduce overhead; avoid redundant work and allocations.”
- “Optimize this SQL/JPQL to improve execution time via better predicates, selective columns, and indexing hints.”
- “Refactor this handler to use asynchronous/non-blocking IO for improved concurrency (without changing semantics).”
- “Add memoization/caching at the boundary to avoid repeated expensive calls; show invalidation strategy.”
- “Ensure only necessary fields are fetched from the DB; remove N+1 patterns; batch where possible.”
- “Streamline the workflow to avoid repeated parsing/serialization; reuse objects where safe.”

---

## Branching & Workflow

1. Create **complexity** branch → apply complexity prompts → run Sonar → record deltas.
2. Create **runtime** branch → apply performance prompts → run tests/NR/loop → record deltas.
3. Never mix changes across KRs. If a change helps both, land it in both branches separately for measurement.
4. Preserve artifacts under `/docs/spike-artifacts/<date>-<KR>/`.

---

## Risks & Mitigations

- **Benchmark Noise:** Warm JVM, fixed dataset, disable turbo mode, single fork, pin CPU where possible.
- **Overfitting to Microbench:** Validate with end-to-end tests/NR.
- **Readability Regressions:** Use small extractions, better names, keep behavior & tests green.
- **Hidden Functional Changes:** Snapshot outputs; run regression tests; diff payloads & side effects.

---

## Decision Rules

- Accept changes that reduce complexity without harming tests, or reduce runtime by ≥ **10–20%** with neutral complexity.
- Defer changes that add complexity for marginal runtime gains unless on a critical path.

---

## Appendix — Curl Timing Loop (local)

```bash
URL="http://localhost:3000/physician-compare?startIndex=0&itemsPerPage=10&entityType=individual&performanceYear=2023&nationalProviderIdentifier=0876543210"
N=20
for i in $(seq 1 $N); do
  curl -s -o /dev/null -w "%{time_total}\n" \
    -H "accept: application/json" \
    -H "qpp-taxpayer-identification-number: 000456789" \
    "$URL"
done | awk '{s+=$1} END{printf "avg_ms=%.2f\n",(s/NR)*1000}'
```

> For **content correctness**, capture one full response separately:
```bash
curl -s \
  -H "accept: application/json" \
  -H "qpp-taxpayer-identification-number: 000456789" \
  "$URL" | jq '.' > baseline.response.json
```

---

## Ready-to-Fill Summary

- **Repo SHA (baseline/after):** … / …
- **Target Files/Endpoints:** …
- **Complexity Δ% (per file):** …
- **Runtime Δ% (unit/API):** …
- **LOC Δ%:** …
- **Key AI Prompts that worked best:** …
- **Adoption Decision:** Accept / Iterate / Reject

