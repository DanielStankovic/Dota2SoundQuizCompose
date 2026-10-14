#!/usr/bin/env bash
# Feedback loop for Crashlytics: IllegalStateException targetPackageName is null
# in com.google.android.play.core.hsdp.service.HsdpShimActivity (hsdp@@2.0.1).
#
# Signal: selected com.google.android.play:hsdp on prodReleaseRuntimeClasspath
# must be >= 2.2.0 (Play release notes 2.2.0: "Fixes a crash issue in HsdpShimActivity").
#
# Exit 1 (RED)   = vulnerable version selected (bug can occur)
# Exit 0 (GREEN) = fixed version selected
# Exit 2         = could not resolve hsdp (inconclusive)
#
# Optional: pass a Crashlytics .txt path as $1 to assert against a report instead
# of the Gradle classpath (useful when reproducing from the original stack).

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

MIN_FIXED="2.2.0"

is_vulnerable() {
  local v="$1"
  local a b c fa fb fc
  IFS=. read -r a b c <<<"$v"
  IFS=. read -r fa fb fc <<<"$MIN_FIXED"
  if (( a < fa )); then return 0; fi
  if (( a > fa )); then return 1; fi
  if (( b < fb )); then return 0; fi
  if (( b > fb )); then return 1; fi
  if (( c < fc )); then return 0; fi
  return 1
}

SELECTED=""

if [[ "${1:-}" != "" && -f "$1" ]]; then
  SELECTED="$(grep -oE 'com\.google\.android\.play:hsdp@@[0-9]+\.[0-9]+\.[0-9]+' "$1" \
    | head -1 | sed 's/.*@@//' || true)"
  echo "SOURCE=crash_report"
else
  OUT="$(mktemp)"
  trap 'rm -f "$OUT"' EXIT
  ./gradlew :app:dependencyInsight \
    --dependency com.google.android.play:hsdp \
    --configuration prodReleaseRuntimeClasspath \
    --quiet >"$OUT" 2>&1

  # dependencyInsight prints the selected module first: group:name:version
  SELECTED="$(grep -E '^com\.google\.android\.play:hsdp:[0-9]+\.[0-9]+\.[0-9]+$' "$OUT" \
    | head -1 | sed 's/.*://' || true)"
  echo "SOURCE=gradle_prodReleaseRuntimeClasspath"
fi

echo "MIN_FIXED=$MIN_FIXED"

if [[ -z "$SELECTED" ]]; then
  echo "VERDICT=INCONCLUSIVE reason=hsdp_not_found"
  exit 2
fi

echo "SELECTED_HSDP=$SELECTED"

if is_vulnerable "$SELECTED"; then
  echo "VULNERABLE=$SELECTED (HsdpShimActivity crash possible: targetPackageName is null)"
  echo "VERDICT=RED symptom=IllegalStateException:targetPackageName_is_null@HsdpShimActivity"
  exit 1
fi

echo "FIXED=$SELECTED"
echo "VERDICT=GREEN"
exit 0
