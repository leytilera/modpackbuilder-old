package anvil.modpackbuilder;

import java.io.*;

public class Reader {

    public static String readFile(File f) {
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String file = "";
            String line;
            while((line = br.readLine()) != null){
                file += line;
            }

            return file;

        } catch (Exception e) {
            return "";
        }
    }

    public static String readFile(String file) {
        return readFile(new File(file));
    }

}
