package com.peergreen.kernel.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionErrorHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/01/13
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@Mojo(name = "build",
      defaultPhase = LifecyclePhase.PACKAGE,
      requiresProject = true,
      requiresDependencyCollection = ResolutionScope.RUNTIME)
public class BuildPlatformMojo extends AbstractMojo {

    private static final String BUNDLES = "bundles";
    private static final String LIB = "lib/";

    @Component(hint = "jar")
    private Archiver archiver;

    @Component(hint = "jar")
    private UnArchiver unarchiver;

    @Component
    private MavenProject project;

    @Parameter(defaultValue = "org.apache.felix/org.apache.felix.framework/4.0.2")
    private String framework;

    @Parameter(defaultValue = "org.osgi/org.osgi.core/4.3.1")
    private String specification;

    @Parameter(defaultValue = "com.peergreen.prototype.platform/platform-launcher/1.0-SNAPSHOT")
    private String launcher;

    @Parameter(defaultValue = "com.peergreen.prototype.platform/platform-bootstrap/1.0-SNAPSHOT")
    private String bootstrap;

    @Parameter(defaultValue = "com.peergreen.platform.bootstrap.Bootstrap")
    private String mainClass;

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.jar")
    private File destFile;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    @Parameter
    private List<StartLevel> levels = new ArrayList<>();

    @Component
    private RepositorySystem repositorySystem;
    @Component
    private MavenSession mavenSession;
    @Component
    private ResolutionErrorHandler errorHandler;

    private Map<String, Integer> bundleToLevel = new HashMap<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        init();

        classes.delete();
        classes.mkdirs();

        // 1. Bootstrap classes
        // -------------------------------------
        unarchiver.setSourceFile(resolveArtifact(bootstrap));
        unarchiver.setDestDirectory(classes);
        unarchiver.extract();
        archiver.addDirectory(classes, null, new String[]{"META-INF/**"});

        // 2. Bundles
        // -------------------------------------
        Set<Artifact> artifacts = project.getDependencyArtifacts();
        for (Artifact artifact : artifacts) {
            int level = findLevel(artifact);
            String path = artifact.getFile().getName();
            String prefix = BUNDLES;
            if (level > 1) {
                prefix += String.valueOf(level);
            }
            archiver.addFile(artifact.getFile(), prefix + "/" + path);
        }

        // 3. Lib
        // -------------------------------------
        File frameworkFile = resolveArtifact(framework);
        File specFile = resolveArtifact(specification);
        File launcherFile = resolveArtifact(launcher);
        if (frameworkFile != null) {
            archiver.addFile(frameworkFile, LIB + frameworkFile.getName());
        }
        if (specFile != null) {
            archiver.addFile(specFile, LIB + specFile.getName());
        }
        if (specFile != null) {
            archiver.addFile(launcherFile, LIB + launcherFile.getName());
        }

        // 4. Create Manifest
        // -------------------------------------
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Main-Class", mainClass);
        manifest.getMainAttributes().putValue("Class-Path", path(LIB, specFile, frameworkFile, launcherFile));
        try {
            ((JarArchiver) archiver).addConfiguredManifest(manifest);
        } catch (ManifestException e) {
            throw new MojoExecutionException("Cannot generate Manifest", e);
        }

        // Do the archive
        try {
            archiver.setDestFile(destFile);
            archiver.createArchive();
            project.getArtifact().setFile(destFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot assemble platform", e);
        }
    }

    private String path(String prefix, File... files) {
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            if (file != null) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append(prefix);
                sb.append(file.getName());
            }
        }
        return sb.toString();
    }

    private int findLevel(Artifact artifact) {
        Integer level = bundleToLevel.get(artifact.getDependencyConflictId());
        return (level == null) ? 1 : level;
    }

    private void init() {
        for (StartLevel level : levels) {
            int sl = level.getLevel();
            for (String bundle : level.getBundles()) {
                bundleToLevel.put(bundle, sl);
            }
        }
    }

    private File resolveArtifact(String artifact) throws MojoFailureException {
        Artifact a = parseArtifact(artifact);
        if (a == null) {
            return null;
        }

        RepositoryRequest rr = DefaultRepositoryRequest.getRepositoryRequest(mavenSession, project);
        ArtifactResolutionRequest arr = new ArtifactResolutionRequest(rr);
        arr.setArtifact(a)
           .setResolveRoot(true)
           .setResolveTransitively(false);
        ArtifactResolutionResult result = repositorySystem.resolve(arr);
        try {
            errorHandler.throwErrors(arr, result);
        } catch (ArtifactResolutionException e) {
            throw new MojoFailureException("Cannot resolve", e);
        }

        Artifact resolved = result.getArtifacts().iterator().next();
        return resolved.getFile();
    }

    private Artifact parseArtifact(String value) throws MojoFailureException {
        String[] fragments = value.split("/");
        if (fragments.length != 3) {
            getLog().warn(value + " is not well formed, will be ignored");
            return null;
        }
        return repositorySystem.createArtifact(fragments[0], fragments[1], fragments[2], "jar");
    }
}
