package edu.stanford.irt.eresources.sax.videos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cyberneko.html.HTMLConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public abstract class JsonVideoEresourceProcessor extends AbstractVideoEresourceProcessor{
    
 
    protected HttpClient httpClient = null;

    private Header USER_AGENT = new BasicHeader("User-Agent",    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:34.0) Gecko/20100101 Firefox/34.");

    
    
    public JsonVideoEresourceProcessor() {
        this.httpClient = HttpClients.createDefault();
    }

    protected JsonNode getJsonNode(String url) throws IOException {
        CloseableHttpResponse res = null;
        try {
            HTMLConfiguration config = new HTMLConfiguration();
            config.setFeature("http://xml.org/sax/features/namespaces", false);
            config.setProperty("http://cyberneko.org/html/properties/default-encoding", StandardCharsets.UTF_8.name());
            config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            HttpGet get = new HttpGet(url);
            get.addHeader(USER_AGENT);
            res = (CloseableHttpResponse) httpClient.execute(get);
            
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(res.getEntity().getContent(), JsonNode.class);
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }


}
