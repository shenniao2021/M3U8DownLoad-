package ASMR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;

/**
 * @Author wanglin
 * 2022/8/23 23:16
 */
public interface  ASMR {
    String getHouzui();
     String getCookie();
     void setCookie(String cookie);
    HttpURLConnection config(URLConnection urlConnection);

    int fileCounts(String sb);
     void download(OutputStream out, InputStream inStrm, int fileLength) throws IOException;
    List<String> findTsName(String m3u8Address, String m3u8);
    String findM3u8(String sb2);
}
