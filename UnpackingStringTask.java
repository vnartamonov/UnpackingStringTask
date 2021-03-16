import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Stack;

public class UnpackingStringTask {

    private final String packetString;

    public UnpackingStringTask(String packetString) {
        this.packetString = packetString;
    }

    public static void main(String [] argv) throws IOException {
        // Ввод данных и автозакрытие потока ввода.
        try(BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            String inputString = consoleReader.readLine();

            // Создание объекта класса и вызов метода распаковки строки.
            System.out.println(new UnpackingStringTask(inputString).unpackingString());
        }
        // Если входные данные невалидные, то выбрасываем исключение.
        catch (UnpackingStringTaskException taskException) {
            // Выводим сообщение о некорректных данных.
            System.out.println(taskException.toString());
        }
    }

    // Проверка валидности данных без выброса исключения.
    public boolean isValid() {
        try {
            unpackingString(packetString);
        }
        catch (Exception exception) {
            return false;
        }

        return true;
    }

    // Распаковка строки. Выбрасываем исключение при невалидных данных.
    public String unpackingString() throws UnpackingStringTaskException {
        String result = null;

        try {
            result = unpackingString(packetString);
        }
        catch (Exception exception) {
            throw new UnpackingStringTaskException();
        }

        return result;
    }

    // Рекурсивный метод распаковки строки.
    // Вызывается для каждого вхождения пары скобок: '[', ']'
    // Выбрасывает исключение при некорректных данных.
    private String unpackingString(String inputString) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        char [] chars = inputString.toCharArray();

        for(int i = 0; i<chars.length; i++) {
            // Накапливаем строку, если является латинской буквой.
            if(isLatin(chars[i])) {
                stringBuilder.append(chars[i]);
            }
            // Если текущий символ является цифрой.
            else if(Character.isDigit(chars[i])) {
                int number = nextInt(chars, i); // Читаем число с позиции i.
                int startIndex = getStartIndex(chars, i); // Вычисление вхождения символа '['.
                int endIndex = getEndIndex(chars, startIndex); // Вычисление входжения символа ']'.

                // Получены некорректные данных.
                if(startIndex == -1 || endIndex == -1) throw new Exception();

                // Рекурсивный вызов unpackingString для полученной подстроки.
                String innerResult = unpackingString(inputString.substring(startIndex+1, endIndex));
                // Повторяем и накапливаем распакованную строку.
                stringBuilder.append(innerResult.repeat(number));

                i = endIndex;
            } else {
                throw new Exception();
            }
        }

        return stringBuilder.toString();
    }

    // Проверка на латинские буквы.
    private boolean isLatin(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    // Чтение числа из chars c позиции currentIndex.
    private int nextInt(char [] chars, int currentIndex) throws NumberFormatException {
        StringBuilder numberBuilder = new StringBuilder();

        while (Character.isDigit(chars[currentIndex])) {
            numberBuilder.append(chars[currentIndex]);
            currentIndex++;
        }

        return Integer.parseInt(numberBuilder.toString());
    }

    // Вычисление позиции вхождения символа '[' после числа.
    // Необходимо для корректной работы с числами с лидирующими нулями.
    private int getStartIndex(char[] chars, int currentIndex) {
        while (currentIndex < chars.length && Character.isDigit(chars[currentIndex])) currentIndex++;
        return (chars[currentIndex] == '[') ? currentIndex : -1;
    }

    // Вычисление позиции вхождения символа ']' с учетом вложенности других скобок.
    private int getEndIndex(char[] chars, int startIndex) {
        // В стеке храним скобки в порядке вхождения.
        Stack<Character> stack = new Stack<>();
        int result = -1;

        for(int i = startIndex; i<chars.length; i++) {
            if(chars[i] == '[' || chars[i] == ']') {
                stack.push(chars[i]);
                if(checkStack(stack)) {
                    result = i;
                    break;
                }
            }
        }

        return result;
    }

    // Проверка пустоты стека.
    // Последовательная пара скобок '[' и ']' взаимоуничтожается.
    private boolean checkStack(Stack<Character> stack) {
        if(stack.size() < 2) return false;

        char a = stack.pop();
        char b = stack.pop();

        if(!(b == '[' && a == ']')) {
            stack.push(b);
            stack.push(a);
        }

        return stack.isEmpty();
    }
}

// Класс исключения. Сообщает о невалидной входной строке.
class UnpackingStringTaskException extends Exception {
    @Override
    public String toString() {
        return "String is not valid.";
    }
}
