private void updateKnowledge(String guess, String comparison) {
    for (int i = 0; i < guess.length(); i++) {
        char guessChar = guess.charAt(i);
        char resultChar = comparison.charAt(i);

        if (resultChar == '✓') {
            // Правильная буква на правильном месте
            correctLetters.add(guessChar);
            correctPositions.put(i, guessChar);
            // Убираем из неправильных позиций, если там была
            wrongPositions.remove(i);
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