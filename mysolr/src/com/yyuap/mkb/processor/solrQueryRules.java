package com.yyuap.mkb.processor;

import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;

public class solrQueryRules {
    public void setFQ(SolrQuery query, String q, String qf, Tenant tenant) {

        String default_qf = "keywords^20 question^10 answer^0.1 title^10 descript^0.1 text^0.01";

        boolean isOnly = isOnlyProductQuery(q);
        if (qf == null || qf.equals("")) {
            // keywords^100 product^1 subproduct^1 title^2
            String t_qf = tenant.getSolr_qf();
            if (isOnly && t_qf != null && !t_qf.equals("")) {
                qf = t_qf;
            } else {
                qf = default_qf;
            }

        }
        query.set("qf", qf);

    }

    public ArrayList<String> addFilterQuery(SolrQuery query, String q, Tenant tenant) {
        ArrayList<String> ret = new ArrayList<String>();
        boolean usefq = tenant.getSorl_useFilterQueries();
        if (usefq) {

            // 处理product
            ArrayList<String> productFQ = getProductSorlFilterQueries(q);

            if (productFQ != null && productFQ.size() > 0) {
                // query.setFilterQueries("product:" + productFQ);
                for (String pro : productFQ) {
                    String fq = "product:" + pro;
                    query.addFilterQuery(fq);// q中含有product，则fq限定范围
                    ret.add(fq);
                }
            }

            // 处理subproduct
            ArrayList<String> subProductFQ = getSubProductSorlFilterQueries(q);
            if (subProductFQ != null && subProductFQ.size() > 0) {
                for (String subP : subProductFQ) {
                    String fq = "subproduct:" + subP;
                    query.addFilterQuery(fq);// q中含有subproduct，则fq限定范围
                    ret.add(fq);
                }
            }

        }
        return ret;
    }

    public void addSort(SolrQuery query, String q, Tenant tenant) {

        boolean isOnlyProduct = isOnlyProductQuery(q);
        if (!isOnlyProduct) {
            return;
        }

        String t_sort = tenant.getSolr_sort();
        if (t_sort == null || t_sort.equals("")) {
            return;
        }

        String[] sorts = t_sort.split(",");
        for (int i = 0, len = sorts.length; i < len; i++) {
            String[] sortItems = sorts[i].split(" ");
            if (sortItems.length != 2 || sortItems[0] == null || sortItems[0].equals("") || sortItems[1] == null
                    || sortItems[1].equals("")) {
                continue;
            } else {
                if (sortItems[1].equalsIgnoreCase("desc")) {
                    query.addSort(sortItems[0], ORDER.desc);
                } else if (sortItems[1].equalsIgnoreCase("asc")) {
                    query.addSort(sortItems[0], ORDER.asc);
                } else {
                    // nothing to do
                }
            }
        }
    }

    private boolean isOnlyProductQuery(String q) {
        boolean ret = true;

        JSONObject product = new JSONObject();
        product.put("移动平台", "移动平台");
        product.put("集成平台", "集成平台");
        product.put("开发平台", "开发平台");// 集成平台 云服务 文档
        product.put("运维平台", "运维平台");
        product.put("公共云服务", "公共云服务");

        JSONObject mobilePlatform = new JSONObject();
        mobilePlatform.put("移动开发平台", "移动开发平台");
        mobilePlatform.put("移动支撑平台", "移动支撑平台");
        mobilePlatform.put("移动管理平台", "移动管理平台");

        JSONObject devPlatform = new JSONObject();
        devPlatform.put("开发工具", "开发工具");
        devPlatform.put("iuap design", "iuap design");
        devPlatform.put("技术组件", "技术组件");
        devPlatform.put("应用支撑组件", "应用支撑组件");
        devPlatform.put("iuap design", "开发工具");

        String[] qqq = q.trim().split(" ");
        for (int i = 0, len = qqq.length; i < len; i++) {
            String word = qqq[i].trim();
            if (product.containsKey(word)) {
                continue;
            } else {
                if (mobilePlatform.containsKey(word)) {

                } else if (devPlatform.containsKey(word)) {

                } else {
                    ret = false;
                    break;
                }
            }

        }
        return ret;
    }

    private ArrayList<String> getProductSorlFilterQueries(String q) {
        ArrayList<String> ret = new ArrayList<String>();
        JSONObject product = new JSONObject();
        product.put("移动平台", "移动平台");
        product.put("集成平台", "集成平台");
        product.put("开发平台", "开发平台");// 集成平台 云服务 文档
        product.put("运维平台", "运维平台");
        product.put("公共云服务", "公共云服务");

        JSONObject mobilePlatform = new JSONObject();
        mobilePlatform.put("移动开发平台", "移动开发平台");
        mobilePlatform.put("移动支撑平台", "移动支撑平台");
        mobilePlatform.put("移动管理平台", "移动管理平台");

        JSONObject subProduct = new JSONObject();
        subProduct.put("开发工具", "开发工具");
        subProduct.put("iuap design", "iuap design");
        subProduct.put("技术组件", "技术组件");
        subProduct.put("应用支撑组件", "应用支撑组件");
        subProduct.put("iuap design", "开发工具");

        String[] qqq = q.trim().split(" ");
        for (int i = 0, len = qqq.length; i < len; i++) {
            String word = qqq[i].trim();
            if (product.containsKey(word)) {
                String productName = product.getString(word);
                if (productName != null && !productName.equals("")) {
                    ret.add(productName);
                }
            }
        }

        return ret;
    }

    private ArrayList<String> getSubProductSorlFilterQueries(String q) {
        ArrayList<String> ret = new ArrayList<String>();

        JSONObject product = new JSONObject();

        JSONObject mobilePlatform = new JSONObject();
        mobilePlatform.put("移动开发平台", "移动开发平台");
        mobilePlatform.put("移动支撑平台", "移动支撑平台");
        mobilePlatform.put("移动管理平台", "移动管理平台");
        // product.put("移动平台", mobile);

        JSONObject devPlatform = new JSONObject();
        devPlatform.put("开发工具", "开发工具");
        devPlatform.put("iuap design", "iuap design");
        devPlatform.put("技术组件", "技术组件");
        devPlatform.put("应用支撑组件", "应用支撑组件");

        if (mobilePlatform.containsKey(q)) {
            ret.add(mobilePlatform.getString(q));
        }
        if (devPlatform.containsKey(q)) {
            ret.add(devPlatform.getString(q));
        }

        return ret;
    }

}
