plugins {
    java
    `maven-publish`
    kotlin("multiplatform") version "1.3.70"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.springframework.boot") version "2.3.1.RELEASE"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://central.artipie.com/akuleshov7/diktat")
}

val kotlinVersion = "1.3.70"
val diktatVersion = "1.0.0"
val ktlintVersion = "0.37.1-fork"
val springBootVersion = "2.3.1.RELEASE"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ord.cqfn.diktat"
            version = diktatVersion
            description = "diktat-demo"
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    js {
        browser { }
    }

    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-thymeleaf") {
                    exclude("org.springframework.boot", "spring-boot-starter-logging")
                }
                implementation("org.springframework.boot:spring-boot-starter-web") {
                    exclude("org.springframework.boot", "spring-boot-starter-logging")
                }
                implementation("org.cqfn.diktat:diktat-common:$diktatVersion") {
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation("com.pinterest.ktlint:ktlint-core:$ktlintVersion")
                implementation("org.cqfn.diktat:diktat-rules:$diktatVersion") {
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("stdlib-js"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
                implementation(kotlin("test"))
            }
        }

//        js()
    }
}
