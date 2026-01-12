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
                if (!normalized.isEmpty()) {
                    words.add(normalized);
                }
            }

            log.println("Загружено " + words.size() + " слов из файла " + filename);

        } catch (FileNotFoundException e) {
            log.println("Файл словаря не найден: " + filename);
            throw new IOException("Файл словаря не найден: " + filename, e);
        }

        if (words.isEmpty()) {
            log.println("Словарь пуст");
            throw new IOException("Словарь пуст");
        }

        return new WordleDictionary(words, log);
    }
}