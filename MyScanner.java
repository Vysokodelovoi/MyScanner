import java.io.*;
import java.nio.charset.Charset;
public class MyScanner {

    public static final int DEFAULT_BUFFER_SIZE = 2048;
    private int BUFFER_SIZE;
    private final String ENCODING;
    private final Reader reader;
    private char[] buffer;
    private int bufferIterator = 0;
    private int curBufSize;
    public static CheckFunction intChecker = new CheckFunction() {
        @Override
        public boolean isValid(char c) {
            return Character.isDigit(c) || c == '-';
        }
    };
    public static  CheckFunction nextChecker = new CheckFunction(){
        @Override
        public boolean isValid(char c) {
            return !Character.isWhitespace(c);
        }
    };
    public static CheckFunction wordChecker = new CheckFunction() {
        @Override
        public boolean isValid(char c) {
            return Character.isLetter(c) ||  c == '\''
                    || Character.getType(c) == Character.DASH_PUNCTUATION;
        }
    };
    public static CheckFunction lineChecker = new CheckFunction() {
        @Override
        public boolean isValid(char c) {
            return c !='\r' && c != '\n';
        }
    };

    private void fillBuffer() throws IOException {
        curBufSize = reader.read(buffer, 0, BUFFER_SIZE);
        bufferIterator = 0;
    }

    private void bufferInit(int size) throws IOException {
        BUFFER_SIZE = size;
        buffer = new char[BUFFER_SIZE];
        fillBuffer();
    }

    public MyScanner(String string) throws IOException {
        this.reader = new StringReader(string);
        bufferInit(Math.min(string.length(), DEFAULT_BUFFER_SIZE));
        ENCODING = Charset.defaultCharset().toString();
    }

    public MyScanner(InputStream source) throws IOException {
        this.ENCODING = Charset.defaultCharset().toString();
        this.reader = new InputStreamReader(source, ENCODING);
        bufferInit(DEFAULT_BUFFER_SIZE);
    }

    public MyScanner(File file, String encoding) throws IOException {
        this.reader = new InputStreamReader(
                new FileInputStream(file), encoding
        );
        this.ENCODING = encoding;
        bufferInit(DEFAULT_BUFFER_SIZE);
    }

    private int findNextChar(CheckFunction checkFunction) throws IOException {
        int nextPtr = bufferIterator;
        while (nextPtr < curBufSize && !checkFunction.isValid(buffer[nextPtr]) ) {
            nextPtr++;
            if (nextPtr == BUFFER_SIZE) {
                fillBuffer();
                nextPtr = 0;
            }
        }
        return nextPtr;
    }

    public boolean hasNext(CheckFunction checkFunction) throws IOException {
        return findNextChar(checkFunction) < curBufSize;
    }
    public  boolean hasNextLine() throws IOException {
        return !isEmpty();
    }

    public boolean hasNextInt() throws IOException {
        return hasNext(intChecker);
    }

    public boolean hasNextWord() throws IOException {
        return hasNext(wordChecker);
    }

    public  String next() throws IOException {
        return next(nextChecker, findNextChar(nextChecker));
    }

    public  String next(CheckFunction checkFunction, int begin) throws IOException {
        StringBuilder sb = new StringBuilder();
        bufferIterator = begin;
        while (bufferIterator < curBufSize && checkFunction.isValid(buffer[bufferIterator])) {
            bufferIterator++;
            if (bufferIterator == buffer.length) {
                sb.append(buffer, begin, bufferIterator - begin);
                fillBuffer();
                begin = bufferIterator;
            }
        }
        sb.append(buffer, begin, bufferIterator - begin);
        return sb.toString();
    }

    public String nextLine() throws IOException {
        String s = next(lineChecker, bufferIterator);
        newLine();
        return s;
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(next(intChecker, findNextChar(intChecker)));
    }

    public String nextWord() throws IOException {
        return next(wordChecker, findNextChar(wordChecker));
    }

    public boolean hasNextLineToken(CheckFunction checkFunction) throws IOException {
        skip(checkFunction);
        return !endOfLine();
    }

    public boolean hasNextLineWord() throws IOException {
        return hasNextLineToken(wordChecker);
    }

    public  boolean hasNextLineInt() throws  IOException {
        return hasNextLineToken(intChecker);
    }

    private void refill() throws IOException {
        if (bufferIterator == BUFFER_SIZE) {
            fillBuffer();
        }
    }

    public boolean isEmpty() throws IOException {
        refill();
        return bufferIterator >= curBufSize;
    }

    public boolean endOfLine() throws IOException {
        return isEmpty() || buffer[bufferIterator] == '\n' || buffer[bufferIterator] == '\r';
    }

    public void newLine() throws IOException {
        while (!endOfLine()) {
            bufferIterator++;
            refill();
        }
        if (buffer[bufferIterator] == '\n') {
            bufferIterator++;
            refill();
        } else if () {

        } else if (bufferIterator + 2 < curBufSize) {
            // \r\n....END_OF_BUFFER
            bufferIterator += 2;
        } else if (bufferIterator + 1 < curBufSize) {
            // \r\nEND_OF_BUFFER...
            fillBuffer();
        } else {
            fillBuffer();
            // \rEND_OF_BUFFER\n...
            bufferIterator = 1;
        }
    }


    public void skip(CheckFunction checkFunction) throws IOException {
        while (!checkFunction.isValid(buffer[bufferIterator]) && !endOfLine()) {
            bufferIterator++;
            refill();
        }
    }

    public void close() throws IOException{
        buffer = null;
        reader.close();
    }
}

