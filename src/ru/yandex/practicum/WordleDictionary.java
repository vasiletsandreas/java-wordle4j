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