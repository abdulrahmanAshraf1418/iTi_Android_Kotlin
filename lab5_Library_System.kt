import kotlin.system.exitProcess

abstract class Person(
    val name: String,
    val id: String
)

class Librarian(
    name: String,
    id: String,
    private var password: String

) : Person(name, id){
    fun getPassword():String{
        return password;
    }
}

class User(
    name: String,
    id: String,
    val job: String
) : Person(name, id)

abstract class LibraryItem(
    val title: String,
    val isbn: String,
    val publication: String,
    val numberOfPages: Int
) {
    abstract fun isAvailable(): Boolean
}

class Book(
    title: String,
    isbn: String,
    publication: String,
    numberOfPages: Int,
    private var available: Boolean = true,
    private var borrowedBy: User? = null
) : LibraryItem(title, isbn, publication, numberOfPages) {

    override fun isAvailable(): Boolean = available

    fun getBorrowedBy(): User? = borrowedBy

    fun setBorrowStatus(isAvailable: Boolean, user: User? = null) {
        this.available = isAvailable
        this.borrowedBy = if (isAvailable) null else user
    }
}

class Magazine(
    title: String,
    isbn: String,
    publication: String,
    numberOfPages: Int,
    val issueNumber: Int,
    private var available: Boolean = true
) : LibraryItem(title, isbn, publication, numberOfPages) {

    override fun isAvailable(): Boolean = available

}

class Journal(
    title: String,
    isbn: String,
    publication: String,
    numberOfPages: Int,
    val volume: Int,
    private var available: Boolean = true
) : LibraryItem(title, isbn, publication, numberOfPages) {

    override fun isAvailable(): Boolean = available

    fun setAvailable(available: Boolean) {
        this.available = available
    }
}

class LibraryDatabase {
    private val books = mutableListOf<Book>()
    private val magazines = mutableListOf<Magazine>()
    private val journals = mutableListOf<Journal>()
    private val users = mutableListOf<User>()
    private val borrowedBooks = mutableMapOf<String, MutableList<Book>>()

    var currentLibrarian: Librarian? = null

    fun addBook(book: Book): Boolean {
        return if (books.none { it.isbn == book.isbn }) {
            books.add(book)
            true
        } else {
            false
        }
    }

    fun addMagazine(magazine: Magazine): Boolean {
        magazines.add(magazine)
        return true
    }

    fun addJournal(journal: Journal): Boolean {
        journals.add(journal)
        return true
    }

    fun addUser(user: User) {
        users.add(user)
        borrowedBooks[user.id] = mutableListOf()
    }

    fun viewAvailableBooks(): List<Book> {
        val availableBooks = books.filter { it.isAvailable() }
        println("\n=== Available Books ===")
        if (availableBooks.isEmpty()) {
            println("There is no Available Books")
        } else {
            availableBooks.forEachIndexed { index, book ->
                println("${index + 1}. ${book.title} - ISBN: ${book.isbn}")
                println("   Author: ${book.publication}, Pages: ${book.numberOfPages}")
            }
        }
        return availableBooks
    }

    fun searchForABook(query: String): List<Book> {
        val results = books.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.isbn.contains(query, ignoreCase = true) ||
                    it.publication.contains(query, ignoreCase = true)
        }

        if (results.isEmpty()) {
            println("Can not find the book")
        } else {
            results.forEachIndexed { index, book ->
                val status = if (book.isAvailable()) "Available" else "Borrowed"
                println("${index + 1}. ${book.title} - $status")
                println("   ISBN: ${book.isbn}, Author: ${book.publication}")
            }
        }
        return results
    }

    fun lendBook(isbn: String, userId: String): Boolean {
        val book = books.find { it.isbn == isbn }
        val user = users.find { it.id == userId }

        return when {
            book == null -> {
                println("Can not find the book")
                false
            }
            user == null -> {
                println("Can not find the user")
                false
            }
            !book.isAvailable() -> {
                println("Book is not available")
                false
            }
            else -> {
                book.setBorrowStatus(false, user)
                borrowedBooks[userId]?.add(book)
                println("Book ${book.title} has been lend for ${user.name}")
                true
            }
        }
    }

    fun retrieveBookFromBorrower(isbn: String, userId: String): Boolean {
        val book = books.find { it.isbn == isbn }
        val user = users.find { it.id == userId }

        return when {
            book == null -> {
                println("This Book is not Borrowed from this library")
                false
            }
            user == null -> {
                println("Can not recognize the user")
                false
            }
            book.isAvailable() -> {
                println("This book is not lend")
                false
            }
            book.getBorrowedBy()?.id != userId -> {
                println("This book is borrowed by another user")
                false
            }
            else -> {
                book.setBorrowStatus(true)
                borrowedBooks[userId]?.remove(book)
                println("The book ${book.title} loaned to ${user.name} has been recovered.")
                true
            }
        }
    }


    fun viewAllBorrowedBooks() {
        println("\n=== Borrowed books ===")
        var hasBorrowedBooks = false

        borrowedBooks.forEach { (userId, books) ->
            if (books.isNotEmpty()) {
                val user = users.find { it.id == userId }
                println("user: ${user?.name} (${user?.id})")
                books.forEach { book ->
                    println("  - ${book.title} (ISBN: ${book.isbn})")
                }
                hasBorrowedBooks = true
            }
        }

        if (!hasBorrowedBooks) {
            println("There are no borrowed books")
        }
    }

}

class LibrarySystem {
    private val database = LibraryDatabase()

    fun login(librarian: Librarian, password: String): Boolean {
       if (password==librarian.getPassword()) {
            database.currentLibrarian = librarian
           println("Welcome ${librarian.name}")
            return true
        }else{
            println("Wrong password")
           exitProcess(1)
       }
    }

    fun addBook(title: String, isbn: String, publication: String, pages: Int): Boolean {
        val book = Book(title, isbn, publication, pages)
        return database.addBook(book)
    }

    fun addMagazine(title: String, isbn: String, publication: String, pages: Int, issueNumber: Int): Boolean {
        val magazine = Magazine(title, isbn, publication, pages, issueNumber)
        return database.addMagazine(magazine)
    }

    fun addJournal(title: String, isbn: String, publication: String, pages: Int, volume: Int): Boolean {
        val journal = Journal(title, isbn, publication, pages, volume)
        return database.addJournal(journal)
    }

    fun registerUser(name: String, id: String, job: String) {
        val user = User(name, id, job)
        database.addUser(user)
    }

    fun viewCurrentBooks() = database.viewAvailableBooks()

    fun searchForBook(query: String) = database.searchForABook(query)

    fun lendBook(isbn: String, userId: String) = database.lendBook(isbn, userId)

    fun retrieveBook(isbn: String, userId: String) = database.retrieveBookFromBorrower(isbn, userId)

    fun viewAllBorrowedBooks() = database.viewAllBorrowedBooks()

}

fun main() {
    val librarySystem = LibrarySystem()

    val librarian = Librarian("Gamal Ali", "LIB001", "1234")

    librarySystem.addBook(
        title = "Pride and Prejudice",
        isbn = "978-0-14-143951-8",
        publication = "Penguin Classics",
        pages = 480
    )
    librarySystem.addBook(
        title = "A Brief History of Time",
        isbn = "978-0-553-17698-8",
        publication = "Bantam",
        pages = 212
    )
    librarySystem.addBook(
        title = "Kotlin Programming",
        isbn = "978-1-23-456789-0",
        publication = "TechBooks Publishing",
        pages = 350
    )

    librarySystem.addMagazine(
        title = "National Geographic",
        isbn = "978-1-4262-0941-5",
        publication = "National Geographic Partners",
        pages = 120,
        issueNumber = 202
    )
    librarySystem.addMagazine(
        title = "TIME",
        isbn = "978-0-1234-5678-9",
        publication = "Time Inc.",
        pages = 98,
        issueNumber = 45)
    librarySystem.addMagazine(
        title = "Science Weekly",
        isbn = "978-9-8765-4321-0",
        publication = "Science Media",
        pages = 60,
        issueNumber = 12
    )
    librarySystem.addJournal(
        title = "Journal of Artificial Intelligence",
        isbn = "978-1-1111-1111-1",
        publication = "AI Research Center",
        pages = 150,
        volume = 1
    )
    librarySystem.addJournal(
        title = "Nature Neuroscience",
        isbn = "978-2-2222-2222-2",
        publication = "Nature Publishing Group",
        pages = 180,
        volume = 33
    )
    librarySystem.addJournal(
        title = "Journal of Artificial Intelligence",
        isbn = "978-1-1111-1111-1",
        publication = "AI Research Center",
        pages = 160,
        volume = 2
    )

    librarySystem.registerUser("Mariam", "USER001", "Software Engineer")
    librarySystem.registerUser("Ali", "USER002", "Android Developer")

    print("Enter the password: ")
    val password = readln()
    librarySystem.login(librarian,password)
    while (true){
        print("\n"+"""===== Main Menu =====
            |1.Add new Book
            |2.Add new Magazine
            |3.Add new Journal
            |4.Add new user
            |5.Show Available books list
            |6.Show borrowed books list
            |7.Search for a book
            |8.Lend a book to a user
            |9.retrieve book from borrower
            |
            |Enter your choice: 
        """.trimMargin())

        val choice : Char = readln()[0]
        when(choice){
            '1' ->{
                print("Enter the title: ")
                val title: String = readln()
                print("Enter the isbn: ")
                val isbn: String = readln()
                print("Enter the publication: ")
                val publication: String = readln()
                print("Enter the number of pages: ")
                val pages : Int = readln().toInt()

                librarySystem.addBook(title, isbn, publication, pages)
            }
            '2' ->{
                print("Enter the title: ")
                val title: String = readln()
                print("Enter the isbn: ")
                val isbn: String = readln()
                print("Enter the publication: ")
                val publication: String = readln()
                print("Enter the number of pages: ")
                val pages : Int = readln().toInt()
                print("Enter the volume: ")
                val volume: Int = readln().toInt()

                librarySystem.addMagazine(title, isbn, publication, pages, volume)
            }
            '3' ->{
                print("Enter the title: ")
                val title: String = readln()
                print("Enter the isbn: ")
                val isbn: String = readln()
                print("Enter the publication: ")
                val publication: String = readln()
                print("Enter the number of pages: ")
                val pages : Int = readln().toInt()
                print("Enter the volume: ")
                val volume: Int = readln().toInt()

                librarySystem.addJournal(title, isbn, publication,pages, volume )
            }
            '4' ->{
                print("Enter the name: ")
                val name: String = readln()
                print("Enter the id: ")
                val id: String = readln()
                print("Enter the job: ")
                val job: String = readln()

                librarySystem.registerUser(name, id, job)
            }
            '5' -> librarySystem.viewCurrentBooks()
            '6' -> librarySystem.viewAllBorrowedBooks()
            '7' ->{
                println("Enter one of (Title or publication or isbn):")
                val query: String = readln()

                librarySystem.searchForBook(query)
            }
            '8' ->{
                print("Enter the book isbn: ")
                val isbn: String = readln()
                print("Enter the user Id: ")
                val userId: String = readln()

                librarySystem.lendBook(isbn,userId)
            }
            '9' ->{
                print("Enter the book isbn: ")
                val isbn: String = readln()
                print("Enter the user Id: ")
                val userId: String = readln()

                librarySystem.retrieveBook(isbn,userId)
            }

        }
    }
}