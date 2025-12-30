
### Gradle
- Gradle은 빌드 자동화 도구로, 소프트웨어 프로젝트의 빌드, 테스트, 배포 과정을 자동화


### Gradle Build Lifecycle

![img.png](img.png)
#### 1. Initialization Phase (초기화)
- 프로젝트 구조를 분석하고 어떤 프로젝트가 빌드에 포함될지 결정
    - `settings.gradle.kts` 파일을 찾아, Settings 객체 생성
    - 해당 객체를 통해서 프로젝트 구조 파악 이후, Project 객체 생성
#### 2. Configuration Phase (구성)
- 빌드 파일을 확인한 이후 요청한 Task에 대하여 Task Graph 생성
  - `build.gradle.kts` 파일을 읽고, 각 프로젝트에 대한 Task와 설정을 구성

#### 3. Execution Phase (실행)
- 구성된 Task Graph에 따라 실제 Task 실행
- 실제 컴파일 및 테스트 패키징등의 작업 수행

> [gradle > build_lifecycle](https://docs.gradle.org/current/userguide/build_lifecycle.html)


## Dependencies

### Configuration
- 의존성을 선언함과 동시에, 해당 의존성이 언제 어떻게 사용될지를 결정
  - 예를 들어 compileOnly의 경우에는 컴파일 시점에만 해당 라이브러리를 참조하여 사용하고 런타임 시점에는 라이브러리가 포함되지 않는다.
    - Lombok 라이브러리가 대표적인 예시
  - 반대로 runtimeOnly의 경우에는 컴파일 서점에는 해당 라이브러리를 참조하지 않지만, 런타임 시점에는 라이브러리가 포함된다.
    - mysql-connector 라이브러리가 대표적인 예시

> https://docs.gradle.org/current/userguide/dependency_configurations.html

### api vs implementation

- api와 implementation의 차이점은 의존성 노출(Dependency Exposure) 여부에 따라 나뉜다.
  - api: 선언한 모듈  
  - implementation: 해당 모듈 내부에서만 라이브러리를 사용하고, 의존하는 다른 모듈에서는 접근 불가능

```groovy
// moduleA
plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
}
```

```groovy
//moduleB
dependencies {
    implementation(project(":moduleA"))
}

dependencies {
  implementation(project(":moduleB"))
}

```

```groovy
//moduleC
dependencies {
  implementation(project(":moduleB"))
}
```


> [dependency configurations](https://docs.gradle.org/current/userguide/dependency_configurations.html)

> https://docs.gradle.org/current/userguide/java_library_plugin.html


> [gradle > build environment](https://docs.gradle.org/current/userguide/build_environment.html)


## Plugin

- Plugin은 ***빌드 프로세스를 확장하고 자동화***하여 특정 작업을 쉽게 수행할 수 있도록 도와준다. 프로젝트에 필요한 의존성을 주입받기 위해서 dependencies에 의존성을 정의하여 사용하는것과 비슷하게, Plugin은 빌드에 필요한 의존성을 추가하여 사용할 수 있다. 
  - 코드 컴파일, 테스트 실행, 패키징, 배포 등 다양한 작업을 수행할 수 있도록 한다.
- Plugin 덕분에 개발자는 빌드 작업에 필요한 코드를 직접 작성할 필요 없이 쉽게 빌드 프로세스를 구성할 수 있다.
  - Gradle에서 `java` 플러그인을 적용하여 컴파일, 테스트, Jar 생성 등의 작업을 자동화할 수 있다.
- 이러한 Plugin 속성 덕분에 외부에서 만들어진 Plugin을 쉽게 적용하던가, 직접 Plugin을 만들어서 배포하여 사내에서 활용이 가능하다.
  - Core Plugin: Gradle에 기본적으로 포함되어 있는 플러그인 (Java)
  - Community Plugins: Gradle Plugin Portal이나 기타 저장소에서 제공되는 플러그인
  - Local or Custom Plugins: 개발자가 직접 작성한 플러그인

> https://docs.gradle.org/current/userguide/plugins.html