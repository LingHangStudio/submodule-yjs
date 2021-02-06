package com.linghang.wusthelper.spider;


import com.linghang.wusthelper.exception.YJSException;
import com.linghang.wusthelper.spider.entity.GraduateScore;
import net.sourceforge.tess4j.Tesseract;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.select.Elements;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.ClassUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


/**
 * 本项目最核心的一个类
 * 研究生爬虫, 爬虫只负责爬虫的相关信息, 用户存在数据库中的cookie, 放在service中完成
 * 异常码 参考YJSException
 * 联系 : 508222866@qq.com
 * Todo : 根据已有cookie解析
 * // 获取学号,姓名,年级,类别,导师 & 通知公告, 我的申请, 课程异动, 学业预警 http://202.114.251.102/pyxx/loging.aspx
 * // 获取学期,院系,专业 http://202.114.251.102/pyxx/topmenu.aspx
 * // 获取培养方案链接 http://202.114.251.102/pyxx/leftmenu.aspx
 * // 获取某一天的课表 http://202.114.251.102/pyxx/App_Ajax/GetkcHandler.ashx?kcdate=2020-10-26&xh=201913703009
 * // 获取某学期的课表(只能是本学期) http://202.114.251.102/pyxx/pygl/kbcx_xs.aspx
 * // 获取评教 http://202.114.251.102/pyxx/dcpg/jxpjlist.aspx
 * // 获取成绩 http://202.114.251.102/pyxx/grgl/xskccjcx.aspx  (弹出窗口, 先注册?)
 * Todo : 一些页面的请求头可有可无, 保险起见还是加上比较好... 但我嫌麻烦就先不加了
 */
public class GraduateDept {

    // 登录页面
    private static final String LOGINURL = "http://202.114.251.102/pyxx/login.aspx";

    // 图片yzm
    private static final String YZMURL = "http://202.114.251.102/pyxx/PageTemplate/NsoftPage/yzm/createyzm.aspx";

    // 获取学号,姓名,年级,类别,导师 & 通知公告, 我的申请, 课程异动, 学业预警 页面
    private static final String LOGINGURL = "http://202.114.251.102/pyxx/loging.aspx";

    // 获取学期,院系,专业
    private static final String TOPMENUURL = "http://202.114.251.102/pyxx/topmenu.aspx";

    // 获取培养方案链接
    private static final String LEFTMENUURL = "http://202.114.251.102/pyxx/leftmenu.aspx";

    // 获取某一天的课表 kcdate=2020-10-26&xh=学号
    private static final String GETKCHANDLERURL = "http://202.114.251.102/pyxx/App_Ajax/GetkcHandler.ashx?";

    // 获取某学期的课表(只能是本学期)
    private static final String KBCX_XSURL = "http://202.114.251.102/pyxx/pygl/kbcx_xs.aspx";

    // 获取评教
    private static final String JXPJLISTURL = "http://202.114.251.102/pyxx/dcpg/jxpjlist.aspx";

    // 获取成绩
    private static final String XSKCCJCXURL = "http://202.114.251.102/pyxx/grgl/xskccjcx.aspx";

    // 一个默认链接, 用来检测cookie是否还有效
    private static final String CHECKCOOKIEURL = "http://202.114.251.102/pyxx/Default.aspx";

    // 连接池管理器
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    // tess4j
    private static Tesseract tesseract = new Tesseract();

    // 配置请求信息
    // 2000 500 10000
    private static RequestConfig config = RequestConfig.custom().setConnectTimeout(5000)   // 创建连接的最长时间
            .setConnectionRequestTimeout(3000)   // 设置获取连接的最长时间， 单位ms
            .setSocketTimeout(5000)        // 设置数据传输的最长时间
            .build();

    private static String errorHtml;

    // 获取请求失败的html页面
    static {
        try {
            tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");// 设置只解析数字
            tesseract.setDatapath("src/main/resources/tessdata");// 设置训练库路径, windows
            // 打成jar包后,需要再Linux服务器上, 把tessdata放进去
//            tesseract.setDatapath("/root/opencv/tessdata");// 绝对路径!, LINUX
            tesseract.setLanguage("eng");// 语言
            CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(LOGINURL));
            errorHtml = EntityUtils.toString(response.getEntity());
            response.close();
            cm.setMaxTotal(200);// 设置最大连接数
        } catch (Exception e) {
            try {
                throw new YJSException(-1, " 研究生官网连接异常! ");
            } catch (YJSException yjsException) {
                yjsException.printStackTrace();
            }
        }
    }


    /**
     * 下载图片到本地
     *
     * @param fileName "src/main/resources/pic/" + num + ".png"
     * @return 返回请求的cookie值, 这个由研究生官网服务器提供
     * @throws Exception
     */
    private static String downloadPic(String fileName) throws YJSException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGetYZM = new HttpGet(YZMURL);
        try {
            CloseableHttpResponse responseYZM = httpClient.execute(httpGetYZM);
            InputStream inputStream = responseYZM.getEntity().getContent();
            BufferedImage bufferedImage = ImageIO.read(inputStream);// 读取输入流
            // 将图片保存到本地
            // 上传linux后运行, 文件夹可能不存在, 会报错, 这里就新建一个文件夹
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 注意图片的格式
            ImageIO.write(bufferedImage, "png", file);
            inputStream.close();
            httpClient.close();
            responseYZM.close();
            return responseYZM.getFirstHeader("Set-Cookie").getValue();
        } catch (Exception e) {
            throw new YJSException(-1, " 验证码下载失败! ");
        }

    }

    /**
     * 解析验证码, 只针对研究生的验证码, 里面的limit和裁剪大小都是定死的
     * 利用 tess4j + opencv
     * 部署的linux服务器需要单独安装opencv和tess4j
     * yum install tesseract
     *
     * @param fileName
     * @return 返回解析的验证码的值
     */
    private static String parseYZM(String fileName) throws YJSException {
        try {
            // 图片裁剪
            Mat src = Imgcodecs.imread(fileName);
            Rect rect = new Rect(5, 11, 60, 24);
            Mat mat = new Mat(src, rect);
            Imgcodecs.imwrite(fileName, mat);
            // 设置临界值
            double limit = 300;
            // 去除干扰线 同时二值化
            Mat image = Imgcodecs.imread(fileName);
            // Set<Double> set = new HashSet<>(); // 可以用来确定临界值
            for (int row = 0, rows = image.rows(); row < rows; ++row) {
                for (int col = 0, cols = image.cols(); col < cols; ++col) {
                    double[] doubles = image.get(row, col);
                    double sum = doubles[0] + doubles[1] + doubles[2];
                    if (sum < limit) {
                        doubles[0] = 0;
                        doubles[1] = 0;
                        doubles[2] = 0;
                    } else {
                        doubles[0] = 255;
                        doubles[1] = 255;
                        doubles[2] = 255;
                    }
                    image.put(row, col, doubles);
                }
            }
            Imgcodecs.imwrite(fileName, image);
            // 识别验证码
            // replaceAll(" ", "");
            String yzm = tesseract.doOCR(new File(fileName)).trim();
            // 解析完成后删除下载的验证码
            File file = new File(fileName);
            file.delete();
            //if (yzm.length()==4)
            return yzm;
        } catch (Exception e) {
//            System.out.println("验证码解析异常");
            throw new YJSException(1, " 验证码解析异常! ");
        }

    }


    /**
     * 检测cookie是否还有效
     *
     * @param cookie
     * @return
     */
    public static boolean checkCookie(String cookie) throws YJSException {
        try {
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
            HttpGet httpGet = new HttpGet(CHECKCOOKIEURL);
            httpGet.addHeader("Cookie", cookie);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String pageHtml = EntityUtils.toString(response.getEntity());
            response.close();
            return pageHtml.equals(errorHtml) ? false : true;
        } catch (Exception e) {
            throw new YJSException(-1, " 研究生官网连接异常");
        }
    }


    /**
     * 模拟登录
     *
     * @param username
     * @param password
     * 检验密码正确与否, 我设计的是cnt, 一般来说1~2次就会成功, 当cnt >= 5时, 就可以认为密码不匹配了
     * @return 设计成map, 可以后续添加更多信息, 虽然目前的cookie只有一个
     * {
     * "Cookie" :   "ASP.NET_SessionId=jdavkjdinojds2xkpem45cnj; path=/; HttpOnly"
     * }
     * @throws Exception
     */
    public static Map<String, String> login(String username, String password) throws YJSException {

        try {
            Map<String, String> resuleMap = new HashMap<>();
            /**
             * Todo : 先查看redis中的cookie是否存在并且有效
             * return 返回登录成功的cookie, 并且更新redis
             * 异常 : 打印日志, 抛出异常
             */

            // cookie无效
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(cm).build();
            // 先下载验证码, 并获取cookie 其中yzm识别正确率85%左右
            String cookie = "";
            int statusCode = 0;
            int cnt = 0;// 记录模拟登录次数, 因为下列代码只能包装验证码识别正确, cnt >= 5 则表明账号密码不匹配
            do {
                cnt++;
                if (cnt >= 5) {
                    throw new YJSException(0, " 用户账号密码不匹配! ");
                }
                File file = new File("../pic");
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdir();
                }
                // fileName随意写了一下吧
                String fileName = "../pic/" + UUID.randomUUID().toString() + ".png";
                // 下载yzm
                cookie = downloadPic(fileName);
                HttpPost httpPost = new HttpPost(LOGINURL);
                // 解析yzm
                String yzm = parseYZM(fileName);
                List<NameValuePair> pairs = new ArrayList<>();// 添加表单信息
                pairs.add(new BasicNameValuePair("_ctl0:ImageButton1.x", "58"));
                pairs.add(new BasicNameValuePair("_ctl0:ImageButton1.y", "19"));
                pairs.add(new BasicNameValuePair("_ctl0:txtyzm", yzm));
                pairs.add(new BasicNameValuePair("_ctl0:txtusername", username));
                pairs.add(new BasicNameValuePair("_ctl0:txtpassword", password));
                pairs.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEdAAYIan/EDxtnGAw+ibSs0dI7UDagjadrq+xukJizXKfuf485DjYUnSc4B1y8D5WGXeCaN+cQ7B52HzGj0ueO5HRlbdfASR9MjKgO1uRUmJC5kWf476Bpzok4CsBoBh+4Dc44PwZx1rDVK21wD5mLmU790/rgRluGO2jm23BSg08Qow=="));
                pairs.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "496CE0B8"));
                pairs.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUENTM4MWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgIFEl9jdGwwOkltYWdlQnV0dG9uMQUSX2N0bDA6SW1hZ2VCdXR0b24yx6seGrOVC0hqWZJ3BfQgJcLBUpGdebiQ8yg3jeOCj/s="));
                UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(pairs, "utf-8");
                httpPost.setEntity(encodedFormEntity);
                httpPost.setConfig(config);
                // 添加请求头, cookie
                httpPost.addHeader("Cookie", cookie);
                httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36");
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
                httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                httpPost.addHeader("Accept-Languag", "zh-CN,zh;q=0.9");
                CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
                statusCode = response.getStatusLine().getStatusCode();
                response.close();
            } while (statusCode != 302);// 直到重定向成功
            // 表单提交成功后, cookie就在研究生官方后台生成了
            // Todo : 可以加一个计数器, 统计yzm识别错误的次数
            resuleMap.put("Cookie", cookie);
            return resuleMap;
        } catch (YJSException e) {
            throw e;
        } catch (Exception e) {
            throw new YJSException(-1, " 网络请求异常, 用户登录失败 ");
        }
    }


    /**
     * 调用前提, 保证cookie有效
     * TODO : 设置请求头(有时间在做)
     *
     * @param cookie
     * @param URL
     * @return 返回html页面内容
     */
    private static String getHtml(String cookie, String URL) throws YJSException {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        HttpGet httpGet = new HttpGet(URL);
        httpGet.addHeader("Cookie", cookie);
        String html = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            html = EntityUtils.toString(response.getEntity());
            response.close();
            return html;
        } catch (IOException e) {
            throw new YJSException(-1, " 获取页面失败! ");
        }

    }


    // 登录之后, 在数据库存储的信息全部放在service中
    // 所有的解析方法, 参数都需要cookie, 一个cookie, 对应一个学生
    // 操作之前, 先检测cookie是否有效

    // 获取今天的课表
    // TODO : 等开学后看课表的展现形式, 先不实现
    private static void getTodayCourses(String cookie) throws Exception {

        // 如果cookie还有效
        if (checkCookie(cookie)) {

        } else {

        }
    }


    /**
     * 获取评教信息
     * 解析采用xpath
     * TODO : 后期如果有需求, 可以添加评教功能
     *
     * @param cookie
     * @return
     * @throws IOException List<TeachingEvaluation>
     */
    public static List<Map<String, String>> getpjList(String cookie) throws YJSException {

        try {
            List<Map<String, String>> resultList = new ArrayList<>();

            // 如果cookie还有效
            if (checkCookie(cookie)) {
                String html = getHtml(cookie, JXPJLISTURL);// 获取页面
                JXDocument doc = JXDocument.create(html);
                String xpath = "//tr[@onmouseover]";
                List<JXNode> jxNodes = doc.selN(xpath);
                // lambda不太会, 先用for循环
                for (int i = 0; i < jxNodes.size(); i++) {
                    Map<String, String> resultMap = new HashMap<>();
                    List<JXNode> sel = jxNodes.get(i).sel("td[@nowrap]");
                    resultMap.put("term", sel.get(0).asElement().text());// 设置评价学期
                    resultMap.put("name", sel.get(1).asElement().text());// 设置评价名称
                    resultMap.put("courseId", sel.get(2).asElement().text());// 设置课程编号
                    resultMap.put("courseName", sel.get(3).asElement().text());// 设置课程名称
                    resultMap.put("teacherName", sel.get(4).asElement().text());// 设置教师名称
                    resultMap.put("ifJoin", sel.get(5).asElement().text());// 设置是否已参评
                    resultList.add(resultMap);
                }
                return resultList;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new YJSException(1, " 获取评教信息失败! ");
        }
    }


    // 获取培养方案
    // TODO : 官方都还没完善内容, 很多培养方案都是空的...
    private static void getTrainingProgram(String cookie) throws Exception {


        // 如果cookie还有效
        if (checkCookie(cookie)) {

        } else {

        }

    }


    /**
     * 获取学生信息(仅限爬虫可以获取的信息)
     *
     * @param cookie
     * @return {
     * "mentorName":"xx",
     * "major":"计算机科学与技术",
     * "stuNum":"201913xxxxxx",
     * "stuCategory":"全日制学术学位硕士",
     * "term":"2020-2021-2学期",
     * "stuName":"xxx",
     * "department":"计算机科学与技术学院   ",
     * "stuGrade":"2019"
     * }
     * @throws IOException
     */
    public static Map<String, String> getStuInfo(String cookie) throws YJSException {

        try {
            Map<String, String> resultMap = new HashMap<>();
            // 如果cookie还有效
            if (checkCookie(cookie)) {
                // 获取学期, 院系, 专业
                String topMenuHtml = getHtml(cookie, TOPMENUURL);
                JXDocument doc = JXDocument.create(topMenuHtml);
                String term = doc.selNOne("//font").asElement().text();// 2020-2021-2学期
                resultMap.put("term", term);// 学期 2020-2021-2学期
                String text = doc.selNOne("//div[@class='navx']").asElement().text();// 院系：计算机科学与技术学院    专业：计算机科学与技术
                // 返回的&nbsp;自动转义为空格
                String department = text.substring(text.indexOf("：") + 1, text.indexOf(" ")).trim();
                String major = text.substring(text.lastIndexOf("：") + 1).trim();
                resultMap.put("department", department);// 院系 计算机科学与技术学院
                resultMap.put("major", major);          // 专业 计算机科学与技术

                // 获取学号, 姓名, 年级, 类别, 导师
                String logingHtml = getHtml(cookie, LOGINGURL);
                doc = JXDocument.create(logingHtml);
                String stuInfoText = doc.selNOne("//div[@class='cardbox']//span").asElement().text();
                String[] strings = stuInfoText.split(" ");

                // 将学号, 姓名, 年级, 类别, 导师姓名添加到map中
                String[] tags = {"stuNum", "stuName", "stuGrade", "stuCategory", "mentorName"};
                for (int i = 0; i < strings.length; ++i) {
                    resultMap.put(tags[i], strings[i].substring(strings[i].indexOf("：") + 1));
                }
                return resultMap;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new YJSException(1, " 获取学生信息失败! ");
        }

    }


    /**
     * 获取成绩, 每门成绩包括, 课程名, 课程学分, 选修学期, 成绩
     *
     * @param cookie
     * @return Achievement(courseName = 人工智能与机器学习, credit = 2.0, term = 1, achievement = 87)
     * Achievement(courseName=现代软件工程学, credit=2.0, term=1, achievement=86)
     * Achievement(courseName=数据挖掘与知识发现, credit=2.0, term=2, achievement=90)
     * @throws IOException
     */
    public static List<GraduateScore> getScores(String cookie) throws YJSException {

        try {
            List<GraduateScore> achievementList = new ArrayList<>();
            // 如果cookie还有效
            if (checkCookie(cookie)) {
                String scoreHtml = getHtml(cookie, XSKCCJCXURL);
                JXDocument doc = JXDocument.create(scoreHtml);
                // <tr class="GridViewRowStyle">
                List<JXNode> nodes = doc.selN("//tr[@class='GridViewRowStyle']");
                // 可以获取下列信息
                // String[] tags = {"courseName", "credit", "term", "score"};
                for (JXNode jxNode : nodes) {
                    // <td>课程名</td><td>课程学分</td><td>选修学期</td><td>成绩</td>
                    // 自然辩证法概论 1.0 2 93
                    String[] strings = jxNode.asElement().children().text().split(" ");
                    // 自增长id和学号先设置为空(因为爬虫无法获取)
                    achievementList.add(new GraduateScore(-1, "", strings[0], Double.parseDouble(strings[1]), strings[2], strings[3]));
                }
                return achievementList;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new YJSException(1, " 获取成绩失败! ");
        }
    }


    // 获取课表, 后期替换成对象
    // 解析课表有点麻烦, 难点!
    // 重点看看我怎么判断每节课在二维数组中的位置的

    // TODO
    /**
     * 获取学生课表
     *
     * @param cookie
     * @throws IOException
     */
    public static void getCourses(String cookie) throws YJSException {

        try {
            // TODO : 后期根据String信息, 替换为对象
            // 使用1~11, 第1节课, 第2节课, ...
            String[][] strings = new String[12][8];
            int[][] length = new int[12][8];    // 记录每节课的长度, 初始值为0, 后续封装的时候, 不是0说明有合并
            int[] cnt = new int[12];            // 每一行的计数器

            if (checkCookie(cookie)) {
                String html = getHtml(cookie, KBCX_XSURL);
                JXDocument doc = JXDocument.create(html);

                // 注意, xpath选择后中的下标从1开始!!!
                // 获取所有的tr, 有用的下标从2~12
                List<JXNode> tr = doc.selN("//table[@id='DataGrid1']//tr");
                //          System.out.println(i1+"----------hello");
                for (int i = 1; i <= 11; i++) {
                    // 每个tr获取所有的td
                    Elements td = tr.get(i).asElement().children();
                    if (td.isEmpty())
                        continue;
                    // 根据i的值, td开始下标进行修改, 注意不要越界
                    // 1,5,9下标从2开始, 其它从1开始
                    int startNum = (i == 1 || i == 5 || i == 9) ? 2 : 1;
                    for (int j = startNum, n = td.size(); j < n; ++j) {
                        String text = td.get(j).text();
                        if (td.get(j).hasClass("rowspan")) {
                            // 获取rowspan
                            String rowspanXpath = "//table[@id='DataGrid1']//tr[" + (i + 1) + "]/td[" + (j + 1) + "]/@rowspan";
                            int rowspan = Integer.parseInt(doc.selNOne(rowspanXpath).toString());
                            length[i][j - startNum + 1 + cnt[i]] = rowspan;// 记录课的长度
                            // 下一行到i+rowspan-1行, 每个col坐标都+1
                            for (int k = i + 1; k < i + rowspan; ++k)
                                cnt[k]++;
                        }
                        // 注意下标!
                        strings[i][j - startNum + 1 + cnt[i]] = text;
                    }
                }

//            tr.forEach(jxNode -> System.out.println(jxNode.asElement().text()));
            } else {
                System.out.println("失败!");
            }
        } catch (Exception e) {
            throw new YJSException(1, " 获取课表失败! ");
        }
    }

    // 获取通知公告
    // 获取title, content
    // TODO : 等研究生教务处什么时候更新了再补充
    private static void getAnnouncements(String cookie) {

    }

}
