maven-version-resolver
======================

Resolves the version from an input Maven artifact to an Artifact Metadata Repository


USAGE
----------------------
```
MavenVersionResolverService mavenVersionResolverService
    = new MavenVersionResolverService(<artifact_repository_impl>);
return mavenVersionResolverService.resolveArtifact(<maven_input_artifact>);
```

SAMPLE
-----------------------
```
MavenVersionResolverService mavenVersionResolverService
    = new MavenVersionResolverService(new MongoDBArtifactRepository());
return mavenVersionResolverService.resolveArtifact(
        new MavenInputArtifact(<groupId>,<artifactId>,"latest.release");
```

For now, there is only one Resolution strategy with the following behaviour
```
"1.5 resolves(-->) 1.5
"1.5.INTEGRATION" resolves(-->) 1.5.INTEGRATION
"1.5.TEST" resolves(-->) 1.5.TEST
"1.5.RELEASE" resolves(-->) 1.5.RELEASE
"1.5.ANYOTHER_STATUS" resolves(-->) 1.5.ANYOTHER_STATUS
"latest.<status> delegate to (-->) the get latest implementation of the given Artifact Repository implementation
```

For now, only a MongoDB metadata repository is supported with the following mapping
```
.INTEGRATION maven version --> INTEGRATION MongoDB status
.TEST maven version --> TEST MongoDB status
.RELEASE maven version --> RELEASE MongoDB status
.other string value extracted from latest.<status> string value --> exact same value with <status>
```


