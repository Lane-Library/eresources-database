package edu.stanford.irt.eresources.sax.videos.hstalks;


import org.apache.commons.lang.StringEscapeUtils;
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
                            StringBuilder keywords = new StringBuilder(ERESOURCE_TYPE.concat(" "));
                            String description = null;
                            String year = null;
                            String url = null;
                            String date = null;
                            String title = videoNode.path("title").textValue();
                            
                            if (null != title) {
                                title = StringEscapeUtils.unescapeXml(title);
                                keywords.append(title);
                            }
                            
                            JsonNode node = videoNode.path("synopsis_fields");
                            if (node != null && node.path("slide") != null) {
                                description = StringEscapeUtils.unescapeXml( node.path("slide").textValue());
                                keywords.append(" " + description);
                            }
                            
                            if (videoNode.path("original_pub_date") != null) {
                                String dateStr = videoNode.path("original_pub_date").textValue();
                                if (dateStr.length() > 3) {
                                    year = dateStr.substring(0, 4);
                                    date = dateStr;
                                }
                            }
                            
                            if (null != videoNode.path("url")) {
                                url = "https://hstalks.com".concat(videoNode.path("url").asText());
                            }
                            
                            super.processEresource(id, ERESOURCE_TYPE, title, description, keywords.toString(), year, date, url, null);
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
