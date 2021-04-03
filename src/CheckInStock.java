import java.io.*;
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

    private String host = "smtp.gmail.com";

    private String body = "";

    private boolean check = false;

    String SUBJECT = "GPU in stock!";

    public static void main(String[] args) throws IOException {

        CheckInStock checkInStock = new CheckInStock();
        checkInStock.checkStock();

        if(checkInStock.check) {
            checkInStock.sendEmail();
        }else{
            System.out.println("None in stock :(");
        }

    }

    private CheckInStock() throws IOException {
        docs = new HashMap<>();

        InputStream input = CheckInStock.class.getResourceAsStream("data/websites.txt");
        Scanner program = new Scanner(input);

        while(program.hasNextLine()){
            docs.put(program.nextLine(), Jsoup.connect(program.nextLine()).timeout(6000).get());
        }

    }

    private void sendEmail() {
        InputStream input1 = CheckInStock.class.getResourceAsStream("data/email.txt");

        Scanner program = new Scanner(input1);

        String username = program.nextLine().trim();
        String password = program.nextLine().trim();
        String address = program.nextLine().trim();
        program.close();

        Properties properties = System.getProperties();

        properties.put("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));

            message.setSubject(SUBJECT);

            message.setText(body);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    private void checkStock() {

        for(Map.Entry<String,Document> entry : docs.entrySet()){
            System.out.println(entry.getKey());
            body += entry.getKey() + "\n";
            System.out.println();
            body += "\n";
            if(entry.getKey().startsWith("NewEgg")){
                checkNewEgg(entry.getValue());
            }else if(entry.getKey().startsWith("MicroCenter")) {
                checkMicro(entry.getValue());
            }else if(entry.getKey().startsWith("Best Buy")) {
                checkBest(entry.getValue());
            }
        }

    }

    public void checkBest(Document doc) {
        Elements sections = doc.select("div#main-results").select("ol").select("li.sku-item");

        for(Element element : sections) {
            System.out.println(element.select("div").select("div").select("div").select("div").select("div").select("div.right-column").select("div.information").select("div:eq(1)").select("div").select("h4").select("a").text());
            body += element.select("div").select("div").select("div").select("div").select("div").select("div.right-column").select("div.information").select("div:eq(1)").select("div").select("h4").select("a").text() + "\n";

            String stock = element.select("div").select("div").select("div").select("div").select("div").select("div.right-column").select("div.price-block").select("div.sku-list-item-button").select("div").select("div").select("div").select("div").select("div").select("button").text();

            if(stock.equals("Sold Out") || stock.equals("Coming Soon")){
                stock = "OUT OF STOCK";
                System.out.println(stock);
                body += stock + "\n";
            }else{
                stock = "ORDER NOW";
                System.out.println(stock);
                body += stock + "\n";

                check = true;
            }

            System.out.println();
            body += "\n";
        }

    }

    public void checkMicro(Document doc) {
        Elements sections = doc.select("article#productGrid").select("ul");

        for(Element element : sections) {
            Elements elements = element.select("div.result_right").select("div.details").select("div.detail_wrapper");
            for(Element item : elements){

                System.out.println(item.select("div.pDescription.compressedNormal2").text());
                body += item.select("div.pDescription.compressedNormal2").text() + "\n";

                if(item.select("div.stock").text().startsWith("Usually ships in")){
                    System.out.println("ORDER NOW");
                    body += "ORDER NOW" + "\n";

                    check = true;
                }else{
                    System.out.println("OUT OF STOCK");
                    body += "OUT OF STOCK" + "\n";
                }
                System.out.println();
                body += "\n";
            }
        }

    }

    public void checkNewEgg(Document doc) {

        Elements sections = doc.select("div.item-cells-wrap.border-cells.items-grid-view.four-cells.expulsion-one-cell");

        for(Element element : sections){
            Elements children = element.children();
            for(Element child : children){
                Elements item = child.select("div.item-container").select("div.item-info");
                if(!item.isEmpty()){

                    System.out.println(item.select("a.item-title").text());
                    body += item.select("a.item-title").text() + "\n";

                    Elements promo = item.select("p.item-promo");

                    if(!promo.isEmpty()){
                        System.out.println(item.select("p.item-promo").text());
                        body += item.select("p.item-promo").text() + "\n";
                    }else{
                        System.out.println("ORDER NOW");
                        body += "ORDER NOW" + "\n";

                        check = true;
                    }
                    System.out.println();
                    body += "\n";
                }

            }
        }


    }

}
