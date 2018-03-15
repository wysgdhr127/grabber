package com.grabber.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
  
/** 
 * jsonson utils 
 * @see http://jackson.codehaus.org/ 
 * @see https://github.com/FasterXML/jackson 
 * @see http://wiki.fasterxml.com/JacksonHome 
 * 
 */  
public class JacksonUtil {  
      
    private static ObjectMapper objectMapper = new ObjectMapper();  
    private static XmlMapper xmlMapper = new XmlMapper();  
    static{
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  
    }
      
    /** 
     * javaBean,list,array convert to json string 
     */  
    public static String obj2json(Object obj){  
        try
		{
			return objectMapper.writeValueAsString(obj);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			LoggerUtil.debugLog.error("Error parsing json", e);
			return null;
		}  
    }  
      
    /** 
     * json string convert to javaBean 
     */  
    public static <T> T json2pojo(String jsonStr,Class<T> clazz){  
        try
		{
			return objectMapper.readValue(jsonStr, clazz);
		}
		catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
    }  
      
    /** 
     * json string convert to map 
     */  
    public static <T> Map<String,Object> json2map(String jsonStr){  
        try
		{
			return objectMapper.readValue(jsonStr, Map.class);
		}
		catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
    }  
      
    /** 
     * json string convert to map with javaBean 
     */  
    public static <T> Map<String,T> json2map(String jsonStr,Class<T> clazz){  
    	try{
    		 Map<String,Map<String,Object>> map =  objectMapper.readValue(jsonStr, new TypeReference<Map<String,T>>() {  
    	        });  
    	     Map<String,T> result = new HashMap<String, T>();  
    	     for (Entry<String, Map<String,Object>> entry : map.entrySet()) {  
    	          result.put(entry.getKey(), map2pojo(entry.getValue(), clazz));  
    	     }  
    	     return result;  
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }  
      
    /** 
     * json array string convert to list with javaBean 
     */  
    public static <T> List<T> json2list(String jsonArrayStr,Class<T> clazz)throws Exception{  
        List<Map<String,Object>> list = objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {  
        });  
        List<T> result = new ArrayList<T>();  
        for (Map<String, Object> map : list) {  
            result.add(map2pojo(map, clazz));  
        }  
        return result;  
    }  
      
    /** 
     * map convert to javaBean 
     */  
    public static <T> T map2pojo(Map map,Class<T> clazz){  
        return objectMapper.convertValue(map, clazz);  
    }  
      
    /** 
     * json string convert to xml string 
     */  
    public static String json2xml(String jsonStr)throws Exception{  
        JsonNode root = objectMapper.readTree(jsonStr);  
        String xml = xmlMapper.writeValueAsString(root);  
        return xml;  
    }  
      
    /** 
     * xml string convert to json string 
     */  
    public static String xml2json(String xml)throws Exception{  
        StringWriter w = new StringWriter();  
        JsonParser jp = xmlMapper.getFactory().createParser(xml);  
        JsonGenerator jg = objectMapper.getFactory().createGenerator(w);  
        while (jp.nextToken() != null) {  
            jg.copyCurrentEvent(jp);  
        }  
        jp.close();  
        jg.close();  
        return w.toString();  
    }  
      
}  