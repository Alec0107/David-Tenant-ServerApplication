package SERVER.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper{

    private static Gson gson;

    public GsonHelper() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
    }

}
