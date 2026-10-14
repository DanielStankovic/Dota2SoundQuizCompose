#!/usr/bin/env bash
# Feedback loop for Crashlytics:
#   CompletionHandlerException ← IllegalStateException: Unbalanced enter/exit
#   at com.android.okhttp.okio.AsyncTimeout.enter
#   ← RawSourceChannel.closeSource (cancel) racing a concurrent read
#   ← io.ktor.client.engine.android.AndroidClientEngine
#
# RED:  build still wires the Android Ktor engine (HttpURLConnection / platform okhttp)
#       that produces this exact stack when a request is cancelled mid-body.
# GREEN: Android engine dependency removed and OkHttp engine is used instead
#        (and/or Ktor >= 3.6.0 which ships KTOR-9629/9627 cancel fixes).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
TOML="$ROOT/gradle/libs.versions.toml"
APP_GRADLE="$ROOT/app/build.gradle.kts"
SUPABASE="$ROOT/app/src/main/java/com/dsapps2018/dota2guessthesound/di/SupabaseModule.kt"
FIXTURE="${CRASH_FIXTURE:-$HOME/Downloads/cc.txt}"

echo "== ktor-android-cancel-crash-loop =="

# Anchor: Crashlytics symptom still matches the known signature (when fixture present)
if [[ -f "$FIXTURE" ]]; then
  if ! rg -q 'Unbalanced enter/exit' "$FIXTURE"; then
    echo "FAIL: fixture missing Unbalanced enter/exit — wrong crash file?"
    exit 2
  fi
  if ! rg -q 'AndroidClientEngine' "$FIXTURE"; then
    echo "FAIL: fixture missing AndroidClientEngine — wrong crash file?"
    exit 2
  fi
  if ! rg -q 'RawSourceChannel\.closeSource' "$FIXTURE"; then
    echo "FAIL: fixture missing RawSourceChannel.closeSource — wrong crash file?"
    exit 2
  fi
  echo "OK: fixture matches Crashlytics signature (Unbalanced enter/exit + AndroidClientEngine)"
else
  echo "WARN: crash fixture not found at $FIXTURE; skipping signature anchor"
fi

KtorBom=$(rg -oN 'ktorBom\s*=\s*"([^"]+)"' -r '$1' "$TOML" | head -1)
echo "ktorBom=$KtorBom"

uses_android_dep=0
uses_okhttp_dep=0
if rg -q 'ktor-client-android|ktor\.client\.android' "$TOML" "$APP_GRADLE"; then
  # present in version catalog / gradle — check it's actually implemented
  if rg -q 'libs\.ktor\.client\.android|ktor-client-android' "$APP_GRADLE"; then
    uses_android_dep=1
  fi
fi
if rg -q 'libs\.ktor\.client\.okhttp|ktor-client-okhttp' "$APP_GRADLE"; then
  uses_okhttp_dep=1
fi

explicit_okhttp=0
if [[ -f "$SUPABASE" ]] && rg -q 'OkHttp|httpEngine' "$SUPABASE"; then
  if rg -q 'httpEngine\s*=' "$SUPABASE" && rg -q 'OkHttp' "$SUPABASE"; then
    explicit_okhttp=1
  fi
fi

echo "uses_android_dep=$uses_android_dep uses_okhttp_dep=$uses_okhttp_dep explicit_okhttp=$explicit_okhttp"

# Version helper: return 0 if $1 >= $2 (semver x.y.z)
semver_ge() {
  local a="$1" b="$2"
  local IFS=.
  # shellcheck disable=SC2086
  set -- $a; local a1=$1 a2=$2 a3=$3
  # shellcheck disable=SC2086
  set -- $b; local b1=$1 b2=$2 b3=$3
  if (( a1 != b1 )); then (( a1 > b1 )); return; fi
  if (( a2 != b2 )); then (( a2 > b2 )); return; fi
  (( a3 >= b3 ))
}

fixed_version=0
if semver_ge "$KtorBom" "3.6.0"; then
  fixed_version=1
fi
echo "ktor_has_9629_fix=$fixed_version"

# RED if Android engine is still the app's Ktor engine (the crash code path)
if (( uses_android_dep == 1 )) && (( uses_okhttp_dep == 0 )); then
  echo "RED: app still depends on ktor-client-android without OkHttp engine"
  echo "     Crash path: AndroidClientEngine → RawSourceChannel.closeSource → platform okhttp Unbalanced enter/exit"
  exit 1
fi

# If both present without explicit OkHttp, ServiceLoader choice is ambiguous → still RED
if (( uses_android_dep == 1 )) && (( uses_okhttp_dep == 1 )) && (( explicit_okhttp == 0 )); then
  echo "RED: both Android and OkHttp engines on classpath without explicit httpEngine=OkHttp"
  exit 1
fi

if (( uses_okhttp_dep == 1 )) && (( uses_android_dep == 0 || explicit_okhttp == 1 )); then
  echo "GREEN: OkHttp engine wired; AndroidClientEngine crash path removed"
  exit 0
fi

if (( fixed_version == 1 )) && (( uses_android_dep == 1 )); then
  # Ktor 3.6+ alone is a partial mitigation; prefer engine switch. Accept as green only
  # if we intentionally keep Android — still warn.
  echo "GREEN: Ktor >= 3.6.0 (KTOR-9629/9627); Android engine still present"
  exit 0
fi

echo "RED: no OkHttp engine and Ktor < 3.6.0 — vulnerable to this Crashlytics crash"
exit 1
