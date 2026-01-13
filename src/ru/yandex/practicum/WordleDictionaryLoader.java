package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    public WordleDictionary loadDictionary(String filename, PrintWriter log) throws IOException {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename),
                        StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line);
                if (!normalized.isEmpty() && normalized.length() == 5) {
                    words.add(normalized);
                }
            }

            log.println("Загружено " + words.size() + " пятибуквенных слов из файла " + filename);

        } catch (FileNotFoundException e) {
            log.println("Файл словаря не найден: " + filename);
            throw new WordNotFoundException("Файл словаря не найден: " + filename, e);
        }

        if (words.isEmpty()) {
            log.println("Словарь пуст");
            throw new WordNotFoundException("Словарь не содержит пятибуквенных слов");
        }

        return new WordleDictionary(words);
    }
}