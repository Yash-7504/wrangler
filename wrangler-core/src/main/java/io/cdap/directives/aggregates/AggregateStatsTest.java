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

 package io.cdap.wrangler.directive;

 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.parser.ByteSize;
 import io.cdap.wrangler.api.parser.TimeDuration;
 import io.cdap.wrangler.test.TestingRig;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.ArrayList;
 import java.util.List;
 
 public class AggregateStatsTest {
 
   @Test
   public void testAggregateStats() throws Exception {
     // Create test data
     List<Row> rows = new ArrayList<>();
     
     Row row1 = new Row();
     row1.setValue("data_size", new ByteSize("10MB"));
     row1.setValue("response_time", new TimeDuration("500ms"));
     rows.add(row1);
     
     Row row2 = new Row();
     row2.setValue("data_size", new ByteSize("5MB"));
     row2.setValue("response_time", new TimeDuration("1.5s"));
     rows.add(row2);
     
     Row row3 = new Row();
     row3.setValue("data_size", new ByteSize("2.5GB"));
     row3.setValue("response_time", new TimeDuration("50ms"));
     rows.add(row3);
     
     // Define recipe
     String[] recipe = new String[] {
       "aggregate-stats :data_size :response_time total_size_mb total_time_sec MB s"
     };
     
     // Execute recipe
     List<Row> results = TestingRig.execute(recipe, rows);
     
     // Verify results
     Assert.assertEquals(1, results.size());
     
     Row result = results.get(0);
     
     // Expected total size: 10MB + 5MB + 2.5GB = 2,575MB
     double expectedTotalSizeInMB = 10 + 5 + (2.5 * 1024);
     Assert.assertEquals(expectedTotalSizeInMB, result.getValue("total_size_mb"), 0.001);
     
     // Expected total time: 500ms + 1.5s + 50ms = 2.05s
     double expectedTotalTimeInSeconds = 0.5 + 1.5 + 0.05;
     Assert.assertEquals(expectedTotalTimeInSeconds, result.getValue("total_time_sec"), 0.001);
   }
   
   @Test
   public void testAggregateStatsWithStringValues() throws Exception {
     // Test with string representations instead of ByteSize/TimeDuration objects
     List<Row> rows = new ArrayList<>();
     
     Row row1 = new Row();
     row1.setValue("data_size", "10MB");
     row1.setValue("response_time", "500ms");
     rows.add(row1);
     
     Row row2 = new Row();
     row2.setValue("data_size", "5MB");
     row2.setValue("response_time", "1.5s");
     rows.add(row2);
     
     // Define recipe
     String[] recipe = new String[] {
       "aggregate-stats :data_size :response_time total_size_kb total_time_ms KB ms"
     };
     
     // Execute recipe
     List<Row> results = TestingRig.execute(recipe, rows);
     
     // Verify results
     Assert.assertEquals(1, results.size());
     
     Row result = results.get(0);
     
     // Expected total size in KB: (10MB + 5MB) * 1024 = 15,360KB
     double expectedTotalSizeInKB = (10 + 5) * 1024;
     Assert.assertEquals(expectedTotalSizeInKB, result.getValue("total_size_kb"), 0.001);
     
     // Expected total time in ms: 500ms + 1.5s = 2000ms
     double expectedTotalTimeInMS = 500 + (1.5 * 1000);
     Assert.assertEquals(expectedTotalTimeInMS, result.getValue("total_time_ms"), 0.001);
   }
 }