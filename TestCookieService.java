package ngmk.auh.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Service
public class TestCookieService {

    private String taxWebSiteUrl = "https://my.soliq.uz/main/";

  //  private String taxLoginUrl = "https://my.soliq.uz/main/auth?user_type=1";

    private String testUrl = "https://my.soliq.uz/cashregister/searchctos?region_code=12&kkmCategoryId=34&_=1654234978907";

    private RestTemplate restTemplate;

    @PostConstruct
    public void createRestTemplate() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("srv-proxy01.ngmk.uz", 3128));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        SimpleClientHttpRequestFactory noRedirectRequestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) {
                connection.setInstanceFollowRedirects(true);
            }
        };
        noRedirectRequestFactory.setProxy(proxy);
        requestFactory.setProxy(proxy);
        restTemplate = new RestTemplate(requestFactory);
    }

    public void checkCookieAuth() throws IOException, InterruptedException {


        WebDriver driver = null;


        ChromeOptions options = new ChromeOptions();
        String proxy = "srv-proxy01.ngmk.uz";
        driver = new ChromeDriver(options);

        driver.get(taxWebSiteUrl);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        Thread.sleep(40000);
        Document doc = Jsoup.parse(driver.getPageSource());

        HttpHeaders headers = new HttpHeaders();
        StringBuilder cookieText = new StringBuilder();
        for (Cookie cookie : driver.manage().getCookies()) {
            cookieText.append(cookie.toString()).append("; ");
        }

        headers.add("Cookie", cookieText.toString());
        ResponseEntity<Object> exchange = restTemplate.exchange(testUrl, HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        System.out.println(exchange);


        System.out.println("DONE");


    }
}
