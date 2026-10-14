#!/usr/bin/env bash
set -euo pipefail

JACOCO_REPORT_PATH="test-coverage/target/site/jacoco-aggregate/jacoco.xml"
MINIMUM_COVERAGE=85
COVERAGE_COMMENT_MARKER="<!-- code-coverage-report -->"
GITHUB_REPO_NAME="${GITHUB_REPOSITORY}"
GITHUB_REPO_API_URL="https://api.github.com/repos/CMSgov/${GITHUB_REPO_NAME}"
WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT

if [ ! -f "$JACOCO_REPORT_PATH" ]; then
  echo "JaCoCo aggregate report not found: ${JACOCO_REPORT_PATH}" >&2
  exit 1
fi

# JaCoCo references an external DTD that is not needed to read coverage counters.
sed 's/<!DOCTYPE report PUBLIC "-\/\/JACOCO\/\/DTD Report 1\.1\/\/EN" "report\.dtd">//g' \
  "$JACOCO_REPORT_PATH" > "$WORK_DIR/report.xml"

xmlstarlet sel -T -t \
  -m '/report/counter | /report//package/sourcefile/counter' \
  -i 'parent::report' -o total -b \
  -i 'parent::sourcefile' -v 'concat(../../@name, "/", ../@name)' -b \
  -o $'\t' -v '@type' \
  -o $'\t' -v '@missed' \
  -o $'\t' -v '@covered' -n \
  "$WORK_DIR/report.xml" > "$WORK_DIR/counters.tsv"

jq -Rn '
  def percentage($counters; $type):
    [$counters[] | select(.[1] == $type)] as $matches |
    if ($matches | length) == 0 then null
    else
      ($matches | map(.[2] | tonumber) | add) as $missed |
      ($matches | map(.[3] | tonumber) | add) as $covered |
      if $missed < 0 or $covered < 0 then error("Invalid JaCoCo counters")
      elif ($missed + $covered) == 0 then null
      else $covered * 100 / ($missed + $covered) end
    end;
  def metrics($counters):
    {
      statements: percentage($counters; "INSTRUCTION"),
      branches: percentage($counters; "BRANCH"),
      functions: percentage($counters; "METHOD"),
      lines: percentage($counters; "LINE")
    };
  [inputs | split("\t")] | group_by(.[0]) |
  map({path: .[0][0]} + metrics(.)) as $groups |
  [$groups[] | select(.path == "total")][0] as $total |
  if $total == null then error("Missing overall JaCoCo counters")
  else
    ($total | del(.path)) +
    {files: [$groups[] | select(.path != "total")]}
  end
' < "$WORK_DIR/counters.tsv" > "$WORK_DIR/coverage.json"

COVERAGE_PASSES="$(jq -r --argjson minimum "$MINIMUM_COVERAGE" '
  [.statements, .branches, .functions, .lines] |
  all(.[]; type == "number" and . >= $minimum and . <= 100)
' "$WORK_DIR/coverage.json")"

check_threshold() {
  if [ "$COVERAGE_PASSES" != "true" ]; then
    echo "Coverage is below the ${MINIMUM_COVERAGE}% minimum."
    exit 1
  fi
}

PR_NUMBER="$(printf '%s' "${CODEBUILD_WEBHOOK_TRIGGER:-}" |
  sed -nE 's#^(pr|pull_request)/([0-9]+)(/.*)?$#\2#p')"

if [ -z "$PR_NUMBER" ]; then
  echo "Not a pull request build; skipping the coverage comment."
  check_threshold
  exit 0
fi

if [ -z "${GITHUB_TOKEN:-}" ]; then
  echo "GITHUB_TOKEN is required to publish the pull request coverage comment."
  exit 1
fi

github_api() {
  curl --fail --silent --show-error \
    --header "Authorization: Bearer ${GITHUB_TOKEN}" \
    --header "Accept: application/vnd.github+json" \
    --header "X-GitHub-Api-Version: 2022-11-28" \
    "$@"
}

fetch_pages() {
  local url="$1" page=1 items="[]" page_items count
  while :; do
    page_items="$(github_api "${url}?per_page=100&page=${page}")"
    count="$(jq -er 'if type == "array" then length else error("Expected a GitHub API array") end' <<< "$page_items")"
    items="$(jq -cn --argjson items "$items" --argjson page "$page_items" '$items + $page')"
    if [ "$count" -lt 100 ]; then break; fi
    page=$((page + 1))
  done
  printf '%s\n' "$items"
}

CHANGED_FILES="$(fetch_pages "${GITHUB_REPO_API_URL}/pulls/${PR_NUMBER}/files")"

jq -nr \
  --arg marker "$COVERAGE_COMMENT_MARKER" \
  --argjson coverage "$(cat "$WORK_DIR/coverage.json")" \
  --argjson changed_files "$CHANGED_FILES" \
  --argjson minimum "$MINIMUM_COVERAGE" '
  def pct($value):
    if $value == null then "n/a" else "\(($value * 10 | floor) / 10)%" end;
  def row($name):
    "| \($name | gsub("\\|"; "\\\\|")) | \(pct(.statements)) | \(pct(.branches)) | \(pct(.functions)) | \(pct(.lines)) |";
  [
    $changed_files[].filename as $filename |
    ($coverage.files | map(select(.path as $path | $filename | endswith("/" + $path))))[0] as $file |
    select($file != null) |
    $file | row("`" + $filename + "`")
  ] as $rows |
  $marker + "\n## Conversion Tool Code Coverage\n\n" +
  "| Scope | Statements | Branches | Functions | Lines |\n|---|---:|---:|---:|---:|\n" +
  ($coverage | row("**Overall**")) + "\n" +
  (if ($rows | length) > 0 then
    "\n### Changed Java files\n\n| File | Statements | Branches | Functions | Lines |\n|---|---:|---:|---:|---:|\n" +
    ($rows | join("\n")) + "\n"
  else
    "\n_No changed Java files with JaCoCo coverage were found._\n"
  end) +
  "\n_Minimum overall coverage: \($minimum)% for each of Statements, Branches, Functions, and Lines._\n"
' > "$WORK_DIR/comment.md"

COMMENTS_URL="${GITHUB_REPO_API_URL}/issues/${PR_NUMBER}/comments"
COMMENTS="$(fetch_pages "$COMMENTS_URL")"
COMMENT_ID="$(jq -r --arg marker "$COVERAGE_COMMENT_MARKER" \
  '[.[] | select((.body // "") | contains($marker))][0].id // empty' <<< "$COMMENTS")"

if [ -n "$COMMENT_ID" ]; then
  COMMENT_METHOD="PATCH"
  COMMENT_URL="${GITHUB_REPO_API_URL}/issues/comments/${COMMENT_ID}"
else
  COMMENT_METHOD="POST"
  COMMENT_URL="$COMMENTS_URL"
fi
jq -n --rawfile body "$WORK_DIR/comment.md" '{body: $body}' | github_api \
  --header "Content-Type: application/json" \
  --request "$COMMENT_METHOD" \
  --data @- \
  --output /dev/null \
  "$COMMENT_URL"
echo "Published code coverage comment on PR #${PR_NUMBER}."
check_threshold
