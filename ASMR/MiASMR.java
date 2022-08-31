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
public class MiASMR implements ASMR {

    String houzhui = ".png";

    @Override
    public String getHouzui() {
        return houzhui;
    }

    @Override
    public String getCookie() {
        return null;
    }

    @Override
    public void setCookie(String cookie) {

    }

    @Override
    public HttpURLConnection config(URLConnection urlConnection){
        HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
        httpURLConnection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Safari/537.36");
        httpURLConnection.setRequestProperty("sec-ch-ua-platform","Windows");
        httpURLConnection.setRequestProperty("referer","https://www.gqtod.com/");
        httpURLConnection.addRequestProperty("Cache-Control", "no-cache");
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
        int skip = 0;
        int length = 181;
        while ((c = inStrm.read(bytes)) != -1) {
            if(c > length || skip >= length){
                if(skip < length){
                    int jump = length - skip;
                    out.write(bytes, length - skip, c - jump);
                    skip += length - skip;
                }else {
                    out.write(bytes, 0, c);
                }
            }else {
                skip = skip + c;
            }

        }
    }

    @Override
    public int fileCounts(String sb){
        //寻找文件数量
        int last = sb.indexOf("#EXT-X-ENDLIST");
        boolean isTrue = true;
        String temp = sb.substring(0,last);
        int tempI = temp.lastIndexOf(".png");
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
        int last = sb2.indexOf("var player_aaaa");
        sb2 = sb2.substring(last);
        int begin = sb2.indexOf("</script> ");
        sb2 = sb2.substring(0,begin+1);
        int https = sb2.lastIndexOf("https");
        sb2 = sb2.substring(https);
        int i = sb2.indexOf("\"");
        return sb2.substring(0,i);
    }
}