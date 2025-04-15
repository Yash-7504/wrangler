/*
 *  Copyright © 2024 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a time duration token with unit (e.g., "10ms", "1.5s").
 */
public class TimeDuration extends Token {
  private static final Pattern PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(ns|ms|s|m|h|d)");
  private final double value;
  private final String unit;

  public TimeDuration(String text) {
    super(text);
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time duration format: " + text);
    }
    this.value = Double.parseDouble(matcher.group(1));
    this.unit = matcher.group(2);
  }

  /**
   * Gets the value in nanoseconds.
   */
  public long getNanos() {
    switch (unit) {
      case "ns":
        return (long) value;
      case "ms":
        return (long) (value * 1_000_000);
      case "s":
        return (long) (value * 1_000_000_000);
      case "m":
        return (long) (value * 60 * 1_000_000_000L);
      case "h":
        return (long) (value * 60 * 60 * 1_000_000_000L);
      case "d":
        return (long) (value * 24 * 60 * 60 * 1_000_000_000L);
      default:
        return (long) value; // Default to nanoseconds
    }
  }

  /**
   * Convert to specified unit.
   */
  public double to(String targetUnit) {
    long nanos = getNanos();
    switch (targetUnit) {
      case "ns":
        return nanos;
      case "ms":
        return nanos / 1_000_000.0;
      case "s":
        return nanos / 1_000_000_000.0;
      case "m":
        return nanos / (60.0 * 1_000_000_000L);
      case "h":
        return nanos / (60.0 * 60 * 1_000_000_000L);
      case "d":
        return nanos / (24.0 * 60 * 60 * 1_000_000_000L);
      default:
        throw new IllegalArgumentException("Unsupported unit: " + targetUnit);
    }
  }

  /**
   * Get the original value as entered.
   */
  public double getValue() {
    return value;
  }

  /**
   * Get the original unit as entered.
   */
  public String getUnit() {
    return unit;
  }
}