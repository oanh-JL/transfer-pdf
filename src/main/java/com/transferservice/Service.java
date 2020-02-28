package com.transferservice;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;


@org.springframework.stereotype.Service
public class Service {
    private final Logger logger = Logger.getLogger(Service.class);
    public static final String uri = "https://api.wepdf.io/render";

    void renderURL(String apiKey, String path) {
        try {
            String url = uri + "?apiKey=" + apiKey + "&url=" + path.trim();
            RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("");

            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(
                    String.valueOf(restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class)));
            System.out.println(entity);
            InputStream targetStream = new ByteArrayInputStream(decodedBytes);
            PDDocument document = PDDocument.load(targetStream);
            document.save("C:/test.pdf");
            FileUtils.writeByteArrayToFile(new File("C:/test.pdf"), decodedBytes);
        } catch (Exception e) {
            logger.error("Could not connect to Server ");
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
