/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.metastore.MetaStoreUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test SessionState
 */
@RunWith(value = Parameterized.class)
public class TestSessionState {

  private final boolean prewarm;

  public TestSessionState(Boolean mode) {
    this.prewarm = mode.booleanValue();
  }

  @Parameters
  public static Collection<Boolean[]> data() {
    return Arrays.asList(new Boolean[][] { {false}, {true}});
  }

  @Before
  public void setup() {
    HiveConf conf = new HiveConf();
    if (prewarm) {
      HiveConf.setBoolVar(conf, ConfVars.HIVE_PREWARM_ENABLED, true);
      HiveConf.setIntVar(conf, ConfVars.HIVE_PREWARM_NUM_CONTAINERS, 1);
    }
    SessionState.start(conf);
  }

  /**
   * test set and get db
   */
  @Test
  public void testgetDbName() throws Exception {
    //check that we start with default db
    assertEquals(MetaStoreUtils.DEFAULT_DATABASE_NAME,
        SessionState.get().getCurrentDatabase());
    final String newdb = "DB_2";

    //set new db and verify get
    SessionState.get().setCurrentDatabase(newdb);
    assertEquals(newdb,
        SessionState.get().getCurrentDatabase());

    //verify that a new sessionstate has default db
    SessionState.start(new HiveConf());
    assertEquals(MetaStoreUtils.DEFAULT_DATABASE_NAME,
        SessionState.get().getCurrentDatabase());

  }

  @Test
  public void testClose() throws Exception {
    SessionState ss = SessionState.get();
    assertNull(ss.getTezSession());
    ss.close();
    assertNull(ss.getTezSession());
  }
}
