package com.github.serivesmejia.eocvsim.util;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StrUtil {

    public static final Pattern URL_PATTERN = Pattern.compile(
            "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
            Pattern.CASE_INSENSITIVE);

    public static String[] findUrlsInString(String str) {

        Matcher urlMatcher = URL_PATTERN.matcher(str);

        ArrayList<String> matches = new ArrayList<>();

        while(urlMatcher.find()) {
            String url = str.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
            matches.add(url);
        }

        return matches.toArray(new String[0]);

    }

    public static String getFileBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if(index == -1)
            return fileName;
        else
            return fileName.substring(0, index);
    }

}
