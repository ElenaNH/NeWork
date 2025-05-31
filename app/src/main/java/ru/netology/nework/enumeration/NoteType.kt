package ru.netology.nework.enumeration

enum class NoteType(val noteTypeCode: Int) {
    // Нельзя использовать ordinal, потому что он может поменяться, но мы хотим сохранить эти данные в БД неизменными
    // Строку тоже не будем хранить - число занимает меньше места
    POST(1),
    EVENT(2),
}
