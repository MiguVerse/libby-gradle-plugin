/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package xyz.miguvt.libby.plugin;

import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator;
import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Optional integration with the Shadow plugin to extract relocations.
 */
public class ShadowPluginIntegration {

    private ShadowPluginIntegration() {
        // utility class
    }

    /**
     * Extracts relocations configured in the shadowJar task.
     *
     * @param project the Gradle project
     * @return list of relocations, or {@code null} if Shadow is not available
     */
    protected static List<LibbyTask.Relocation> extractShadowJarRelocations(Project project) {
        Logger logger = project.getLogger();

        ShadowJar task;
        try {
            task = project.getTasks().withType(ShadowJar.class).named("shadowJar").get();
        } catch (Exception e) {
            logger.debug("libby: shadowJar task not available, skipping relocation extraction", e);
            return null;
        }

        Collection<Relocator> relocators;
        try {
            relocators = task.getRelocators().get();
        } catch (Exception e) {
            logger.debug("libby: could not resolve shadowJar relocators", e);
            return Collections.emptyList();
        }

        var relocations = new ArrayList<LibbyTask.Relocation>();

        for (Relocator relocator : relocators) {
            if (relocator instanceof SimpleRelocator simpleRelocator) {
                LibbyTask.Relocation rel = extractRelocation(simpleRelocator, logger);
                if (rel != null) {
                    relocations.add(rel);
                }
            } else {
                logger.debug("libby: unsupported relocator type {}", relocator.getClass().getName());
            }
        }

        return relocations;
    }

    private static LibbyTask.Relocation extractRelocation(SimpleRelocator relocator, Logger logger) {
        try {
            Field patternField = SimpleRelocator.class.getDeclaredField("pattern");
            patternField.setAccessible(true);
            Field shadedPatternField = SimpleRelocator.class.getDeclaredField("shadedPattern");
            shadedPatternField.setAccessible(true);

            String from = (String) patternField.get(relocator);
            String to = (String) shadedPatternField.get(relocator);
            return new LibbyTask.Relocation(from, to);
        } catch (NoSuchFieldException e) {
            logger.debug("libby: SimpleRelocator internals changed, cannot extract relocation", e);
        } catch (ReflectiveOperationException | SecurityException e) {
            logger.debug("libby: reflection failed for SimpleRelocator", e);
        }
        return null;
    }
}
