# LEARNING.md — 플랫폼 전파 체계 실습 시나리오

> 커리큘럼 Day 2-3 의 본문. 순서대로 진행하되, **막힌 지점에서만** 해당 문서 챕터를 연다.
> 저장소 역할은 mavenLocal(`~/.m2/repository`)이 대신한다. 3주차에 Docker Nexus 로 교체.

## 구조

```
bootstrap/          settings 플러그인 — conventions 의 버전을 결정 (핵심 로직은 직접 구현)
conventions/        convention 플러그인 스위트 — 전사 공통 빌드 규칙
example-consumer/   소비자 빌드 — 버전 없이 플러그인만 선언
```

세 빌드는 includeBuild 로 연결되지 않는다. **오직 배포물(mavenLocal)로만** 만난다.
실행은 항상 각 디렉토리에서 `../gradlew <task>`.

---

## 0단계 — 의도된 실패 관찰

목표: "플러그인 해석"이 어느 단계에서, 어떤 순서로, 어디를 뒤지며 일어나는지 에러로 배운다.

- [ ] `cd example-consumer && ../gradlew build` 실행 → **실패해야 정상**
- [ ] 에러 메시지에서 읽어낼 것:
  - 어떤 플러그인 ID 가, 어떤 좌표(마커 아티팩트)로 변환되어 검색됐는가?
  - 어떤 저장소들을 어떤 순서로 뒤졌는가? (settings 의 repositories 순서와 대조)
  - 실패는 빌드 라이프사이클 3단계 중 어디서 일어났는가?
- [ ] `cd bootstrap && ../gradlew publishToMavenLocal` → bootstrap 만 배포
- [ ] 다시 consumer 빌드 → **두 번째 실패** 관찰: 이번엔 어떤 플러그인이 왜 실패하는가?
  (bootstrap 은 적용됐지만 conventions 는 여전히 없음 — println 로그로 확인)
- [ ] 막히면: Dependency Resolution 기초 절, Plugin Marker Artifacts 절

## 1단계 — 킬 스위치

목표: 전사 장애 시 bootstrap 을 즉시 무력화하는 탈출구. 프로퍼티 우선순위 체득.

- [ ] **직접 구현**: `bootstrap/src/main/kotlin/com.example.platform.bootstrap.settings.gradle.kts`
  - `platform.bootstrap.enabled=false` 면 아무것도 안 하고 반환
  - 구현 후 재배포(publishToMavenLocal) 잊지 말 것 — 재배포 없이는 소비자가 옛 코드를 쓴다
- [ ] 검증: `-Pplatform.bootstrap.enabled=false` 로 실행 → bootstrap println 이 "스킵" 을 찍는가
- [ ] 같은 프로퍼티를 4곳에 각각 넣고 우선순위 실측:
  CLI `-P` / consumer `gradle.properties` / `~/.gradle/gradle.properties` / 환경변수 `ORG_GRADLE_PROJECT_...`
- [ ] 막히면: Gradle Properties 챕터의 우선순위 표

## 2단계 — publishToMavenLocal + 핀

목표: `latest.release` 가 무엇을 읽고 어떻게 동작하는지, 핀(소비자 명시)이 이기는 구조.

- [ ] `cd conventions && ../gradlew publishToMavenLocal`
- [ ] **필수 관찰**: `~/.m2/repository/com/example/platform/` 열어서
  - 마커 아티팩트 디렉토리 구조 (플러그인 ID ↔ 좌표 매핑 규칙 확인)
  - `maven-metadata-local.xml` 직접 읽기 — `<latest>`, `<release>`, `<versions>` 태그
- [ ] **직접 구현**: bootstrap 의 `eachPlugin { }` 에서 `com.example.platform.` ID 에 `useVersion(...)`
  - 1차: 고정 `"1.0.0"` → consumer 빌드 성공 확인 (Main.kt 컴파일, 툴체인 21 확인)
  - 2차: `"latest.release"` 로 교체 → conventions 를 `1.1.0` 으로 올려 재배포 → 소비자가 자동으로 받는가
- [ ] **핀 실습**: consumer 가 `id(...) version "1.0.0"` 으로 명시하면?
  - `requested.version` 이 null 인 경우에만 useVersion 하도록 수정 → "명시가 이긴다" 구현
- [ ] 막히면: Declaring Versions, Handling versions which change over time

## 3단계 — 로컬 http 매니페스트 + ENV 분기

목표: "버전 결정권"을 코드 밖(매니페스트)으로 빼기 — 재배포 없이 버전 지정/롤백.

- [ ] 매니페스트 서빙: `docs/manifest/` 에 `versions.txt` (내용: `1.1.0` 한 줄) 만들고
  `python3 -m http.server 8085` 로 서빙
- [ ] **직접 구현**: bootstrap 이 `http://localhost:8085/versions.txt` 를 읽어 useVersion 에 전달
  - ENV `PLATFORM_MANIFEST_URL` 있으면 그 주소, 없으면 기본값
  - 서버가 죽어 있으면? 폴백 정책을 직접 정하고 주석으로 근거 남기기
    (후보: latest.release 폴백 / 마지막 성공값 캐시 / 빌드 실패)
- [ ] 검증: 매니페스트를 `1.0.0` 으로 바꾸고 재빌드 → **재배포 없이** 롤백되는가
- [ ] 서버 내리고 재빌드 → 폴백 동작 확인
- [ ] 주의: settings 단계의 네트워크 호출은 config cache 의 최대 적 — Day 8-9 의 복선. 지금은 동작만.

---

## 역인덱싱 습관

각 단계에서 나온 에러/로그를 그대로 붙여넣고:
"이 동작의 근거가 되는 Gradle 문서 챕터와 절을 알려줘"
