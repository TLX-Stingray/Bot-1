package discord.bot;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DonateSys {
    public static Set<String> donateTier1 = new HashSet<>();

    public static Set<String> loadTier1()
    {
        File tier1 = new File(App.storeMod + "Tier1.tier");
        if (!tier1.exists())
        {
            try {
                FileOutputStream fileOut = new FileOutputStream(tier1);
                fileOut.close();

                System.out.print("Tier 1 File Created Success");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> tier1IDs = new ArrayList<String>();
        try {
            InputStream fileIn = new FileInputStream(tier1.getPath());
            BufferedReader r = new BufferedReader(new InputStreamReader(fileIn));
            String line;
            while ((line = r.readLine()) != null) {
                tier1IDs.add(line);
            }
        } catch (FileNotFoundException  e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        //Read File Content
        donateTier1 = new HashSet<>(tier1IDs);

        return donateTier1;
    }

    public static Boolean addTier1(String userID)
    {
        try {
            File tier1 = new File(App.storeMod + "Tier1.tier");
            BufferedWriter fileWrite = new BufferedWriter(new FileWriter(tier1));
            fileWrite.write(userID + "\n");
            fileWrite.close();
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
