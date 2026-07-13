package com.autoservice.domain.pessoa.validators;

import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CPFValidator extends Validator {

    private static final String RAW_REGEX = "\\d{11}";
    private static final String FORMATTED_REGEX = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
    private static final String CPF_NULO_MESSAGE = "CPF não deve ser nulo ou vazio";
    private static final String CPF_FORMATO_MESSAGE = "CPF deve estar no formato válido (XXX.XXX.XXX-XX ou XXXXXXXXXXX)";
    private static final String CPF_INVALIDO_MESSAGE = "CPF inválido";
    private static final List<String> ALL_CPF_REGEX = List.of(
            RAW_REGEX,
            FORMATTED_REGEX
    );
    private static final List<Integer> WEIGHTS_FIRST_CHECK_DIGIT = List.of(
            10, 9, 8, 7, 6, 5, 4, 3, 2
    );
    private static final List<Integer> WEIGHTS_SECOND_CHECK_DIGIT = List.of(
            11, 10, 9, 8, 7, 6, 5, 4, 3, 2
    );
    private final CPF cpf;

    public CPFValidator(
            final CPF aCpf,
            final ValidationHandler handler
    ) {
        super(handler);
        this.cpf = aCpf;
    }

    public static boolean isCpf(String input) {
        if (input == null) {
            return false;
        }

        return ALL_CPF_REGEX.stream()
                .anyMatch(regex -> Pattern.matches(regex, input));
    }

    public static boolean isValidCpf(String input) {
        if (!isCpf(input)) {
            return false;
        }

        final String rawCpf = input.replaceAll("[^\\d]", "");

        if (areAllDigitsTheSame(rawCpf)) {
            return false;
        }

        final List<Integer> cpfAsIntegerList = rawCpf
                .chars()
                .map(CPFValidator::charToInt)
                .boxed()
                .collect(Collectors.toList());

        final String expectedCheckDigits =
                obtainExpectedFirstCheckDigit(cpfAsIntegerList)
                        + obtainExpectedSecondCheckDigit(cpfAsIntegerList);

        final String actualCheckDigits = rawCpf.substring(9);

        return expectedCheckDigits.equals(actualCheckDigits);
    }

    private static boolean areAllDigitsTheSame(String rawCpf) {
        char firstDigit = rawCpf.charAt(0);

        for (char digit : rawCpf.toCharArray()) {
            if (digit != firstDigit) {
                return false;
            }
        }

        return true;
    }

    private static String obtainExpectedFirstCheckDigit(
            List<Integer> cpfAsIntegerList
    ) {
        int sum = sumWeighted(
                cpfAsIntegerList.subList(0, 9),
                WEIGHTS_FIRST_CHECK_DIGIT
        );

        int digit = 11 - (sum % 11);

        return digit >= 10
                ? "0"
                : String.valueOf(digit);
    }

    private static String obtainExpectedSecondCheckDigit(
            List<Integer> cpfAsIntegerList
    ) {
        int sum = sumWeighted(
                cpfAsIntegerList.subList(0, 10),
                WEIGHTS_SECOND_CHECK_DIGIT
        );

        int digit = 11 - (sum % 11);

        return digit >= 10
                ? "0"
                : String.valueOf(digit);
    }

    private static int charToInt(int ch) {
        return ch - '0';
    }

    private static int sumWeighted(
            List<Integer> digits,
            List<Integer> weights
    ) {
        int sum = 0;

        for (int i = 0; i < digits.size(); i++) {
            sum += digits.get(i) * weights.get(i);
        }

        return sum;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final String value = this.cpf.getValue();

        if (value == null || value.isBlank()) {
            this.validationHandler()
                    .append(new Error(CPF_NULO_MESSAGE));
            return;
        }

        if (!isCpf(value)) {
            this.validationHandler()
                    .append(new Error(CPF_FORMATO_MESSAGE));
            return;
        }

        if (!isValidCpf(value)) {
            this.validationHandler()
                    .append(new Error(CPF_INVALIDO_MESSAGE));
        }
    }
}
