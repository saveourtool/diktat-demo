plugins {
    java
    `maven-publish`
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.springframework.boot") version "2.3.1.RELEASE"
}

repositories {
    jcenter()
}

val kotlinVersion = "1.3.72"
val diktatVersion = "1.0.2"
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
        repositories {
            mavenLocal()
            mavenCentral()
            maven { url = uri("https://central.artipie.com/akuleshov7/diktat") }
        }
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
                    // todo: choose logging framework
                    exclude("ch.qos.logback", "logback-classic")
                }
                implementation("org.springframework.boot:spring-boot-starter-web") {
                    exclude("ch.qos.logback", "logback-classic")
                }
                implementation("org.cqfn.diktat:diktat-common:$diktatVersion")
                implementation("com.pinterest.ktlint:ktlint-core:$ktlintVersion")
                implementation("com.pinterest.ktlint:ktlint-ruleset-standard:$ktlintVersion")
                implementation("org.cqfn.diktat:diktat-rules:$diktatVersion")
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
                implementation(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(npm("ace-builds", "1.4.11"))
            }
        }
    }
}

tasks.getByName("jvmMainClasses").dependsOn(tasks.getByName("jsMainClasses"))
tasks.getByName("jvmMainClasses") {
    // todo proper way to include this in spring boot
    doLast {
        mkdir("build/processedResources/jvm/main/static/js")
        copy {
            from("build/js/packages/diktat-demo/kotlin")
            include("*.js", "*.js.map")
            into("build/processedResources/jvm/main/static/js")
        }
    }
}
