<!--
? Project: ExSearch
? File: README.md
? Directory: /
? Description: User and developer guide for ExSearch.
? Created by: Watty
? Created on: 2026-08-07 16:33 EDT
? Last modified by: Watty
? Last modified on: 2026-08-07 16:33 EDT
-->

# ExSearch

**A builder-first Creative inventory search engine for Minecraft Java 1.21.1 / NeoForge.**

ExSearch upgrades Minecraft's normal **Creative → Search Items** tab instead of adding another item browser. It is designed for large modpacks where finding the right block by name alone is not enough.

JEI can remain installed for recipes and usages. ExSearch focuses on finding blocks and items quickly while building.

## Requirements

- Minecraft Java Edition **1.21.1**
- NeoForge **21.1.x** (built/tested with **21.1.234**)
- Java **21**
- Client side only

## Installation

1. Download `exsearch-1.0.0.jar` from the GitHub release.
2. Put it in the client's `mods` folder.
3. Start Minecraft and open Creative inventory → Search Items.
4. The server does not need ExSearch installed.

## Search examples

```text
@create
@create brass pipe
#create:upright_on_belt
$tooltip
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
source:vanilla
source:modded
@create color:gray shape:panel -copper
"reinforced glass"
white futuristic wall panels
dark industrial floor
cyan glowing light
similar:create:brass_casing
palette:create:brass_casing,minecraft:polished_deepslate
```

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
| `similar:` | Find visually/semantically similar content | `similar:create:brass_casing` |
| `palette:` | Find blocks that work with selected seeds | `palette:id1,id2` |
| `-` | Exclude a term/filter | `@create -copper` |
| `"..."` | Exact phrase token | `"reinforced glass"` |

Plain text also uses fuzzy matching, so searches do not have to use the advanced syntax.

## Builder-oriented search

ExSearch automatically classifies installed content into useful concepts such as:

- colors and dominant visual colors
- metals, stone, glass, wood, concrete, fabric and other materials
- panels, pipes, ducts, cables, slabs, stairs, walls, doors, lights and full blocks
- walls, floors, trims, storage, machines and lighting
- industrial, sci-fi, cyberpunk, clean and weathered/rusty styles

These classifications are inferred at runtime and can be enriched with bundled or local metadata.

## Color and visual indexing

For blocks with usable baked-model sprites, ExSearch samples the texture currently loaded by Minecraft. This means color search can follow the player's active resource packs rather than relying only on an item's name.

Color matching uses perceptual CIE Lab distance instead of simple RGB distance. When visual sampling is unavailable, ExSearch falls back safely to Minecraft map color and semantic inference.

## Similarity search

Use:

```text
similar:minecraft:iron_block
```

Similarity ranking combines color, material, shape, use/style categories and relevant block properties. The seed itself is excluded from the results.

Middle-click support in the Creative Search tab can also start a similarity search from the block under the cursor.

## Palette search

Use:

```text
palette:minecraft:white_concrete,minecraft:iron_block
```

Palette search looks for useful companion blocks rather than just duplicates. It considers visual contrast, nearby colors, materials, styles and building uses.

Shift + middle-click can add/remove palette seeds while browsing Creative Search.

## Autocomplete and controls

- Prefix-aware suggestions for mods, tags, colors, materials, shapes, uses and styles.
- `Ctrl + Space` accepts the top suggestion.
- `F6` rebuilds the runtime search index.
- `F7` toggles the ExSearch help/suggestion overlay.
- Search history is retained for the current session.

## Live modpack discovery

ExSearch does **not** require a hardcoded list of installed mods.

The live Minecraft item/block registries are the source of truth. New or removed mods are discovered from the actual client. The bundled ExilesMC metadata is only an enrichment layer and never determines whether an item exists.

This allows ExSearch to work if the ExilesMC pack changes and also makes the core suitable for other NeoForge packs.

## No GitHub/network dependency

Minecraft does not contact GitHub to perform searches or build its index.

ExSearch ships its bundled metadata inside the JAR and supports optional local overrides. Search and learning are local to the client.

## Local metadata overrides

Curated metadata can be added without recompiling ExSearch:

```text
config/aerosearch/metadata.json
```

Example:

```json
{
  "schema": 1,
  "entries": {
    "create:brass_casing": {
      "aliases": ["brass panel", "machine wall"],
      "styles": ["industrial", "scifi"],
      "uses": ["wall", "machine"],
      "materials": ["metal", "brass"],
      "shapes": ["fullblock", "panel"],
      "keywords": ["factory"],
      "rankBoost": 1.25,
      "hidden": false
    }
  }
}
```

## Local adaptive ranking

ExSearch can locally boost items selected for repeated searches. This information remains on the client; no telemetry or search history is sent to a service.

## Compatibility philosophy

ExSearch owns non-empty queries in Minecraft's native Creative Search tab. It does not replace JEI's recipe/usage interface and does not require JEI, Searchables, Quark or Inventory Profiles Next.

## Building from source

Clone the repository and build with Java 21:

```bash
gradle clean build
```

The production JAR is generated under:

```text
build/libs/
```

GitHub Actions also builds every pushed revision to verify the NeoForge project compiles.

## Project status

`v1.0.0` is the first production build of ExSearch. The project currently targets Minecraft 1.21.1 / NeoForge 21.1.234.

## License

All Rights Reserved unless the repository license is changed in a future release.
