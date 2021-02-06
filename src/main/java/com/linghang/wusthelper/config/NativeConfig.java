package com.linghang.wusthelper.config;

import org.opencv.core.Core;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

// 解压获取jar包的dll/os      也可在直接采用方法2, 绝对路径的形式设置路径
// 至于tessdata太多, 就直接以绝对路径的方式设置文件地址来写了

/**
 * 加载opencv配置, tessdata
 * 方法①是为了将获取打包成jar包后, 获取dll/so 的方法, 不推荐, 很烦
 * 方法②, 直接配置IDEA vm options里的参数, 具体方法参考附加文档
 */
@Configuration
public class NativeConfig {

    // 方法1
//    static {
//        try {
//            // 注意将资源文件补充好!
////            String opencvName = "/86/opencv_java451.dll";// windows
//            String opencvName = "libopencv_java451.so";//LINUX
//            // getClass().getClassLoader().getResourceAsStream(arg0);
//            // System.out.println("MainTest.class.getClass()"+MainTest.class.getClass().toString());
//            InputStream in = NativeConfig.class
//                    .getResourceAsStream("/opencv/linux/" + opencvName);
////                    .getResourceAsStream("/opencv/x64/" + opencvName);
//
//            File ffile = new File("");
//            String filePath = null;
//            filePath = ffile.getAbsolutePath() + File.separator
//                    + opencvName;
//            File dll = new File(filePath);
//            FileOutputStream out = new FileOutputStream(dll);
//
//            int i;
//            byte[] buf = new byte[1024];
//            try {
//                while ((i = in.read(buf)) != -1) {
//                    out.write(buf, 0, i);
//                }
//            } finally {
//                in.close();
//                out.close();
//            }
//            System.load(dll.getAbsolutePath());//
//            dll.deleteOnExit();
//
//        } catch (Exception e) {
//            System.err.println("加载dll失败!");
//        }
//    }

    // 方法②在IDEA vm options里面添加参数, 注意是dll/os的父路径
    static {
        // /usr/local/lib/
        // LINUX : -Djava.library.path=/usr/local/lib, 这里面需要有libopencv_java.so
        // WINDOWS : -Djava.library.path=F:\WustHelper-Server\submodule-yjs\src\main\resources\opencv\x86
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


}
