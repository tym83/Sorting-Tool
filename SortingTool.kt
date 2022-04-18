package sorting
import java.io.File
import java.util.Scanner
import kotlin.math.roundToInt
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val sortEngine = SortEngine(args)
    sortEngine.mainMenu()
}

class SortEngine(private val args: Array<String>) {

    private var sortingType = "natural"
    private val inputFile = if ("-inputFile" in args) args[args.indexOf("-inputFile") + 1] else ""
    private val outputFile = if ("-outputFile" in args) args[args.indexOf("-outputFile") + 1] else ""

    fun mainMenu() {
        checkArgs()
        when {
            args[args.indexOf("-dataType") + 1] == "long" -> sortNumbers(sortingType)
            args[args.indexOf("-dataType") + 1] == "line" -> sortLine(sortingType)
            args[args.indexOf("-dataType") + 1] == "word" -> sortWord(sortingType)
            else -> sortLine(sortingType)
        }
    }

    private fun checkArgs() {
        val rightArguments = Regex("-sortingType|-dataType|-inputFile|-outputFile")
        if (args.contains("-sortingType")) {
            try {
                args[args.indexOf("-sortingType") + 1].matches("(natural|byCount)".toRegex())
            } catch (e: ArrayIndexOutOfBoundsException) {
                println("No sorting type defined!")
                exitProcess(-1)
            }
        } else if (args.contains("-dataType")) {
            try {
                args[args.indexOf("-dataType") + 1].matches("(long|line|word)".toRegex())
            } catch (e: ArrayIndexOutOfBoundsException) {
                println("No data type defined!")
                exitProcess(-1)
            }
        }

        args.filter { it.matches("-[a-zA-Z]+".toRegex()) && !it.matches(rightArguments) }
            .forEach { println("$it is not a valid parameter. It will be skipped.") }

        if (args.contains("-sortingType")) sortingType = args[args.indexOf("-sortingType") + 1]
    }

    private fun inputData(): MutableList<String> {
        val listOfLines = mutableListOf<String>()
        val scanner = Scanner(System.`in`)

        if (inputFile.isEmpty()) {
            while (scanner.hasNextLine()) {
                listOfLines.add(scanner.nextLine())
            }
        } else {
            val input = File(inputFile)
            input.createNewFile()
            input.forEachLine { listOfLines.add(it) }
        }

        return listOfLines
    }

    private fun sortLine(arg: String) {
        val listOfLines = inputData()

        println("Total lines: ${listOfLines.size}")
        if (arg == "byCount") {
            val linesCount = mutableMapOf<String, Int>()
            listOfLines.sorted().toSet().forEach { x -> listOfLines.count { it == x }.let { y -> linesCount[x] = y } }
            printByCount(linesCount, listOfLines.size)
        } else {
            println("Sorted data:\n${listOfLines.sorted().joinToString("\n")}")
        }
    }

    private fun sortWord(arg: String) {
        val listOfWords = mutableListOf<String>()
        inputData().forEach { listOfWords.addAll(it.split("\\s+".toRegex()).map { element -> element }) }

        println("Total words: ${listOfWords.size}")
        if (arg == "byCount") {
            val wordsCount = mutableMapOf<String, Int>()
            listOfWords.sorted().toSet().forEach { x -> listOfWords.count { it == x }.let { y -> wordsCount[x] = y } }
            printByCount(wordsCount, listOfWords.size)
        } else {
            println("Sorted data: ${listOfWords.sorted().joinToString(" ")}")
        }
    }

    private fun sortNumbers(arg: String) {
        val listOfNumbers = mutableListOf<Int>()
        val listOfWords = mutableListOf<String>()
        inputData().forEach { listOfWords.addAll(it.split("\\s+".toRegex()).map { element -> element }) }

        listOfWords.forEach {
            try {
                listOfNumbers.add(it.toInt())
            } catch (e: NumberFormatException) {
                println("$it is not a long. It will be skipped.")
            }
        }

        println("Total numbers: ${listOfNumbers.size}.")
        if (arg == "byCount") {
            val numbersCount = mutableMapOf<Int, Int>()
            listOfNumbers.sorted().toSet().forEach { x: Int -> listOfNumbers.count { it == x }
                .let { y -> numbersCount[x] = y } }

            printByCount(numbersCount, listOfNumbers.size)
        } else {
            println("Sorted data: ${listOfNumbers.sorted().joinToString(" ")}")
        }
    }

    private fun <T> printByCount(mapOfData: MutableMap<T, Int>, listSize: Int) {
        var outputString = ""
            mapOfData.toList().sortedBy { (_, value) -> value }.toMap()
            .forEach { (k, v) -> outputString += "$k: $v time(s), ${(v.toDouble() / listSize * 100).roundToInt()}%\n" }

        if (outputFile.isEmpty()) {
            println(outputString)
        } else {
            val output = File(outputFile)
            output.createNewFile()
            output.writeText(outputString)
            output.forEachLine { println("String: $it") }
        }
    }
}
