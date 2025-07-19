fun main(){
    for (i in 0..5) {

        for (j in 0..i) {
            print("*")
        }

        for (j in 5 downTo i ) {
            print("  ")
        }
        print("   ")
        for (j in 0..i) {
            print("* ")
        }
        println()
    }
}