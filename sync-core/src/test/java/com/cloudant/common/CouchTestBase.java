/**
 * Copyright (c) 2015 Cloudant, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.common;

import com.cloudant.mazha.CloudantConfig;
import com.cloudant.mazha.CouchConfig;
import com.cloudant.sync.util.Misc;
import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tomblench on 20/01/15.
 */
public abstract class CouchTestBase {

    public static final Boolean TEST_WITH_CLOUDANT = Boolean.valueOf(
            System.getProperty("test.with.cloudant",Boolean.FALSE.toString()));

    public CouchConfig getCouchConfig(String db) {
        if(TEST_WITH_CLOUDANT) {
            CouchConfig config = CloudantConfig.defaultConfig(db);
            if(Strings.isNullOrEmpty(config.getRootUri().getUserInfo())) {
                throw new IllegalStateException("Cloudant account info" +
                        " is required to run tests with Cloudant.");
            }
            return config;
        } else {
            String host;
             // If we're running on the Android emulator, 127.0.0.1 is the emulated device, rather
             // than the host machine. Instead we connect to 10.0.2.2.
            if(Misc.isRunningOnAndroid()){
                host = "10.0.2.2";
            } else {
                host = "127.0.0.1";
            }
            return this.defaultConfig(host, db);
        }
    }

    public CouchConfig defaultConfig(String host, String databasePath) {
        try {
            // we use String.format rather than the multi-arg URI constructor to avoid database
            // names being (double) escaped
            String urlString = String.format("http://%s:5984/%s", host, databasePath);
            CouchConfig config = new CouchConfig(new URI(urlString));
            return config;
        } catch (URISyntaxException use) {
            use.printStackTrace();
            return null;
        }
    }
}
