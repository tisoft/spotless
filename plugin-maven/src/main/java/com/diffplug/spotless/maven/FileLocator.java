/*
 * Copyright 2016 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless.maven;

import static com.diffplug.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.util.FileUtils;

public class FileLocator {

	static final String TMP_RESOURCE_FILE_PREFIX = "spotless-resource-";

	private final ResourceManager resourceManager;
	private final File baseDir, buildDir;

	public FileLocator(ResourceManager resourceManager, File baseDir, File buildDir) {
		this.resourceManager = Objects.requireNonNull(resourceManager);
		this.baseDir = Objects.requireNonNull(baseDir);
		this.buildDir = Objects.requireNonNull(buildDir);
	}

	/**
	 * If the given path is a local file returns it as such unchanged,
	 * otherwise extracts the given resource to a randomly-named file in the build folder.
	 */
	public File locateFile(String path) {
		if (isNullOrEmpty(path)) {
			return null;
		}

		File localFile = new File(path);
		if (localFile.exists() && localFile.isFile()) {
			return localFile;
		}

		String outputFile = tmpOutputFileName(path);
		try {
			return resourceManager.getResourceAsFile(path, outputFile);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException("Unable to locate file with path: " + path, e);
		} catch (FileResourceCreationException e) {
			throw new RuntimeException("Unable to create temporary file '" + outputFile + "' in the output directory", e);
		}
	}

	private static String tmpOutputFileName(String path) {
		String extension = FileUtils.extension(path);
		return TMP_RESOURCE_FILE_PREFIX + UUID.randomUUID() + '.' + extension;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public File getBuildDir() {
		return buildDir;
	}
}
