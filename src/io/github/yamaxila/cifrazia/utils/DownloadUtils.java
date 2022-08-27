package io.github.yamaxila.cifrazia.utils;

import io.github.yamaxila.cifrazia.ui.windows.MainWindow;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class DownloadUtils {

    private String url, outputName, outputPath;

    public DownloadUtils(String url, String outputName, String outputPath) {
        this.url = url.replace("\"", "%22").replace(" ", "%20");
        this.outputName = outputName;
        this.outputPath = outputPath;
    }

    public String downloadFile() {
        System.out.println(url);
        File outFile = new File(this.outputPath + File.separator + this.outputName);

        if(!new File(this.outputPath).exists())
            new File(this.outputPath).mkdirs();

        if(outFile.exists()) {
            outFile.delete();
        }
        try {
            outFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try  {
            URLConnection con = new URL(this.url).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
            con.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
            con.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + Long.toString(new Random().nextLong(), 36));
            InputStream fileIn = con.getInputStream();
            BufferedInputStream in = new BufferedInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(outFile.getAbsolutePath());

            if(con.getContentLength() == -1)
                MainWindow.getInstance().setInfinityProgress();

            long size = 0;
            byte[] dataBuffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                size += (bytesRead/1024);
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                if(con.getContentLength() != -1) {
                    MainWindow.getInstance().setProgress(size / (con.getContentLength() / 1024D));
                    MainWindow.getInstance().setInfo("Current file: " + this.outputName + " | " + (int)size/1000 + (con.getContentLength() != -1 ? " / " + (int)((con.getContentLength()/1024D)/1000) : "") + "MB.");
                } else {
                    MainWindow.getInstance().setInfo("Current file: " + this.outputName + " | " + size/1000 + "MB.");
                }
                System.out.print("\rCurrentSize: " + size);
            }
            System.out.println();
            return outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getOutputName() {
        return outputName;
    }

    public String getOutputPath() {
        return outputPath;
    }
}
