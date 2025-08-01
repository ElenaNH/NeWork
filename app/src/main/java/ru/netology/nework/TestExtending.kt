package ru.netology.nework

abstract class Animal(
    open val name: String,
    open val cellNumber: Int,
    val type: String,
) {
    fun printName() {
        print(name)
    }
}

interface flagMarkable {
    val flag: Boolean
}

data class Dog(
    override val name: String,
    override val cellNumber: Int,
    override val flag: Boolean = false,
) : Animal(name, cellNumber, "Dog"), flagMarkable

data class Cat(
    override val name: String,
    override val cellNumber: Int,
) : Animal(name, cellNumber, "Cat")

fun testExtending(param: Int = 0) {
    val myCat = Cat("Pushok", 15)
    val myDog = Dog("Bonney", 25, true)
    myDog.printName()
    val myAnimal = if (param == 0) myCat else myDog

    // Если обнаружили, что поле flag имеется (т.к. наследует интерфейс), то используем это поле, иначе - не обращаемся к нему
    if (myAnimal is flagMarkable) print("Flag=${myAnimal.flag}") else print("No Flag")

}