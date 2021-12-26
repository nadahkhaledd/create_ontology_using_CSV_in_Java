package football;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import java.io.*;
import java.util.Vector;

public class readAndConvert {

    static public Vector<String[]> getFullData(String filePath) throws IOException {
        BufferedReader br;
        String line = "";
        Vector<String[]> data = new Vector<String[]>();
        String[] columns;

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null){
                columns = line.split(",");
                data.add(columns);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }


}
