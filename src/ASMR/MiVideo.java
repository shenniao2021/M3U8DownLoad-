package ASMR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wanglin
 * 2022/8/23 23:24
 */
public class MiVideo implements ASMR {
    String cookie = "";

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
        httpURLConnection.setRequestProperty("referer","https://www.gqtod.com/");
        httpURLConnection.addRequestProperty("Cache-Control", "no-cache");
        httpURLConnection.addRequestProperty("cookie",cookie);
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
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
    public List<String> findTsName(String m3u8Address,String m3u8){
        List<String> list = new ArrayList<String>();
        int i = m3u8.indexOf("#EXT-X-ENDLIST") + 1;
        m3u8 = m3u8.substring(0,i);
        if(m3u8.contains("https")){
            int i1 = m3u8.indexOf(".key");
            if(i1 != -1){
                m3u8 = m3u8.substring(i1);
            }
            int https = m3u8.indexOf("https");
            while (https != -1){
                m3u8 = m3u8.substring(https);
                int i2 = m3u8.indexOf("#");
                list.add( m3u8.substring(0,i2));
                m3u8 = m3u8.substring(i2);
                https = m3u8.indexOf("https");
            }
        }else {
            int a = m3u8Address.lastIndexOf("com");
            m3u8Address = m3u8Address.substring(0, a+3);

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
        }


        return list;
    }

    @Override
    public String findM3u8(String sb2){
        StringBuilder array = new StringBuilder();
        int last = sb2.indexOf("var player_aaaa");
        sb2 = sb2.substring(last);
        int begin = sb2.indexOf("</script> ");
        sb2 = sb2.substring(0,begin+1);
        int m3u8 = 0;
        while ((m3u8 = sb2.lastIndexOf(".m3u8")) != -1){
            int https = sb2.lastIndexOf("https");
            array.append(sb2, https, m3u8 + ".m3u8".length()).append(";");
            sb2 = sb2.substring(0,https);
        }
        return array.toString();
    }
}
