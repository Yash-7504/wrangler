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

 package io.cdap.wrangler.api.directive;

 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.parser.ByteSize;
 import io.cdap.wrangler.api.parser.ColumnName;
 import io.cdap.wrangler.api.parser.Text;
 import io.cdap.wrangler.api.parser.TimeDuration;
 import io.cdap.wrangler.api.parser.TokenType;
 import io.cdap.wrangler.api.parser.UsageDefinition;
 
 import java.util.List;
 
 /**
  * A directive for aggregating size and time statistics.
  */
 public class AggregateStats implements Directive, Aggregator {
   private String sizeColumnName;
   private String timeColumnName;
   private String totalSizeColumnName;
   private String totalTimeColumnName;
   private String sizeUnit = "MB"; // Default output unit
   private String timeUnit = "s"; // Default output unit
   
   private long totalBytes = 0;
   private long totalNanos = 0;
   private int rowCount = 0;
   
   @Override
   public UsageDefinition define() {
     UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
     builder.define("source_size_column", TokenType.COLUMN);
     builder.define("source_time_column", TokenType.COLUMN);
     builder.define("target_size_column", TokenType.COLUMN);
     builder.define("target_time_column", TokenType.COLUMN);
     builder.define("size_unit", TokenType.TEXT, Optional.TRUE);
     builder.define("time_unit", TokenType.TEXT, Optional.TRUE);
     return builder.build();
   }
   
   @Override
   public void initialize(Arguments args) throws DirectiveParseException {
     this.sizeColumnName = ((ColumnName) args.value("source_size_column")).value();
     this.timeColumnName = ((ColumnName) args.value("source_time_column")).value();
     this.totalSizeColumnName = ((ColumnName) args.value("target_size_column")).value();
     this.totalTimeColumnName = ((ColumnName) args.value("target_time_column")).value();
     
     if (args.contains("size_unit")) {
       this.sizeUnit = ((Text) args.value("size_unit")).value();
     }
     
     if (args.contains("time_unit")) {
       this.timeUnit = ((Text) args.value("time_unit")).value();
     }
   }
   
   @Override
   public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
     // Use the context to store aggregation values between method calls
     Object bytesObj = context.getTransientStore().get("totalBytes");
     Object nanosObj = context.getTransientStore().get("totalNanos");
     Object countObj = context.getTransientStore().get("rowCount");
     
     // Initialize or retrieve values from store
     long bytes = bytesObj != null ? (Long) bytesObj : 0L;
     long nanos = nanosObj != null ? (Long) nanosObj : 0L;
     int count = countObj != null ? (Integer) countObj : 0;
     
     // Process each row
     for (Row row : rows) {
       try {
         // Process size column
         Object sizeObj = row.getValue(sizeColumnName);
         if (sizeObj != null) {
           ByteSize byteSize;
           if (sizeObj instanceof ByteSize) {
             byteSize = (ByteSize) sizeObj;
           } else {
             byteSize = new ByteSize(sizeObj.toString());
           }
           bytes += byteSize.getBytes();
         }
         
         // Process time column
         Object timeObj = row.getValue(timeColumnName);
         if (timeObj != null) {
           TimeDuration timeDuration;
           if (timeObj instanceof TimeDuration) {
             timeDuration = (TimeDuration) timeObj;
           } else {
             timeDuration = new TimeDuration(timeObj.toString());
           }
           nanos += timeDuration.getNanos();
         }
         
         count++;
       } catch (Exception e) {
         // Skip rows with parsing errors
         context.getLogger().warn("Error processing row: " + e.getMessage());
       }
     }
     
     // Store updated values
     context.getTransientStore().put("totalBytes", bytes);
     context.getTransientStore().put("totalNanos", nanos);
     context.getTransientStore().put("rowCount", count);
     
     // If this is the last batch, produce the final row
     if (context.isLast()) {
       Row resultRow = new Row();
       
       // Convert bytes to requested unit
       double sizeValue;
       switch (sizeUnit.toLowerCase()) {
         case "b": sizeValue = bytes; break;
         case "kb": sizeValue = bytes / 1024.0; break;
         case "mb": sizeValue = bytes / (1024.0 * 1024.0); break;
         case "gb": sizeValue = bytes / (1024.0 * 1024.0 * 1024.0); break;
         case "tb": sizeValue = bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0); break;
         default: sizeValue = bytes / (1024.0 * 1024.0); // Default to MB
       }
       
       // Convert nanoseconds to requested unit
       double timeValue;
       switch (timeUnit.toLowerCase()) {
         case "ns": timeValue = nanos; break;
         case "ms": timeValue = nanos / 1_000_000.0; break;
         case "s": timeValue = nanos / 1_000_000_000.0; break;
         case "m": timeValue = nanos / (60.0 * 1_000_000_000); break;
         case "h": timeValue = nanos / (60.0 * 60 * 1_000_000_000L); break;
         default: timeValue = nanos / 1_000_000_000.0; // Default to seconds
       }
       
       resultRow.setValue(totalSizeColumnName, sizeValue);
       resultRow.setValue(totalTimeColumnName, timeValue);
       
       return Collections.singletonList(resultRow);
     }
     
     // Return empty list for intermediate batches
     return Collections.emptyList();
   }
 
   @Override
   public void destroy() {
    
   }
 }
