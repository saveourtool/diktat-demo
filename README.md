# KTlint/diKTat demo project
This is simple demo for ktlint and diktat project (a number of codestyle rules for Kotlin powered by [KTlint](https://github.com/pinterest/ktlint) framework)
This simple demo is written on Java/Kotlin with the usage of Spring Boot and Thymeleaf and shows how a style of your code can be checked with diKTat rule set.

Many thanks for this idea and some design of the cite to [YAPF demo project](https://github.com/pinterest/ktlint), we appreciate it, but we love Java more than Python (that's why Spring Boot is here instead of flask)

# Usage
1) Write your Kotlin code snippets to the text block at the left.
2) At the bottom choose the mode `fix` or `check` and one  codestyle `ktlint` or `diktat`. In case you have chosen `check` mode you won't see any changes in the code.
3) In case you would like to configure diktat - you can upload diktat configuration. Example can be found [here](https://github.com/cqfn/diKTat/blob/master/diktat-rules/src/main/resources/diktat-analysis.yml)
4) Press submit and see warnings above and fixed file in the right text block.

# Current problems
This demo can use some old versions of ktlint framework and diktat rule set.

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


