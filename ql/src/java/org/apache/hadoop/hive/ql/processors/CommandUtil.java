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

package org.apache.hadoop.hive.ql.processors;

import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAccessControlException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzPluginException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import org.apache.hadoop.hive.ql.session.SessionState;

class CommandUtil {

  /**
   * Authorize command of given type and arguments
   *
   * @param ss
   * @param type
   * @param command
   * @return null if there was no authorization error. Otherwise returns  CommandProcessorResponse
   * capturing the authorization error
   */
  static CommandProcessorResponse authorizeCommand(SessionState ss, HiveOperationType type,
      List<String> command) {
    if (ss == null) {
      // ss can be null in unit tests
      return null;
    }
    if (ss.isAuthorizationModeV2()) {
      try {
        authorizeCommandThrowEx(ss, type, command);
        // authorized to perform action
        return null;
      } catch (HiveAuthzPluginException e) {
        return CommandProcessorResponse.create(e);
      } catch (HiveAccessControlException e) {
        return CommandProcessorResponse.create(e);
      }
    }
    return null;
  }
  /**
   * Authorize command. Throws exception if the check fails
   * @param ss
   * @param type
   * @param command
   * @throws HiveAuthzPluginException
   * @throws HiveAccessControlException
   */
  static void authorizeCommandThrowEx(SessionState ss, HiveOperationType type,
      List<String> command) throws HiveAuthzPluginException, HiveAccessControlException {
    HivePrivilegeObject commandObj = HivePrivilegeObject.createHivePrivilegeObject(command);
    ss.getAuthorizerV2().checkPrivileges(type, Arrays.asList(commandObj), null, null);
  }


}