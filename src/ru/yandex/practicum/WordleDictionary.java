package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.Collections;
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

        int length = guess.length();
        char[] result = new char[length];
        boolean[] answerUsed = new boolean[length];
        boolean[] guessUsed = new boolean[length];

        // Первый проход: точные совпадения
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '✓';
                answerUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Второй проход: буквы есть, но на других позициях
        for (int i = 0; i < length; i++) {
            if (result[i] == '✓') {
                continue; // Уже обработано
            }

            char guessChar = guess.charAt(i);
            boolean found = false;

            // Ищем букву в ответе, которая еще не использована
            for (int j = 0; j < length; j++) {
                if (!answerUsed[j] && guessChar == answer.charAt(j)) {
                    // Проверяем, что эта позиция в guess еще не обработана
                    // и что это не точное совпадение (уже обработано в первом проходе)
                    if (!guessUsed[i]) {
                        result[i] = '~';
                        answerUsed[j] = true;
                        guessUsed[i] = true;
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                result[i] = '×';
                guessUsed[i] = true;
            }
        }

        return new String(result);
    }
}