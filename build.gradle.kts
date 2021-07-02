import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("com.github.ben-manes.versions") version "0.39.0"
    java
    `maven-publish`
    kotlin("multiplatform") version "1.5.20"
    kotlin("plugin.spring") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    id("org.springframework.boot") version "2.4.5"
    id("org.cqfn.diktat.diktat-gradle-plugin") version "1.0.0-rc.1"
    id("com.palantir.git-version") version "0.12.3" apply (System.getenv("SOURCE_VERSION") == null)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
}

val diktatVersion = libs.versions.diktat.get()
val ktlintVersion = libs.versions.ktlint.get()

val reactVersion = "17.0.2"
val kotlinReactVersion = "17.0.2-pre.156-kotlin-1.5.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ord.cqfn.diktat"
            version = project.version as String
            description = "diktat-demo"
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    js(LEGACY).browser {
        repositories {
            mavenCentral()
            maven("https://kotlin.bintray.com/js-externals")  // for kotlin-js-jquery
            maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers/")
        }
    }

    jvm {
        repositories {
            mavenCentral()
            maven("https://repo.spring.io/milestone")
        }
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(libs.spring.boot.starter.web)
                implementation(libs.spring.fu.kofu)
                implementation("org.cqfn.diktat:diktat-common:$diktatVersion") {
                    // exclude to use logback provided by spring
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation("org.cqfn.diktat:diktat-rules:$diktatVersion") {
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation("com.pinterest.ktlint:ktlint-core:$ktlintVersion")
                implementation("com.pinterest.ktlint:ktlint-ruleset-standard:$ktlintVersion")
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(libs.spring.boot.starter.test)
            }
        }

        getByName("jsMain") {
            dependencies {
                compileOnly("kotlin.js.externals:kotlin-js-jquery:3.2.0-0")
                implementation(npm("ace-builds", "1.4.11"))
                implementation("org.jetbrains:kotlin-react:$kotlinReactVersion")
                implementation("org.jetbrains:kotlin-react-dom:$kotlinReactVersion")
                implementation(npm("react", reactVersion))
                implementation(npm("react-dom", reactVersion))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
            }
        }
    }
}

val generateVersionFileTaskProvider = tasks.register("generateVersionFile") {
    val versionsFile = File("$buildDir/generated/src/generated/Versions.kt")

    outputs.file(versionsFile)

    doFirst {
        // heroku sets `SOURCE_VERSION` variable during build, while git repo is unavailable
        // for successful build either .git directory should be present or SOURCE_VERSION should be set
        val gitRevisionEnv = System.getenv("SOURCE_VERSION") ?: run {
            ext.properties["gitVersion"].let { it as groovy.lang.Closure<String> }.invoke()
        }
        versionsFile.parentFile.mkdirs()
        versionsFile.writeText(
            """
            package generated

            internal const val PROJECT_VERSION = "$version"
            internal const val PROJECT_REVISION = "$gitRevisionEnv"
            internal const val DIKTAT_VERSION = "$diktatVersion"
            internal const val KTLINT_VERSION = "$ktlintVersion"

            """.trimIndent()
        )
    }
}
val generatedKotlinSrc = kotlin.sourceSets.create("generated") {
    kotlin.srcDir("$buildDir/generated/src")
}
kotlin.sourceSets.getByName("jsMain").dependsOn(generatedKotlinSrc)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().forEach {
    it.dependsOn(generateVersionFileTaskProvider)
}

tasks.getByName("jvmMainClasses") {
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    doLast {
        mkdir("build/processedResources/jvm/main/static")
        copy {
            from("$buildDir/distributions")
            into("build/processedResources/jvm/main/static")
        }
    }
}

tasks.getByName<Copy>("jvmProcessResources") {
    // workaround for gradle >= 7, because we have created resources dir when copying frontend bundle
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.getByName<BootJar>("bootJar") {
    mainClass.set("org.cqfn.diktat.demo.DiktatDemoApplicationKt")
    requiresUnpack("**/kotlin-compiler-embeddable-*.jar")
}
tasks.getByName<BootRun>("bootRun") {
    mainClass.set("org.cqfn.diktat.demo.DiktatDemoApplicationKt")
}

diktat {
    inputs = files("src/*/kotlin/**/*.kt")
}
