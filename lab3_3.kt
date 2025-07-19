fun main(args:Array<String>){
    var name : String? = null
    println(name?.length ?:0)
    name = "any Value"
    println(name.length)
}