package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private boolean gameWon;
    private final List<String> guesses;
    private final List<String> hints;

    // Для фильтрации слов
    private final Set<Character> correctLetters;
    private final Set<Character> wrongLetters;
    private final Map<Integer, Character> correctPositions;
    private final Map<Integer, Character> wrongPositions;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = 6;
        this.gameWon = false;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();

        this.correctLetters = new HashSet<>();
        this.wrongLetters = new HashSet<>();
        this.correctPositions = new HashMap<>();
        this.wrongPositions = new HashMap<>();

        log.println("Загадано слово: " + answer);
    }

    public String makeGuess(String guess) throws GameException {
        if (isGameOver()) {
            throw new GameException("Игра уже завершена");
        }

        String normalizedGuess = WordleDictionary.normalizeWord(guess);

        // Проверяем базовую корректность
        if (normalizedGuess.length() != 5) {
            throw new GameException("Слово должно содержать 5 букв");
        }

        if (!normalizedGuess.matches("[а-яё]+")) {
            throw new GameException("Слово должно содержать только русские буквы");
        }

        // Проверяем наличие в словаре
        if (!dictionary.contains(normalizedGuess)) {
            throw new WordNotFoundInDictionaryException("Слово не найдено в словаре");
        }

        // Уменьшаем количество попыток
        steps--;
        guesses.add(normalizedGuess);

        // Проверяем, угадал ли игрок слово
        if (normalizedGuess.equals(answer)) {
            gameWon = true;
            log.println("Игрок угадал слово: " + answer);
            return "Поздравляем! Вы угадали слово!";
        }

        // Анализируем совпадение
        String comparison = WordleDictionary.compareWords(normalizedGuess, answer);
        updateKnowledge(normalizedGuess, comparison);

        log.println("Попытка: " + normalizedGuess + ", результат: " + comparison);

        return normalizedGuess + "\n" + comparison + " (осталось попыток: " + steps + ")";
    }

    public String getHint() {
        if (isGameOver()) {
            return null;
        }

        // Фильтруем слова на основе текущих знаний
        List<String> possibleWords = dictionary.filterWords(
                correctLetters, wrongLetters, correctPositions, wrongPositions);

        // Убираем уже использованные слова
        possibleWords.removeAll(guesses);
        possibleWords.removeAll(hints);

        if (possibleWords.isEmpty()) {
            return null;
        }

        // Выбираем случайное слово из подходящих
        Random random = new Random();
        String hint = possibleWords.get(random.nextInt(possibleWords.size()));
        hints.add(hint);

        log.println("Подсказка: " + hint);

        return hint;
    }

    private void updateKnowledge(String guess, String comparison) {
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            char resultChar = comparison.charAt(i);

            if (resultChar == '✓') {
                // Правильная буква на правильном месте
                correctLetters.add(guessChar);
                correctPositions.put(i, guessChar);
            } else if (resultChar == '~') {
                // Правильная буква на неправильном месте
                correctLetters.add(guessChar);
                wrongPositions.put(i, guessChar);
            } else if (resultChar == '×') {
                // Неправильная буква
                wrongLetters.add(guessChar);
            }
        }
    }

    public boolean isGameOver() {
        return steps <= 0 || gameWon;
    }

    public boolean isWon() {
        return gameWon;
    }

    public int getRemainingSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }
}

// Исключения для игровых ситуаций
class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}

class WordNotFoundInDictionaryException extends GameException {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}