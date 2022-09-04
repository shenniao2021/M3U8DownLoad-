import ASMR.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.net.www.protocol.http.Handler;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static javax.swing.JFrame.EXIT_ON_CLOSE;


/**
 * Author wanglin
 */
public class m3u8Download {
    {
        try {
            System.setOut(new PrintStream(new File("message.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Security.addProvider(new BouncyCastleProvider());
        asmr = new AiASMR();
    }
    String cipher2 = "";
    String cipher1 = "";
    byte[] cipherIv = null;
    String outPath = "G:\\233\\temp";
    String key = "";
    String filename = "";
    int length = 2048;
    public ASMR asmr;
    int fileLength;
    JLabel state;

    int countdown = 5;
    int index = 0;
    int count = 0;

    public void strat() {

        JFrame jf = new JFrame();
        jf.setLayout(null);

        jf.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - 250,(Toolkit.getDefaultToolkit().getScreenSize().height)/2 - 250,500,700);

        JEditorPane je = new JEditorPane();
        je.setBounds(0,100,400,100);
        je.addKeyListener(new KeyAdapter() {
            String keyText;
            @Override
            public void keyReleased(KeyEvent e) {
                String temp = KeyEvent.getKeyText(e.getKeyCode());
                if(keyText != null){
                    if("V".equals(keyText.toUpperCase()) && "CTRL".equals(temp.toUpperCase())){
                        je.setText(je.getText() + ";");
                    }
                }
                keyText = temp;
                super.keyReleased(e);
            }
        });
        jf.add(je);

        JFileChooser fdl = new JFileChooser();
        fdl.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JLabel jLabel = new JLabel(outPath);
        jLabel.setBounds(0,0,300,100);

        jf.add(jLabel);

        JButton btn = new JButton("选择保存目录");
        btn.setBounds(300,0,200,100);
        jf.add(btn);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fdl.showSaveDialog(new JLabel());
                jLabel.setText(String.valueOf(fdl.getSelectedFile()));
                outPath = jLabel.getText();
            }
        });

        state = new JLabel("状态显示");
        state.setBounds(0,300,1500,100);
        state.setFont(new Font("微软雅黑", Font.BOLD,13));
        jf.add(state);

        JEditorPane countdownJE = new JEditorPane();
        countdownJE.setBounds(0,250,100,20);
        countdownJE.setFont(new Font("微软雅黑", Font.BOLD,13));
        countdownJE.setText("默认重连次数 5");
        jf.add(countdownJE);

        JButton aiasmr = new JButton("爱上asmr");
        aiasmr.setBounds(100,250,100,20);
        aiasmr.setFont(new Font("微软雅黑", Font.BOLD,13));
        aiasmr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                asmr = new AiASMR();
                setMessage("爱上asmr");
            }
        });
        jf.add(aiasmr);

        JButton miasmr = new JButton("美姬asmr");
        miasmr.setBounds(200,250,100,20);
        miasmr.setFont(new Font("微软雅黑", Font.BOLD,13));
        miasmr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                asmr = new MiASMR();
                setMessage("美姬asmr");
            }
        });
        jf.add(miasmr);

        JButton miVideo = new JButton("美姬Video");
        miVideo.setBounds(300,250,100,20);
        miVideo.setFont(new Font("微软雅黑", Font.BOLD,13));
        miVideo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                asmr = new MiVideo();
                setMessage("美姬Video");
            }
        });
        jf.add(miVideo);
        JEditorPane je3 = new JEditorPane();


        JButton jbt = new JButton("提交");
        jbt.setBounds(400,100,100,100);
        jf.add(jbt);
        jbt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    setMessage("开始下载");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                    if(!je3.getText().startsWith("默认")){
                                        asmr.setCookie(je3.getText());
                                    }
                                    if (!countdownJE.getText().startsWith("默认")){
                                        countdown = Integer.parseInt(countdownJE.getText());
                                    }
                                    findM3u8(je.getText().trim());
                                } catch (Exception ex) {
                                    setMessage(ex.getMessage());
                                }
                            }
                    });
                   thread.start();
            }
        });

        je3.setText("默认cookie值如不行后 则自行查找cookie替换");
        je3.setBounds(0,400,400,100);
        jf.add(je3);


        SSLContext sslcontext = null;
        try {
            try {
                sslcontext = SSLContext.getInstance("SSL","SunJSSE");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslsession) {
                setMessage("WARNING: Hostname is not matched for cert.");
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
        jf.setVisible(true);
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public  void findM3u8(String url1) throws Exception {
        String[] urlList = url1.split(";");
        count = urlList.length;
        for(int i = 0;i < count;i++){
            index = i;
            URL url = new URL(urlList[i].trim().replace("\"",""));
            URLConnection rulConnection = null;
            HttpURLConnection httpUrlConnection = null;
            InputStream inStrm = null;
            int temp = countdown;
                try{
                    while (temp > 0){
                        rulConnection = url.openConnection();
                        httpUrlConnection = asmr.config(rulConnection);
                        if(httpUrlConnection.getResponseCode() == 200){
                            temp = -1;
                            setMessage("找寻M3U8链接建立成功");
                            inStrm = httpUrlConnection.getInputStream();
                        }else {
                            temp--;
                            setMessage("找寻M3U8链接建立失败 状态码为" + httpUrlConnection.getResponseCode());
                        }
                    }

                }catch (Exception exception){
                    exception.printStackTrace();
                }

                if(temp != -1){
                    continue;
                }
            byte[] bytes = new byte[length];
            StringBuilder sb = new StringBuilder();
            int c = 0;
            while ((c = inStrm.read(bytes))!=-1){
                sb.append(new String(bytes,0,c));
            }
            inStrm.close();
            setFilename(sb);
            try {
                String replace = asmr.findM3u8(new String(sb)).replace("\\", "");
                String[] split = replace.split(";");
                count += split.length - 1;
                index--;
                for(String m3u8Url : split){
                    index++;
                    parseM3u8(m3u8Url);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public  void setFilename(StringBuilder filenameF) throws UnsupportedEncodingException {
        int begin = filenameF.indexOf("<title>") + "<title>".length();
        int end = filenameF.indexOf("</title>");
        filename = filenameF.substring(begin,end).replaceAll("[\\\\/|><?]*","");
    }

    public  void parseM3u8(String m3u8) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        URL url = new URL(m3u8);
        URLConnection rulConnection;
        HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        int temp = countdown;
            try{
                while (temp > 0){
                    rulConnection = url.openConnection();
                    httpUrlConnection = asmr.config(rulConnection);
                    if(httpUrlConnection.getResponseCode() == 200){
                        temp = -1;
                        setMessage("m3u8链接建立成功");
                        inStrm = httpUrlConnection.getInputStream();
                    }else {
                        temp--;
                        setMessage("m3u8链接建立失败 状态码为" + httpUrlConnection.getResponseCode());
                    }
                }
            }catch (IOException io){
                setMessage(io.getMessage());
            }
        if(temp != -1){
            return;
        }
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[length];
        int c = 0;
        while ((c = inStrm.read(bytes))!=-1){
            sb.append(new String(bytes,0,c));
        }
        String m3u8Inn = new String(sb).replaceAll("[\\n\\r]*","");
        if(m3u8Inn.indexOf("m3u8") != -1){
            int i = m3u8Inn.indexOf("/");
            String substring = m3u8Inn.substring(i);
            int com = m3u8.indexOf("com");
            parseM3u8(m3u8.substring(0,com+3) + substring);
        }else {
            //判断有没有加密
            if(sb.indexOf("#EXT-X-KEY")!=-1){
                int begin = sb.indexOf("METHOD=") + "METHOD=".length();
                String method = sb.substring(begin);
                int last = method.indexOf(",");
                method = method.substring(0,last);
                if((last = method.indexOf('-')) != -1){
                    method = method.substring(0,last);
                }
                cipher1 = method + "/";

                //判断有没有向量
                if(sb.indexOf("IV=") != -1){
                    begin = sb.indexOf("IV=") + "IV=".length();
                    String IV = sb.substring(begin);
                    last = IV.indexOf("#");
                    cipherIv = toBytes(IV.substring(0,last));
                    cipher2 = "CBC/";
                }else {
                    cipher2 = "ECB/";
                }

                //寻找Key
                //判断是相对还是绝对路径

                if(sb.indexOf("URI")!=-1){
                    begin = sb.indexOf("URI=\"") + "URI=\"".length();
                    String s = sb.substring(begin);
                    last = s.indexOf("\"");

                    if(!s.substring(0,last).startsWith("https")){
                        int start = m3u8.indexOf("//") + "//".length();
                        String ht = m3u8.substring(start);
                        int end = ht.indexOf("/");
                        ht = m3u8.substring(0,end);

                        getKey( ht + s.substring(0,last));
                    }else {
                        getKey(s.substring(0,last));
                    }
                }
            }


            try {
                download(asmr.findTsName(m3u8,m3u8Inn));
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void setMessage(String message){
        state.setText(" ");
        state.setText(message);
    }


    public  void getKey(String keyPath) throws IOException {
        URL url = new URL(keyPath);
        URLConnection rulConnection = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream inStrm = null;
        int temp = countdown;
            try{
                while (temp > 0){
                    rulConnection = url.openConnection();
                    httpUrlConnection = asmr.config(rulConnection);
                    if(httpUrlConnection.getResponseCode() == 200){
                        temp = -1;
                        setMessage("key建立成功");
                        inStrm = httpUrlConnection.getInputStream();
                    }else {
                        temp--;
                        setMessage("key建立失败 状态码为" + httpUrlConnection.getResponseCode());
                    }
                }
            }catch (IOException io){
                setMessage(io.getMessage());
            }
        if(temp != -1){
            return;
        }
        StringBuilder sb1 = new StringBuilder();
        int c = 0;
        byte[] bytes = new byte[1024 * 1024];
        while ((c = inStrm.read(bytes)) != -1) {
            sb1.append(new String(bytes,0,c));
        }
        key = sb1.toString();
    }

    public  void download(List<String> urlList) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
         File newFile = new File(outPath+"\\abc");
        fileLength = urlList.size();
         if(!newFile.exists()){
             newFile.mkdir();
         };
        File file = null;
       for(int i = 0;i < fileLength;i++){
           URL url = new URL(urlList.get(i));
           URLConnection rulConnection = null;
           HttpURLConnection httpUrlConnection = null;
           InputStream inStrm = null;
               try{
                   rulConnection = url.openConnection();
                   httpUrlConnection = asmr.config(rulConnection);
                   inStrm = httpUrlConnection.getInputStream();
               }catch (IOException io){
                   setMessage(io.getMessage());
               }
           OutputStream out = new FileOutputStream(newFile.getPath()+"\\"+i+".ts");
           asmr.download(out,inStrm,length);
           //判断是否存在key 为空则没有key
           if(key.trim().length() != 0){

                   Cipher cipher = Cipher.getInstance(cipher1+cipher2+"NoPadding");
                   SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), cipher1.replace("/",""));
                   //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
                   if(cipherIv != null){
                       AlgorithmParameterSpec paramSpec = new IvParameterSpec(cipherIv);
                       cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
                   }else {
                       cipher.init(Cipher.DECRYPT_MODE, keySpec);
                   }

                   InputStream inStrm2 = new FileInputStream(newFile.getPath()+"\\"+i+".ts");
                   byte[] bytes1 = new byte[inStrm2.available()];

                    file = new File(outPath+"\\"+ filename +".ts");
                    OutputStream out2 = new FileOutputStream(file, true);
                   if(inStrm2.read(bytes1) != -1){
                       out2.write(cipher.doFinal(bytes1));
                   }
               out2.flush();
               out2.close();
               inStrm2.close();

           }else {
               InputStream inStrm2 = new FileInputStream(newFile.getPath()+"\\"+i+".ts");
               byte[] bytes1 = new byte[inStrm2.available()];
               file = new File(outPath+"\\"+ filename +".ts");
               OutputStream out2 = new FileOutputStream(file, true);
               if (inStrm2.read(bytes1) != -1) {
                   out2.write(bytes1);
               }
               out2.flush();
               out2.close();
               inStrm2.close();
           }
           setMessage("共"+count+"个文件 当前第"+(index+1)+"个文件已下载片段数:"+i+"    当前文件片段总数总数:" + fileLength);

           inStrm.close();
           out.close();

           if(i == fileLength-1){

               File[] list = newFile.listFiles();
               for(File fileT : list){
                   fileT.delete();
               }
              if(count == index+1){
                  setMessage("完成");
              }
           }

       }
       cutTs(file.getAbsolutePath(),file.getParent() +"\\"+ filename +".mp4");
       file.delete();

    }

    public  byte[] toBytes(String b){
       b = b.replace("0x","");
       byte[] olabytes = b.getBytes();
       byte[] bytes = new byte[16];
       int b1 = 0;
       for(int i = 0;i < bytes.length;i++){
           bytes[i] = (byte) (olabytes[b1++] + olabytes[b1++]);
       }
       return bytes;
    }

    private void cutTs(String source, String target){
        setMessage("开始转码");
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(source);
        command.add("-acodec");
        command.add("copy");
        command.add("-vcodec");
        command.add("copy");
        command.add("-absf");
        command.add("aac_adtstoasc");
        command.add(target);
        try {
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(videoProcess.getInputStream()))) {
                String inputLine = "";
                while ((inputLine = bufferedReader.readLine()) != null) {
                    System.out.println(inputLine);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 读取进程异常输出
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(videoProcess.getErrorStream()))
            ){
                String inputLine = "";
                while ((inputLine = bufferedReader.readLine()) != null) {
                    System.out.println(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            videoProcess.waitFor();
            videoProcess.destroy();
        } catch (IOException e) {
            setMessage(e.getMessage());
        } catch (InterruptedException e) {
            setMessage(e.getMessage());
        }
        setMessage("转码完成");

    }
}
