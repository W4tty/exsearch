<!--
? Project: ExSearch
? File: README.md
? Directory: /
? Description: User and developer guide for ExSearch.
? Created by: Watty
? Created on: 2026-08-07 16:33 EDT
? Last modified by: Watty
? Last modified on: 2026-08-07 16:55 EDT
-->

# ExSearch

**A builder-first Creative inventory search engine for Minecraft Java 1.21.1 / NeoForge.**

ExSearch upgrades Minecraft's normal **Creative → Search Items** tab instead of adding another item browser. JEI can remain installed for recipes and usages; ExSearch focuses on finding the right blocks and items fast in large modpacks.

## Requirements

- Minecraft Java Edition **1.21.1**
- NeoForge **21.1.x** (built/tested with **21.1.234**)
- Java **21**
- Client side only

## Installation

1. Download `exsearch-1.1.0.jar` from Releases.
2. Put it in the client's `mods` folder.
3. Open Creative inventory → Search Items.
4. The server does not need ExSearch.

## Core search examples

```text
@create
@create brass pipe
#create:upright_on_belt
$energy
&create:brass_casing
color:cyan
color:#18d7e8
material:metal
shape:panel
use:storage
style:industrial
light:>10
transparent:true
waterloggable:true
redstone:true
fullblock:true
slab:true
stairs:true
wall:true
emissive:true
source:modded
@create color:gray shape:panel -copper
"reinforced glass"
white futuristic wall panels
similar:create:brass_casing
palette:create:brass_casing,minecraft:polished_deepslate
```

## Color Theory Palette search

ExSearch 1.1 adds real color-theory harmony ranking based on the **actual rendered color** of the selected block, not only its name. The engine uses HSV hue relationships for harmony logic and CIE Lab/lightness data for perceptual ranking, then mixes in material/style/shape information so the result is useful for building.

Syntax:

```text
harmony:<mode>:<registry_id>
```

Examples:

```text
harmony:auto:create:brass_casing
harmony:monochromatic:minecraft:cyan_concrete
harmony:analogous:minecraft:blue_concrete
harmony:complementary:create:brass_casing
harmony:split:minecraft:cyan_concrete
harmony:triadic:minecraft:orange_concrete
harmony:tetradic:minecraft:purple_concrete
harmony:neutral:minecraft:red_concrete
harmony:accent:minecraft:light_gray_concrete
```

Supported harmony modes:

- **auto** — picks a useful mode automatically; low-saturation seed blocks favor neutral support, saturated blocks favor complementary matching.
- **monochromatic** — same hue family with useful light/dark variation.
- **analogous** — neighboring hues around the seed color.
- **complementary** — approximately 180° opposite on the color wheel.
- **split complementary** — two colors around the direct complement for softer contrast.
- **triadic** — roughly 120° / 240° hue relationships.
- **tetradic** — four-color rectangular harmony.
- **neutral** — low-saturation supporting whites, grays, blacks, metals and similar blocks.
- **accent** — stronger high-saturation contrast intended for trim, lighting and detail work.

### Builder roles

Add a role filter to make color theory answer the question you actually have:

```text
harmony:complementary:create:brass_casing role:floor
harmony:analogous:minecraft:cyan_concrete role:wall
harmony:accent:minecraft:white_concrete role:light
harmony:auto:minecraft:deepslate_tiles role:trim
```

Supported roles include `any`, `wall`, `floor`, `trim`, `accent`, `light`, `panel`, `glass`, `storage`, and `machine`. Roles use ExSearch's live semantic index, so modded blocks can participate even when they were never manually curated.

## Search language

| Syntax | Purpose | Example |
| --- | --- | --- |
| `@` | Mod / namespace | `@create` |
| `#` | Item or block tag | `#c:ingots` |
| `$` | Tooltip text | `$energy` |
| `&` | Registry ID | `&create:brass_casing` |
| `color:` | Visual color | `color:cyan` or `color:#18d7e8` |
| `material:` | Material family | `material:metal` |
| `shape:` | Physical/building shape | `shape:panel` |
| `use:` | Builder use | `use:floor` |
| `style:` | Visual/build style | `style:industrial` |
| `light:` | Light level comparison | `light:>10` |
| `hardness:` | Hardness comparison | `hardness:>=3` |
| `blast:` | Blast resistance comparison | `blast:>6` |
| `source:` | Vanilla or modded | `source:modded` |
| `similar:` | Visual/semantic similarity | `similar:create:brass_casing` |
| `palette:` | Multi-seed companion palette | `palette:id1,id2` |
| `harmony:` | Color-theory palette | `harmony:triadic:minecraft:cyan_concrete` |
| `role:` | Restrict harmony results by build purpose | `role:floor` |
| `-` | Exclude term/filter | `@create -copper` |
| `"..."` | Phrase token | `"reinforced glass"` |

Plain words also use fuzzy matching and semantic aliases.

## How indexing works

The live Minecraft item/block registries are always the source of truth. ExSearch scans what is actually installed, then enriches entries with names, namespaces, tags, tooltips, block properties, semantic categories and visual data.

For usable baked block models, ExSearch samples the currently loaded particle sprite. This means color search and harmony ranking can follow active resource packs. If sprite sampling is unavailable, it falls back to Minecraft map color and semantic inference.

The bundled ExilesMC metadata is an optional enrichment layer only. Minecraft does **not** contact GitHub at runtime, and new or removed mods are discovered locally.

## Similarity and palette modes

`similar:<id>` ranks visually and semantically similar blocks using perceptual color, material, shape, style, use and block properties.

`palette:<id,id,...>` finds companion blocks across multiple selected seeds. Color-theory `harmony:` is different: it deliberately targets established color-wheel relationships from one seed block.

## Autocomplete and controls

- Prefix-aware suggestions for mods, tags, colors, materials, shapes, uses, styles, harmony modes and roles.
- `Ctrl + Space` accepts the top suggestion.
- `F6` rebuilds the runtime search index.
- `F7` toggles the ExSearch help/suggestion overlay.
- Middle-click on a Creative Search result starts similarity search.
- Shift + middle-click adds/removes palette seeds.

## Local metadata overrides

Curated metadata can be added without recompiling:

```text
config/aerosearch/metadata.json
```

Runtime registry discovery still wins: metadata cannot make a missing block exist.

## Privacy

Search, visual indexing, metadata merging and adaptive ranking happen locally. ExSearch does not send search history or telemetry to a service.

## Building from source

```bash
gradle clean build
```

The JAR is generated in `build/libs/`. GitHub Actions compiles every pushed revision against the NeoForge project configuration.

## Release history

### v1.1.0

- Added color-theory harmony search.
- Added auto, monochromatic, analogous, complementary, split-complementary, triadic, tetradic, neutral and accent modes.
- Added builder-role filtering for walls, floors, trims, accents, lights, panels, glass, storage and machines.
- Added harmony/role autocomplete.
- Kept live registry/resource-pack indexing as the source of truth.

### v1.0.0

Initial production release with JEI-style prefixes, semantic search, fuzzy matching, visual/color indexing, similarity search, palette search and local adaptive ranking.

## License

All Rights Reserved unless the repository license changes in a future release.
