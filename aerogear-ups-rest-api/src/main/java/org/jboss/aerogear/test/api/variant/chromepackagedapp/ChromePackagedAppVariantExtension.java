/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.test.api.variant.chromepackagedapp;

import org.jboss.aerogear.unifiedpush.api.ChromePackagedAppVariant;

public abstract class ChromePackagedAppVariantExtension<EXTENSION extends ChromePackagedAppVariantExtension<EXTENSION>>
        extends ChromePackagedAppVariant {

    private static final long serialVersionUID = 1L;

    protected final ChromePackagedAppVariantContext context;

    public ChromePackagedAppVariantExtension(ChromePackagedAppVariantContext context) {
        this.context = context;
    }

    public EXTENSION name(String name) {
        setName(name);
        return castInstance();
    }


    public EXTENSION description(String description) {
        setDescription(description);
        return castInstance();
    }

    public EXTENSION clientId(String clientId) {
        setClientId(clientId);
        return castInstance();
    }

    public EXTENSION clientSecret(String clientSecret) {
        setClientSecret(clientSecret);
        return castInstance();
    }

    public EXTENSION refreshToken(String refreshToken) {
        setRefreshToken(refreshToken);
        return castInstance();
    }

    @SuppressWarnings("unchecked")
    private EXTENSION castInstance() {
        return (EXTENSION) this;
    }
}
