fun main(args:Array<String>){
    greet("Ahmed")
    greet()
}
fun greet (name :String = "Ali"){
    println("Hello $name")
}