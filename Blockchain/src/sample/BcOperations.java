package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class BcOperations {

    //Change this to your directory of bitcoin-cli location
    private static String bitcoinDir = "/home/krasiren/Downloads/bitcoinSv_1.0.7/bin/bitcoin-cli";
    private static String startCommand = "-datadir=/home/krasiren/Downloads/bitcoinSv_1.0.7/data";

    //returns null if something goes oopsie or if String arg is not hexadecimal or not divisible by 2
    //else creates a new block with String arg data and returns its hash
    public static String createBlock (String niz) {
        if (!(checkIfHex(niz))) return null;
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

    //preveri ali je string hex in divisible by 2
    private static boolean checkIfHex(String s) {
        if ( s.length() == 0 ||
                (s.charAt(0) != '-' && Character.digit(s.charAt(0), 16) == -1))
            return false;
        if (s.length() % 2 != 0) return false;

        for ( int i = 1 ; i < s.length() ; i++ )
            if ( Character.digit(s.charAt(i), 16) == -1 )
                return false;
        return true;
    }

    public static String toHex(String n) {
        StringBuffer sb = new StringBuffer();
        char ch[] = n.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        return sb.toString();
    }

    //looks up data in a specific transaction and returns it in hex value
    public static String decodeData(String hash) {
        String[] args= {bitcoinDir, startCommand, "decoderawtransaction", hash};
        String transaction = "";
        try {
            Process p = Runtime.getRuntime().exec(args);
            p.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                transaction += line;
            }

            int ix = transaction.indexOf("\"0 OP_RETURN");
            int ex = ix + 1;
            while (transaction.charAt(ex) != '"') {
                ex++;
            }

            return transaction.substring(ix + 13, ex);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //ime pove kaj dela bruvkek
    public static String hexToString(String hex) {
        if (hex == null) return null;
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
