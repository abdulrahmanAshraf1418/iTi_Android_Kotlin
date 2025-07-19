fun main(){
    for (i in 0..5) {
        for (j in 0..i) {
            print("*")
        }
        println()
    }
    println()
    for (i in 0..5) {
        for (j in 4 downTo i ) {
            print(" ")
        }
        for (j in 0..i) {
            print("* ")
        }
        println()
    }
}