import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.pdfbox.pdmodel.PDDocument;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            String server = "https://www.slideshare.net/loianeg/angular-2-61549741";
            server = "https://www.slideshare.net/rdahis/quero-ser-um-growth-hacker?qid=60f15c4c-3da0-42b8-ad29-1233bb6567ef&v=&b=&from_search=12";
            PDDocument document = new PDDocument();
            Document doc = Jsoup.connect(server).get();
            Elements title = doc.getElementsByClass("slide_container");
            int pagina = 0;
            for (Element e : title){
                Elements t = e.getElementsByClass("slide_image");
                for (Element i : t){
                    String url = i.attr("data-normal");
                    String destino = "./imgs/"+ getMd5(url)+".png";
                    saveImage(url, destino);
                    File file = new File(destino);
                    PDXObjectImage ximage = new PDJpeg(document, new FileInputStream(file ) );
                    PDRectangle rec = new PDRectangle(ximage.getWidth(), ximage.getHeight());
                    PDPage page = new PDPage(rec);
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
                    contentStream.drawImage( ximage, -1, 0 );
                    contentStream.close();
                    System.out.println(pagina+"/"+ t.size() );
                    pagina++;
                    try {
                        Files.delete(file.toPath());
                    }catch (Exception err){
                        System.out.println(err.toString());
                    }
                }
            }
            try {
                document.save("./"+getMd5(server)+".pdf");
            } catch (COSVisitorException e) {
                e.printStackTrace();
            }
            document.close();
        }catch (Exception e){

        }
    }
    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }
    public static String getMd5(String name) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(name.getBytes());
        byte[] hashMd5 = md.digest();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < hashMd5.length; i++) {
            int parteAlta = ((hashMd5[i] >> 4) & 0xf) << 4;
            int parteBaixa = hashMd5[i] & 0xf;
            if (parteAlta == 0) s.append('0');
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }
}
