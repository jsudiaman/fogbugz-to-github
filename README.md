# FogBugz to GitHub (FB2GH)

![Logo](/fb2gh-logo.png)

**FB2GH** is designed to help you programmatically migrate your [FogBugz cases](https://www.fogcreek.com/fogbugz/) into [GitHub issues](https://guides.github.com/features/issues/).

[![Build Status](https://travis-ci.org/sudiamanj/fogbugz-to-github.svg?branch=master)](https://travis-ci.org/sudiamanj/fogbugz-to-github) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sudiamanj_fogbugz-to-github&metric=coverage)](https://sonarcloud.io/dashboard?id=sudiamanj_fogbugz-to-github) [![Maven Central](https://img.shields.io/maven-central/v/com.sudicode/fogbugz-to-github.svg)](https://search.maven.org/artifact/com.sudicode/fogbugz-to-github) [![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg?logo=java)](http://fb2gh.sudicode.com)

## Usage
```java
// Login to FogBugz
// How to get an API token: http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token
FogBugz fogBugz = FBFactory.newFogBugz(fogBugzURL, fogBugzAPIToken);

// You can also get a token through FB2GH
FogBugz otherFb = FBFactory.newFogBugz(fogBugzURL, fogBugzEmail, fogBugzPassword);
System.out.println(otherFb.getAuthToken());

// FogBugz::searchCases(String) functions exactly like the search box in FogBugz
List<FBCase> caseList = fogBugz.searchCases("123");

// Login to GitHub
GitHub github = GHFactory.newGitHub(githubOAuthToken); // Using OAuth (https://github.com/settings/tokens/new)
GitHub otherGh = GHFactory.newGitHub(githubUsername, githubPassword); // Using basic authentication
GHRepo ghRepo = github.getRepo(repoOwner, repoName); // GitHub repository to migrate to

// Migrate caseList to ghRepo
Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).build();
migrator.migrate();
```

## Configuration
The `Migrator` class uses the builder pattern which makes it flexible in terms of customization.

For instance, here's how to define your own labeling function:
```java
Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo)
    .fbCaseLabeler(new FBCaseLabeler() {
        @Override
        public List<GHLabel> getLabels(FBCase fbCase) {
            List<GHLabel> list = new ArrayList<GHLabel>();
            list.add(new GHLabel("F" + fbCase.getId(), "92602c"));
            if (fbCase.getSalesforceCaseId() != 0) {
                list.add(new GHLabel("S" + fbCase.getSalesforceCaseId(), "178cda"));
            }
            list.add(new GHLabel(fbCase.getCategory()));
            list.add(new GHLabel(fbCase.getPriority()));
            return list;
        }
    })
    .build();
```

You can set as many options as you want before building, like so:
```java
Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo)
    .closeIf(new Predicate<FBCase>() {
        @Override
        public boolean test(FBCase fbCase) {
            return !fbCase.getStatus().equals("Active");
        }
    })
    .usernameMap(Collections.singletonMap("Jonathan Sudiaman", "sudiamanj"))
    .postDelay(500L)
    .build();
```

## Troubleshooting

### Why am I seeing the following message?
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

This project uses [SLF4J](http://www.slf4j.org), which allows end users to choose a logging facility at runtime. If you don't care about logging and want to disable this message, simply add [slf4j-nop](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-nop%7C1.7.21%7Cjar) as a dependency. If you want to use SLF4J's logger, use [slf4j-simple](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-simple%7C1.7.21%7Cjar). For more options, see [SLF4J user manual](http://www.slf4j.org/manual.html).
