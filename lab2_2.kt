fun main(){
    var n = 20
    var i = 1
    while (n-- > 0){
        if ( i % 4 == 0){
            i++
            continue
        }
        if (i == 15)
            break
        println(i++)
    }
}