package com.yyuap.mkb.processor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;

public class solrQueryRules {
    public void setQF(SolrQuery query, String q, String qf, Tenant tenant) {

        String default_qf = "keywords^20 question^10 answer^0.1 title^10 descript^0.1 text^0.01";

        // keywords^100 product^1 subproduct^1 title^2
        String product_qf = "product^1000 subProduct^100 keywords^10 title^1 descript^0.1 text^0.01";

        boolean isOnly = isOnlyProductOrSubQuery(q);
        if (qf == null || qf.equals("")) {
            String db_qf = tenant.getSolr_qf();
            if (isOnly) {
                qf = product_qf;
            } else if (db_qf != null && !db_qf.equals("")) {
                qf = db_qf;
            } else {
                qf = default_qf;
            }

        }
        query.set("qf", qf);

    }

    public String addFilterQuery(SolrQuery query, String q, Tenant tenant) {
        String new_q = "";

        boolean usefq = tenant.getSorl_useFilterQueries();
        if (usefq) {

            // 处理product
            ArrayList<String> productFQ = new ArrayList<String>();
            new_q = getProductSorlFilterQueriesAndNewQ(q, productFQ);
            if (productFQ.size() > 0) {
                addFQ(query, "product", productFQ);
            }

            // 处理subproduct
            ArrayList<String> subProductFQ = getSubProductSorlFilterQueries(q);
            if (subProductFQ.size() > 0) {
                addFQ(query, "subproduct", subProductFQ);
            }

        } else {
            new_q = q;
        }
        return new_q;
    }

    private void addFQ(SolrQuery query, String filterName, ArrayList<String> filterValues) {
        if (filterValues == null || filterValues.size() == 0)
            return;

        if (filterValues.size() > 0) {
            String fq = "";
            for (String filterValue : filterValues) {
                if (fq.length() > 0) {
                    fq += " OR " + filterName + ":" + filterValue;
                } else {
                    fq += filterName + ":" + filterValue;
                }

            }

            query.addFilterQuery(fq);// q中含有subproduct，则fq限定范围
        }
    }

    public void addSort(SolrQuery query, String q, Tenant tenant) {

        boolean isOnlyProduct = isOnlyProductOrSubQuery(q);
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

    private boolean isOnlyProductOrSubQuery(String q) {
        boolean ret = true;

        JSONObject product = this.getData_product();

        JSONObject mobilePlatform = this.getData_subProduct().getJSONObject("移动平台");

        JSONObject devPlatform = this.getData_subProduct().getJSONObject("开发平台");

        JSONObject integrationPlatform = this.getData_subProduct().getJSONObject("集成平台");

        JSONObject cloudServicePlatform = this.getData_subProduct().getJSONObject("公共云服务");

        JSONObject yunweiPlatform = this.getData_subProduct().getJSONObject("运维平台");

        JSONObject devCenterPlatform = this.getData_subProduct().getJSONObject("开发者中心");

        String[] qqq = q.trim().split(" ");
        for (int i = 0, len = qqq.length; i < len; i++) {
            String word = qqq[i].trim();
            if (product.containsKey(word)) {// 产品
                continue;
            } else {// 子产品
                if (mobilePlatform.containsKey(word)) {

                } else if (devPlatform.containsKey(word)) {

                } else if (integrationPlatform.containsKey(word)) {

                } else if (cloudServicePlatform.containsKey(word)) {

                } else if (yunweiPlatform.containsKey(word)) {

                } else if (devCenterPlatform.containsKey(word)) {

                } else {
                    ret = false;
                    break;
                }
            }

        }
        return ret;
    }

    private String getProductSorlFilterQueriesAndNewQ(String q, ArrayList<String> filterProduct) {

        JSONObject product = this.getData_product();
        String ret_q = "";
        String[] qqq = q.trim().split(" ");
        for (int i = 0, len = qqq.length; i < len; i++) {
            String word = qqq[i].trim();
            if (product.containsKey(word)) {
                String productName = product.getString(word);
                if (productName != null && !productName.equals("")) {
                    filterProduct.add(productName);
                }
            } else {
                ret_q = ret_q + " " + word;
            }
        }

        ret_q = ret_q.trim();

        // 再次分词
        String ret_new_q = ret_q;
        try {
            if (ret_q.length() > 0) {

                ArrayList<String> new_wordList = this.segmentLexeme(ret_q);

                ret_new_q = this.getProductAndNewQ(new_wordList, product, filterProduct);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ret_new_q;
    }

    private String getProductAndNewQ(ArrayList<String> wordStrings, JSONObject product, ArrayList<String> ret) {

        String ret_q = "";
        ret = ret == null ? new ArrayList<String>() : ret;

        for (int i = 0, len = wordStrings.size(); i < len; i++) {
            String word = wordStrings.get(i).trim();
            if (product.containsKey(word)) {
                String productName = product.getString(word);
                if (productName != null && !productName.equals("")) {
                    ret.add(productName);
                }
            } else {
                ret_q = ret_q + " " + word;
            }
        }

        return ret_q;
    }

    private ArrayList<String> getSubProductSorlFilterQueries(String q) {
        ArrayList<String> ret = new ArrayList<String>();

        JSONObject product = this.getData_subProduct();
        Iterator<String> iterator = product.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject subP = product.getJSONObject(key);
            if (subP.containsKey(q)) {
                ret.add(q);
            }
        }

        return ret;
    }

    public JSONObject getData_product() {
        JSONObject product = new JSONObject();
        product.put("移动平台", "移动平台");
        product.put("集成平台", "集成平台");
        product.put("开发平台", "开发平台");
        product.put("运维平台", "运维平台");
        product.put("公共云服务", "公共云服务");
        product.put("开发者中心", "开发者中心");
        return product;
    }

    public JSONObject getData_subProduct() {
        JSONObject ret = new JSONObject();

        JSONObject mobilePlatform = new JSONObject();
        mobilePlatform.put("移动开发平台", "移动开发平台");
        mobilePlatform.put("移动支撑平台", "移动支撑平台");
        mobilePlatform.put("移动管理平台", "移动管理平台");

        JSONObject devPlatform = new JSONObject();
        devPlatform.put("开发工具", "开发工具");
        devPlatform.put("前端技术平台", "前端技术平台");
        devPlatform.put("技术组件", "技术组件");
        devPlatform.put("应用支撑组件", "应用支撑组件");

        JSONObject integrationPlatform = new JSONObject();
        devPlatform.put("主数据管理", "主数据管理");
        devPlatform.put("企业服务总线", "企业服务总线");
        devPlatform.put("统一身份认证", "统一身份认证");

        JSONObject cloudServicePlatform = new JSONObject();
        devPlatform.put("云审批", "云审批");
        devPlatform.put("云打印", "云打印");
        devPlatform.put("云表单", "云表单");

        JSONObject yunweiPlatform = new JSONObject();
        devPlatform.put("运维平台", "运维平台");

        JSONObject devCenterPlatform = new JSONObject();
        devPlatform.put("开发者中心", "开发者中心");

        ret.put("移动平台", mobilePlatform);
        ret.put("集成平台", integrationPlatform);
        ret.put("开发平台", devPlatform);
        ret.put("运维平台", yunweiPlatform);
        ret.put("公共云服务", cloudServicePlatform);
        ret.put("开发者中心", devCenterPlatform);

        return ret;
    }

    private ArrayList<String> segmentLexeme(String text) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        StringReader sr = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        while ((lex = ik.next()) != null) {
            String word = lex.getLexemeText();

            ret.add(word);
        }
        // String[] a = new Array();
        // MKBLogger.info("分词结果：" + ret.toArray(a).toString());
        return ret;
    }
}
