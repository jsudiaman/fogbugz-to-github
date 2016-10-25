# FogBugz to GitHub (FB2GH)
**FB2GH** is designed to help you programatically migrate your [FogBugz cases](https://www.fogcreek.com/fogbugz/) into [GitHub issues](https://guides.github.com/features/issues/). It also can alternatively serve as a lightweight Java API for FogBugz or GH Issues.

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
GitHub github = GHFactory.newGitHub(githubOAuthToken);                // Using OAuth (https://github.com/settings/tokens/new)
GitHub otherGh = GHFactory.newGitHub(githubUsername, githubPassword); // Using basic authentication
GHRepo ghRepo = github.getRepo(repoOwner, repoName);                  // GitHub repository to migrate to

// Migrate caseList to ghRepo
Migrator migrator = new Migrator.Builder(fogBugz, caseList, ghRepo).get();
migrator.migrate();
```

## Troubleshooting

### Why am I getting the following error?
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

This project uses [SLF4J](http://www.slf4j.org), which allows end users to choose a logging facility at runtime. If you don't care about logging and want to disable this message, simply add [slf4j-nop](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-nop%7C1.7.21%7Cjar) to your CLASSPATH. If you want to use SLF4J's logger, use [slf4j-simple](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-simple%7C1.7.21%7Cjar). For more options, see [SLF4J user manual](http://www.slf4j.org/manual.html).
