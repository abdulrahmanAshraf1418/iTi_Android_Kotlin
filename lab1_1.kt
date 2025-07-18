fun main() {
    println("Enter first number: ")
    val num1:Double = readln().toDouble()
    println("Enter second number: ")
    val num2:Double = readln().toDouble()
    println("The summation = ${num1+num2}")
    println("The subtraction = ${num1-num2}")
    println("The multiplication = ${num1*num2}")
    if (num2 != 0.0 ){
        println("The division = ${num1/num2}")
    }else {
        println("Can not divide by zero")
    }
}