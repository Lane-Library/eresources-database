package edu.stanford.irt.eresources.sax.videos.clinicalkey;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.xml.sax.helpers.AttributesImpl;

import com.fasterxml.jackson.databind.JsonNode;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.JsonVideoEresourceProcessor;

public class ClinicalkeyEresourceProcessor extends JsonVideoEresourceProcessor {

    private final String ERESOURCE_TYPE = "clinicalkey";

    @Override
    public void process() {
        try {
            getNewSession();
            int index = 0;
            int offSet = 0;
            int maxAuthor = 10;
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            JsonNode jsonMap = getJsonNode(this.URLs.get(1).concat(String.valueOf(offSet)));
            JsonNode jsonResult = jsonMap.findPath("docs");
            while (jsonResult.size() != 0) {
                for (int i = 0; i < jsonResult.size(); i++) {
                    JsonNode videoNode = jsonResult.get(i);
                    String id = videoNode.path("hubeid").textValue();
                    if (null != id && !"".equals(id)) {
                        id = String.valueOf(++index);
                        StringBuilder keywords = new StringBuilder(ERESOURCE_TYPE.concat(" "));
                        String description = null;
                        String year = null;
                        String url = null;
                        String authors = null;
                        String title = videoNode.path("itemtitle").textValue();
                        if (null != title) {
                            keywords.append(title);
                        }
                        if (null != videoNode.path("sourcetitle") && videoNode.path("sourcetitle").textValue() != null) {
                            keywords.append(" " + videoNode.path("sourcetitle").textValue());
                        }
                        if (videoNode.path("summary_s") != null && videoNode.path("summary_s").textValue() != null) {
                            description = videoNode.path("summary_s").textValue();
                            keywords.append(" " + description);
                        }
                        if (videoNode.path("copyrightyear") != null && videoNode.path("copyrightyear").get(0) != null) {
                            year = videoNode.path("copyrightyear").get(0).textValue();
                        }
                        if (null != videoNode.path("refimage") && null != videoNode.path("refimage").get(0).textValue()) {
                            String encodedTitle = URLEncoder.encode(title, "UTF-8");
                            url = "https://www.clinicalkey.com/#!/search/".concat(encodedTitle).concat("/%7B%22facetquery%22:%5B%22+contenttype:VD%22%5D,%22query%22:%22")
                                    .concat(encodedTitle).concat("%22%7D");
                        }
                        if (videoNode.path("authorlist") != null) {
                            List<String> alreadyIn = new ArrayList<String>();
                            StringBuilder sb = new StringBuilder();
                            JsonNode authorList =  videoNode.path("authorlist");
                            for (int j = 0; j < maxAuthor; j++) {
                                if (null != authorList.get(j) ) {
                                    String author = authorList.get(j).textValue().trim();
                                    if(!alreadyIn.contains(author)){
                                        sb.append(author);
                                        sb.append("; ");
                                        alreadyIn.add(author);
                                    }
                                }
                            }
                            authors = sb.toString().trim();
                            if(authors.length()>0){
                                authors = authors.substring(0, authors.length()-1);
                            }
                            if( maxAuthor < authorList.size()){
                               authors = authors + ";.....";   
                            }    
                       }
                       
                       super.processEresource(id, ERESOURCE_TYPE, title, description, keywords.toString(), year, null, url, authors);
                    }
                }
                offSet = offSet + 100;
                Thread.sleep(100);
                jsonMap = getJsonNode(this.URLs.get(1).concat(String.valueOf(offSet)));
                jsonResult = jsonMap.findPath("docs");
             }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }

    private void getNewSession() throws ClientProtocolException, IOException {
        HttpPost post = new HttpPost(URLs.get(0));
        CloseableHttpResponse res = (CloseableHttpResponse) super.httpClient.execute(post);
        res.getEntity().getContent();
        res.close();
    }
}
