package io.github.yamaxila.cifrazia.utils;

import io.github.yamaxila.core.utils.OsUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class LauncherUtils {

    public static void loadLibraries(List<URL> jars) throws Exception {
        URLClassLoader loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        jars.parallelStream().filter(p -> !Arrays.asList(loader.getURLs()).contains(p)).forEach((file) -> {
            try {
                method.invoke(loader, file);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void restartLauncher(boolean debug) throws Exception {

        File libsPath = new File(SimpleConfigUtils.getValue("libs_path", ""));

        if(!libsPath.exists())
            throw new RuntimeException("Libs path not exists!");

        List<String> params = new LinkedList<>();

        params.add(System.getProperty("java.home") + ("/bin/java"));

        params.add("-Xmx" + SimpleConfigUtils.getValue("ram", "2048") + "M");

        if(debug) {
            params.add("-Dlog4j.configurationFile=./log4j.xml");
            params.add("-Dio.netty.leakDetection.level=advanced");
        }

        if(OsUtils.getOS() == OsUtils.OS.LINUX)
            params.add("-Djdk.gtk.version=2");

        params.add("-classpath");
        String separator = OsUtils.getOS() == OsUtils.OS.WINDOWS ? ";" : ":";
        params.add("./Core.jar" + separator + LauncherUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        params.add("io.github.yamaxila.cifrazia.Main");
        params.add(libsPath.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(params);
        pb.inheritIO();
        pb.directory(new File("."));

        Process p = pb.start();

        new Thread(() -> {
            Thread.currentThread().setName("ProcessCheckerThread");
            System.setOut(new PrintStream(p.getOutputStream()));
            Scanner serr = new Scanner(p.getErrorStream());

            while (p.isAlive()) {
                while (serr.hasNextLine()) {
                    System.err.println(serr.nextLine());
                }
            }
            crashLauncher();
        }).start();
    }


    public static void crashLauncher() {
        try {
            Class<?> af = Class.forName("java.lang.Shutdown");
            Method m = af.getDeclaredMethod("halt0", int.class);
            m.setAccessible(true);
            m.invoke(null, 1);
        } catch (Exception x) {
        }
    }

}
