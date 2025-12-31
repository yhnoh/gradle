import org.gradle.kotlin.dsl.support.KotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

println("[Configuration Phase] build.gradle.kts 스크립트 읽기 시작")

plugins {
    kotlin("jvm") version "2.2.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply (plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        println("[Configuration Phase] Dependencies 설정")

        testImplementation(kotlin("test"))
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.register("hello") {
    println("[Configuration Phase] hello task 등록")
    doFirst {
        println("[Execution Phase] hello task 실행 doFirst 블록")
    }
    doLast {
        println("[Execution Phase] hello task 실행 doLast 블록")
    }
}

println("[Configuration Phase] build.gradle.kts 스크립트 읽기 완료")