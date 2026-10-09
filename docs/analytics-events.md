# Product analytics event catalog

Firebase / GA4 events the app logs. Use this when inspecting **Analytics → Events** (or DebugView). Implementation: `ProductAnalytics`. Automatic SDK events (`first_open`, `session_start`, `user_engagement`, `screen_view`, AdMob `ad_impression` / `ad_reward`) are **not** listed here.

Register custom parameters as event-scoped custom dimensions / metrics in GA4 (**Admin → Custom definitions**) or they will not appear in standard reports. Prescribed GA4 params (`level_name`, `success`, `score`, `method`, `content_type`, `content_id`, `virtual_currency_name`, `value`, `item_name`) do not need that.

String custom params (`first_clear`, `replay`, `extra_life_used`) are `"true"` / `"false"`. `success` on `level_end` is `1` / `0`.

`level_name` and `journey_level` are the catalog level number (`"1"`, `"2"`, …). Affixes are display names (`Mirror Mode`, `Blurred Vision`), comma-separated, truncated to 100 characters.

There is **no** `level_up` event. Progression is `level_end` with `first_clear=true` plus user property `journey_level`.

## User flow (what fires when)

```
App open
  [auto] first_open / session_start
  user property signed_in, journey_level  (Home)

Home — Quiz card tap
  select_content  content_type=mode  content_id=quiz
  → Quiz screen
      mode_start  mode=quiz
      [wrong + Extra Life Gate available]
        extra_life_offer  mode=quiz  gate_offer=heart
        extra_life_recover | extra_life_decline
      → Play again
          post_score  mode=quiz  score=<points>
          earn_virtual_currency  source=quiz  (if score > 0)

Home — Fast Finger card tap
  select_content  content_id=fast_finger
  → Pick 30/60/90 → Fast Finger screen
      mode_start  mode=ff_30 | ff_60 | ff_90
      → Play again
          post_score  mode=ff_*  score=<weighted>
          earn_virtual_currency  source=ff_*  (if above mode threshold; value = 30/60/90)

Home — Journey card, signed out
  journey_gated
  (no navigation)

Home — Journey card, signed in
  select_content  content_id=journey
  → Level list → start a level
      level_start  level_name=<N>  first_clear / replay  affixes
      [Extra Life Gate]
        extra_life_offer  gate_offer=heart|time
        extra_life_recover | extra_life_decline
      [Assist tray]
        Sound Count Reveal buy (60 coins):
          spend_virtual_currency  item_name=sound_count_reveal
          assist_action  assist=sound_count_reveal  method=coins
        Sound Sampler unlock (rewarded ad completes):
          assist_action  assist=sound_sampler  method=rewarded_ad
        Coin Ad Grant (+40, rewarded ad completes):
          earn_virtual_currency  source=coin_ad_grant
          assist_action  assist=coin_ad_grant  method=rewarded_ad
      Win:
        level_end  success=1  clear_time_ms  extra_life_used  gate_offer
        earn_virtual_currency  source=journey_first_clear  value=20  (first clear only)
        user property journey_level = N  (first clear only)
      Fail / back out:
        level_end  success=0  fail_reason=hearts|race_timeout|sudden_death|gate_declined|quit

Home — Invoker card (enough coins) → explanation → confirm entry
  spend_virtual_currency  item_name=invoker_entry  value=40
  select_content  content_id=invoker
  → Invoker round
      mode_start  mode=invoker
      → Play again
          post_score  mode=invoker  score=<points>

Home — Invoker Coin Ad Grant (+40; shared 5 min / 5 per UTC day)
  earn_virtual_currency  source=coin_ad_grant  value=40

Chapter reader — last Chapter, star tap (1–5, different from current)
  issue_rate  issue_id  issue_slug  stars  is_rerate

Profile — Google sign-in
  sign_up  (only first cloud game_data row)
  login  method=Google
  user properties signed_in=true, journey_level=<watermark>

Profile — sign-out
  user properties signed_in=false, journey_level=0
```

## Events

### `select_content` (GA4 recommended)

| When | `content_type` | `content_id` |
| --- | --- | --- |
| Home Quiz tap | `mode` | `quiz` |
| Home Fast Finger tap | `mode` | `fast_finger` |
| Home Journey tap (signed in) | `mode` | `journey` |
| Invoker entry spend succeeds | `mode` | `invoker` |

### `journey_gated` (custom)

Signed-out tap on the Journey card. No parameters.

### `login` / `sign_up` (GA4 recommended)

| Event | When | Params |
| --- | --- | --- |
| `login` | Auth Session Google sign-in succeeds | `method` = `Google` |
| `sign_up` | First `game_data` row created for that account | `method` = `Google` |

### `mode_start` (custom)

Arcade round actually begins (pool ready / first sound). Not Invoker entry.

| `mode` | When |
| --- | --- |
| `quiz` | Quiz ViewModel starts the round |
| `ff_30` / `ff_60` / `ff_90` | Fast Finger round starts (picked duration) |
| `invoker` | Invoker Round `start` |

### `level_start` (GA4 games)

Journey Round board becomes `Ready` (including Journey Round briefing hold). Once per attempt.

| Param | Values |
| --- | --- |
| `level_name` | `"1"`, `"2"`, … |
| `mode` | `journey` |
| `journey_level` | same number (Long) |
| `first_clear` | `true` if this level is above the current watermark |
| `replay` | `true` if this level is already cleared |
| `affixes` | comma-separated Affix **names**, or empty |
| `affix_count` | count (Long) |

### `level_end` (GA4 games)

Once per attempt: win (`Correct`), Game Over, Extra Life Gate decline, or leaving the screen mid-round (`quit`).

| Param | Values |
| --- | --- |
| `level_name` | `"1"`, `"2"`, … |
| `success` | `1` win, `0` fail |
| `mode` | `journey` |
| `journey_level` | level number |
| `first_clear` / `replay` | same as start of this attempt |
| `affixes` / `affix_count` | same as start |
| `extra_life_used` | `true` if Extra Life Gate recovered this attempt |
| `gate_offer` | `heart` / `time` / `none` (last offer this attempt) |
| `fail_reason` | fail only: `hearts`, `race_timeout`, `sudden_death`, `gate_declined`, `quit` |
| `clear_time_ms` | win only; Ready clock → Correct (paused under ads / Gate) |

### `post_score` (GA4 games)

Arcade Mode Result submit only (not Journey).

| `mode` | `score` |
| --- | --- |
| `quiz` | Quiz points (Long/Double) |
| `invoker` | Invoker points |
| `ff_30` / `ff_60` / `ff_90` | Fast Finger weighted score |

### `earn_virtual_currency` / `spend_virtual_currency` (GA4 recommended)

`virtual_currency_name` is always `coins`. Skipped when amount is 0.

| Event | When | Extra params |
| --- | --- | --- |
| `earn_virtual_currency` | Journey first-clear **20** | `source` = `journey_first_clear` |
| | Quiz result coins (= score) | `source` = `quiz` |
| | Fast Finger threshold grant (= 30/60/90) | `source` = `ff_30` / `ff_60` / `ff_90` |
| | Coin Ad Grant **40** (Assist tray / Home Invoker) | `source` = `coin_ad_grant` |
| `spend_virtual_currency` | Invoker entry **40** | `item_name` = `invoker_entry` |
| | Sound Count Reveal **60** | `item_name` = `sound_count_reveal` |

### `assist_action` (custom)

Fired when a Journey Assist is successfully obtained (not on tray open / disabled taps).

| Param | Values |
| --- | --- |
| `assist` | `sound_count_reveal` / `sound_sampler` / `coin_ad_grant` |
| `method` | `coins` / `rewarded_ad` |
| `mode` | `journey` |
| `journey_level` | catalog level number |

Count Reveal also logs `spend_virtual_currency`. Coin Ad Grant also logs `earn_virtual_currency`. Sound Sampler unlock has no currency event — `assist_action` is the signal.

### `extra_life_offer` / `extra_life_recover` / `extra_life_decline` (custom)

| Param | Journey | Quiz |
| --- | --- | --- |
| `mode` | `journey` | `quiz` |
| `gate_offer` | `heart` or `time` | `heart` |
| `journey_level` | level number | omitted |
| `affixes` | Affix names | omitted |

Offer: dialog shown. Recover: rewarded grant applied. Decline: skip, or rewarded ad closed without a grant.

Game Over interstitial is **not** these events.

## User properties

| Name | Values | When updated |
| --- | --- | --- |
| `signed_in` | `true` / `false` | Home (session) and Auth Session sign-in / sign-out |
| `journey_level` | watermark as string (`"0"`, `"12"`) | Home (progress), sign-in, first-clear win, sign-out → `"0"` |

## Custom definitions to register

GA4 **Admin → Data display → Custom definitions**. Paste **Description** into that field (it is a reminder for you, not shown to players). Parameter names must match the app exactly. Skip prescribed params (`content_id`, `level_name`, `success`, `score`, `method`, …).

### Event-scoped dimensions

| Dimension name | Event parameter | Description (paste) |
| --- | --- | --- |
| Mode | `mode` | Which mode this event belongs to. Set on arcade round start (`mode_start`), arcade finish (`post_score`), Extra Life Gate offer/recover/decline, and every Journey `level_start` / `level_end`. Values: quiz, ff_30, ff_60, ff_90, invoker, journey. |
| First clear | `first_clear` | Journey attempt on a level the player has not cleared yet (above the watermark). Set on `level_start` and `level_end`. `"true"` / `"false"`. |
| Replay | `replay` | Journey attempt on a level already cleared. Set on `level_start` and `level_end`. `"true"` / `"false"`. |
| Affixes | `affixes` | Comma-separated Affix display names on this Journey attempt (e.g. Mirror Mode, Blurred Vision). Set on `level_start`, `level_end`, and Journey Extra Life Gate events. Empty when the level has none. |
| Fail reason | `fail_reason` | Why a Journey attempt ended without a win. Only on `level_end` with success=0: hearts, race_timeout, sudden_death, gate_declined, quit (left the screen mid-round). |
| Extra life used | `extra_life_used` | Whether Extra Life Gate recovered this Journey attempt. Set on `level_end`. `"true"` / `"false"`. |
| Gate offer | `gate_offer` | Extra Life Gate type. On Journey/Quiz `extra_life_*` events: heart or time. On Journey `level_end`: last offer this attempt, or none. |
| Coin source | `source` | Why coins were granted. Only on `earn_virtual_currency`: journey_first_clear (win, 20 coins), quiz (result = score), ff_30/ff_60/ff_90 (threshold grant), coin_ad_grant (+40 Assist tray / Home Invoker). |
| Assist | `assist` | Which Journey Assist succeeded. Only on `assist_action`: sound_count_reveal, sound_sampler, coin_ad_grant. |

### Event-scoped metrics

| Metric name | Event parameter | Unit | Description (paste) |
| --- | --- | --- | --- |
| Journey level (event) | `journey_level` | Standard | Catalog level number of this Journey attempt (1, 2, 3…). Set on `level_start`, `level_end`, and Journey Extra Life Gate events. Not the player's watermark. |
| Affix count | `affix_count` | Standard | How many Affixes this Journey level has. Set on `level_start` and `level_end`. |
| Clear time ms | `clear_time_ms` | Standard | Journey win duration in milliseconds (Ready clock → Correct; paused under ads / Gate). Only on `level_end` with success=1. |

### User-scoped dimensions

| Dimension name | User property | Description (paste) |
| --- | --- | --- |
| Signed in | `signed_in` | Whether the player currently has Auth. Updated on Home (session), Google sign-in (`true`), and sign-out (`false`). |
| Journey level (user) | `journey_level` | Player's Journey watermark (highest cleared level as text, `"0"` if none). Updated on Home, sign-in, first-clear win, and sign-out → `"0"`. |

## DebugView

Prod:

```
adb shell setprop debug.firebase.analytics.app com.dsapps2018.dota2guessthesound
```

Dev flavor (`applicationIdSuffix` `.dev`):

```
adb shell setprop debug.firebase.analytics.app com.dsapps2018.dota2guessthesound.dev
```

### `issue_rate` (custom)

Fired when the player sets or overwrites an **Issue Rating** on the last Chapter (same-star tap is a no-op and is not logged).

| Param | Type | Notes |
| --- | --- | --- |
| `issue_id` | number | Catalog Issue id |
| `issue_slug` | string | Issue slug (truncated to 100) |
| `stars` | number | 1–5 |
| `is_rerate` | string | `"true"` if overwriting a previous Rating; `"false"` on first Rating |

## Not logged (intentional)

Affix tray open, per-sound plays, Soundquake ticks, `level_up`, per-heart drain, Journey `post_score`, Play Games, a second analytics vendor.
