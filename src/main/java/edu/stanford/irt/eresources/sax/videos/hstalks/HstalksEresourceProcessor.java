package edu.stanford.irt.eresources.sax.videos.hstalks;

import org.xml.sax.helpers.AttributesImpl;

import com.fasterxml.jackson.databind.JsonNode;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.sax.videos.JsonVideoEresourceProcessor;

public class HstalksEresourceProcessor extends JsonVideoEresourceProcessor {

    private final String ERESOURCE_TYPE = "hstalks";

    @Override
    public void process() {
        try {
            this.contentHandler.startDocument();
            this.contentHandler.startElement("", ERESOURCES, ERESOURCES, new AttributesImpl());
            int offSet = 0;
                JsonNode jsonMap = getJsonNode(this.URLs.get(0).concat(String.valueOf(offSet)));
                JsonNode jsonResult = jsonMap.findPath("results");
                while (jsonResult.size() != 0) {
                    for (int i = 0; i < jsonResult.size(); i++) {
                        JsonNode videoNode = jsonResult.get(i);
                        String id = videoNode.path("id").textValue();
                        if (null != id && !"".equals(id)) {
                            StringBuilder keywords = new StringBuilder();
                            String description = null;
                            String year = null;
                            String url = null;
                            String title = videoNode.path("title").textValue();
                            
                            if (null != title) {
                                keywords.append(title);
                            }
                            
                            JsonNode node = videoNode.path("synopsis_fields");
                            if (node != null && node.path("slide") != null) {
                                description = node.path("slide").textValue();
                                keywords.append(" " + description);
                            }
                            
                            if (videoNode.path("original_pub_date") != null) {
                                year = videoNode.path("original_pub_date").textValue();
                                if (year.length() > 3) {
                                    year = year.substring(0, 4);
                                }
                            }
                            
                            if (null != videoNode.path("url")) {
                                url = "https://hstalks.com".concat(videoNode.path("url").asText());
                            }
                            
                            super.processJson(id, ERESOURCE_TYPE, title, description, keywords.toString(), year, url);
                        }
                    }
                    offSet = offSet + 10;
                    Thread.sleep(1000);
                    jsonMap = getJsonNode(this.URLs.get(0).concat(String.valueOf(offSet)));
                    jsonResult = jsonMap.findPath("results");
            }
            this.contentHandler.endElement("", ERESOURCES, ERESOURCES);
            this.contentHandler.endDocument();
        } catch (Exception e) {
            throw new EresourceDatabaseException(e);
        }
    }
    
   
    
}
