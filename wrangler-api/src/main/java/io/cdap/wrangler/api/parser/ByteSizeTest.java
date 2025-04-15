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

public class ByteSizeTest {

  @Test
  public void testValidByteSizes() {
    // Test bytes
    ByteSize bytes = new ByteSize("100B");
    Assert.assertEquals(100, bytes.getBytes());
    
    // Test kilobytes
    ByteSize kb = new ByteSize("2KB");
    Assert.assertEquals(2 * 1024, kb.getBytes());
    
    // Test megabytes
    ByteSize mb = new ByteSize("3MB");
    Assert.assertEquals(3 * 1024 * 1024, mb.getBytes());
    
    // Test gigabytes
    ByteSize gb = new ByteSize("1.5GB");
    Assert.assertEquals((long)(1.5 * 1024 * 1024 * 1024), gb.getBytes());
    
    // Test case insensitivity
    ByteSize kb2 = new ByteSize("5kb");
    Assert.assertEquals(5 * 1024, kb2.getBytes());
  }
  
  @Test
  public void testUnitConversion() {
    ByteSize mb = new ByteSize("5MB");
    
    // Convert to different units
    Assert.assertEquals(5 * 1024 * 1024, mb.to("B"), 0.001);
    Assert.assertEquals(5 * 1024, mb.to("KB"), 0.001);
    Assert.assertEquals(5, mb.to("MB"), 0.001);
    Assert.assertEquals(5 / 1024.0, mb.to("GB"), 0.001);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat() {
    new ByteSize("invalid");
  }
}