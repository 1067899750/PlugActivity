package com.example.plugactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * @author puyantao
 * @description :
 * @date 2020/2/25
 */
public class UpdateManager {
    private static String packageName;// = "com.yipinzhe"; // 应用的包名
    private static String jsonUrl = "version.txt"; // JSON版本文件URL
    private static String xmlUrl = "version.xml"; // XML版本文件URL
    private static final String DOWNLOAD_DIR = "/"; // 应用下载后保存的子目录

    private Context mContext;
    HashMap<String, String> mHashMap;// 保存解析的XML信息
    int versionCode, isNew;

    public UpdateManager(Context context) {
        this.mContext = context;
        packageName = context.getPackageName();
        jsonUrl = MyApplication.site + jsonUrl;
        xmlUrl = MyApplication.site + xmlUrl;
        checkVersion();
    }

    Handler checkHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                // 发现新版本，提示用户更新
                StringBuffer message = new StringBuffer();
                message.append(mHashMap.get("note").replace("|", "\n"));
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("软件升级")
                        .setMessage(message.toString())
                        .setPositiveButton("更新",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 开启更新服务UpdateService
                                        System.out.println("你点击了更新");
                                        Intent updateIntent = new Intent(
                                                mContext, UpdateService.class);
                                        /**
                                         * updateIntent.putExtra("downloadDir",
                                         * DOWNLOAD_DIR);
                                         * updateIntent.putExtra("apkUrl",
                                         * mHashMap.get("url"));
                                         */
                                        mContext.startService(updateIntent);
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                alert.create().show();
            }
        };
    };

    /**
     *检查是否有新版本
     */
    public void checkVersion() {
        try {
            // 获取软件版本号,对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().getPackageInfo(
                    packageName, 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                String result = null;
                /**
                 * try { //如果服务器端是JSON文本文件 result =
                 * MyApplication.handleGet(jsonUrl); if (result != null) {
                 * mHashMap = parseJSON(result); } } catch (Exception e1) {
                 * e1.printStackTrace(); }
                 */

                InputStream inStream = null;
                try {
                    // 本机XML文件
                    inStream = UpdateManager.class.getClassLoader().getResourceAsStream("version.xml");
                    // 如果服务器端是XML文件
                    inStream = new URL(xmlUrl).openConnection().getInputStream();
                    if (inStream != null)
                        mHashMap = parseXml(inStream);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (mHashMap != null) {
                    int serviceCode = Integer.valueOf(mHashMap.get("version"));
                    if (serviceCode > versionCode) {// 版本判断,返回true则有新版本
                        isNew = 1;
                    }
                }
                checkHandler.sendEmptyMessage(isNew);
            };
        }.start();
    }

    /**
     *解析服务器端的JSON版本文件
     */
    public HashMap<String, String> parseJSON(String str) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(str);
            hashMap.put("version", obj.getString("version"));
            hashMap.put("name", obj.getString("name"));
            hashMap.put("url", obj.getString("url"));
            hashMap.put("note", obj.getString("note"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /**
     *解析服务器端的XML版本文件
     */
    public HashMap<String, String> parseXml(InputStream inputStream) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inputStream, "GBK");//设置数据源编码
            int eventCode = parser.getEventType();//获取事件类型
            while(eventCode != XmlPullParser.END_DOCUMENT) {
                System.out.println("循环开始");
                switch (eventCode){
                    case XmlPullParser.START_DOCUMENT: //开始读取XML文档
                        System.out.println("START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG://开始读取某个标签
                        if("version".equals(parser.getName())) {
                            hashMap.put(parser.getName(), parser.nextText());
                        } else if("name".equals(parser.getName())) {
                            hashMap.put(parser.getName(), parser.nextText());
                        } else if("url".equals(parser.getName())) {
                            hashMap.put(parser.getName(), parser.nextText());
                        } else if("note".equals(parser.getName())) {
                            hashMap.put(parser.getName(), parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventCode = parser.next();//继续读取下一个元素节点，并获取事件码

            }
            System.out.println(hashMap.get("version"));
        } catch(Exception e) {

        }
        return hashMap;


        /**
         *try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse(inStream);
         Element root = document.getDocumentElement();//获取根节点
         NodeList childNodes = root.getChildNodes();//获得所有子节点,然后遍历
         for (int j = 0; j < childNodes.getLength(); j++) {
         Node childNode = childNodes.item(j);
         if (childNode.getNodeType() == Node.ELEMENT_NODE) {
         Element childElement = (Element) childNode;
         if ("version".equals(childElement.getNodeName())) {
         hashMap.put("version", childElement.getFirstChild()
         .getNodeValue());
         }
         else if (("name".equals(childElement.getNodeName()))) {
         hashMap.put("name", childElement.getFirstChild()
         .getNodeValue());
         }
         else if (("url".equals(childElement.getNodeName()))) {
         hashMap.put("url", childElement.getFirstChild()
         .getNodeValue());
         } else if (("note".equals(childElement.getNodeName()))) {
         hashMap.put("note", childElement.getFirstChild()
         .getNodeValue());
         }
         }
         }
         } catch (Exception e) {
         e.printStackTrace();
         }*/

    }
}
