package com.company;

import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.io.IOException;

public class Utils {
    public static String readFile(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        return IOUtils.toString(fis, "UTF-8");
    }
}
