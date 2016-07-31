# Tenorite TetriNET Server [![Build Status](https://travis-ci.org/jsiebens/tenorite.svg?branch=master)](https://travis-ci.org/jsiebens/tenorite)

Tenorite is a new TetriNET server written in Java, 
built with some modern frameworks such as [Netty](http://netty.io), [Akka](http://akka.io), [Spring Boot](http://projects.spring.io/spring-boot)

### Features

* TetriNET and TetriFAST support
* Elo Rating based winlists
* Multiple channels
* Multiple game modes:
    * Classic
    * Pure
    * Sticks & Squares
    * Jelly
    * Sprint
    * Seven 'o Four
    * GBomb
    * Break Out!
* Sudden death
* Game statistics (dropping rate, lines cleared, specials used, combos made, ...)
* Player statistics (games played/won, total blocks dropped, total lines cleared, ...)
* Badges
* Replay a game in the browser
* ...

### Requirements

* Java 8+
* MongoDB

### Running the server

Tenorite TetriNET server is built with Spring Boot, so an executable jar is available to run the server.

* Install MongoDB
* Make sure Java 8 is available
* Download the latest release jar [1.1.0](https://github.com/jsiebens/tenorite/releases/download/v1.1.0/tenorite-server-1.1.0.jar)
* Start the server using `java -jar`, for example:
    * `java -jar tenorite-server-1.1.0.jar`
* MongoDB connection can be configured using command line properties or by using an `application.properties` file, for example:
    * `java -jar tenorite-server-1.1.0.jar --spring.data.mongodb.uri=mongodb://localhost:27017/tenorite`
    * more information can be found [here](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
    * other available properties can be found at [Appendix A. Common Application Properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) of the Spring Boot Documentation