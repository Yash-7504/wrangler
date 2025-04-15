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
  * Represents a byte size token with unit (e.g., "10KB", "1.5MB").
  */
 public class ByteSize extends Token {
   private static final Pattern PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(b|B|kb|KB|mb|MB|gb|GB|tb|TB|pb|PB)");
   private final double value;
   private final String unit;
 
   public ByteSize(String text) {
     super(text);
     Matcher matcher = PATTERN.matcher(text);
     if (!matcher.matches()) {
       throw new IllegalArgumentException("Invalid byte size format: " + text);
     }
     this.value = Double.parseDouble(matcher.group(1));
     this.unit = matcher.group(2).toLowerCase();
   }
 
   /**
    * Gets the value in bytes.
    */
   public long getBytes() {
     switch (unit) {
       case "b":
         return (long) value;
       case "kb":
         return (long) (value * 1024);
       case "mb":
         return (long) (value * 1024 * 1024);
       case "gb":
         return (long) (value * 1024 * 1024 * 1024);
       case "tb":
         return (long) (value * 1024L * 1024L * 1024L * 1024L);
       case "pb":
         return (long) (value * 1024L * 1024L * 1024L * 1024L * 1024L);
       default:
         return (long) value; // Default to bytes
     }
   }
 
   /**
    * Convert to specified unit.
    */
   public double to(String targetUnit) {
     long bytes = getBytes();
     switch (targetUnit.toLowerCase()) {
       case "b":
         return bytes;
       case "kb":
         return bytes / 1024.0;
       case "mb":
         return bytes / (1024.0 * 1024.0);
       case "gb":
         return bytes / (1024.0 * 1024.0 * 1024.0);
       case "tb":
         return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
       case "pb":
         return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
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