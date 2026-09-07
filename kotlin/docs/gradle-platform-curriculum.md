# Gradle 플랫폼 개발자 커리큘럼

> 목표: "유저 가이드 전체 이해"가 아니라, **1등급 5개 주제를 자기 코드(bootstrap/conventions)로 설명할 수 있는 상태**.
> 방법론: 문서를 순서대로 읽지 않는다. **실습 → 막힘 → 해당 챕터** 루프로만 읽는다.
> 기간: 실습 포함 2주 (업무 병행 기준) + 인프라 트랙(Nexus 운영) 1주. 이후는 필요 기반 참조.

---

## 0. 학습 지도 (전체 구조)

```
[1등급 — 정독 + 실습]                        [내 시스템에서의 대응물]
 Build Lifecycle              ───────────►  bootstrap이 settings 플러그인인 이유
 Custom Plugins / Convention  ───────────►  convention 플러그인 스위트 그 자체
 Dependency Resolution 계열    ───────────►  latest.release, 캐시 TTL, useVersion
 Configuration Cache          ───────────►  리뷰 1번 (최대 미해결 리스크)
 Lazy Configuration (Provider)───────────►  providers.gradleProperty, convention()

[2등급 — 위치만 기억]
 Gradle Properties / Publishing / Structuring Builds(includeBuild)
 Version Catalogs / TestKit / Init Scripts

[인프라 트랙 — Gradle 문서 밖, 별도 정독]
 Nexus 운영                   ───────────►  latest.release 가 읽는 maven-metadata 의 주인
                                            (아래 "3주차 — Nexus 운영" 참조)

[3등급 — 버림]
 언어별 튜토리얼, IDE 연동, 개별 코어 플러그인 레퍼런스, Ant
```

문서 루트: https://docs.gradle.org/current/userguide/userguide.html

---

## 1주차 — 라이프사이클과 해석 메커니즘

### Day 1: Build Lifecycle 정독 (유일하게 "먼저 읽는" 챕터)
- [ ] 읽기: Build Lifecycle 챕터 (반나절)
- [ ] 확인 질문 (답을 자기 말로 쓸 것):
  - initialization / configuration / execution 각각에서 무엇이 일어나는가?
  - settings 플러그인과 project 플러그인은 각각 어느 단계의 존재인가?
  - `settings.gradle.allprojects { }` 는 왜 "실행"이 아니라 "예약"인가?
- [ ] Claude Code 실습: 스켈레톤의 `example-consumer`에서
  `./gradlew help --info` 출력을 붙여넣고 "이 로그에서 3단계 경계가 어디인지 표시해줘" 요청

### Day 2-3: LEARNING.md 실습 0~3단계 (막힐 때만 문서)
- [ ] 0단계: 의도된 실패 관찰 → 막히면: *Dependency Resolution* 기초 절
- [ ] 1단계: 킬 스위치 → 막히면: *Gradle Properties* (우선순위 표 확인)
- [ ] 2단계: publishToMavenLocal + 핀 → 막히면: *Declaring Versions*, maven-metadata 개념
  - [ ] 필수 관찰: `~/.m2/repository` 열어서 `maven-metadata-local.xml` 직접 읽기
- [ ] 3단계: 로컬 http 매니페스트 + ENV 분기 → 캐시 폴백 확인
- [ ] Claude Code 실습: 각 단계에서 나온 에러/로그를 그대로 붙여넣고
  "이 동작의 근거가 되는 Gradle 문서 챕터와 절을 알려줘" 로 역인덱싱

### Day 4: Dynamic Versions & 캐싱 정독
- [ ] 읽기: *Handling versions which change over time* (dynamic/changing versions)
- [ ] 확인 질문:
  - `latest.release` 와 `latest.integration` 의 차이는?
  - 동적 버전 캐시 TTL의 기본값과 조정 방법은?
  - `--refresh-dependencies` 는 정확히 무엇을 다시 하는가?
- [ ] 실습: `cacheDynamicVersionsFor` 를 1분으로 줄이고 코어 재배포 → 전파 시간 실측
- [ ] 우리 시스템 연결: "CI(캐시 없음)는 즉시, 로컬은 TTL 이내" 를 실측으로 재현

### Day 5: Lazy Configuration (Provider API)
- [ ] 읽기: *Lazy Configuration* 챕터
- [ ] 확인 질문:
  - `Provider` / `Property` 가 즉시값 대신 쓰이는 이유는? (config cache, 순서 문제)
  - `convention()` 과 `set()` 의 차이 — 소비자 명시가 이기는 메커니즘은?
  - `providers.gradleProperty` vs `System.getProperty` — 뭐가 다른가?
- [ ] Claude Code 실습: 실물 bootstrap 코드를 열고
  "이 파일에서 eager 하게 값을 읽는 곳을 전부 찾아 Provider 기반으로 바꾸는 diff 제안해줘"

---

## 2주차 — 플러그인 개발과 최대 리스크 해소

### Day 6-7: Custom Plugins + Convention Plugins 정독
- [ ] 읽기: *Developing Custom Gradle Plugins*, *Sharing Build Logic between Subprojects*
- [ ] 확인 질문:
  - precompiled script plugin vs binary plugin — 우리 conventions 는 어느 쪽이고 왜?
  - extension 을 만들고 소비자 입력을 받는 표준 패턴은?
  - 플러그인 ID ↔ 마커 아티팩트 매핑 규칙은? (bootstrap 의 useVersion 이 먹히는 이유)
- [ ] 실습: LEARNING.md 5단계 — 코어에 규칙 1개 추가(off 스위치 포함) → 매니페스트로 전파
- [ ] Claude Code 실습: "이 규칙에 `jobis.convention.<기능>.enabled` 스위치를
  프로퍼티 컨벤션 규칙 6개에 맞게 추가해줘" (컨벤션 문서를 컨텍스트로 제공)

### Day 8-9: Configuration Cache — 리뷰 1번을 실전 과제로
- [ ] 읽기: *Configuration Cache* 챕터 (특히 Requirements 절, ValueSource 절)
- [ ] 확인 질문:
  - config cache 가 히트하면 settings 스크립트/플러그인은 재실행되는가?
  - 왜 `System.getenv` 직접 호출이 문제인가?
  - `ValueSource` 는 무엇을 해결하는 통로인가?
- [ ] **실전 과제 (이 커리큘럼의 보스전)**: 실물 bootstrap 을 config cache 켠 상태로 검증
  - [ ] `--configuration-cache` 로 2회 연속 빌드 → 히트 시 매니페스트 갱신이 전파되는지 확인
  - [ ] 문제 확인되면: env 읽기를 `providers.environmentVariable` 로, 매니페스트 조회를
        ValueSource 로 감싸는 수정
- [ ] Claude Code 실습: 실패 리포트(`build/reports/configuration-cache/...`)를 붙여넣고
  원인 분석 → 수정 diff → 재검증 루프

### Day 10: 함정 목록 점검 + Best Practices
- [ ] 읽기: 공식 *Best Practices* 섹션 훑기
- [ ] 실물 코드 자가 점검 체크리스트:
  - [ ] configuration 단계 조기 의존성 해석 없음? (bootstrap 은 의도적 예외 — 근거 문서화)
  - [ ] `System.getenv` / `System.getProperty` 직접 호출 없음?
  - [ ] `tasks.all` / `create` 대신 `configureEach` / `register` 사용?
  - [ ] cross-project 직접 접근 (`project(":x").tasks...`) 없음?
  - [ ] `afterEvaluate` 최소화? 각 사용처에 이유 주석 있음?
  - [ ] 플러그인 의존성 최소? (소비자 클래스패스 오염 없음)
- [ ] Claude Code 실습: "이 체크리스트로 conventions 저장소 전체를 감사하고 위반 목록을 표로 만들어줘"

---

## 3주차 — Nexus 운영 (인프라 트랙)

> 위치: Gradle 유저 가이드 밖. 교재는 Sonatype Nexus Repository 공식 문서 + 우리 Nexus 실물.
> 전제: bootstrap 의 `latest.release` 는 결국 **Nexus 가 만들어주는 maven-metadata.xml** 을 읽는다.
> Nexus 를 모르면 전파 체계의 절반만 아는 것이다.
> 문서 루트: https://help.sonatype.com/en/sonatype-nexus-repository.html

### Day 11: 저장소 모델 — hosted / proxy / group
- [ ] 읽기: Repository Management 개념 절 (hosted, proxy, group 차이)
- [ ] 확인 질문:
  - 소비자 빌드에 group URL 하나만 노출하는 이유는? (repositories 순서 문제와 어떻게 연결되는가)
  - proxy 저장소의 원격 아티팩트 캐시는 언제 갱신되는가? (negative cache 포함)
  - releases / snapshots 를 hosted 로 분리하는 이유는? (version policy 가 강제하는 것)
- [ ] 실습: 우리 Nexus 관리 화면에서 저장소 목록 열기 → 각 저장소의 타입/policy/
      group 멤버십을 표로 정리 (이 표가 온보딩 문서 1페이지가 된다)
- [ ] 우리 시스템 연결: conventions 마커 아티팩트가 어느 hosted 에 올라가고,
      소비자는 어느 group 을 통해 받는지 경로를 끝까지 추적

### Day 12: maven-metadata.xml — latest.release 의 근원
- [ ] 읽기: Maven metadata 개념 + Nexus 의 "Rebuild Maven Metadata" 태스크 문서
- [ ] 확인 질문:
  - `<latest>` 와 `<release>` 태그의 차이는? Gradle `latest.release` 는 어느 쪽을 읽는가?
  - metadata 는 언제 갱신되는가? (배포 시 자동 vs 수동 rebuild 가 필요한 경우)
  - metadata 가 깨지면 (버전 목록 불일치) 소비자 빌드에 정확히 무슨 일이 생기는가?
- [ ] 실습: `curl` 로 우리 코어 아티팩트의 maven-metadata.xml 직접 받아서 읽기 →
      2단계에서 본 `maven-metadata-local.xml` 과 구조 비교
- [ ] 실습: 구버전 컴포넌트 하나를 테스트 저장소에서 삭제 → metadata 가 자동 갱신되는지,
      rebuild 태스크가 필요한지 실측
- [ ] Claude Code 실습: metadata XML 을 붙여넣고 "Gradle 이 latest.release 해석 시
      이 파일에서 어떤 순서로 뭘 읽는지 설명해줘" 로 역인덱싱

### Day 13: 배포 정책 — 불변성과 재배포 금지
- [ ] 읽기: Deployment Policy 설정 문서 (allow redeploy / disable redeploy / read-only)
- [ ] 확인 질문:
  - release 저장소에서 redeploy 를 금지해야 하는 이유는? (동일 버전 다른 바이트 = 캐시 지옥)
  - 잘못 배포한 release 버전의 올바른 처리는? — 삭제 후 재배포가 아니라 **새 버전 roll-forward** 인 이유
  - snapshot 은 왜 redeploy 가 정상인가?
- [ ] 우리 시스템 연결: "롤백 리허설" (운영 방법론 절) 과 연결 —
      매니페스트 revert 는 즉시, Nexus 컴포넌트 삭제는 최후 수단인 이유를 캐시 TTL 로 설명
- [ ] 사고 시나리오 작성: "코어 1.4.0 이 깨진 채 배포됨" → 대응 절차를
      (1) 매니페스트 계열 (2) latest.release 계열 각각에 대해 문서화
      — latest.release 계열은 삭제+metadata rebuild 없이는 못 멈춘다는 것이 핵심 (킬 스위치의 존재 이유)

### Day 14: 정리 정책과 디스크 — cleanup policy
- [ ] 읽기: Cleanup Policies 문서 (조건: last downloaded, published before, version 수)
- [ ] 확인 질문:
  - snapshot 은 몇 개 / 며칠 보관이 적정한가? 우리 CI 배포 빈도 기준으로 계산
  - release 는 cleanup 대상에서 제외해야 하는가? (재현 가능 빌드 관점)
  - proxy 저장소의 미사용 캐시 정리는 소비자에게 어떤 위험이 있는가?
- [ ] 실습: 테스트 저장소에 cleanup policy 하나 만들어 dry-run(예상 대상 미리보기) →
      실제 적용 전 삭제 목록 검토하는 습관
- [ ] 함정: cleanup 이 지운 버전이 어느 팀 lock 파일에 박혀 있으면 그 팀 빌드가 깨진다 —
      정리 전 "최근 N일 다운로드 로그" 확인 절차를 정책에 포함

### Day 15: 권한 / CI 계정 / 장애 대응
- [ ] 읽기: Security(roles, privileges, user tokens) + Backup/Restore 문서
- [ ] 확인 질문:
  - 배포 권한과 읽기 권한을 분리하는 최소 role 구성은? (개발자 = 읽기, CI = 쓰기)
  - CI 에는 왜 개인 계정이 아니라 서비스 계정 + user token 인가?
  - Nexus 가 죽으면 소비자 빌드는 언제 깨지는가? — Gradle 로컬 캐시 히트 시 vs 미스 시,
    dynamic version TTL 만료 시. `--offline` 은 어디까지 구해주는가?
  - blob store 백업과 DB 백업이 **한 쌍**이어야 하는 이유는?
- [ ] 실습: (스테이징에서) Nexus 접근 차단 후 소비자 빌드 실행 → 깨지는 지점과
      에러 메시지 채집 → "Nexus 장애 시 개발자 안내" 문서 1장 작성
- [ ] Claude Code 실습: 우리 role/권한 목록을 붙여넣고
      "최소 권한 원칙 위반과 과잉 권한을 표로 정리해줘"

---



> 유저 가이드에는 없다. 이 시스템을 굴리며 세운 규칙들이 교재다.

- [ ] **ADR 작성**: "bootstrap 은 latest.release, conventions 는 매니페스트" 결정과 전제
      (bootstrap 다이어트 불변식)를 ADR 로 저장소에 커밋
- [ ] **프로퍼티 컨벤션 문서화**: 규칙 6개 + 공개 프로퍼티 표 → 온보딩 가이드에 포함
- [ ] **롤백 리허설**: 일부러 문제 버전 배포 → 매니페스트 revert → 복구 시간 실측 → SLA 숫자 확보
- [ ] **텔레메트리 1단계**: projectsEvaluated 에 JSON 한 줄 로그 → 로그 수집기 쿼리로 버전 대시보드
- [ ] **관찰 학습**: nebula-plugins 의 gradle-resolution-rules / dependency-recommender
      닫힌 이슈 10개 읽기 — "중앙 규칙이 빌드를 깼다" 류 리포트와 대응 방식
- [ ] **파일럿 회고**: 파일럿 팀에 "뭐가 걱정되냐" 먼저 묻기 → 설계 반영 기록 남기기

---

## Claude Code 활용 수칙

1. **컨텍스트를 먼저 준다**: 작업 전에 이 커리큘럼 + 프로퍼티 컨벤션 문서 + ADR 을 읽게 할 것.
   규칙을 모르는 상태의 제안은 컨벤션을 깨기 쉽다.
2. **diff 로 받고, 머지 전 직접 읽는다**: 특히 bootstrap 은 전사에 자동 전파되는 코드 —
   생성 코드를 이해 없이 머지하지 않는다 (이해가 이 커리큘럼의 목적이다).
3. **검증 명령까지 요구한다**: "수정해줘" 가 아니라 "수정하고, 검증하는 gradlew 명령과
   기대 출력까지 알려줘" 로 요청.
4. **막힌 질문은 문서 역인덱싱으로**: 에러/로그를 주고 "근거가 되는 공식 문서 위치" 를 묻는
   습관 — 답과 함께 1등급 지식이 쌓인다.

---

## 완료 기준 (자가 시험)

아래 8문항을 문서 없이, 자기 코드를 예로 들어 설명할 수 있으면 수료:

1. bootstrap 이 settings 플러그인이어야 하는 이유를 빌드 라이프사이클로 설명하라.
2. `latest.release` 가 Nexus 의 무엇을 읽어 어떻게 동작하며, 캐시 TTL 이 전파에 미치는 영향은?
3. `eachPlugin { useVersion() }` 이 동작하는 원리(플러그인 ID → 마커 아티팩트)를 설명하라.
4. config cache 히트 시 우리 bootstrap 에 생기는 문제와 ValueSource 해법을 설명하라.
5. `convention()` 과 `set()` 의 차이가 "명시가 이긴다" 원칙을 어떻게 구현하는지 설명하라.
6. conventions 를 latest.release 로 하지 않는 이유를 사고 시나리오 3개로 설명하라.
7. "코어 1.4.0 이 깨진 채 배포됨" 상황에서 매니페스트 계열과 latest.release 계열의
   대응 절차 차이를 Nexus 동작(metadata, 캐시)까지 포함해 설명하라.
8. release 저장소 redeploy 금지와 roll-forward 원칙이 왜 한 쌍인지,
   Gradle 캐시와 Nexus metadata 관점에서 설명하라.
