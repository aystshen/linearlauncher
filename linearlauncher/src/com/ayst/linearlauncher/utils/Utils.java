package com.ayst.linearlauncher.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by Administrator on 2016/3/9.
 */
public class Utils {

    private static int mVersionCode = 0;

    public static boolean isConnNetWork(Context context) {
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        return ((network != null) && conManager.getActiveNetworkInfo().isConnected());
    }

    public static String parseXml(String xml, String nodeName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            NodeList childNodes = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement().getChildNodes();
            for(int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    if (nodeName.equals(childElement.getNodeName())) {
                        return childElement.getFirstChild().getNodeValue();
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        if (mVersionCode == 0) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                mVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mVersionCode;
    }
}
