package android.trc.com.trdevapp.config;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigDataProvider {
    static File configRootDir;

    public static void init(Context context) {
        configRootDir = new File(context.getFilesDir(), "configs");
    }

    public static File[] getConfigFiles(String platform) {
        return getConfigDir(platform).listFiles();
    }

    public static File getConfigDir(String platform) {
        File dir = new File(configRootDir, platform);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static String getConfigContent(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            str = bufferedReader.readLine();
            while (str != null) {
                stringBuilder.append(str).append("\n");
                str = bufferedReader.readLine();
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();

    }

    public static boolean saveToConfigFile(File dir, String str, String logFileName) {
        File file = new File(dir, logFileName);
        try {
            if (!dir.exists()) dir.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            byte[] bytes = str.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
