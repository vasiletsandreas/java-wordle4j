package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
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

    // Фильтрация слов по известным буквам и позициям
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

    // Нормализация слова для игры
    public static String normalizeWord(String word) {
        if (word == null) return null;
        return word.toLowerCase()
                .replace('ё', 'е')
                .trim();
    }

    // Сравнение двух слов для получения подсказки
    public static String compareWords(String guess, String answer) {
        if (guess.length() != answer.length()) {
            throw new IllegalArgumentException("Слова разной длины");
        }

        StringBuilder result = new StringBuilder();
        char[] guessChars = guess.toCharArray();
        char[] answerChars = answer.toCharArray();
        boolean[] matched = new boolean[answer.length()];

        // Сначала отмечаем точные совпадения
        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == answerChars[i]) {
                matched[i] = true;
            }
        }

        // Затем проверяем остальные буквы
        for (int i = 0; i < guessChars.length; i++) {
            if (matched[i]) {
                result.append('✓'); // Правильная буква на правильном месте
            } else {
                boolean found = false;
                for (int j = 0; j < answerChars.length; j++) {
                    if (!matched[j] && guessChars[i] == answerChars[j]) {
                        found = true;
                        matched[j] = true;
                        break;
                    }
                }
                result.append(found ? '~' : '×'); // ~ - буква есть, но не на месте, × - буквы нет
            }
        }

        return result.toString();
    }
}