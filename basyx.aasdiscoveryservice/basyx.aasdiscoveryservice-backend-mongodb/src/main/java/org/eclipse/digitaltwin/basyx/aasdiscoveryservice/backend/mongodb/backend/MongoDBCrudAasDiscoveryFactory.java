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
package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Simple AAS Discovery factory that creates a {@link MongoDBCrudAasDiscovery} with a
 * backend provider and a service factory
 * 
 * @author zielstor, fried, mateusmolina
 * 
 */
@Component
@ConditionalOnProperty(name = "basyx.backend", havingValue = "MongoDB")
public class MongoDBCrudAasDiscoveryFactory implements AasDiscoveryServiceFactory {

	private final AasDiscoveryDocumentBackend aasDiscoveryDocumentBackend;
	private final String aasDiscoveryName;

	@Autowired
	public MongoDBCrudAasDiscoveryFactory(AasDiscoveryDocumentBackend aasDiscoveryDocumentBackend, @Value("${basyx.aasdiscoveryservice.name:aas-discovery-service}") String aasDiscoveryName) {
		this.aasDiscoveryDocumentBackend = aasDiscoveryDocumentBackend;
		this.aasDiscoveryName = aasDiscoveryName;
	}

	public MongoDBCrudAasDiscoveryFactory(AasDiscoveryDocumentBackend aasBackendProvider) {
		this(aasBackendProvider, "aas-discovery-service");
	}

	@Override
	public AasDiscoveryService create() {
		return new MongoDBCrudAasDiscovery(aasDiscoveryDocumentBackend, aasDiscoveryName);
	}

}
