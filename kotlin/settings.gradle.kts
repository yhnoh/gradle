rootProject.name = "kotlin"


include(":dependency-configuration:api:modulea")
include(":dependency-configuration:api:moduleb")
include(":dependency-configuration:api:modulec")

include(":dependency-configuration:implementation:modulea")
include(":dependency-configuration:implementation:moduleb")
include(":dependency-configuration:implementation:modulec")


println("[Initialization Phase] 프로젝트 구조 파악")

