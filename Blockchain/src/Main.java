import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class Main {

    //Change this to your directory of bitcoin-cli location
    private static String bitcoinDir = "/home/krasiren/Downloads/bitcoinSv_1.0.7/bin/bitcoin-cli";
    private static String startCommand = "-datadir=/home/krasiren/Downloads/bitcoinSv_1.0.7/data";

    public static void main(String[] args) {
        System.out.println(createBlock("4545755667"));
    }

    public static String createBlock (String niz) {
        //if (!(checkIfHex(niz))) return;
        Process p;
        String hash = "";
        String[] args = {bitcoinDir, startCommand, "createrawtransaction", "[]", "{\"data\":\"" + niz +"\"}"};

        try {
            //create
            p = Runtime.getRuntime().exec(args);
            p.waitFor();

            String line = "";
            hash = "";
            BufferedReader br = new BufferedReader(new InputStreamReader((p.getInputStream())));
            while ((line = br.readLine())!= null) {
                hash += line;
            }

            String[] args2 = new String[4];
            args2[0] = bitcoinDir;
            args2[1] = startCommand;
            args2[2] = "fundrawtransaction";
            args2[3] = hash;

            //fund
            p = Runtime.getRuntime().exec(args2);
            p.waitFor();

            line = "";
            hash = "";
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine())!= null) {
                hash += line;
            }
            String[] arr = hash.split(",");
            String[] ar = arr[0].split(": ");
            hash = ar[1].substring(1, ar[1].length()-1);

            args2[2] = "signrawtransaction";
            args2[3] = hash;

            //sign
            p = Runtime.getRuntime().exec(args2);
            p.waitFor();

            line = "";
            hash = "";
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine())!= null) {
                hash += line;
            }
            String[] arr2 = hash.split(",");
            String[] ar2 = arr2[0].split(": ");
            hash = ar2[1].substring(1, ar2[1].length()-1);

            args2[2] = "sendrawtransaction";
            args2[3] = hash;

            //send
            p = Runtime.getRuntime().exec(args2);
            p.waitFor();

            args2[2] = "generate";
            args2[3] = "1";

            //generate 1
            p = Runtime.getRuntime().exec(args2);

            return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
