// 플랫폼 bootstrap — 소비자 settings 에 적용되는 settings 플러그인.
// 역할: 소비자가 버전 없이 선언한 conventions 플러그인의 버전을 여기서 결정한다.
//
// ┌─ 실습에서 직접 구현할 것 ─────────────────────────────────────────────
// │ 1단계: 킬 스위치
// │   - gradle property `platform.bootstrap.enabled` 가 false 면
// │     아무것도 하지 않고 즉시 반환 (전사 장애 시 탈출구)
// │   - 힌트: settings 안에서는 providers.gradleProperty("...") 사용
// │
// │ 2단계: 버전 해석
// │   - pluginManagement 의 resolutionStrategy.eachPlugin { } 에서
// │     requested.id 가 "com.example.platform." 으로 시작하면 useVersion(...)
// │   - 먼저 고정 버전("1.0.0")으로 동작 확인 → "latest.release" 로 교체
// │   - 소비자가 버전을 명시(핀)했다면? requested.version 관찰해볼 것
// │
// │ 3단계: 매니페스트
// │   - 로컬 http 서버에서 버전 문자열을 읽어 useVersion 에 전달
// │   - ENV 로 매니페스트 URL 분기, 실패 시 폴백 동작 정의
// └───────────────────────────────────────────────────────────────────────

println("[bootstrap] 적용됨 — 아직 버전 해석 로직 없음 (0단계)")
