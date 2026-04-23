/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.core.filerepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * An InMemory implementation of the {@link FileRepository}
 * 
 * @author danish
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
public class InMemoryFileRepository implements FileRepository {

	private static final String TEMP_DIRECTORY_PREFIX = "basyx-temp";
	private final Path tmpDirectoryPath = Paths.get(getTemporaryDirectoryPath()).toAbsolutePath().normalize();
	private final Map<String, String> originalFileNames = new ConcurrentHashMap<>();

	@Override
	public String save(FileMetadata fileMetadata) throws FileHandlingException {
		Path targetPath = createFilePath(fileMetadata.getFileName());
		String filePath = targetPath.toString();

		if (Files.exists(targetPath))
			throw new FileHandlingException("File '%s' already exists.".formatted(filePath));

		java.io.File targetFile = targetPath.toFile();

		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			IOUtils.copy(fileMetadata.getFileContent(), outStream);
		} catch (IOException e) {
			throw new FileHandlingException(fileMetadata.getFileName(), e);
		}

		fileMetadata.setFileName(filePath);
		String originalFileName = fileMetadata.getOriginalFileName();

		if (originalFileName == null || originalFileName.isBlank())
			originalFileName = targetPath.getFileName().toString();

		originalFileNames.put(filePath, originalFileName);

		return filePath;
	}

	@Override
	public InputStream find(String fileId) throws FileDoesNotExistException {
		Path filePath;

		try {
			filePath = resolveStoredFilePath(fileId);
		} catch (IllegalArgumentException | SecurityException e) {
			throw new FileDoesNotExistException();
		}

		try {
			return new FileInputStream(filePath.toFile());
		} catch (FileNotFoundException e) {
			throw new FileDoesNotExistException();
		}
	}

	@Override
	public void delete(String fileId) throws FileDoesNotExistException {
		Path filePath;

		try {
			filePath = resolveStoredFilePath(fileId);
		} catch (IllegalArgumentException | SecurityException e) {
			throw new FileDoesNotExistException();
		}

		if (!Files.exists(filePath))
			throw new FileDoesNotExistException();

		java.io.File tmpFile = filePath.toFile();

		tmpFile.delete();
		originalFileNames.remove(filePath.toString());
	}

	@Override
	public boolean exists(String fileId) {
		if(fileId == null) return false;

		if (fileId.isBlank())
			return false;

		Path filePath;

		try {
			filePath = resolveStoredFilePath(fileId);
		} catch (IllegalArgumentException | SecurityException e) {
			return false;
		}

		return Files.exists(filePath);
	}

	@Override
	public String getOriginalFileName(String fileId) {
		Path filePath;

		try {
			filePath = resolveStoredFilePath(fileId);
		} catch (IllegalArgumentException | SecurityException e) {
			return fileId;
		}

		String key = filePath.toString();
		return originalFileNames.getOrDefault(key, filePath.getFileName().toString());
	}

	private String getTemporaryDirectoryPath() {
		String tempDirectoryPath = "";

		try {
			tempDirectoryPath = Files.createTempDirectory(TEMP_DIRECTORY_PREFIX).toAbsolutePath().toString();
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to create the temporary directory with prefix: %s", TEMP_DIRECTORY_PREFIX));
		}

		return tempDirectoryPath;
	}

	private Path createFilePath(String fileName) {
		if (fileName == null || fileName.isBlank())
			throw new IllegalArgumentException("File name must not be null or blank.");

		Path resolvedPath;

		try {
			resolvedPath = tmpDirectoryPath.resolve(fileName).normalize();
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException("Invalid file name '%s'.".formatted(fileName), e);
		}

		if (!resolvedPath.startsWith(tmpDirectoryPath))
			throw new SecurityException("Path traversal attempt detected.");

		return resolvedPath;
	}

	private Path resolveStoredFilePath(String fileId) {
		Path filePath;

		try {
			filePath = Paths.get(fileId);
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException("Invalid file id '%s'.".formatted(fileId), e);
		}

		if (!filePath.isAbsolute())
			filePath = tmpDirectoryPath.resolve(filePath);

		filePath = filePath.normalize();

		if (!filePath.startsWith(tmpDirectoryPath))
			throw new SecurityException("Path traversal attempt detected.");

		return filePath;
	}

}
