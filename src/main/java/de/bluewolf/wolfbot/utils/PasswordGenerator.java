package de.bluewolf.wolfbot.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.utils
 * @created 23/Dez/2020 - 23:29
 */
public class PasswordGenerator
{

    private static final char[] UPPER_LETTERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] LOWER_LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final char[] SPECIAL_CHARACTERS = { '~', '`', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{',
            ']', '}', '\\', '|', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?' };

    private static final SecureRandom secureRandom = new SecureRandom();

    private static String generateRandomString(char[] characters)
    {
        Random random = new Random();
        int numberOfCharacters = random.nextInt((3 - 1) + 1) + 1;

        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < numberOfCharacters; i++)
        {
            int index = secureRandom.nextInt(characters.length);
            randomString.append(characters[index]);
        }

        return randomString.toString();
    }

    private static String shuffleString(String input)
    {
        List<String> shuffledString = Arrays.asList(input.split(""));
        Collections.shuffle(shuffledString);

        return String.join("", shuffledString);
    }

    /**
     *
     * @return String with a length between 8 and 12
     */
    public static String generatePassword()
    {

        String password = generateRandomString(UPPER_LETTERS) +
                generateRandomString(LOWER_LETTERS) +
                generateRandomString(NUMBERS) +
                generateRandomString(SPECIAL_CHARACTERS);

        return shuffleString(password);
    }

}
