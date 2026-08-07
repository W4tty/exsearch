// ? Project: Aero Search
// ? File: MetadataRepository.java
// ? Directory: /src/main/java/gg/theexiles/aerosearch/metadata
// ? Description: Loads bundled metadata and local overrides without network access.
// ? Created by: Watty
// ? Created on: 2026-08-07 14:16 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 15:02 EDT

package gg.theexiles.aerosearch.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import gg.theexiles.aerosearch.AeroSearch;
import net.neoforged.fml.loading.FMLPaths;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class MetadataRepository {
    private static final Gson GSON = new Gson();
    private final Map<String, MetadataEntry> entries = new HashMap<>();
    public void reload() { entries.clear(); loadBundled(); loadLocal(); }
    public MetadataEntry get(String id) { return entries.getOrDefault(id, new MetadataEntry()); }
    public Map<String, MetadataEntry> snapshot() { return Collections.unmodifiableMap(entries); }
    private void loadBundled() {
        try (InputStream in = MetadataRepository.class.getResourceAsStream("/assets/aerosearch/metadata/bundled.json")) {
            if (in == null) return;
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) { mergeDocument(GSON.fromJson(reader, JsonObject.class)); }
        } catch (Exception e) { AeroSearch.LOGGER.warn("Could not load bundled metadata", e); }
    }
    private void loadLocal() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("aerosearch"), file = dir.resolve("metadata.json");
        try {
            Files.createDirectories(dir);
            if (!Files.exists(file)) Files.writeString(file, "{\n  \"schema\": 1,\n  \"entries\": {}\n}\n", StandardCharsets.UTF_8);
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) { mergeDocument(GSON.fromJson(reader, JsonObject.class)); }
        } catch (IOException e) { AeroSearch.LOGGER.warn("Could not load local metadata override {}", file, e); }
    }
    private void mergeDocument(JsonObject root) {
        if (root == null || !root.has("entries")) return;
        var type = new TypeToken<Map<String, MetadataEntry>>() {}.getType();
        Map<String, MetadataEntry> incoming = GSON.fromJson(root.get("entries"), type);
        if (incoming != null) incoming.forEach((id, entry) -> entries.computeIfAbsent(id, ignored -> new MetadataEntry()).merge(entry));
    }
}
