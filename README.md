# FogBugz to GitHub (FB2GH)
**FB2GH** is designed to help you programatically migrate your [FogBugz cases](https://www.fogcreek.com/fogbugz/) into [GitHub issues](https://guides.github.com/features/issues/). It also can alternatively serve as a lightweight Java API for FogBugz or GH Issues.

## Usage

TBD

## Troubleshooting

### Why am I getting the following error?
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

This project uses [SLF4J](http://www.slf4j.org), which allows end users to choose a logging facility at runtime. If you don't care about logging and want to disable this message, simply add [slf4j-nop](https://mvnrepository.com/artifact/org.slf4j/slf4j-nop) to your CLASSPATH. If you want to use SLF4J's logger, use [slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple). For more options, see [SLF4J user manual](http://www.slf4j.org/manual.html).
