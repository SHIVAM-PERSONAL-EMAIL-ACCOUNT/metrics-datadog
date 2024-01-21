package org.coursera.metrics.datadog;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.coursera.metrics.datadog.TaggedName.TaggedNameBuilder;
import static org.coursera.metrics.datadog.TaggedName.SelectiveTaggedNameBuilder;
import static org.junit.Assert.*;

public class SelectiveTaggedNameBuilderTest {

    @Test(expected = NullPointerException.class)
    public void givenNullAllowedTags_whenBuild_thenThrowNullPointerException() {
        new SelectiveTaggedNameBuilder(null)
                .metricName("metric")
                .build();
    }

    @Test
    public void givenEmptyAllowedTags_whenBuildWithTags_thenReturnTaggedNameWithoutTags() {
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(new HashMap<>())
                .metricName("metric");

        builder.addTag("k1", "v1");
        builder.addTag("k2", "v2");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTag_whenBuildWithNotAllowedTagKey_thenReturnTaggedNameWithoutTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k2", "v2");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTag_whenBuildWithNotAllowedTagValue_thenReturnTaggedNameWithoutTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k1", "v2");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTags_whenBuildWithAllowedTag_thenReturnTaggedNameWithTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k1", "v1");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertEquals(1, taggedName.getEncodedTags().size());
        assertEquals("k1:v1", taggedName.getEncodedTags().get(0));
    }

    @Test
    public void givenEmptyAllowedTags_whenBuildWithEncodedTags_thenReturnTaggedNameWithoutTags() {
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(new HashMap<>())
                .metricName("metric");

        builder.addTag("k1:v1");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTag_whenBuildWithNotAllowedEncodedTagKey_thenReturnTaggedNameWithoutTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k2:v2");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTag_whenBuildWithNotAllowedEncodedTagValue_thenReturnTaggedNameWithoutTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k1:v2");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertTrue(taggedName.getEncodedTags().isEmpty());
    }

    @Test
    public void givenAllowedTags_whenBuildWithEncodedAllowedTag_thenReturnTaggedNameWithTags() {
        Map<String,String> allowedTags = new HashMap<>();
        allowedTags.put("k1", "v1");
        TaggedNameBuilder builder = new SelectiveTaggedNameBuilder(allowedTags)
                .metricName("metric");

        builder.addTag("k1:v1");

        TaggedName taggedName = builder.build();
        assertEquals("metric", taggedName.getMetricName());
        assertEquals(1, taggedName.getEncodedTags().size());
        assertEquals("k1:v1", taggedName.getEncodedTags().get(0));
    }
}
