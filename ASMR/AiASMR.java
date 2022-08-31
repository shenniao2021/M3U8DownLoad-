package ASMR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wanglin
 * 2022/8/23 23:24
 */
public class AiASMR implements ASMR {
    String cookie = "starstruck_f6be77eddc2f40ba30a030eb7d294bcd=a8e0a2b2097fbc4e35fb21b1170cc394; wordpress_test_cookie=WP+Cookie+check; _ga=GA1.1.1069357527.1659824620; wordpress_logged_in_f6be77eddc2f40ba30a030eb7d294bcd=guozhi%7C1659997477%7CNzqw4wqPRTM52elFYpXPhqhfvXyZnDG48hjP1a4JROc%7Cdfd8ef80019d1768a990a4b1cb6d0815cfcb7f6ecad3d9a6216c273e289510ed; PHPSESSID=er9t0qlnu1giigtfdovbg5c3rh; _ga_2456WDN56B=GS1.1.1659823721.6.1.1659824682.0";
    String houzhui = ".ts";

    @Override
    public String getHouzui() {
        return houzhui;
    }

    @Override
    public String getCookie() {
        return cookie;
    }

    @Override
    public void setCookie(String cookie) {
         this.cookie = cookie;
    }

    @Override
    public HttpURLConnection config(URLConnection urlConnection){
        HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
        httpURLConnection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Safari/537.36");
        httpURLConnection.setRequestProperty("sec-ch-ua-platform","Windows");
        httpURLConnection.addRequestProperty("cookie",cookie);
        httpURLConnection.setRequestProperty("referer","https://www.aisasmr.com/");
        httpURLConnection.addRequestProperty("Cache-Control", "no-cache");
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
    }

    @Override
    public int fileCounts(String sb){
        //寻找文件数量
        int last = sb.indexOf("#EXT-X-ENDLIST");
        boolean isTrue = true;
        String temp = sb.substring(0,last);
        int tempI = temp.lastIndexOf(".ts");
        temp = temp.substring(0,tempI);
        int length = temp.length() - 1;
        while (isTrue){
            int c1 = temp.charAt(length);
            if(c1<48 || c1 > 57){
                isTrue = false;
                break;
            }
            length--;
        }
        length++;
        return Integer.parseInt(temp.substring(length));
    }

    @Override
    public void download(OutputStream out, InputStream inStrm, int fileLength) throws IOException {
        byte[] bytes = new byte[fileLength];
        int c;
        while ((c = inStrm.read(bytes)) != -1) {
            out.write(bytes,0,c);
        }
    }

    @Override
    public List<String> findTsName(String m3u8Address, String m3u8){


        int a = m3u8Address.lastIndexOf("/");
        m3u8Address = m3u8Address.substring(0, a);
        List<String> list = new ArrayList<String>();
        int i = m3u8.indexOf("#EXT-X-ENDLIST") + 1;
        m3u8 = m3u8.substring(0,i);

        int start = m3u8.indexOf("#EXTINF");
        m3u8 = m3u8.substring(start);
        start = m3u8.indexOf(",");
        while (start != -1){
            m3u8 = m3u8.substring(start + 1);
            int i1 = m3u8.indexOf("#");
            list.add(m3u8Address + "/" + m3u8.substring(0,i1));
            m3u8 = m3u8.substring(i1);
            start = m3u8.indexOf(",");
        }
        return list;

    }

    @Override
    public String findM3u8(String sb2){
        int last = sb2.indexOf("new DPlayer");
        String m3u8URL = "";

        if(last!=-1){
            sb2 = sb2.substring(last);
            int begin = sb2.indexOf("]");
            m3u8URL = sb2.substring(0,begin+1);
        }else {
            throw new RuntimeException("未找到m3u8地址");
        }
        int httpsI = m3u8URL.lastIndexOf("https");
        m3u8URL = m3u8URL.substring(httpsI);
        int i1 = m3u8URL.lastIndexOf("\"");
        m3u8URL = m3u8URL.substring(0, i1);
        return m3u8URL;
    }
}
