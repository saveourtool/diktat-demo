# diKTat POC demo project
This is simple demo for diktat project (a number of codestyle rules for Kotlin powered by [KTlint](https://github.com/pinterest/ktlint) framework)
This simple demo is written on Java/Kotlin with the usage of Spring Boot and Thymeleaf and shows how a style of your code can be checked with diKTat rule set.

Many thanks for this idea and some design of the cite to [YAPF demo project](https://github.com/pinterest/ktlint), we appreciate it, but love Java more than Python (that's why Spring Boot is here instead of flask)

# Current problems
diKTat uses customized code of KTlint that was not yet promoted to it's mainline, that's why there can be some problems with building this demo project (some code won't be compiled properly due to modified KTlint code).
That's why check diKTat repository first if you want to build and start this project.

As diKTat is not released yet there can be some problems related to missing artifacts of the diKTat in maven central. Check the existing diKTat code first or wait for the release - it will be made soon.

# How to start this demo app

The server will be started on `http://localhost:8082/` main page is `/demo` - you will be redirected there from the host.
To build and start the app simple do the following:
```bash
$ mvn clean install
$ mvn spring-boot:run
```

if you want to have a fat-jar and run it - use assembly plugin to build this app and simply run it as jar file:
```bash
$ mvn clean install
$ java -jar target/demo-0.0.1-SNAPSHOT.jar
```

# What should be done in the nearest time
1) We will need to fix concurrent issue that can appear with the code now.
2) We should support different KTlint rule sets that are not supported by for now (like ktlint-standard)
3) Release and hosting of this site to some host