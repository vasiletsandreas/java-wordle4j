package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private static WordleDictionary testDictionary;

    @BeforeEach
    void setUp() {
        // Создаем тестовый словарь только с 5-буквенными словами
        java.util.List<String> testWords = Arrays.asList(
                "столы", "стуль", "окнаа", "дверь", "комод",
                "диван", "шкафа", "полка", "ручка", "книга"
        );
        testDictionary = new WordleDictionary(testWords);
    }

    @Test
    void testDictionaryNormalization() {
        String normalized = WordleDictionary.normalizeWord("Слово");
        assertEquals("слово", normalized);

        normalized = WordleDictionary.normalizeWord("Ёлка");
        assertEquals("елка", normalized);

        normalized = WordleDictionary.normalizeWord("  Тест  ");
        assertEquals("тест", normalized);
    }

    @Test
    void testDictionaryContains() {
        assertTrue(testDictionary.contains("столы"));
        assertFalse(testDictionary.contains("абвгд"));
        assertTrue(testDictionary.contains("стуль"));
    }

    @Test
    void testDictionaryGetFiveLetterWords() {
        List<String> fiveLetterWords = testDictionary.getFiveLetterWords();
        assertEquals(10, fiveLetterWords.size());
        assertTrue(fiveLetterWords.stream().allMatch(word -> word.length() == 5));
    }

    @Test
    void testGameInitialization() {
        WordleGame game = new WordleGame(testDictionary);
        assertFalse(game.isGameOver());
        assertEquals(6, game.getRemainingSteps());
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
    }

    @Test
    void testMakeGuessValid() {
        WordleGame game = new WordleGame(testDictionary);
        String answer = game.getAnswer();

        // Создаем валидное предположение (возьмем любое слово из словаря)
        String validGuess = testDictionary.getWords().get(0);

        try {
            String result = game.makeGuess(validGuess);
            assertNotNull(result);
            assertEquals(5, game.getRemainingSteps());
        } catch (GameException e) {
            fail("Не должно выбрасывать исключение для валидного слова");
        }
    }

    @Test
    void testMakeGuessInvalidLength() {
        WordleGame game = new WordleGame(testDictionary);

        assertThrows(GameException.class, () -> {
            game.makeGuess("слов");
        });

        assertThrows(GameException.class, () -> {
            game.makeGuess("длинноеслово");
        });
    }

    @Test
    void testMakeGuessNotInDictionary() {
        WordleGame game = new WordleGame(testDictionary);

        assertThrows(WordNotFoundInDictionaryException.class, () -> {
            game.makeGuess("абвгд");
        });
    }

    @Test
    void testCompareWords() {
        String result = WordleDictionary.compareWords("столы", "стуль");
        assertNotNull(result);
        assertEquals(5, result.length());

        // Проверяем, что результат содержит только допустимые символы
        for (char c : result.toCharArray()) {
            assertTrue(c == WordleDictionary.CORRECT_POSITION ||
                    c == WordleDictionary.WRONG_POSITION ||
                    c == WordleDictionary.WRONG_LETTER);
        }
    }

    @Test
    void testCompareWordsDifferentLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            WordleDictionary.compareWords("слово", "слова");
        });
    }

    @Test
    void testGetHint() {
        WordleGame game = new WordleGame(testDictionary);

        // Первая подсказка должна вернуть слово
        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(testDictionary.contains(hint));
    }

    @Test
    void testGameOverAfterSixAttempts() {
        WordleGame game = new WordleGame(testDictionary);

        // Делаем 6 попыток
        for (int i = 0; i < 6; i++) {
            try {
                game.makeGuess("столы");
            } catch (GameException e) {
                // Игнорируем, если слово не подходит
            }
        }

        assertTrue(game.isGameOver());
        assertEquals(0, game.getRemainingSteps());
    }

    @Test
    void testIsWon() {
        // Создаем словарь с одним словом
        java.util.List<String> singleWordList = Arrays.asList("слово");
        WordleDictionary singleDict = new WordleDictionary(singleWordList);

        // Проверяем, что слово действительно 5-буквенное
        if (singleDict.getFiveLetterWords().size() > 0) {
            WordleGame game = new WordleGame(singleDict);
            String answer = game.getAnswer();

            try {
                game.makeGuess(answer);
                assertTrue(game.isWon());
                assertTrue(game.isGameOver());
            } catch (GameException e) {
                fail("Не должно выбрасывать исключение для правильного слова");
            }
        }
    }

    @Test
    void testFilterWords() {
        Set<Character> correctLetters = new HashSet<>();
        correctLetters.add('с');

        Set<Character> wrongLetters = new HashSet<>();
        wrongLetters.add('а');

        Map<Integer, Character> correctPositions = new HashMap<>();
        correctPositions.put(0, 'с');

        Map<Integer, Character> wrongPositions = new HashMap<>();
        wrongPositions.put(1, 'т');

        List<String> filtered = testDictionary.filterWords(
                correctLetters, wrongLetters, correctPositions, wrongPositions);

        assertNotNull(filtered);

        // Проверяем, что все отфильтрованные слова соответствуют критериям
        for (String word : filtered) {
            assertTrue(word.contains("с"));
            assertFalse(word.contains("а"));
            assertEquals('с', word.charAt(0));
            assertNotEquals('т', word.charAt(1));
        }
    }
}