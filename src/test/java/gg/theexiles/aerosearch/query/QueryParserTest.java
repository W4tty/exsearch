// ? Project: ExSearch
// ? File: QueryParserTest.java
// ? Directory: /src/test/java/gg/theexiles/aerosearch/query
// ? Description: Regression tests for advanced and color-theory query parsing.
// ? Created by: Watty
// ? Created on: 2026-08-07 16:55 EDT
// ? Last modified by: Watty
// ? Last modified on: 2026-08-07 16:55 EDT

package gg.theexiles.aerosearch.query;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueryParserTest {
    @Test
    void parsesHarmonyModeSeedAndRole() {
        Query q = QueryParser.parse("harmony:triadic:minecraft:cyan_concrete role:floor");
        assertEquals(HarmonyMode.TRIADIC, q.harmonyMode);
        assertEquals("minecraft:cyan_concrete", q.harmonySeed);
        assertEquals("floor", q.harmonyRole);
    }

    @Test
    void defaultsHarmonyToAutoWhenOnlyRegistryIdIsGiven() {
        Query q = QueryParser.parse("harmony:create:brass_casing");
        assertEquals(HarmonyMode.AUTO, q.harmonyMode);
        assertEquals("create:brass_casing", q.harmonySeed);
    }

    @Test
    void retainsExistingAdvancedFilters() {
        Query q = QueryParser.parse("@create color:gray shape:panel -copper light:>10");
        assertEquals(4, q.terms.size());
        assertEquals(1, q.excluded.size());
        assertTrue(q.terms.get(3).comparison().test(11));
    }
}
