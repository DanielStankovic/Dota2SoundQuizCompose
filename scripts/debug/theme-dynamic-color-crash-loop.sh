#!/usr/bin/env bash
# Feedback loop for Crashlytics Resources$NotFoundException #0x106006c
# (android.R.color.system_surface_light) from Theme.kt → dynamicLightColorScheme34.
#
# RED  (exit 1): Theme still calls dynamic{Light,Dark}ColorScheme without catching
#                Resources.NotFoundException (the crash path).
# GREEN (exit 0): Theme guards that call with a NotFoundException fallback.
#
# Also verifies the Material3 1.4.0 sources still map 0x106006c via system_surface_light
# in dynamicLightColorScheme34 (load-bearing for this exact symptom).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
THEME="$ROOT/app/src/main/java/com/dsapps2018/dota2guessthesound/presentation/ui/theme/Theme.kt"
M3_SOURCES_JAR=$(ls -1 "$HOME"/.gradle/caches/modules-2/files-2.1/androidx.compose.material3/material3-android/*/834fadd2d4f9e620209f7c5de18b4f3d672ff9e/material3-android-*-sources.jar 2>/dev/null | head -1)
# fallback: any material3-android sources jar
if [[ -z "${M3_SOURCES_JAR:-}" ]]; then
  M3_SOURCES_JAR=$(find "$HOME"/.gradle/caches/modules-2/files-2.1/androidx.compose.material3/material3-android -name '*-sources.jar' 2>/dev/null | sort -V | tail -1)
fi

echo "== theme-dynamic-color-crash-loop =="
echo "THEME=$THEME"

if [[ ! -f "$THEME" ]]; then
  echo "FAIL: Theme.kt missing"
  exit 2
fi

# Symptom anchor: Material3 API-34 path reads system_surface_light (= 0x106006c)
if [[ -n "${M3_SOURCES_JAR:-}" && -f "$M3_SOURCES_JAR" ]]; then
  if ! jar tf "$M3_SOURCES_JAR" | rg -q 'DynamicTonalPalette\.android\.kt$'; then
    echo "WARN: DynamicTonalPalette.android.kt not in $M3_SOURCES_JAR"
  else
    TMP=$(mktemp -d)
    # shellcheck disable=SC2064
    trap 'rm -rf "$TMP"' EXIT
    (cd "$TMP" && jar xf "$M3_SOURCES_JAR")
    DT=$(find "$TMP" -name 'DynamicTonalPalette.android.kt' | head -1)
    if ! rg -q 'system_surface_light' "$DT"; then
      echo "FAIL: Material3 sources no longer reference system_surface_light — loop anchor broken"
      exit 2
    fi
    if ! rg -q 'fun dynamicLightColorScheme34' "$DT"; then
      echo "FAIL: dynamicLightColorScheme34 missing — loop anchor broken"
      exit 2
    fi
    echo "OK: Material3 still maps dynamicLightColorScheme34 → system_surface_light (0x106006c)"
  fi
else
  echo "WARN: material3 sources jar not found; skipping library anchor check"
fi

# Exact bug pattern: unguarded dynamic*ColorScheme call site in Theme.kt
if ! rg -q 'dynamicLightColorScheme|dynamicDarkColorScheme' "$THEME"; then
  echo "GREEN: Theme no longer calls dynamic color APIs (crash path removed)"
  exit 0
fi

# Must have NotFoundException handling around those calls
if rg -q 'Resources\.NotFoundException|NotFoundException' "$THEME"; then
  # Ensure catch/fallback is present (not just an import comment)
  if rg -q 'catch\s*\(.*NotFoundException' "$THEME"; then
    echo "GREEN: Theme catches Resources.NotFoundException around dynamic color"
    exit 0
  fi
fi

echo "RED: Theme calls dynamicLight/DarkColorScheme without NotFoundException fallback"
echo "     Crashlytics symptom: Resources\$NotFoundException Resource ID #0x106006c"
echo "     at ThemeKt.Dota2SoundQuizComposeTheme → dynamicLightColorScheme34 → system_surface_light"
exit 1
