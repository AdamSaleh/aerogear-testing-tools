package org.jboss.aerogear.test.maven

import java.text.MessageFormat

import org.arquillian.spacelift.execution.Task
import org.arquillian.spacelift.process.Command
import org.arquillian.spacelift.process.CommandBuilder
import org.jboss.aerogear.test.GradleSpacelift
import org.jboss.aerogear.test.utils.EnvironmentUtils
import org.slf4j.LoggerFactory

class MavenExecutor extends Task<Object, Void>{

    def static final log = LoggerFactory.getLogger('MavenExecutor')

    def projectDir

    def batchMode = true

    def settingsXml

    def goals = []

    def profiles = []

    def properties = []

    private def command = []

    MavenExecutor() {
        def project = GradleSpacelift.currentProject()
        this.settingsXml = "${project.aerogearTestEnv.workspace}/settings.xml"
        // add settings xml as property so SWR finds it as well
        properties << "org.apache.maven.user-settings=${project.aerogearTestEnv.workspace}/settings.xml"
    }

    @Override
    protected Void process(Object input) throws Exception {

        def command = GradleSpacelift.tools('mvn')

        if (batchMode) {
            command.parameter('-B')
        }

        command.parameters(getProfiles())
        command.parameter('-f')
        command.parameter(projectDir + "/pom.xml")
        command.parameter('-s')
        command.parameter(settingsXml)
        command.parameters(goals)
        command.parameters(getProperties())

        command.interaction(GradleSpacelift.ECHO_OUTPUT).execute().await()

        return null;
    }

    def withoutBatchMode() {
        batchMode = false
        this
    }

    def withSettings(settingsXml) {
        this.settingsXml = settingsXml
        this
    }

    def withGoal(goal) {
        this.goals.add(goal)
        this
    }

    def withGoals(CharSequence...goals) {
        this.goals.addAll(goals)
        this
    }

    def withProperty(property) {
        this.properties.add(property)
        this
    }

    def withProperties(CharSequence...properties) {
        this.properties.addAll(properties)
        this
    }

    def withProfile(profile) {
        this.profiles.add(profile)
        this
    }

    def withProfiles(CharSequence...profiles) {
        this.profiles.addAll(profiles)
        this
    }

    def withAndroidTarget(target) {
        // TODO identify all possible combinations for Android Target settings
        this.properties << "arq.group.containers.container.android.configuration.target=${target}"
        this
    }

    def withSurefireSuffix(suffix) {
        this.properties << "surefire.reportNameSuffix=${suffix}"
        this
    }

    private def getProfiles() {
        def profs = []
        this.profiles.each { p ->
            profs << "-P" + p
        }
        profs
    }

    private def getProperties() {
        def props = []
        this.properties.each { p -> props << "\"-D${p}\"" }
        props
    }
}