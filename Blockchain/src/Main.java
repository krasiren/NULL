import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    //Change this to your directory of bitcoin-cli location
    private static String bitcoinDir = "/home/krasiren/Downloads/bitcoin-sv-0.2.2.beta";
    private static String startCommand = "/bin/bitcoin-cli -datadir=./data";

    public static void main(String[] args) {
        System.out.println("radi");
        Process p;
        StringBuffer output = new StringBuffer();
        try {
            p = Runtime.getRuntime().exec("ls " + bitcoinDir);

            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(output);
    }

    public void createBlock (String niz) {
        //if (!(checkIfHex(niz))) return;
        Process p;
        StringBuffer hash = new StringBuffer();

        try {
            p = Runtime.getRuntime().exec(bitcoinDir + startCommand + " createrawtransaction \"[]\" \"{\\\"data\\\":\\\"657572656b61\\\"}\"\n");
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = br.readLine())!= null) {
                hash.append(line + "\n");
            }

            p = Runtime.getRuntime().exec(bitcoinDir + startCommand + " fundrawtransaction " + hash);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
