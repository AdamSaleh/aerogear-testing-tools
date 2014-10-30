/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.aerogear.unifiedpush.utils.variant;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VariantMetadata {

    public String googleKey;

    private String projectNumber;

    private String certificatePath;

    private String certificatePass;

    private boolean production;

    public void setProduction(boolean production) {
        this.production = production;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public void setCertificatePass(String certificatePass) {
        this.certificatePass = certificatePass;
    }

    public boolean getProduction() {
        return production;
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public String getCertificatePass() {
        return certificatePass;
    }

}