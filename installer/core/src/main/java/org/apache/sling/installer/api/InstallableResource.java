/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.installer.api;

import java.io.InputStream;
import java.util.Dictionary;


/**
 * A piece of data that can be installed by the {@link OsgiInstaller}
 * Currently the OSGi installer supports bundles and configurations.
 *
 * The installable resource contains as much information as the client
 * can provide. An input stream or dictionary is mandatory everything
 * else is optional. All optional values will be tried to be evaluated
 * by the OSGi installer. If such evaluation fails the resource will
 * be ignore during installation.
 *
 */
public class InstallableResource {

    /**
     * The type for a bundle - in this case {@link #getInputStream} must
     * return an input stream to the bundle. {@link #getDictionary()} might
     * return additional information.
     */
    public static final String TYPE_BUNDLE = "bundle";

    /**
     * The type for a configuration - in this case {@link #getDictionary()}
     * must return a dictionary with the configuration.
     */
    public static final String TYPE_CONFIG = "config";

    /**
     * Optional parameter in the dictionary if a bundle is installed. If this
     * is set with a valid start level, the bundle is installed in that start level.
     */
    public static final String BUNDLE_START_LEVEL = "bundle.startlevel";

    /** Default resource priority */
    public static final int DEFAULT_PRIORITY = 100;

    private final String id;
    private final String digest;
    private final InputStream inputStream;
    private final Dictionary<String, Object> dictionary;
    private final int priority;
    private final String resourceType;

    /**
     * Create a data object - this is a simple constructor just using the
     * values as they are provided.
     * @param id Unique id for the resource, For auto detection if the resource
     *           type, the id should contain an extension like .jar, .cfg etc.
     * @param is The input stream to the data or
     * @param dict A dictionary with data
     * @param digest A digest of the data
     * @param type The resource type if known
     * @param priority Optional priority - if not specified {@link #DEFAULT_PRIORITY}
     *                 is used
     * @throws IllegalArgumentException if something is wrong
     */
    public InstallableResource(final String id,
            final InputStream is,
            final Dictionary<String, Object> dict,
            final String digest,
            final String type,
            final Integer priority) {
        if ( id == null ) {
            throw new IllegalArgumentException("id must not be null.");
        }
        if ( is == null ) {
            // if input stream is null, config through dictionary is expected!
            if ( dict == null ) {
                throw new IllegalArgumentException("dictionary must not be null (or input stream must not be null).");
            }
        }

        this.id = id;
        this.inputStream = is;
        this.dictionary = dict;
        this.digest = digest;
        this.priority = (priority != null ? priority : DEFAULT_PRIORITY);
        this.resourceType = type;
    }

    /**
     * Return this data's id. It is opaque for the {@link OsgiInstaller}
     * but should uniquely identify the resource within the namespace of
     * the used installation mechanism.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the type of this resource.
     * @return The resource type or <code>null</code> if the type is unnown for the client.
     */
    public String getType() {
        return this.resourceType;
    }

    /**
     * Return an input stream with the data of this resource.
     * Null if resource contains a configuration instead. Caller is responsible for
     * closing the stream.
     * If this resource is of type CONFIG it must not return an input stream and
     * if this resource is of type BUNDLE it must return an input stream!
     * @return The input stream or null.
     */
    public InputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Return this resource's dictionary.
     * Null if resource contains an InputStream instead. If this resource is of
     * type CONFIG it must return a dictionary and if this resource is of type BUNDLE
     * it might return a dictionary!
     * @return The resource's dictionary or null.
     */
    public Dictionary<String, Object> getDictionary() {
        return this.dictionary;
    }

    /**
     * Return this resource's digest. Not necessarily an actual md5 or other digest of the
     * data, can be any string that changes if the data changes.
     * @return The digest or null
     */
    public String getDigest() {
        return this.digest;
    }

    /**
     * Return the priority of this resource. Priorities are used to decide which
     * resource to install when several are registered for the same OSGi entity
     * (bundle, config, etc.)
     */
    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", priority=" + priority + ", id=" + id;
    }
}