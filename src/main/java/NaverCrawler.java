import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaverCrawler {

    final String baseUrl = "https://openapi.naver.com/v1/search/movie.json?query=";

    public String search(String clientId, String password, String _url) {
        HttpURLConnection con = null;
        String result = "";

        try{
            URL url = new URL(baseUrl + _url);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", password);

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
                result = readBody(con.getInputStream());
            else
                result = readBody(con.getErrorStream());
        } catch (Exception e) {
            System.out.println("연결 오류 : " + e);
        } finally {
            con.disconnect();
        }
        return result;
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try(BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while((line = lineReader.readLine()) != null) {
                responseBody.append(line).append("\n");
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
    
    /*
    * 결과 매핑하는 메소드
    * */
    public Map<String, Object> getResultMapping(String response, String[] fields) {
        Map<String, Object> rtnObj = new HashMap<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(response);
            rtnObj.put("total", result.get("total"));

            JSONArray items = (JSONArray) result.get("items");
            List<Map<String, Object>> itemList = new ArrayList<>();

            for(int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                Map<String, Object> itemMap = new HashMap<>();

                for(String field : fields) {
                    itemMap.put(field, item.get(field));
                }
                itemList.add(itemMap);
            }
            rtnObj.put("result", itemList);

        } catch (ParseException e) {
            System.out.println("getResult Error -> " + "Parsing Error, " + e.getMessage());
        }
        return rtnObj;
    }
}