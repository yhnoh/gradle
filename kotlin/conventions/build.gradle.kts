plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "com.example.platform"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // convention 플러그인이 kotlin("jvm") 을 적용하려면
    // KGP 가 이 빌드의 클래스패스에 있어야 한다 (소비자에게도 전이됨)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
}
