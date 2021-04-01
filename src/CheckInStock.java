import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;

public class CheckInStock {

    private Map<String, Document> docs;
    private FileWriter myWriter;

    public static void main(String[] args) throws IOException {

        CheckInStock checkInStock = new CheckInStock();
        checkInStock.checkStock();
        checkInStock.close();
    }

    private void close() throws IOException {
        myWriter.close();
    }

    public CheckInStock() throws IOException {
        docs = new HashMap<>();

        File newFile = new File("data/websites.txt");
        Scanner program = new Scanner(newFile);

        while(program.hasNextLine()){
            docs.put(program.nextLine(), Jsoup.connect(program.nextLine()).timeout(6000).get());
        }

        myWriter = new FileWriter("F:\\CheckInStock\\data\\result.txt");
    }

    public void checkStock() throws IOException {

        for(Map.Entry<String,Document> entry : docs.entrySet()){
            if(entry.getKey().equals("NewEgg")){
                checkNewEgg(entry.getValue());
            }
        }

    }

    public void checkNewEgg(Document doc) throws IOException {
        Elements sections = doc.select("div.item-cells-wrap.border-cells.items-grid-view.four-cells.expulsion-one-cell");

        for(Element element : sections){
            Elements children = element.children();
            for(Element child : children){
                Elements item = child.select("div.item-container").select("div.item-info");
                if(!item.isEmpty()){

                    System.out.println(item.select("a.item-title").text());
                    myWriter.write(item.select("a.item-title").text() + "\n");
                    System.out.println(item.select("p.item-promo").text());
                    myWriter.write(item.select("p.item-promo").text() + "\n");
                    System.out.println();
                    myWriter.write("\n");
                }

            }
        }


    }

}
