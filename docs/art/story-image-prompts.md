# Story image prompts

Paste-ready prompts for the Issue picker covers and the reader backdrops. Archived stories are not included.

Each fenced block is one complete prompt. Copy one block, paste it into the image agent, generate, then copy the next.

---

## How to send them

Do **not** send a cover and a background as two independent jobs in parallel. The second image will invent its own face.

Do **not** ask for both images in one generation call either. You want two files, not a collage.

**Send them in the same chat, in this order:**

1. Paste the **cover** prompt. Generate until the hero looks right. Keep that image.
2. In the **same session**, attach the approved cover as a character reference, then paste that story's **background** prompt.
3. Tell the agent to match the attached cover's face, body, costume, and colors, and to change only the scene.

**Invoker:** regenerate the **cover first** so the three wisps have faces. Then attach that new cover and paste the backdrop. If you use the old cover as the reference, the wisps will stay faceless fireballs. The shipped backdrop is the Tiny fight in the canyon, not the earlier Anchor / whispering-woods plates.

**Tinker:** regenerate the **cover first** (three teenagers sitting, laughing). Do not attach the old standing lineup or Rubick will stay the adult Grand Magus. Then attach the new cover and paste the Room 12 backdrop.

**Backdrops are story stills, not character lineups.** Imagine a camera already rolling on the scene — dinner, a rescue, a date going wrong — and this image is one frame. The heroes sit, talk, work, run, look at each other. They do not stand beside a table in a hero pose. Same painterly Dota 2 look as the covers; the change is the acting, not the art style.

**Across stories:** start a new chat per story, unless you are deliberately matching a shared art style. If you want the whole set to look like one deck, generate one approved cover first (Lina or Storm Spirit), keep that image, and attach it to later cover jobs as a *style* reference — not as a character reference.

Covers have no title painted in. Titles are drawn in the app so they can be translated.

Reader backdrops stay dim on purpose so light text can sit on them. They must still read as a scene — visible midtones in walls, furniture, and floor, not a black room. The first Storm Spirit backdrop overshot into crushed blacks; keep that file, and do not add a heavy scrim on top of it in code. For later backdrops, the prompts below ask for candlelit midtones. In the reader, use light text plus a *light* overlay if needed (about 15–25%), never a 50%+ wash.

---

## 1. Who's That Handsome Devil?

Story: `storm-spirit-handsome-devil.md`
Hero: Storm Spirit (Raijin Thunderkeg). Muerta appears only in the background.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 hero identity: costume, silhouette, colors, signature props. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Storm Spirit (Raijin Thunderkeg) as a jolly blue storm elemental in a man's body. Bright blue armor, favorite hat, huge cheesy grin, flexing one arm, holding a fresh flower like a man who thinks he is irresistible. Confident, comic, not menacing. Optional tiny spark of lightning at his fingertips, not a full storm.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Storm Spirit's face, body, costume, hat, and colors. Muerta is a second official Dota 2 hero: pale, ghostly, dark clothes, cold glowing eyes. She is supporting, not the cover hero.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles.

SUBJECT: A date in progress at a dim fancy restaurant. Storm Spirit and Muerta are both sitting at a corner table, not standing. Storm Spirit leans forward over the table mid-sentence, cheesy grin, one elbow on the cloth, talking at her with total confidence, maybe glancing at his own reflection in a silver spoon. Muerta sits opposite him, upright, not laughing, cold glowing eyes, a faint annoyed tightness in her mouth, hands still on the table. Plates, glasses, a candle between them. A pretty woman in a red dress is walking past in the background, out of focus. Warm low lamplight, visible wood and tablecloth midtones. Center of the frame is the empty tablecloth between them, not their faces. Mood: cringe romcom, not horror. They are having dinner. Do not stand them beside the table like posed game characters.
```

---

## 2. Marci's Battle Speech

Story: `marci-battle-speech.md`
Hero: Marci.

The cover must look like a real battle-speech story. Do not hint at whistling or muteness.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 hero identity: costume, silhouette, colors, signature props. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Marci from Dota 2, the mute loyal fighter: short, compact, fierce, pink/magenta hair, simple combat clothes, bracers. Standing as if about to address troops before a fight — chin up, one fist clenched, serious face, no smile. No whistle visible, no musical notes, no speech bubble. She looks like a protagonist of a war story.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Marci's face, body, costume, hair, and colors.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, musical notes, whistles.

SUBJECT: The moment before a fight, already underway as a scene. Marci stands on a low rock facing her own side, not the camera, one hand raised as if she is about to speak. Soldiers in front of her have actually stopped and turned toward her, waiting, banners sagging in the dawn wind. Her face is serious, mouth closed — she looks like she is gathering herself for a speech. Dusty muted gold light. No whistle, no musical notes, no open mouth mid-shout, no hero-pose toward camera. The middle of the frame is open dusty air between her and the troops. It should feel like we walked in on the start of a battle speech.
```

---

## 3. Lina and the Golden Rolling Pin

Story: `lina-golden-rolling-pin.md`
Hero: Lina.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 hero identity: costume, silhouette, colors, signature props. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Lina, young red-haired fire mage, baker's look: flour-dusted apron over her usual red/orange costume, a golden wooden rolling pin in one hand, a small candle-sized flame in the other (gentle, controlled — not an explosion). Confident, warm, a little dangerous. No dragon, no sister in this plate.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Lina's face, body, costume, hair, apron, rolling pin, and colors.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, shop signs.

SUBJECT: The longest bake. Night bakery, doors open onto a blizzard. Lina is at work, not posing: kneeling or sitting on a low stool at the cracked oven, both hands at the oven mouth, holding a tiny candle-sized flame steady, face tired and concentrated, hair damp with snow. The bakery is the only warm room on the street. Snow blows in across the floor. Through the open door, the narrow street and a faint purple window across the way. Oven glow is amber and contained, not a fireball. Lower edge is dark cobbles and snow. Hopeful, quiet, exhausted. She is keeping the town alive, not standing next to an oven.
```

---

## 4. Rubick the Wooden Puppet

Story: `rubick-the-wooden-puppet.md`
Hero: Rubick as a living wooden puppet boy.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Rubick identity (green magic, magus silhouette) translated into a wooden puppet boy: dark carved wood, visible joints. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Young Rubick as a living wooden puppet boy, not a human child: dark carved wood, visible joints, stiff pose, blank-then-curious wooden face, thick burned-away string remnants at the wrists. Soft green light glowing from inside his chest. One hand slightly raised as if he just learned to move. Innocent, strange, not scary. No staff yet, no cape, still a puppet.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Rubick's wooden body, joints, face, chest glow, and colors. He is still a puppet, not a human child, not the adult Grand Magus.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles.

SUBJECT: The puppet has just come alive. Inside a dark toy shop after a green storm. Rubick the wooden boy is mid-action: one stiff wooden hand on the window sill, legs awkward as if he just stood up, strings burned and still smoking at the wrists, wooden eyes newly open, looking around the room in confusion. Green light in his chest is soft, not a spotlight. Toys and the shop interior recede into gloom. Center of the frame is empty floorboards. Wonder, not horror. No staff, still a puppet. He is discovering he can move, not standing in a doorway for a portrait.
```

---

## 5. Coach by Mistake: The Demon of Amsterdam

Story: `queen-of-pain-coach-by-mistake.md`
Hero: Akasha (Queen of Pain). Store-safe gym comedy. Theatrical demon costume, not sexual, not BDSM.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Queen of Pain identity: sharp features, piercing eyes, demonic silhouette — translated into gym-coach costume with theatrical wings and tail. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Akasha (Queen of Pain) as a stunning, sharp-featured gym coach in tight black training gear, silver spikes on the shoulders, theatrical fake dark bat wings and a thin pointed devil tail. Heavy skipping rope coiled in one hand, wicked small smile, piercing eyes. Powerful and comic, not sexy pin-up, no cleavage focus, no whip, no club lighting. Amsterdam-night attitude, not lingerie.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Akasha's face, body, and colors. In this scene she is still the cleaner — dirty cleaning clothes, mop, bucket — not the later winged costume. Store-safe gym comedy. The rope hits the floor, not a person. Not sexual, not BDSM, not a club.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, whips, lingerie, sexual posing.

SUBJECT: The accident that makes her a coach. Dark studio in The Pit. Akasha is mid-scene, not posing: mop and bucket dropped at her feet, skipping rope in one hand just cracked against the floor, face annoyed and confused, mouth open as if she just shouted at the mess. A huge muscular man (Davion) is on the rubber floor doing push-ups, looking up at her excited, as if she is a real trainer. She still looks like she came to clean. Dim industrial lights, chalk dust. Store-safe gym comedy. The rope hits the floor, not a person. No sexy pin-up, no whip, no club. Center of the frame is dark rubber floor between them.
```

---

## 6. Eleven Bullets

Story: `sniper-eleven-bullets.md`
Hero: Sniper (Kardel Sharpeye).

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Sniper / Keen Folk identity: costume, silhouette, colors, signature rifle. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Sniper (Kardel Sharpeye), Keen Folk marksman, 41, long rifle, wide-brim hat, coat with a few visible rifle cartridges. Dry western hero, a little too pleased with himself, not a modern soldier. Rifle resting on his shoulder, mountain wind on the coat. No blood, no army behind him on this plate.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Sharpeye's face, body, hat, coat, rifle, and colors.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, blood, gore.

SUBJECT: The last shot, held. Dawn in a mountain pass. Sharpeye is lying prone on the slate roof of the village hall, cheek to the rifle, aiming up and left at a distant bell on the snowy mountain, not at the army. His hat is on the slates beside him. He is waiting, still, for the wind to stop. Tiny villagers watch from the wall. Nine thousand men are only a dark packed texture in the pass below. Center of the frame is cold air and rock between the rifle and the bell. No blood, no bright snow glare, no standing hero-with-gun pose. Epic and quiet.
```

---

## 7. Tinker and the Cupboard Kids

Story: `tinker-the-cupboard-kids.md`
Heroes: Tinker, Meepo, Rubick as school-age underdogs. Keep the scene simple: Room 12, sitting, talking.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, group of three SITTING close together, not a standing lineup. Small padding on all sides so nothing clips.
OUTPUT: isolated characters on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left. Medium detail density: costume and faces stay sharp.

CHARACTER: Match official Dota 2 identities for Tinker, Meepo, and Rubick, translated into teenagers — school uniforms mixed with their hero silhouettes. They are kids, not adult in-game models. Draw only one Meepo, not five copies. Rubick is a school-age magus, not the adult Grand Magus in a tall collar with a huge crystal staff.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels, standing lineup, adult Rubick staff, five Meepos, storm, lift, cable car.

SUBJECT: Three school underdogs sitting together like friends in a club, laughing, looking at each other, not posing for a portrait. Tinker is the smallest, goggles on his forehead, a small gadget in his hands, half-shy grin. Meepo is one goblin boy in a school jumper, grinning at the joke. Rubick is a teenage magus in a too-big school coat, mid-laugh (his laugh is famous and loud). They lean toward each other. Warm, ordinary, the cupboard kids. No popular kids, no dance hall, no rescue.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: Small indoor room, warm dim lamplight — readable as a scene, not a black cupboard. Visible wood shelves, chairs, and a chalkboard. Soft, low-contrast, slightly hazy. No bright window blowout, no hot spotlight on faces in the dead center.

SUBJECT PLACEMENT: The three boys sit off-center in the little room. Keep the sharpest faces out of the dead center of the page, where the story text will live. Recede them slightly so they are in the room, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band quieter (scuffed floorboards, shadow under chairs). No faces, no glowing props down there. That strip will hold buttons.

DEPTH: shelves and walls recede; the boys are midground.

STORY STILL: One easy frozen moment. They are sitting and talking. They are not standing in a hero lineup and they are not saving a lift in a storm.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any readable letters, numbers, captions, logos, or watermarks. Chalk scribbles must be unreadable marks, not real words.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather). Not photoreal, not anime chibi, not grimdark. Warm bulb light. Medium detail density.

CHARACTER CONSISTENCY: Match the attached NEW cover for Tinker, Meepo, and Rubick — identity only. Same three teenagers. Only one Meepo. Do NOT copy a standing lineup. Do NOT use adult Grand Magus Rubick.

EXCLUDE: readable text, titles, letters, numbers, logos, watermarks, signatures, UI frames, HUD, speech bubbles, storm, rain, hill lift, cable, mine cart, five Meepos, adult Rubick staff, dance hall.

SUBJECT: Room 12, a school cupboard used as a club room. Tiny: a window the size of a book, a sink that does not work, four old chairs, three of them occupied, one empty. The three boys sit in the chairs, chatting and laughing. Tinker holds a small walking gadget or notebook, explaining something, shy smile. Meepo leans in, grinning. Rubick is mid-laugh, head tipped back. On the wall behind them: a chalkboard / pinned board covered in gears, arrows, wiring diagrams, and scribbled formula-shapes that are NOT readable letters or numbers. Tools, bolts, a mug, a broken toaster on the shelves. Warm single bulb. Ordinary and fond — the Machine Club after school, not a rescue. Center of the frame is empty floor and table between them. Bottom edge is scuffed wood.
```

---

## 8. Pudge and the Butcher of Blackwater

Story: `pudge-butcher-of-blackwater.md`
Hero: Pudge. Kind-scary, not splatter.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Pudge identity: huge butcher silhouette, hook, stitches — translated into a school gardener. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Pudge as a huge school gardener, a man as big as a door: stained work clothes, hook and chain hanging at his side, a shy whole-arm wave, almost friendly. Frightening silhouette, kind face in the fat and stitches. No blood spray, no severed parts, no monster behind him. A jam jar in the other hand is fine.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: Overcast afternoon — lighter than night. Visible wet grass, grey sky, stone school, and road. Soft fog, not crushed blacks, not a white blowout. Low-contrast enough for light text, but the whole scene must still read. No hot center spotlight. Texture is low-frequency, painterly, slightly hazy.

SUBJECT PLACEMENT: Stage the scene off-center. Pudge occupies the road the way a person walking does. Keep the sharpest face out of the dead center of the page, where the story text will live.

BOTTOM EDGE: Keep the lowest band quieter (wet track, grass). No faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial.

STORY STILL: This is one frozen moment from the story. Pudge is acting in the scene — walking, looking back — not standing in the cover's product pose. Same painterly Dota 2 look; the change is the acting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Soft overcast daylight, not a dark hallway poster. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover for Pudge's face, huge body, gardener clothes, hook, and colors — identity only. Do NOT copy the cover pose, the raised-hand wave at camera, or the jam jar. This is a different moment of the same man. Kind-scary. No gore, no visible monster body.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, blood, gore, corpses.

SUBJECT: Tuesday. He has been sent away. Overcast afternoon, lighter than night — visible wet grass, grey sky, the stone school on the hill behind him. Pudge is walking DOWN the hill, body in profile / three-quarter from behind, one small half-empty bag in his fist. He looks back over his shoulder at the building, arm half-raised toward the SCHOOL, not toward us. His face is confused and a little hurt, not the cover's toothy grin at camera. Every window of Greywall is packed with small silhouettes who do not wave. Only a tiny girl (Hoodwink) on the boiler-room roof waves back. Hook hangs unused. No jam jar. No students in the same hallway. Horror-comedy heartbreak: the scary man is leaving and nobody will look at him except one kid. Center of the frame is empty road and grey air. Bottom edge is wet track.
```

---

## 9. Juggernaut and the Sixty-One Carvings

Story: `juggernaut-sixty-one-carvings.md`
Hero: Juggernaut (Yurnero). The mask stays on.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Juggernaut identity: wooden mask, katana on the back, island-warrior clothes. Mask stays on. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Juggernaut (Yurnero): large silent swordsman, wooden mask on, katana on his back, island-warrior clothes wet from the sea. In one hand a small carved wooden totem / ward, not a spell VFX logo. Still, dignified, not mid-slash. Mask stays on.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: Overcast coastal daylight — lighter than the other backdrops. Pale grey sky, visible wet sand, wooden hall, beds, and window light. Soft fog, not night, not crushed blacks, not a white blowout. Low-contrast enough for light text, but the whole scene must still read. No hot center spotlight, no large pale faces in the middle. Texture is low-frequency, painterly, slightly hazy.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band quieter (worn floorboards, shadow under beds). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Soft overcast daylight from windows, not a dark rim-light poster. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover for Yurnero's mask, body, clothes, and sword — identity only. Do NOT copy the cover pose. Do NOT have him standing facing camera holding the finished totem up like a product. Mask never comes off. No unmasking, no battle.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles.

SUBJECT: The first totem. Inside the village hall, overcast daylight through windows — a lighter, grey-gold room full of sickbeds. A small girl is sitting up in a bed, coughing, unable to lie down. Yurnero is kneeling beside her, back and side to us, mask on, sword still on his back unused. He is carving a little wooden thing from driftwood with a knife, both hands busy, concentrated, not looking at camera. Villagers crowd the doorway behind him, watching, not understanding. Soft green light has not started yet, or is only just beginning to hum in the wood. He is a nurse in a warrior's body. Quiet and mythic. Center of the frame is empty floorboards between the bed and the door. Bottom edge is worn wood.
```

---

## 10. The Satyr's Strange Dota Match

Story: `riki-the-satyrs-strange-dota-match.md`
Hero: Riki.

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Riki identity: satyr, horns, purple-grey stealth hero. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels.

SUBJECT: Riki the satyr, Dota 2 stealth hero: purple-grey fur, horns, lean trickster. He holds a wooden pub mug in one hand and a light katana (Yasha) in the other, as if he cannot tell which world he is in. Cheeky, confused, not a killer pose. No pub interior, no Ancient, isolated figure. No letters on the sword.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached cover exactly for Riki's face, satyr body, horns, fur, and colors. He is in the pub with a wooden mug. No sword in this frame.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, minimap, item icons, letters on the sword, speech bubbles.

SUBJECT: The gulp that takes him. Dim local pub. Riki the satyr is sitting at a wooden table with friends, mid-gulp from a wooden mug, body just starting to lift off the bench as a blinding white light swallows the room. His eyes are wide, confused. His friends are still laughing, mid-drink, not yet noticing. Tables, mugs, warm lamplight still visible as midtones. The pub is vanishing around them — not a posed satyr holding a mug and a sword in a mixed landscape. No HUD, no minimap, no letters, no Yasha in this frame (he does not have it yet). Comedy isekai. Center of the frame is the table and the blooming light, not a close-up of his face.
```

---

## 11. Invoker and the Three Wisps

Story: `invoker-three-wisps.md`
Hero: young Kael (Invoker as a boy), plus three wisps: Quas (blue), Wex (purple), Exort (red).

### Cover

```text
ROLE: story-picker cover plate for a mobile carousel.
VIEW: three-quarter, full body or knees-up, hero facing slightly toward camera, small amount of padding on all sides so nothing clips.
OUTPUT: isolated character and wisps on a fully transparent background, PNG with real alpha. No ground plane, no floor, no backdrop, no opaque rectangle, no drop shadow. Strong silhouette that still reads at ~200px wide. No text of any kind.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density: costume and face stay sharp.

CHARACTER: Match official Dota 2 Invoker identity translated into a boy (same face, hair, scholar clothes as the approved kid Invoker). The three wisps are the stars of this plate: they are little floating brothers with faces, not faceless spell VFX. No extra original characters unless named.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, phone mockups, carousel chrome, speech bubbles, comic panels, faceless swirls, giant adult orbs, adult Invoker, crown.

WISPS ARE CHARACTERS (must follow):
Each wisp is a small floating orb of light, about the size of an apple, WITH a simple readable face (two eyes and a mouth). They are triplet brothers. Faces must still read at ~200px wide. Not human bodies, not chibi mascots with limbs, not empty energy swirls.
- Exort (red-orange fire): loud, keen, angry; shouting mouth, fierce little eyebrows, sparks. The hothead.
- Wex (purple lightning): fast, teasing, cheerful; wide grin, maybe a wink, motion streaks, never still. The sparky one.
- Quas (blue ice): slow, deep, unimpressed; half-lidded eyes, tiny flat mouth, frost motes, drifting. The tired one.

SUBJECT: Young Kael, unsure, a heavy spellbook under one arm. The three talking wisps crowd him and argue. Exort is in his face, shouting proudly. Wex loops behind his head, grinning, leaving a purple streak. Quas hangs lower, looking done with both of them. Kael is trying to hold them in a loose orbit with his hands, overwhelmed, not casting. The magic is theirs. Young-audience, not grim.
```

### Background

```text
ROLE: full-page story-reader backdrop on a phone. Body text covers most of the image. Only the bottom ~12% is reserved for a Continue button, chapter counter, and previous-chapter control. A slow Ken Burns zoom (in/out) plays underneath.
ASPECT: 9:16 portrait, extra margin on all sides so a 10–15% zoom never clips heads, hands, or signature props.

VALUE AND DETAIL: The whole frame stays dim and low-contrast, like dusk or candlelight — but it must still read as a scene. Walls, furniture, and floor keep visible midtones so a slow zoom still has something to show. Not a black room, not crushed shadows. No bright sky filling the frame, no white walls, no hot center spotlight, no large pale faces in the middle. Texture is low-frequency everywhere — painterly, atmospheric, slightly hazy — because paragraphs will sit on top of every region.

SUBJECT PLACEMENT: Stage the scene off-center. Characters may sit, kneel, lie, or work — they occupy the furniture and the ground the way people do in a room. Keep the sharpest faces and hands out of the dead center of the page, where the story text will live. Recede them slightly so they are in the world, not pasted on top of it.

BOTTOM EDGE: Keep the lowest band even quieter and darker (floor, fog, shadow, wet ground). No bright objects, no faces, no glowing props down there. That strip will hold buttons.

DEPTH: clear foreground / midground / background so a slow zoom still feels spatial, but all three layers stay dim.

STORY STILL: This is one frozen moment from the story, as if a camera caught the characters in the middle of doing something. They are acting in the scene: sitting, talking, working, looking at each other. They are not standing in a product pose next to furniture. Faces show the feeling of that moment. Same painterly Dota 2 look — this is about acting and staging, not about photoreal cinema lighting.

Do NOT paint a UI gradient, black rectangle, fake scrim, vignette bar, or any text, letters, numbers, captions, logos, or watermarks.

ART DIRECTION: Stylized Dota 2 key art, painterly illustration, heroic silhouettes, slightly exaggerated proportions, rich materials (metal, wood, cloth, leather, magic light). Not photoreal, not anime chibi, not grimdark photobash, not Disney cute. Clean readable shapes. Single key light from upper left, colored rim light matching the hero. Medium detail density.

CHARACTER CONSISTENCY: Match the attached NEW cover for young Kael's face, body, clothes, and book, AND for the three wisps' faces and colors. All three brothers appear. He is a boy, not adult Invoker. Do NOT copy the cover's standing carousel pose. Do NOT turn the wisps back into faceless fireballs. Tiny is a second official Dota 2 hero: a huge mossy stone giant with glowing eyes and a wooden tree-club.

EXCLUDE: text, titles, letters, numbers, captions, logos, watermarks, signatures, UI frames, HUD, speech bubbles, faceless energy swirls, adult Invoker, crown, Ogre Magi, two-headed ogre.

WISPS ARE CHARACTERS (must follow):
Each wisp is a small floating orb of light, about the size of an apple, WITH a simple readable face (two eyes and a mouth). Triplet brothers, same faces as the cover.
- Exort (red-orange): shouting, fierce eyebrows, the hothead.
- Wex (purple): grinning, zipping, never still.
- Quas (blue): half-lidded, unimpressed, slow.

SUBJECT: The Tiny fight. Dim canyon path at dusk. Young Kael stands in the lower-left foreground, spellbook under one arm, looking up in wonder (not fear), one hand raised. Behind him a huge Tiny fills the canyon — mossy stone body, glowing eyes, wooden club raised. The three faced wisps harass the giant: Exort shouts near the club, Wex grins by Tiny's shoulder, Quas hangs lower looking done with this. Young-audience scrap, not a raid-boss poster. Center of the frame is empty canyon path between Kael and Tiny. Bottom edge is dark ground and fog. Visible rock and fog midtones so a slow zoom still reads.
```
