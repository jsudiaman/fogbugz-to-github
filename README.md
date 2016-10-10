# FogBugz to GitHub (FB2GH)
**FB2GH** is designed to help you programatically migrate your [FogBugz cases](https://www.fogcreek.com/fogbugz/) into [GitHub issues](https://guides.github.com/features/issues/). It also can alternatively serve as a lightweight Java API for FogBugz or GH Issues.

## Usage

### Creating a FogBugz instance
First, we need to be able to access the FogBugz API. If you have an [API token](http://help.fogcreek.com/8447/how-to-get-a-fogbugz-xml-api-token), then you can obtain a FogBugz instance like so:

`FogBugz fb = new FogBugz(baseURL, authToken);`

Where **baseURL** is the URL of your FogBugz instance (without default.asp) and **authToken** is your API token.

If you don't have an API token, you may use this other constructor which generates one for you:

`FogBugz fb = new FogBugz(baseURL, email, password);`

Then afterwards, you can call `fb.getAuthToken();` to get the generated token.

### Creating a GitHub instance
Now let's access the GitHub API. Similar to FogBugz, you can construct a GitHub instance using an [OAuth Token](https://developer.github.com/v3/oauth/) like so:

`GitHub gh = new GitHub(token);`

or using your username and password:

`GitHub gh = new GitHub(String username, String password);`

(Using a token is generally safer.)

## Troubleshooting

### Why am I getting the following error?
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

This project uses [SLF4J](http://www.slf4j.org), which allows end users to choose a logging facility at runtime. If you don't care about logging and want to disable this message, simply add [slf4j-nop](https://mvnrepository.com/artifact/org.slf4j/slf4j-nop) to your CLASSPATH. If you want to use SLF4J's logger, use [slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple). For more options, see [SLF4J user manual](http://www.slf4j.org/manual.html).
