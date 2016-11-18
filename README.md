# FogBugz to GitHub (FB2GH)
![Logo](http://sudicode.com/images/fb2gh-logo.png)  
[![CircleCI](https://circleci.com/gh/sudiamanj/fogbugz-to-github.svg?style=svg)](https://circleci.com/gh/sudiamanj/fogbugz-to-github)  
**FB2GH** is designed to help you programmatically migrate your [FogBugz cases](https://www.fogcreek.com/fogbugz/) into [GitHub issues](https://guides.github.com/features/issues/).

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

## Download
JAR File:  
[fb2gh-1.0.jar](https://github.com/sudiamanj/maven-repo/raw/master/com/sudicode/fb2gh/1.0/fb2gh-1.0.jar)

Maven:  
```xml
<repositories>
    <repository>
        <id>sudiamanj</id>
        <name>maven-repo</name>
        <url>https://github.com/sudiamanj/maven-repo/raw/master/</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.sudicode</groupId>
        <artifactId>fb2gh</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

## Configuration
The `Migrator` class uses the [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) which makes it flexible in terms of customization.

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

This project uses [SLF4J](http://www.slf4j.org), which allows end users to choose a logging facility at runtime. If you don't care about logging and want to disable this message, simply add [slf4j-nop](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-nop%7C1.7.21%7Cjar) to your CLASSPATH. If you want to use SLF4J's logger, use [slf4j-simple](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-simple%7C1.7.21%7Cjar). For more options, see [SLF4J user manual](http://www.slf4j.org/manual.html).

### Why am I getting an AssertionError?
These are usually thrown by the GitHub API, looking something like this:
```
Exception in thread "main" java.lang.AssertionError: HTTP response status is not equal to 200:
404 Not Found [https://api.github.com/repos/foo/bar/labels]
Server: GitHub.com
... 
{"message":"Not Found","documentation_url":"https://developer.github.com/v3"}>
	at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
	at com.jcabi.http.response.RestResponse.assertStatus(RestResponse.java:111)
	at com.jcabi.github.RtValuePagination$Items.fetch(RtValuePagination.java:193)
	at com.jcabi.github.RtValuePagination$Items.hasNext(RtValuePagination.java:179)
	at com.sudicode.fb2gh.github.GHRepo.getLabels(GHRepo.java:111)
	at CallingClass.main(CallingClass.java:30)
```
This means that you're trying to do something which wouldn't be possible on GitHub (e.g. access a nonexistent repo, add duplicate labels, etc). Read through your error message to figure out exactly what's going on. If the error persists, please [raise an issue](https://github.com/sudiamanj/fogbugz-to-github/issues).
