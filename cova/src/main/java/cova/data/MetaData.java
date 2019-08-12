/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package cova.data;

public class MetaData {
  private String apkName;
  private long size;// dex file in KB
  private long reachableMethods;// number of reachable methods
  private double covaTime;// cova time in seconds
  private double failedAliasing;// portion of failed aliasing
  private int z3Queries;// number of z3 queries
  private int timeout;// 1 if cova timed out
  private double z3Time;// z3 time in seconds
  private double fTime;// flowdroid time in seconds


  public MetaData(String apkName, long size, long reachableMethods, double fTime,
      boolean covaTimeout, double covaTime,
      double z3Time,
      double failedAliasing,
      int z3Queries) {
    this.apkName = apkName;
    this.size = size;
    this.reachableMethods = reachableMethods;
    this.fTime = fTime;
    this.covaTime = covaTime;
    this.z3Time = z3Time;
    this.failedAliasing = failedAliasing;
    this.z3Queries = z3Queries;
    if (covaTimeout) {
      timeout = 1;
    } else {
      timeout = 0;
    }
  }

  public MetaData(String apkName) {
    this.apkName = apkName;
  }

  public String getApkName() {
    return apkName;
  }

  public long getSize() {
    return size;
  }

  public String getTime() {
    return String.format("%.3f", covaTime).replace(".", ",");
  }

  public String getFailedAliasing() {
    String s = String.format("%.2f", failedAliasing);
    return s.replace(".", ",");
  }

  public int getZ3Queries() {
    return z3Queries;
  }

  public int getTimeout() {
    return timeout;
  }

  public String getZ3Time() {
    return String.format("%.3f", z3Time).replace(".", ",");
  }

  public String getTimeForFlowDroid() {
    return String.format("%.3f", fTime).replace(".", ",");
  }

  public long getReachableMethods() {
    return reachableMethods;
  }
}
