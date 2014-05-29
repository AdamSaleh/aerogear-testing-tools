package org.jboss.aerogear.test.container.db.module

import java.io.File;

import org.jboss.aerogear.test.container.db.DatabaseModule
import org.jboss.aerogear.test.container.manager.ManagedContainerConfiguration
import org.jboss.aerogear.test.container.spacelift.JBossCLI
import org.jboss.aerogear.test.container.spacelift.JBossStarter;
import org.jboss.aerogear.test.container.spacelift.JBossStopper;
import org.jboss.aerogear.test.maven.*
import org.arquillian.spacelift.execution.Tasks

class MySQLDatabaseModule extends DatabaseModule {

    //private static final String DEFAULT_VERSION = "5.1.18"

    MySQLDatabaseModule(String name, String jbossHome, String destination) {
        super(name, jbossHome, destination)
    }

    MySQLDatabaseModule(String name, File jbossHome, File destination) {
        super(name, jbossHome.getAbsolutePath(), destination.getAbsolutePath())
    }

    @Override
    def install() {

        if (! new File("${destination}/mysql-connector-java-${version}.jar").exists() ) {
            Tasks.prepare(MavenExecutor)
                    .goal("dependency:copy")
                    .property("artifact=mysql:mysql-connector-java:${version}")
                    .property("outputDirectory=${destination}")
                    .execute()
                    .await()
        }

        if (! new File(jbossHome + "/modules/com/mysql").exists()) {
            
            startContainer()

            Tasks.prepare(JBossCLI)
                    .environment("JBOSS_HOME", jbossHome)
                    .connect()
                    .cliCommand("module add --name=com.mysql --resources=${destination}/mysql-connector-java-${version}.jar --dependencies=javax.api,javax.transaction.api")
                    .execute()
                    .await()

            stopContainer()
        }

    }

    @Override
    def uninstall() {
        startContainer()

        Tasks.prepare(JBossCLI).environment("JBOSS_HOME", jbossHome).connect().cliCommand("module remove --name=com.mysql").execute().await()

        stopContainer()
    }
}