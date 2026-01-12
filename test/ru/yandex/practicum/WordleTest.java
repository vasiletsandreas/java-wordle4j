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

        // Создаем тестовый словарь
        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        testDictionary = loader.loadDictionary("words_ru.txt", testLog);
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
        // Предполагаем, что в словаре есть слово "слово"
        assertTrue(testDictionary.contains("слово"));
    }

    @Test
    void testWordComparison() {
        String result = WordleDictionary.compareWords("стол", "стул");
        // 'с' и 'т' на правильных местах, 'о' нет в слове, 'л' на неправильном месте
        assertEquals("✓✓×~", result);
    }

    @Test
    void testGameInitialization() {
        WordleGame game = new WordleGame(testDictionary, testLog);
        assertFalse(game.isGameOver());
        assertEquals(6, game.getRemainingSteps());
    }

    @Test
    void testMakeGuess() {
        WordleGame game = new WordleGame(testDictionary, testLog);

        // Тестируем исключение при неверной длине слова
        assertThrows(GameException.class, () -> game.makeGuess("абвг"));

        // Тестируем исключение при слове не из словаря
        assertThrows(WordNotFoundInDictionaryException.class,
                () -> game.makeGuess("абвгд"));
    }

    @Test
    void testGameOver() {
        WordleGame game = new WordleGame(testDictionary, testLog);

        // Симулируем 6 неверных попыток
        for (int i = 0; i < 6; i++) {
            try {
                game.makeGuess("стол");
            } catch (GameException e) {
                // Игнорируем, если слово не из словаря
            }
        }

        assertTrue(game.isGameOver());
        assertFalse(game.isWon());
    }
}