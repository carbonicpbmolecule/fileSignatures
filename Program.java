import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Program {

    public static Vector<Integer> convertByteToVectorInt(byte[] buff) {
        Vector<Integer> result = new Vector<Integer>();

        for (int i = 0; i < buff.length; ++i) {
            result.add(Byte.toUnsignedInt(buff[i]));
        }
        return result;
    }
    public static Vector<Integer> parseFileSignature(FileInputStream file, int len) throws IOException {
        byte[] buff = new byte[len];
        file.read(buff);

        return convertByteToVectorInt(buff);
    }

    public static Map<Vector<Integer>, String> getSignatures(FileInputStream file) {
        Scanner in = new Scanner(file);
        Map<Vector<Integer>, String> result = new HashMap<>();

        while (in.hasNextLine()) {
            String type = in.next();
            type = type.substring(0, type.length() - 1);
            Vector<Integer> bytes = new Vector<>();
            while (in.hasNextInt(16)) {
                bytes.add(in.nextInt(16));
            }
            result.put(bytes, type);
        }
        in.close();
        return result;
    }

    public static String getFormat(Map<Vector<Integer>, String> signatures, String path) throws IOException {
        Set<Vector<Integer>> keys = signatures.keySet();
        for (Vector<Integer> key : keys) {
            FileInputStream file = new FileInputStream(path);
            int len = key.size();
            Vector<Integer> magic = parseFileSignature(file, len);
            file.close();
            if (signatures.containsKey(magic)) {
                System.out.println("PROCESSED");
                return signatures.get(magic);
            }
            magic.clear();
        }
        throw new UnknownFormatException("Add the signature to the file: signatures.txt");
    }

    public static void main(String[] args) throws IOException {
        FileInputStream signatures_file = new FileInputStream("signatures.txt");
        Map<Vector<Integer>, String> signatures = getSignatures(signatures_file);
        FileOutputStream result_file = new FileOutputStream("result.txt");

        Scanner in = new Scanner(System.in);
        String path;
        while (!(path = in.nextLine()).equals("42")) {
            String format = getFormat(signatures, path);
            result_file.write((format + '\n').getBytes());
        }
    }
}

class UnknownFormatException extends RuntimeException {
    public UnknownFormatException(String msg) {
        super(msg);
    }
}
