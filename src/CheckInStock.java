import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CheckInStock {

    private Map<String, Document> docs;
    private FileWriter myWriter;

    private String address = "windland.6@buckeyemail.osu.edu";
    private String host = "smtp.example.com";

    public static void main(String[] args) throws IOException {

        CheckInStock checkInStock = new CheckInStock();
        checkInStock.checkStock();
        checkInStock.close();
        checkInStock.sendEmail();
    }

    private void sendEmail(){

            // Get system properties
            Properties properties = System.getProperties();

            // Setup mail server
            properties.setProperty("mail.smtp.host", host);

            // Get the default Session object.
            Session session = Session.getDefaultInstance(properties);

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(address));

                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));

                // Set Subject: header field
                message.setSubject("This is the Subject Line!");

                // Now set the actual message
                message.setText("This is actual message");

                // Send message
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
    }

    private void close() throws IOException {
        myWriter.close();
    }

    private CheckInStock() throws IOException {
        docs = new HashMap<>();

        File newFile = new File("data/websites.txt");
        Scanner program = new Scanner(newFile);

        while(program.hasNextLine()){
            docs.put(program.nextLine(), Jsoup.connect(program.nextLine()).timeout(6000).get());
        }

        myWriter = new FileWriter("F:\\CheckInStock\\data\\result.txt");
    }

    private void checkStock() throws IOException {

        for(Map.Entry<String,Document> entry : docs.entrySet()){
            System.out.println(entry.getKey());
            myWriter.write(entry.getKey() + "\n");
            System.out.println();
            myWriter.write("\n");
            if(entry.getKey().startsWith("NewEgg")){
                checkNewEgg(entry.getValue());
            }else if(entry.getKey().startsWith("MicroCenter")) {
                checkMicro(entry.getValue());
            }
        }

    }

    public void checkMicro(Document doc) throws IOException {
        Elements sections = doc.select("article#productGrid").select("ul");

        for(Element element : sections) {
            Elements elements = element.select("div.result_right").select("div.details").select("div.detail_wrapper");
            for(Element item : elements){

                System.out.println(item.select("div.pDescription.compressedNormal2").text());
                myWriter.write(item.select("div.pDescription.compressedNormal2").text() + "\n");

                if(item.select("div.stock").text().startsWith("Usually ships in")){
                    System.out.println("ORDER NOW");
                    myWriter.write("ORDER NOW" + "\n");
                }else{
                    System.out.println("OUT OF STOCK");
                    myWriter.write("OUT OF STOCK" + "\n");
                }
                System.out.println();
                myWriter.write("\n");
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

                    Elements promo = item.select("p.item-promo");

                    if(!promo.isEmpty()){
                        System.out.println(item.select("p.item-promo").text());
                        myWriter.write(item.select("p.item-promo").text() + "\n");
                    }else{
                        System.out.println("ORDER NOW");
                        myWriter.write("ORDER NOW");
                    }
                    System.out.println();
                    myWriter.write("\n");
                }

            }
        }


    }

}
