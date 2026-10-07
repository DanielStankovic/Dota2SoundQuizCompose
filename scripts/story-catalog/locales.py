"""Curated Reading Locale codes for Issue i18n. `en` is the source, not a target."""

from __future__ import annotations

# code → register note (read by translators; seed_i18n.py uses the keys)
LOCALES: dict[str, str] = {
    "zh": "Simplified Chinese.",
    "zh-Hant": "Traditional Chinese. Retell; do not convert a zh file.",
    "es": "Spanish. Neutral Latin American, not Spain slang.",
    "pt": "Brazilian Portuguese, not European.",
    "hi": "Hindi. Devanagari.",
    "ar": "Arabic. Modern standard, not dialect.",
    "bn": "Bengali.",
    "ru": "Russian.",
    "ja": "Japanese.",
    "pa": "Punjabi. Gurmukhi.",
    "id": "Indonesian. Malay readers share this locale.",
    "ur": "Urdu. Nastaliq-friendly Arabic script.",
    "de": "German.",
    "fr": "French.",
    "ko": "Korean.",
    "tr": "Turkish.",
    "vi": "Vietnamese.",
    "sr-Latn": "Serbian Latin. Also Bosnian, Montenegrin, Croatian. Never Cyrillic; do not use sr.",
    "fil": "Filipino.",
    "uk": "Ukrainian.",
    "th": "Thai.",
    "ro": "Romanian.",
}
