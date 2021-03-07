package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandling {


    public HashMap<String, String> exportToHashMap(Path path) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String line = bufferedReader.readLine();

            while (line != null) {
                map.put(line.substring(0, line.lastIndexOf("\t")),
                        line.substring(line.lastIndexOf("\t") + 1, line.length()));
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Long sumCreditorAmount(HashMap<String, String> mapPardakht) {
        Long sum = 0L;

        for (Map.Entry<String, String> entry : mapPardakht.entrySet()) {
            if (entry.getKey().contains("c")) {
                sum += Long.parseLong(entry.getValue());
            }
        }
        return sum;
    }

    public boolean comparing(Long exist, Long required) {
        if (exist >= required) {
            return true;
        } else {
            return false;
        }

    }

    public List<String> getDepositNumbD(HashMap<String, String> map) {
        List<String> depositDList = new ArrayList<>();
        for (String s : map.keySet()) {
            if (s.contains("d")) {
                depositDList.add(s);
            }
        }
        return depositDList;
    }

    public List<String> getDepositeNumbC(HashMap<String, String> map) {
        List<String> depositCList = new ArrayList<>();
        for (String s : map.keySet()) {
            if (s.contains("c")) {
                depositCList.add(s);
            }
        }
        return depositCList;
    }

    public String isPayable(HashMap mapPardakht, HashMap mapMojoodi) {
        FileHandling fh = new FileHandling();
        Long required = sumCreditorAmount(mapPardakht);
        List<String> depositNumbD = fh.getDepositNumbD(mapPardakht);
        for (int i = 0; i < depositNumbD.size(); i++) {
            String s = depositNumbD.get(i);

            boolean comparing = fh.comparing(Long.parseLong(String.valueOf(mapMojoodi.get(s.substring(s.lastIndexOf("\t") + 1, s.length())))), required);
            if (comparing)
                return depositNumbD.get(i);

        }
        return null;
    }

    public String updateDAccounts(HashMap<String, String> mapPardakht, HashMap<String, String> mapMojoodi) {
        String finalString = "";
        List<String> depositNumbD = getDepositNumbD(mapPardakht);
        Long required = sumCreditorAmount(mapPardakht);
        String payableAcc = isPayable(mapPardakht, mapMojoodi);

        String oldMount = mapMojoodi.get(payableAcc.substring(payableAcc.indexOf("\t") + 1, payableAcc.length()));
        Long oldmount = Long.parseLong(oldMount);

        Long updatedmount = oldmount - required;
        String updatedMount = String.valueOf(updatedmount);

        for (int i = 0; i < depositNumbD.size(); i++) {
            String s = depositNumbD.get(i);
            if (s.equals(payableAcc)) {
                finalString = finalString.concat(payableAcc.substring(
                        payableAcc.indexOf("\t") + 1, payableAcc.length()) + "\t" + updatedMount + "\r\n");
            } else {
                finalString = finalString.concat(s.substring(s.indexOf("\t") + 1, s.length()) + "\t"
                        + mapMojoodi.get(s.substring(s.indexOf("\t") + 1, s.length())) + "\r\n");
            }
        }
        return finalString;
    }



    public String updateCAccounts(HashMap<String, String> mapPardakht, HashMap<String, String> mapMojoodi) {
        String finalString = "";
        //payable account
        String payable = isPayable(mapPardakht, mapMojoodi);


        List<String> depositeNumbCs = getDepositeNumbC(mapPardakht);
        for (int i = 0; i < depositeNumbCs.size(); i++) {
            String s = depositeNumbCs.get(i);

            String oldMount = mapMojoodi.get(s.substring(s.indexOf("\t") + 1, s.length()));
            Long oldmount = Long.parseLong(oldMount);

            String newMount = mapPardakht.get(s);
            Long newmount = Long.parseLong(newMount);

            Long updatedmount = oldmount + newmount;
            String updatedMount = String.valueOf(updatedmount);
            writeLog(payable.substring(payable.indexOf("\t") + 1, payable.length())
                    + "\t" + s.substring(s.indexOf("\t") + 1, s.length()) + "\t" + newMount + "\r\n");

            finalString = finalString.concat(s.substring(s.indexOf("\t") + 1, s.length()) + "\t" + updatedMount + "\r\n");
        }
        return finalString;

    }

    public void writeUpdatedMount(String updatedString, Path pathMojoodi) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(pathMojoodi)) {
            bufferedWriter.write(updatedString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLog(String s) {
        Path path = Paths.get("Files/log.txt");
        try {
            Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
