import java.net.URLEncoder;
import java.util.*;
import java.util.Map;

public class ParameterParsing {

    public static String buildParameterString(HashMap<String,String> hashMap) {

        StringBuilder parameter = new StringBuilder();
        parameter.append("?");
        
        for (HashMap.Entry<String,String> param: hashMap.entrySet()) {
                parameter.append(param.getKey());
                parameter.append("=");
                parameter.append(param.getValue());
                parameter.append("&");
        }

        String parameterString = parameter.toString();
        return parameterString;
    }
}