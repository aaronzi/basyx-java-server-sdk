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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Creates and starts the {@link HierarchicalSubmodelRegistryFeature} for tests
 * 
 * @author mateusmolina
 *
 */
@SpringBootApplication(scanBasePackages = { "org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy", "org.eclipse.digitaltwin.basyx.submodelregistry.service.api", "org.eclipse.digitaltwin.basyx.submodelregistry.service.events",
		"org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration", "org.eclipse.digitaltwin.basyx.submodelregistry.service.errors", "org.eclipse.digitaltwin.basyx.common.hierarchy" })
public class DummySubmodelRegistryComponent {
	public static void main(String[] args) {
		SpringApplication.run(DummySubmodelRegistryComponent.class, args);
	}
}