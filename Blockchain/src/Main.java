import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("radi");
        Process p;
        StringBuffer output = new StringBuffer();
        try {
            p = Runtime.getRuntime().exec("ls");

            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (IOException e) {

        } catch (InterruptedException c) {

        }
        System.out.println(output);
    }
}
