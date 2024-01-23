package org.coursera.metrics.datadog;

import java.lang.*;
import java.lang.Object;
import java.lang.Override;
import java.lang.StringBuilder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaggedName {
  private static final Pattern tagPattern = Pattern
      .compile("([\\w\\.-]+)\\[([\\w\\W]+)\\]");

  private final String metricName;
  private final List<String> encodedTags;

  private TaggedName(String metricName, List<String> encodedTags) {
    this.metricName = metricName;
    this.encodedTags = encodedTags;
  }

  public String getMetricName() {
    return metricName;
  }

  public List<String> getEncodedTags() {
    return encodedTags;
  }

  public String encode() {
    if (!encodedTags.isEmpty()) {
      StringBuilder sb = new StringBuilder(this.metricName);
      sb.append('[');
      String prefix = "";
      for (String encodedTag : encodedTags) {
        sb.append(prefix);
        sb.append(encodedTag);
        prefix = ",";
      }
      sb.append(']');
      return sb.toString();
    } else {
      return this.metricName;
    }
  }

  public static TaggedName decode(String encodedTaggedName) {
    TaggedNameBuilder builder = new TaggedNameBuilder();

    Matcher matcher = tagPattern.matcher(encodedTaggedName);
    if (matcher.find() && matcher.groupCount() == 2) {
      builder.metricName(matcher.group(1));
      for(String t : matcher.group(2).split("\\,")) {
        builder.addTag(t);
      }
    } else {
      builder.metricName(encodedTaggedName);
    }

    return builder.build();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TaggedName that = (TaggedName) o;

    if (metricName != null ? !metricName.equals(that.metricName) : that.metricName != null) return false;
    if (encodedTags != null ? !encodedTags.equals(that.encodedTags) : that.encodedTags != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = metricName != null ? metricName.hashCode() : 0;
    result = 31 * result + (encodedTags != null ? encodedTags.hashCode() : 0);
    return result;
  }

  public static class TaggedNameBuilder {
    private String metricName;
    private final List<String> encodedTags = new ArrayList<String>();

    public TaggedNameBuilder metricName(String metricName) {
      this.metricName = metricName;
      return this;
    }

    public TaggedNameBuilder addTag(String key, String val) {
      assertNonEmpty(key, "tagKey");
      encodedTags.add(new StringBuilder(key).append(':').append(val).toString());
      return this;
    }

    public TaggedNameBuilder addTag(String encodedTag) {
      assertNonEmpty(encodedTag, "encodedTag");
      encodedTags.add(encodedTag);
      return this;
    }

    protected void assertNonEmpty(String s, String field) {
      if (s == null || "".equals(s.trim())) {
        throw new IllegalArgumentException((field + " must be defined"));
      }
    }

    public TaggedName build() {
      assertNonEmpty(this.metricName, "metricName");

      return new TaggedName(this.metricName, this.encodedTags);
    }
  }

  public static class SelectiveTaggedNameBuilder extends TaggedNameBuilder {

    private static final String NOT_ALLOWED = "";

    private final Map<String,String> allowedTags;

    public SelectiveTaggedNameBuilder(Map<String,String> allowedTags) {
      Objects.requireNonNull(allowedTags);
      this.allowedTags = allowedTags;
    }

    @Override
    public TaggedNameBuilder addTag(String key, String val) {
      assertNonEmpty(key, "key");
      return allowed(key,val) ? super.addTag(key,val) : this;
    }

    private boolean allowed(String key, String val) {
      return val.equals(allowedTags.computeIfAbsent(key, (ignore) -> NOT_ALLOWED));
    }

    @Override
    public TaggedNameBuilder addTag(String encodedTag) {
      assertNonEmpty(encodedTag, "encodedTag");
      return allowed(encodedTag) ? super.addTag(encodedTag) : this;
    }

    private boolean allowed(String encodedTag) {
      return allowedTags
              .entrySet()
              .stream()
              .map(e -> e.getKey() + ":" + e.getValue())
              .anyMatch(encodedTag::equals);
    }
  }
}
