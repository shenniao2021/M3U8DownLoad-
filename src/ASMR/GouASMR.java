package ASMR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wanglin
 * 2022/8/23 23:24
 */
public class GouASMR implements ASMR {
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
        httpURLConnection.setRequestProperty("referer","https://www.822gw.com");
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
        int last = sb2.indexOf("https://www.822gw.com/e/DownSys/play/");
        String sb = sb2.substring(last);
        int i = sb.indexOf("\"");
        sb = sb.substring(0, i);

        URL url = null;
        try {
            url = new URL(sb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(sb);
        URLConnection rulConnection = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        try{
                rulConnection = url.openConnection();
                httpUrlConnection = this.config(rulConnection);
                inStrm = httpUrlConnection.getInputStream();
        }catch (IOException io){
            io.printStackTrace();
        }
        byte[] bytes = new byte[1024];
        StringBuilder sbAfter = new StringBuilder();
        int c = 0;
        try {
            while ((c = inStrm.read(bytes)) != -1){
               sbAfter.append(new String(bytes,0,c));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int afterLast = sbAfter.indexOf("new DPlayer");
        String m3u8URL = "";
        if(afterLast!=-1){
            m3u8URL = sbAfter.substring(afterLast);
            int begin = m3u8URL.indexOf("url: ");
            m3u8URL = m3u8URL.substring(begin+6);
        }else {
            throw new RuntimeException("未找到m3u8地址");
        }
        int httpsI = m3u8URL.indexOf("m3u8");
        m3u8URL = m3u8URL.substring(0,httpsI);
        return m3u8URL + "m3u8";
    }
}
