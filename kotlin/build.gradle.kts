println("[Configuration Phase] build.gradle.kts 스크립트 읽기 시작")

plugins {
    id("java")
    id("java-library")  // api
}

group = "org.example"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply { plugin("java") }
    apply { plugin("java-library") }
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

dependencies {
    println("[Configuration Phase] Dependencies 설정")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

println("[Configuration Phase] build.gradle.kts 스크립트 읽기 완료")