fun main() {
    println("Enter first number: ")
    val num1:Double = readln().toDouble()
    println("Enter second number: ")
    val num2:Double = readln().toDouble()
    println("Enter the Process: (+,-,*,/)")
    val process:Char = readln()[0]
    when(process){
        '+' -> println("The summation is: ${num1+num2}")
        '-' -> println("The subtraction is: ${num1-num2}")
        '*' -> println("The multiplication is: ${num1*num2}")
        '/' -> {
            if (num2 !=0.0 ) println("The division is: ${num1/num2}")
            else println("Can not divide by zero")
        }
        else -> println("Invalid input")

    }
}