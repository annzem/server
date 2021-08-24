package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {

    private static Map<String, String> fileCache = new ConcurrentHashMap<>();

    private static final Gson gson = new GsonBuilder().
            registerTypeAdapter(OffsetDateTime.class, new TypeAdapter<OffsetDateTime>() {

                private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                @Override
                public void write(JsonWriter jsonWriter, OffsetDateTime offsetDateTime) throws IOException {
                    jsonWriter.value(fmt.format(offsetDateTime));
                }

                @Override
                public OffsetDateTime read(JsonReader jsonReader) throws IOException {
                    throw new NotImplementedException();
                }
            }).
            setPrettyPrinting().create();

    public static String readFile(String path, boolean updateCache) throws IOException {
        if (updateCache || !fileCache.containsKey(path)) {
            FileInputStream fis = new FileInputStream(path);
            fileCache.put(path, IOUtils.toString(fis, "UTF-8"));
        }
        return fileCache.get(path);
    }

    public static Gson getGson() {
        return gson;
    }
}
