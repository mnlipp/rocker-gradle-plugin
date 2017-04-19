package de.mnl.gradle.rocker.test;
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16

import org.gradle.internal.impldep.org.apache.commons.io.FileExistsException
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginTest extends Specification {
	@Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
	File buildFile

	def setup() {
		def target = testProjectDir.newFolder("src","test","templates")
		new AntBuilder().copy(todir: target) {
			fileset(dir: "src/test/templates")
		}
		buildFile = testProjectDir.newFile('build.gradle')
	}

	def "Basic Test"() {
		given:
		buildFile << """
buildscript {
    repositories {
        jcenter() // Needed for plugin's dependencies
    }
}

plugins {
		id 'rocker-gradle-plugin'
}

sourceCompatibility = 1.8

sourceSets {
    main {
        rocker {
            // Directory that has your Template.rocker.html files
            srcDir('src/test/templates')
        }
    }
}

rocker {
    // (All settings are shown with their defaults)
    // 
    // Skips building templates all together
    skip false
    // Base directory for generated java sources, actual target is sub directory 
    // with the name of the source set. The value is passed through project.file(). 
    outputBaseDirectory = "\$buildDir/generated-src/rocker"
    // Base directory for the directory where the hot reload feature 
    // will (re)compile classes to at runtime (and where `rocker-compiler.conf`
    // is generated, which is used by RockerRuntime.getInstance().setReloading(true)).
    // The actual target is a sub directory with the name of the source set. 
    // The value is passed through project.file().
    classBaseDirectory = "\$buildDir/classes"

    failOnError true
    skipTouch true
    // must not be empty when skipTouch is equal to false
    touchFile ""
    javaVersion '1.8'
    extendsClass null
    extendsModelClass null
    optimize null
    discardLogicWhitespace null
    targetCharset null
    suffixRegex null
    postProcessing null
}

// For each source set "name" a task "generate<name>RockerTemplateSource"
// is generated (with an empty "name" for the main source set). It is
// possible to override the directories derived from the base names
// by setting the tasks' properties "classDir" and "outputDir".

// For a complete build.gradle you also need:

repositories {
    mavenCentral()
}

dependencies {
    compile group: 'com.fizzed',
            name: 'rocker-compiler',
            version: '0.18.0'

    compile group: 'com.fizzed',
            name: 'rocker-runtime',
            version: '0.18.0'

    compile group: 'org.slf4j',
            name: 'slf4j-simple',
            version: '1.6.1'

    testCompile group: 'junit', name: 'junit', version: '4.11'
}
"""

		when:
		def result = GradleRunner.create()
			.withGradleVersion("3.5")
			.withPluginClasspath()
			.withProjectDir(testProjectDir.root)
			.withArguments(":generateRockerTemplateSource", "--debug")
			.build()

		then:
		println result.output
		result.task(':generateRockerTemplateSource').outcome == SUCCESS
		isFile("build/generated-src/rocker/main/org/acme/foo/templates/HelloTemplate.java")
	}
	
	def boolean isFile(relativePath) {
		new File(testProjectDir.root, relativePath).file
	}
}
