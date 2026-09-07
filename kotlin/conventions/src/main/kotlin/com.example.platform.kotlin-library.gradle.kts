// 전사 공통 Kotlin 라이브러리 컨벤션.
// 소비자는 이 플러그인 하나만 적용하면 툴체인/테스트/저장소 설정을 전부 물려받는다.

plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

println("[conventions] kotlin-library 컨벤션 적용됨 (version 1.0.0)")
