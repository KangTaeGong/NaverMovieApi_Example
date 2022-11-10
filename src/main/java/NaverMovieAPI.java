import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class NaverMovieAPI {
    public static void main(String[] args) {
        String id = "";
        String secret = "";

        try{
            NaverCrawler crawler = new NaverCrawler();
            String url = URLEncoder.encode("공조", "UTF-8");
            String response = crawler.search(id, secret, url);

            String[] fields = {"title", "image", "pubDate", "director", "actor", "userRating"};
            Map<String, Object> result = crawler.getResultMapping(response, fields);

            if(result.size() > 0)
                System.out.println("total -> " + result.get("total"));

            List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("result");

            for(Map<String, Object> item : items) {
                System.out.println("=====================================");

                for(String field : fields)
                    System.out.println(field + "->" + item.get(field));
            }
            //System.out.println(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
