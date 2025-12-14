/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package xyz.kyngs.libby.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension for configuring the libby Gradle plugin.
 * <p>
 * Use this extension to exclude dependencies or disable checksum calculation for specific dependencies.
 */
public class LibbyExtension {

    private List<String> excludedDependencies = new ArrayList<>();
    private List<String> noChecksumDependencies = new ArrayList<>();

    /** Default constructor. */
    public LibbyExtension() {
    }

    /**
     * Returns the list of excluded dependency patterns.
     *
     * @return list of regex patterns for excluded dependencies
     */
    public List<String> getExcludedDependencies() {
        return excludedDependencies;
    }

    /**
     * Returns the list of dependency patterns excluded from checksum calculation.
     *
     * @return list of regex patterns for no-checksum dependencies
     */
    public List<String> getNoChecksumDependencies() {
        return noChecksumDependencies;
    }

    /**
     * Add a dependency to exclude from the libby.json file. <br>
     * <br>
     * The dependency is a regex matching the format "group:name:version" <br>
     * For example "org\\.company:library:.*" will exclude all versions of the library "library" from the group "org.company"
     *
     * @param dependency The dependency to exclude
     */
    public void excludeDependency(String dependency) {
        excludedDependencies.add(dependency);
    }

    /**
     * Add a dependency to exclude from the checksum calculation. <br>
     * <br>
     * The dependency is a regex matching the format "group:name:version" <br>
     * For example "org\\.company:library:.*" will exclude all versions of the library "library" from the group "org.company"
     *
     * @param dependency the dependency pattern to exclude from checksum
     */
    public void noChecksumDependency(String dependency) {
        noChecksumDependencies.add(dependency);
    }
}
