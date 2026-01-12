package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Этот класс содержит в себе список слов List<String>.
 * Его методы похожи на методы списка, но учитывают особенности игры.
 * Также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private List<String> words;
    private final PrintWriter log;

    public WordleDictionary(List<String> words, PrintWriter log) {
        this.words = words;
        this.log = log;
    }

    public int size() {
        return words.size();
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    /**
     * Фильтрация слов по известным буквам и позициям.
     *
     * @param correctLetters буквы, которые точно есть в слове
     * @param wrongLetters буквы, которых точно нет в слове
     * @param correctPositions буквы на правильных позициях
     * @param wrongPositions буквы на неправильных позициях
     * @return список подходящих слов
     */
    public List<String> filterWords(Set<Character> correctLetters,
                                    Set<Character> wrongLetters,
                                    Map<Integer, Character> correctPositions,
                                    Map<Integer, Character> wrongPositions) {

        return words.stream()
                .filter(word -> {
                    // Проверяем, что слово содержит все правильные буквы
                    for (char c : correctLetters) {
                        if (word.indexOf(c) == -1) {
                            return false;
                        }
                    }

                    // Проверяем, что слово не содержит неправильные буквы
                    for (char c : wrongLetters) {
                        if (word.indexOf(c) != -1) {
                            return false;
                        }
                    }

                    // Проверяем правильные позиции
                    for (Map.Entry<Integer, Character> entry : correctPositions.entrySet()) {
                        int pos = entry.getKey();
                        char expected = entry.getValue();
                        if (pos >= word.length() || word.charAt(pos) != expected) {
                            return false;
                        }
                    }

                    // Проверяем неправильные позиции (буква есть, но не на этой позиции)
                    for (Map.Entry<Integer, Character> entry : wrongPositions.entrySet()) {
                        int pos = entry.getKey();
                        char letter = entry.getValue();
                        if (pos < word.length() && word.charAt(pos) == letter) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Нормализация слова для игры.
     *
     * @param word исходное слово
     * @return нормализованное слово
     */
    public static String normalizeWord(String word) {
        if (word == null) {
            return null;
        }
        return word.toLowerCase()
                .replace('ё', 'е')
                .trim();
    }

    /**
     * Сравнение двух слов для получения подсказки.
     *
     * @param guess предположение игрока
     * @param answer правильный ответ
     * @return строка с символами ✓, ~, ×
     */
    public static String compareWords(String guess, String answer) {
        if (guess.length() != answer.length()) {
            throw new IllegalArgumentException("Слова разной длины");
        }

        StringBuilder result = new StringBuilder();
        char[] guessChars = guess.toCharArray();
        char[] answerChars = answer.toCharArray();
        boolean[] answerMatched = new boolean[answer.length()];

        // Сначала отмечаем точные совпадения
        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == answerChars[i]) {
                result.append('✓'); // Правильная буква на правильном месте
                answerMatched[i] = true;
            } else {
                result.append(' '); // Заполнитель для второго прохода
            }
        }

        // Затем проверяем остальные буквы
        for (int i = 0; i < guessChars.length; i++) {
            if (result.charAt(i) == '✓') {
                continue; // Уже обработали
            }

            char guessChar = guessChars[i];
            boolean found = false;

            // Ищем букву в неиспользованных позициях ответа
            for (int j = 0; j < answerChars.length; j++) {
                if (!answerMatched[j] && guessChar == answerChars[j]) {
                    found = true;
                    answerMatched[j] = true;
                    break;
                }
            }

            result.setCharAt(i, found ? '~' : '×');
        }

        return result.toString();
    }
}