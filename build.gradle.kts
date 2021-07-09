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
    id("org.cqfn.diktat.diktat-gradle-plugin") version "1.0.0-rc.2"
    id("com.palantir.git-version") version "0.12.3" apply (System.getenv("SOURCE_VERSION") == null)
}

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

val diktatVersion = libs.versions.diktat.get()
val ktlintVersion = libs.versions.ktlint.get()

kotlin {
    js(LEGACY).browser {
        repositories {
            mavenCentral()
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
                // todo: kotlin plugin 1.5.20 doesn't support dependencies on `Provider<MinimalExternalModuleDependency>` with configuration lambda
                implementation("org.cqfn.diktat:diktat-common:$diktatVersion") {
                    // exclude to use logback provided by spring
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation("org.cqfn.diktat:diktat-rules:$diktatVersion") {
                    exclude("org.slf4j", "slf4j-log4j12")
                }
                implementation(libs.ktlint.core)
                implementation(libs.ktlint.rulesets.standard)
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(libs.spring.boot.starter.test)
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation(libs.kotlin.js.react)
                implementation(libs.kotlin.js.reactDom)
                implementation(npm("ace-builds", "1.4.11"))
                implementation(npm("jquery", "1.12.4"))
                implementation(npm("react", libs.versions.react.get()))
                implementation(npm("react-dom", libs.versions.react.get()))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

val generateVersionFileTaskProvider = tasks.register("generateVersionFile") {
    val versionsFile = File("$buildDir/generated/src/generated/Versions.kt")

    // heroku sets `SOURCE_VERSION` variable during build, while git repo is unavailable
    // for successful build either .git directory should be present or SOURCE_VERSION should be set
    val gitRevision = System.getenv("SOURCE_VERSION")
        ?: (ext.properties["gitVersion"] as groovy.lang.Closure<String>).invoke()

    inputs.property("project version", version.toString())
    inputs.property("project revision", gitRevision)
    inputs.property("diktat version", diktatVersion)
    inputs.property("ktlint version", ktlintVersion)
    outputs.file(versionsFile)

    doFirst {
        versionsFile.parentFile.mkdirs()
        versionsFile.writeText(
            """
            package generated

            internal const val PROJECT_VERSION = "$version"
            internal const val PROJECT_REVISION = "$gitRevision"
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
    // workaround for gradle >= 7, because some bug with kotlin plugin's `withJava` setting, at least for 1.5.20
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
