
tasks.register("hello") {
    println("[Configuration Phase] hello task 등록")
    doFirst {
        println("[Execution Phase] hello task 실행 doFirst 블록")
    }
    doLast {
        println("[Execution Phase] hello task 실행 doLast 블록")
    }
}
