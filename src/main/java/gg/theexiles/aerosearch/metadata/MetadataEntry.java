// ? Project: Aero Search
// ? File: MetadataEntry.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/metadata
// ? Description: Curated semantic enrichment record.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 14:16 EDT

package gg.theexiles.aerosearch.metadata;

import java.util.LinkedHashSet;
import java.util.Set;

public final class MetadataEntry {
    public Set<String> aliases = new LinkedHashSet<>();
    public Set<String> styles = new LinkedHashSet<>();
    public Set<String> uses = new LinkedHashSet<>();
    public Set<String> materials = new LinkedHashSet<>();
    public Set<String> shapes = new LinkedHashSet<>();
    public Set<String> keywords = new LinkedHashSet<>();
    public double rankBoost = 1.0;
    public boolean hidden = false;
    public void merge(MetadataEntry other) {
        if (other == null) return;
        aliases.addAll(other.aliases); styles.addAll(other.styles); uses.addAll(other.uses);
        materials.addAll(other.materials); shapes.addAll(other.shapes); keywords.addAll(other.keywords);
        rankBoost *= other.rankBoost; hidden |= other.hidden;
    }
}
