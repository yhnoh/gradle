rootProject.name = "kotlin"


include("lifecycle")


include("dependency:api:modulea")
include("dependency:api:moduleb")
include("dependency:api:modulec")

include("dependency:implementation:modulea")
include("dependency:implementation:moduleb")
include("dependency:implementation:modulec")


println("[Initialization Phase] 프로젝트 구조 파악")

