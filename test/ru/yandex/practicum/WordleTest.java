package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private static WordleDictionary testDictionary;
    private PrintWriter testLog;

    @BeforeEach
    void setUp() throws IOException {
        // Используем System.out для логов в тестах
        testLog = new PrintWriter(new OutputStreamWriter(System.out));

        // Создаем тестовый словарь с несколькими известными словами
        // Вместо загрузки из файла создадим тестовый словарь вручную
        java.util.List<String> testWords = java.util.Arrays.asList(
                "слово", "стул", "стол", "окно", "дверь",
                "комод", "диван", "кресло", "шкаф", "полка"
        );
        testDictionary = new WordleDictionary(testWords, testLog);
    }

    @Test
    void testDictionaryNormalization() {
        String normalized = WordleDictionary.normalizeWord("Слово");
        assertEquals("слово", normalized);

        normalized = WordleDictionary.normalizeWord("Ёлка");
        assertEquals("елка", normalized);
    }

    @Test
    void testDictionaryContains() {
        assertTrue(testDictionary.contains("слово"));
        assertFalse(testDictionary.contains("абвгд"));
    }

    @Test
    void testWordComparison() {
        String result = WordleDictionary.compareWords("стол", "стул");
        // с - ✓ (правильно), т - ✓ (правильно), о - × (нет в слове), л - ~ (есть, но на другой позиции)
        assertEquals("✓✓×~", result);

        result = WordleDictionary.compareWords("слово", "слово");
        assertEquals("✓✓✓✓✓", result);

        result = WordleDictionary.compareWords("ааааа", "ббббб");
        assertEquals("×××××", result);
    }

    @Test
    void testGameInitialization() {
        WordleGame game = new WordleGame(testDictionary, testLog);
        assertFalse(game.isGameOver());
        assertEquals(6, game.getRemainingSteps());
    }

    @Test
    void testMakeGuess() throws GameException {
        // Создаем словарь с известным словом для теста
        java.util.List<String> testWords = java.util.Arrays.asList("стол", "стул", "слово");
        WordleDictionary smallDict = new WordleDictionary(testWords, testLog);
        WordleGame game = new WordleGame(smallDict, testLog);

        // Сохраняем правильный ответ
        String answer = game.getAnswer();

        // Тестируем исключение при неверной длине слова
        assertThrows(GameException.class, () -> game.makeGuess("абвг"));

        // Тестируем исключение при слове не из словаря
        assertThrows(WordNotFoundInDictionaryException.class,
                () -> game.makeGuess("абвгд"));

        // Тестируем правильный ввод
        if (answer.equals("стол")) {
            String result = game.makeGuess("стул");
            assertNotNull(result);
            assertEquals(5, game.getRemainingSteps());
        }
    }

    @Test
    void testGameOver() throws GameException {
        // Создаем маленький словарь для теста
        java.util.List<String> testWords = java.util.Arrays.asList("стол", "стул", "слово");
        WordleDictionary smallDict = new WordleDictionary(testWords, testLog);
        WordleGame game = new WordleGame(smallDict, testLog);

        String answer = game.getAnswer();

        // Находим слово, которое не является правильным ответом
        String wrongWord = testWords.stream()
                .filter(word -> !word.equals(answer))
                .findFirst()
                .orElse("стол");

        // Симулируем 6 неверных попыток
        for (int i = 0; i < 6; i++) {
            game.makeGuess(wrongWord);
        }

        assertTrue(game.isGameOver(), "Игра должна завершиться после 6 попыток");
        assertFalse(game.isWon(), "Игрок не должен выиграть с неправильными попытками");
        assertEquals(0, game.getRemainingSteps(), "Не должно остаться попыток");
    }

    @Test
    void testWinGame() throws GameException {
        // Создаем словарь с одним словом, чтобы гарантировать победу
        java.util.List<String> testWords = java.util.Arrays.asList("слово");
        WordleDictionary smallDict = new WordleDictionary(testWords, testLog);
        WordleGame game = new WordleGame(smallDict, testLog);

        // Делаем правильную попытку
        game.makeGuess("слово");

        assertTrue(game.isGameOver());
        assertTrue(game.isWon());
        assertEquals(5, game.getRemainingSteps()); // Одна попытка использована
    }
}