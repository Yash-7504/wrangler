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

 import org.junit.Assert;
 import org.junit.Test;
 
 public class TimeDurationTest {
 
   @Test
   public void testValidTimeDurations() {
     // Test nanoseconds
     TimeDuration ns = new TimeDuration("500ns");
     Assert.assertEquals(500, ns.getNanos());
     
     // Test milliseconds
     TimeDuration ms = new TimeDuration("200ms");
     Assert.assertEquals(200 * 1_000_000, ms.getNanos());
     
     // Test seconds
     TimeDuration s = new TimeDuration("10s");
     Assert.assertEquals(10 * 1_000_000_000L, s.getNanos());
     
     // Test minutes
     TimeDuration m = new TimeDuration("5m");
     Assert.assertEquals(5 * 60 * 1_000_000_000L, m.getNanos());
     
     // Test hours
     TimeDuration h = new TimeDuration("2h");
     Assert.assertEquals(2 * 60 * 60 * 1_000_000_000L, h.getNanos());
     
     // Test decimal values
     TimeDuration d = new TimeDuration("1.5d");
     Assert.assertEquals((long)(1.5 * 24 * 60 * 60 * 1_000_000_000L), d.getNanos());
   }
   
   @Test
   public void testUnitConversion() {
     TimeDuration s = new TimeDuration("30s");
     
     // Convert to different units
     Assert.assertEquals(30 * 1_000_000_000L, s.to("ns"), 0.001);
     Assert.assertEquals(30 * 1000, s.to("ms"), 0.001);
     Assert.assertEquals(30, s.to("s"), 0.001);
     Assert.assertEquals(0.5, s.to("m"), 0.001);
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testInvalidFormat() {
     new TimeDuration("invalid");
   }
 }