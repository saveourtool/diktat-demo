# KTlint/diKTat [demo project](https://ktlint-demo.herokuapp.com)

[![Run diKTat](https://github.com/saveourtool/diktat-demo/workflows/Run%20diKTat/badge.svg?branch=master)](https://github.com/cqfn/diktat)

This is simple demo for ktlint and [diktat](https://github.com/cqfn/diktat) project (a number of codestyle rules for Kotlin powered by [KTlint](https://github.com/pinterest/ktlint) framework)
This simple demo is written in Kotlin using Spring Boot, Thymeleaf and Kotlin/JS and shows how a style of your code can be checked with diKTat rule set.

Many thanks for this idea and some design of the site to [YAPF demo project](https://github.com/jpadilla/yapf-online), we appreciate it, but we love Java more than Python (that's why Spring Boot is here instead of flask).

You can try diktat-demo online [right now](https://ktlint-demo.herokuapp.com).

# Usage
1) Open demo at https://ktlint-demo.herokuapp.com or run it locally
2) Write your Kotlin code snippets to the text block at the left.
3) At the bottom choose the mode `fix` or `check` and one  codestyle `ktlint` or `diktat`. In case you have chosen `check` mode you won't see any changes in the code.
4) In case you would like to configure diktat - you can upload diktat configuration. Example can be found [here](https://github.com/cqfn/diKTat/blob/master/diktat-rules/src/main/resources/diktat-analysis.yml)
5) Press submit and see warnings above and fixed file in the right text block.

# How to start this demo app locally
The server will be started on `http://localhost:8082/` main page is `/demo` - you will be redirected there from the host.
To build and start the app simple do the following:
```bash
$ gradle bootRun
```

if you want to have a fat-jar and run it - use gradle command to build the app and then run it as jar file:
```bash
$ gradle build
$ java -jar build/libs/diktat-demo-0.0.1-SNAPSHOT.jar
```