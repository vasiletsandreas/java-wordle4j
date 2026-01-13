package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    // Константы для символов сравнения
    public static final char CORRECT_POSITION = '✓';
    public static final char WRONG_POSITION = '~';
    public static final char WRONG_LETTER = '×';

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream("wordle.log"),
                        StandardCharsets.UTF_8
                ),
                true
        )) {

            log.println("Запуск игры Wordle");

            // Создаем загрузчик словаря и загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt", log);

            log.println("Словарь загружен, слов: " + dictionary.size());

            // Создаем игру
            WordleGame game = new WordleGame(dictionary);

            // Запускаем игровой цикл с использованием Scanner
            try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
                System.out.println("Добро пожаловать в Wordle!");
                System.out.println("У вас есть 6 попыток, чтобы угадать слово.");
                System.out.println("Введите слово или нажмите Enter для подсказки.");
                System.out.println();

                while (!game.isGameOver()) {
                    System.out.print("Попытка " + (7 - game.getRemainingSteps()) + "/6: ");
                    String input = scanner.nextLine().trim();

                    try {
                        if (input.isEmpty()) {
                            // Запрос подсказки
                            String hint = game.getHint();
                            if (hint != null) {
                                System.out.println("Подсказка: " + hint);
                            } else {
                                System.out.println("Не могу подсказать слово");
                            }
                        } else {
                            // Проверяем введенное слово
                            String result = game.makeGuess(input);
                            System.out.println(result);
                        }
                    } catch (GameException e) {
                        System.out.println(e.getMessage());
                    }

                    System.out.println();
                }

                // Выводим результат игры
                if (game.isWon()) {
                    System.out.println("Поздравляем! Вы угадали слово!");
                } else {
                    System.out.println("К сожалению, вы проиграли.");
                    System.out.println("Загаданное слово было: " + game.getAnswer());
                }

            } catch (Exception e) {
                log.println("Ошибка ввода: " + e.getMessage());
                System.out.println("Ошибка ввода. Проверьте консоль.");
            }

            log.println("Игра завершена");

        } catch (Exception e) {
            // Логируем все необработанные исключения
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Произошла ошибка в игре. Детали в лог-файле.");
        }
    }
}