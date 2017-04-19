package de.mnl.gradle.rocker.test;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import de.mnl.gradle.rocker.RockerGradleException;
import de.mnl.gradle.rocker.RockerPlugin;
import de.mnl.gradle.rocker.RockerTask;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RockerGradlePluginTest {

    @Test
    public void testJavaFileIsCreatedInOutputDirectory() throws ClassNotFoundException {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(RockerPlugin.class);

        RockerTask.doCompileRocker(project, new File("src/test/templates"),
        		new File("build/generated/source/apt/main"),
        		new File("build/classes/main"));
        File templateFile =
                new File("build/generated/source/apt/main/org/acme/foo/templates/HelloTemplate.java");
        assertTrue(templateFile.exists());
    }

    @Test
    public void testRockerCompilerConfIsGeneratedInClassesDirectory() throws ClassNotFoundException {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(RockerPlugin.class);

        RockerTask.doCompileRocker(project, new File("src/test/templates"),
        		new File("build/generated/source/apt/main"),
        		new File("build/classes/main"));
        File templateFile =
                new File("build/classes/main/rocker-compiler.conf");
        assertTrue(templateFile.exists());
    }

    @Test
    public void testNullOutputDirectoryThrowsException() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(RockerPlugin.class);

        try {
            RockerTask.doCompileRocker(project, new File("src/test/templates"),
            		null,
            		new File("build/classes/main"));
        } catch (RockerGradleException e) {
            assertTrue(e.getMessage().contains("Output dir"));
            return;
        }
        fail("Exception not thrown for outputDirectory");
    }

    @Test
    public void testNullClassDirectoryThrowsException() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(RockerPlugin.class);

        try {
            RockerTask.doCompileRocker(project, new File("src/test/templates"),
            		new File("build/generated/source/apt/main"),
            		null);
        } catch (RockerGradleException e) {
        	// rocker simply throws an (uninformative) NullPointerException
            return;
        }
        fail("Exception not thrown for classDirectory");
    }

    @Test
    public void testNullTemplateDirectoryThrowsException() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(RockerPlugin.class);

        try {
            RockerTask.doCompileRocker(project, null,
            		new File("build/generated/source/apt/main"),
            		new File("build/classes/main"));
        } catch (RockerGradleException e) {
            assertTrue(e.getMessage().contains("Template dir"));
            return;
        }
        fail("Exception not thrown for templateDirectory");
    }

}
