pluginManagement {
    repositories {
        mavenLocal() // 실습용 Nexus 대체 — bootstrap/conventions 마커가 여기서 해석된다
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // 0단계 의도된 실패 지점:
    // bootstrap 이 아직 어디에도 배포되지 않았으므로 이 줄에서 해석이 실패한다.
    // 에러 메시지를 그대로 읽고 "어디를 뒤졌는지" 목록을 관찰할 것.
    id("com.example.platform.bootstrap") version "1.0.0"
}

rootProject.name = "example-consumer"
