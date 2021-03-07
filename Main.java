package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Path path1 = Paths.get("files/pardakht.txt");
        Path path2 = Paths.get("files/mojoodi.txt");

        FileHandling fh = new FileHandling();
        HashMap<String, String> toHashMapPrdkht = fh.exportToHashMap(path1);
        HashMap<String, String> toHashMapMojd = fh.exportToHashMap(path2);

        String payable = fh.isPayable(toHashMapPrdkht, toHashMapMojd);
        String updatedMojoodi = "";
        if(payable!=null){
            System.out.println("you can pay with this "+payable.substring(payable.indexOf("\t"),payable.length())+
                    "   deposite number!");
            updatedMojoodi = fh.updateDAccounts(toHashMapPrdkht, toHashMapMojd);
            updatedMojoodi = updatedMojoodi.concat(fh.updateCAccounts(toHashMapPrdkht, toHashMapMojd));
            fh.writeUpdatedMount(updatedMojoodi, path2);

        }else {
            System.out.println("you dont have enough money ! ");
        }







    }


}
