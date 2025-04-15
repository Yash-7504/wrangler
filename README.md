# Wrangler Byte Size and Time Duration Parsers

This enhancement adds support for parsing and utilizing byte size and time duration units within Wrangler recipes.

## New Parsers

### Byte Size Parser

The Byte Size parser supports the following units:
- b, B (bytes)
- kb, KB (kilobytes)
- mb, MB (megabytes)
- gb, GB (gigabytes)
- tb, TB (terabytes)
- pb, PB (petabytes)

Example values: "10KB", "1.5MB", "2GB"

### Time Duration Parser

The Time Duration parser supports the following units:
- ns (nanoseconds)
- ms (milliseconds)
- s (seconds)
- m (minutes)
- h (hours)
- d (days)

Example values: "100ms", "1.5s", "30m"

## New Aggregate Directive

The `aggregate-stats` directive allows you to calculate statistics on byte size and time duration columns.

### Syntax

aggregate-stats   target_size_column target_time_column [size_unit] [time_unit]

### Parameters

- `source_size_column`: The column containing byte size values
- `source_time_column`: The column containing time duration values
- `target_size_column`: The name for the output column containing total size
- `target_time_column`: The name for the output column containing total time
- `size_unit` (optional): The output size unit (B, KB, MB, GB, TB, PB), defaults to MB
- `time_unit` (optional): The output time unit (ns, ms, s, m, h, d), defaults to s

### Example
aggregate-stats   total_size_mb total_time_s MB s

This aggregates all byte sizes from `data_transfer_size` and all time durations from `response_time`, then outputs the totals in megabytes and seconds respectively.


## License and Trademarks

Copyright © 2016-2019 Cask Data, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions
and limitations under the License.

Cask is a trademark of Cask Data, Inc. All rights reserved.

Apache, Apache HBase, and HBase are trademarks of The Apache Software Foundation. Used with
permission. No endorsement by The Apache Software Foundation is implied by the use of these marks.
