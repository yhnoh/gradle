


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

> [gradle > build_lifecycle](https://docs.gradle.org/current/userguide/build_lifecycle.html)