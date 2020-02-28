package com.transferservice;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;

@RestController
public class Controller {

    private final Logger logger = Logger.getLogger(Service.class);
    public static final String uri = "https://api.wepdf.io/render";

    @GetMapping("/render")
    public String transferUrl(@RequestParam String apikey,
                            @RequestParam String path) {
        try {
            String url = uri + "?apikey=" + apikey + "&url=" + path.trim();
            RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
            HttpEntity<String> entity = new HttpEntity<>("");
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("/home/oanhpham/Desktop/example.pdf"));

            // mở file để thực hiện viết
            document.open();
            // thêm nội dung sử dụng add function
            document.add(new Paragraph(responseEntity.getBody()));
            // đóng file
            document.close();

            return "done";
        } catch (Exception e) {
            logger.error("Could not connect to Server ");
            return "toang";
        }
    }
    public ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
