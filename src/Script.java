import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Jerry M Yang (z5421983)
 * 
 * Script created for COMP6841 to find the maximum number of ending charcaters in a hash are identical when
 * adding random whitespace characters to random lines in one of the files
 */

 public class Script {
    public static void main(String[] args) throws Exception {
        int iterations = Integer.parseInt(args[0]);

        File real = new File("src/real.txt");
        String realHash = hashFile(real);

        File fake = new File("src/fake.txt");

        Random random = new Random();
        int numMatches = 0;
        int maxMatches = 0;

        for (int i = 0; i < iterations; i++) {
            File temp = addRandomWhiteSpace(fake, random);
            String fakeHash = hashFile(temp);
            numMatches = compareHashes(realHash, fakeHash);

            if (numMatches > maxMatches) {
                System.out.println("Found new best file with hash: " + fakeHash + " comparing to: " + realHash + "\n - Matching a total of: " + numMatches + " characters!");
                maxMatches = numMatches;
                File newBest = new File("src/bestMatch.txt");

                if (!newBest.createNewFile()) newBest.delete();
                newBest.createNewFile();
                newBest.setWritable(true);

                copyFile(temp, newBest);
            }
        }

        System.out.println(">> Found best match with a total of: " + maxMatches + " character matches (reversed) over " + iterations + " iterations <<");
    }

    /**
     * Creates a new file called temp.txt used to store the newly written lines
     *
     * @param fakeFile
     * @param random
     */
    private static File addRandomWhiteSpace(File fakeFile, Random random) throws Exception {
        BufferedReader myReader = new BufferedReader(new FileReader(fakeFile));
        File temp = new File("src/temp.txt");
        String whiteSpace = String.valueOf(' ');

        if (!temp.createNewFile()) {
            temp.delete();
        }

		temp.createNewFile();
		temp.setWritable(true);

        FileWriter myWriter = new FileWriter(temp, true);

        for (String line = myReader.readLine(); line != null; line = myReader.readLine()) {
            String newLine = line + (whiteSpace.repeat(random.nextInt(1000))) + '\n';
            myWriter.write(newLine);
        }

        myReader.close();
        myWriter.close();

        return temp;
    }

    /**
     * Hashes a file and returns the hash as a string
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static String hashFile(File file) throws Exception {
        byte[] tempBytes = Files.readAllBytes(file.toPath());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileHash = digest.digest(tempBytes);

        StringBuilder hexString = new StringBuilder(2 * fileHash.length);
        for (int i = 0; i < fileHash.length; i++) {
            String hex = Integer.toHexString(0xff & fileHash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Compares two hashes and returns the number of identical characters at the end of the hash string they contain 
     * 
     * @param hash1
     * @param hash2
     * @return
     */
    private static int compareHashes(String hash1, String hash2) {
        String reversedHash1 = new StringBuilder(hash1).reverse().toString();
        String reversedHash2 = new StringBuilder(hash2).reverse().toString();

        for (int i = 0; i < reversedHash1.length(); i++) {
            if (reversedHash1.charAt(i) != reversedHash2.charAt(i)) {
                return i;
            }
        }

        return reversedHash1.length();
    }

    private static void copyFile(File source, File dest) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            in.close();
            out.close();
        }
    }
}