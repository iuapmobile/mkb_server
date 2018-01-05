/**
 * 
 */
package com.yyuap.mkb.pl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.KBSynonym;
import com.yyuap.mkb.entity.QaCollection;
import com.yyuap.mkb.processor.SolrManager;

/**
 * @author gct
 * @created 2017-5-20
 */
public class DbUtil {

    /**
     * @param sql
     */
    public static void insert(String sql, KBIndex kbIndex, DBConfig dbconf) throws SQLException {
        // "insert into kbIndexInfo(title, decript, descriptImg, url,
        // author,keywords,tag,category,grade,domain,createTime,updateTime)
        // values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        try {

            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();

            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(sql);
            ps.setString(1, kbIndex.getId());
            ps.setString(2, kbIndex.getTitle());
            ps.setString(3, kbIndex.getDescript());
            ps.setString(4, kbIndex.getDescriptImg());
            ps.setString(5, kbIndex.getUrl());

            ps.setString(6, kbIndex.getText());

            ps.setString(7, kbIndex.getAuthor());
            ps.setString(8, kbIndex.getKeywords());
            ps.setString(9, kbIndex.getTag());
            ps.setString(10, kbIndex.getCategory());

            ps.setString(11, kbIndex.getGrade());
            ps.setString(12, kbIndex.getDomain());

            ps.setString(13, kbIndex.getWeight());
            ps.setString(14, kbIndex.getContent());
            ps.setString(15, kbIndex.getProduct());
            ps.setString(16, kbIndex.getSubproduct());
            if (null == kbIndex.getS_top()) {
                ps.setNull(17, Types.NULL);
            } else {
                ps.setInt(17, kbIndex.getS_top() == null ? null : Integer.valueOf(kbIndex.getS_top()).intValue());
            }
            if (null == kbIndex.getS_kbsrc()) {
                ps.setNull(18, Types.NULL);
            } else {
                ps.setInt(18, kbIndex.getS_kbsrc() == null ? null : Integer.valueOf(kbIndex.getS_kbsrc()).intValue());
            }
            if (null == kbIndex.getS_kbcategory()) {
                ps.setNull(19, Types.NULL);
            } else {
                ps.setInt(19, kbIndex.getS_kbcategory() == null ? null
                        : Integer.valueOf(kbIndex.getS_kbcategory()).intValue());
            }
            if (null == kbIndex.getS_hot()) {
                ps.setNull(20, Types.NULL);
            } else {
                ps.setInt(20, kbIndex.getS_hot() == null ? null : Integer.valueOf(kbIndex.getS_hot()).intValue());
            }
            // ps.setInt(17,
            // kbIndex.getS_top()==null?null:Integer.valueOf(kbIndex.getS_top()).intValue());
            // ps.setInt(18,
            // kbIndex.getS_kbsrc()==null?null:Integer.valueOf(kbIndex.getS_kbsrc()).intValue());
            // ps.setInt(19,
            // kbIndex.getS_kbcategory()==null?null:Integer.valueOf(kbIndex.getS_kbcategory()).intValue());
            // ps.setInt(20,
            // kbIndex.getS_hot()==null?null:Integer.valueOf(kbIndex.getS_hot()).intValue());
            ps.setString(21, kbIndex.getKbid());
            ps.setString(22, kbIndex.getExt_supportsys());
            ps.setString(23, kbIndex.getExt_resourcetype());
            ps.setString(24, kbIndex.getExt_scope());

            ps.setString(25, kbIndex.getCreateTime());
            ps.setString(26, kbIndex.getUpdateTime());
            ps.setString(27, kbIndex.getKtype());

            boolean flag = ps.execute();
            if (!flag) {
                System.out.println("import data : title = " + kbIndex.getTitle() + " , url = " + kbIndex.getUrl()
                        + ", descript = " + kbIndex.getDescript() + " succeed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static ArrayList<KBQA> selectOne(String sql, KBQA kbqa, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<KBQA> list = new ArrayList<KBQA>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            if (null == kbqa.getQtype()) {// and answer = ?
                ps = conn.prepareStatement("select * from qa where question = ?  and qtype is null");

                ps.setString(1, kbqa.getQuestion());
                // ps.setString(2, kbqa.getAnswer());

                rs = ps.executeQuery();
            } else {
                ps = conn.prepareStatement(sql);

                ps.setString(1, kbqa.getQuestion());
                // ps.setString(2, kbqa.getAnswer());
                // ps.setString(2, kbqa.getQtype());

                rs = ps.executeQuery();
            }

            while (rs.next()) {
                String q = rs.getString("question");
                String a = rs.getString("answer");
                String qtype = rs.getString("qtype");
                String id = rs.getString("id");
                if (!q.equals("") && !a.equals("")) {
                    KBQA qa = new KBQA();
                    qa.setQuestion(q);
                    qa.setAnswer(a);
                    qa.setId(id);
                    qa.setQtype(qtype);
                    list.add(qa);
                } else {
                    // nothing to do
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public static List selectOne(String sql, KBIndex kbIndex, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List list = new ArrayList();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            ps = conn.prepareStatement(sql);

            ps.setString(1, kbIndex.getTitle());
            ps.setString(2, kbIndex.getDescript());
            ps.setString(3, kbIndex.getUrl());

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("title").equals(kbIndex.getTitle()) && rs.getString("url").equals(kbIndex.getUrl())
                        && rs.getString("descript").equals(kbIndex.getDescript())) {
                    list.add(1);
                } else {
                    list.add(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public static void update(String sql, KBIndex kbIndex, DBConfig dbconf) throws SQLException {
        // "insert into kbIndexInfo(title, decript, descriptImg, url,
        // author,keywords,tag,category,grade,domain,createTime,updateTime)
        // values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // public static final String UPDATE_KBINDEXINFO_SQL = "UPDATE
        // kbIndexInfo SET `title`=?, `descript`=?, `descriptImg`=?, `text`=?,
        // `url`=?, `keywords`=?, `domain`=?, `tag`=? WHERE `id`=?";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);

            ps.setString(1, kbIndex.getTitle());
            ps.setString(2, kbIndex.getDescript());
            ps.setString(3, kbIndex.getDescriptImg());
            ps.setString(4, kbIndex.getText());
            ps.setString(5, kbIndex.getUrl());

            ps.setString(6, kbIndex.getKeywords());
            ps.setString(7, kbIndex.getDomain());
            ps.setString(8, kbIndex.getTag());
            ps.setString(9, kbIndex.getId());

            boolean flag = ps.execute();
            if (!flag) {
                System.out.println("update data : title = " + kbIndex.getTitle() + " , url = " + kbIndex.getUrl()
                        + ", descript = " + kbIndex.getDescript() + " succeed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static String insertQA(String insertSql, KBQA qa, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String ret = null;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(insertSql);
            String id = qa.getId();
            if (id.equals("")) {
                id = UUID.randomUUID().toString();
            }
            ps.setString(1, id);
            ps.setString(2, qa.getLibraryPk());
            ps.setString(3, qa.getQuestion());
            ps.setString(4, qa.getAnswer());
            ps.setString(5, qa.getQtype());

            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            ps.setString(6, datetime);
            ps.setString(7, datetime);
            ps.setString(8, qa.getCreateBy());
            ps.setString(9, qa.getUpdateBy());
            ps.setString(10, qa.getIstop());
            if ("1".equals(qa.getIstop())) {
                ps.setString(11, datetime);
            } else {
                ps.setString(11, null);
            }
            ps.setString(12, qa.getUrl());// url
            ps.setString(13, qa.getKbid());// kbid
            ps.setString(14, qa.getExt_scope());// 可见范围
            if (null == qa.getDomain()) {
                ps.setNull(15, Types.NULL);
            } else {
                ps.setString(15, qa.getDomain());
            }
            if (null == qa.getProduct()) {
                ps.setNull(16, Types.NULL);
            } else {
                ps.setString(16, qa.getProduct());
            }
            if (null == qa.getSubproduct()) {
                ps.setNull(17, Types.NULL);
            } else {
                ps.setString(17, qa.getSubproduct());
            }
            if (null == qa.getExtend0()) {
                ps.setNull(18, Types.NULL);
            } else {
                ps.setString(18, qa.getExtend0());
            }
            if (null == qa.getExtend1()) {
                ps.setNull(19, Types.NULL);
            } else {
                ps.setString(19, qa.getExtend1());
            }
            if (null == qa.getExtend2()) {
                ps.setNull(20, Types.NULL);
            } else {
                ps.setString(20, qa.getExtend2());
            }
            if (null == qa.getExtend3()) {
                ps.setNull(21, Types.NULL);
            } else {
                ps.setString(21, qa.getExtend3());
            }
            if (null == qa.getExtend4()) {
                ps.setNull(22, Types.NULL);
            } else {
                ps.setString(22, qa.getExtend4());
            }
            if (null == qa.getExtend5()) {
                ps.setNull(23, Types.NULL);
            } else {
                ps.setString(23, qa.getExtend5());
            }
            if (null == qa.getExtend6()) {
                ps.setNull(24, Types.NULL);
            } else {
                ps.setString(24, qa.getExtend6());
            }
            if (null == qa.getExtend7()) {
                ps.setNull(25, Types.NULL);
            } else {
                ps.setString(25, qa.getExtend7());
            }
            if (null == qa.getExtend8()) {
                ps.setNull(26, Types.NULL);
            } else {
                ps.setString(26, qa.getExtend8());
            }
            if (null == qa.getExtend9()) {
                ps.setNull(27, Types.NULL);
            } else {
                ps.setString(27, qa.getExtend9());
            }
            if (null == qa.getExtend10()) {
                ps.setNull(28, Types.NULL);
            } else {
                ps.setString(28, qa.getExtend10());
            }
            if (null == qa.getExtend11()) {
                ps.setNull(29, Types.NULL);
            } else {
                ps.setString(29, qa.getExtend11());
            }
            if (null == qa.getExtend12()) {
                ps.setNull(30, Types.NULL);
            } else {
                ps.setString(30, qa.getExtend12());
            }
            if (null == qa.getExtend13()) {
                ps.setNull(31, Types.NULL);
            } else {
                ps.setString(31, qa.getExtend13());
            }
            if (null == qa.getExtend14()) {
                ps.setNull(32, Types.NULL);
            } else {
                ps.setString(32, qa.getExtend14());
            }
            if (null == qa.getExtend15()) {
                ps.setNull(33, Types.NULL);
            } else {
                ps.setString(33, qa.getExtend15());
            }
            if (null == qa.getExtend16()) {
                ps.setNull(34, Types.NULL);
            } else {
                ps.setString(34, qa.getExtend16());
            }
            if (null == qa.getExtend17()) {
                ps.setNull(35, Types.NULL);
            } else {
                ps.setString(35, qa.getExtend17());
            }
            if (null == qa.getExtend18()) {
                ps.setNull(36, Types.NULL);
            } else {
                ps.setString(36, qa.getExtend18());
            }
            if (null == qa.getExtend19()) {
                ps.setNull(37, Types.NULL);
            } else {
                ps.setString(37, qa.getExtend19());
            }

            if (qa.getKtype() == null || qa.getKtype().equals("")) {
                // ps.setNull(38, Types.NULL);
                ps.setString(38, "qa");// 默认都是qa类型的问答
            } else {
                ps.setString(38, qa.getKtype());
            }

            boolean flag = ps.execute();
            if (!flag) {
                ret = id;
                qa.setCreateTime(datetime);
                qa.setUpdateTime(datetime);
                System.out.println("insert a row into QA table success!: question=" + qa.getQuestion() + ", answer="
                        + qa.getAnswer() + ", url=" + qa.getUrl());

            }
        } catch (Exception e) {
            // e.toString()
            String reason = e.toString();
            throw new KBInsertSQLException(reason);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;
    }

    public static ArrayList<JSONObject> selectAnswer(String sql, String q, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);

            ps.setString(1, q);

            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String ques = rs.getString("question");
                String ans = rs.getString("answer");
                String ktype = rs.getString("ktype");
                String qtype = rs.getString("qtype");
                String url = rs.getString("url");
                obj.put(q, ans);// 不能写成ques，sql比较不区分大小写
                obj.put("request_q", q);
                obj.put("kb_q", ques);
                obj.put("a", ans);
                obj.put("kbid", rs.getString("kbid"));
                obj.put("ktype", ktype);
                obj.put("qtype", qtype);
                obj.put("url", url);
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public static ArrayList<JSONObject> selectAnswerSimilarFromDB(String sql, String q, DBConfig dbconf, String[] tag)
            throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        ArrayList<String> qsidList = new ArrayList<String>();// 存放 qa的id
        ArrayList<String> qidList = new ArrayList<String>();// 存放 qa的id
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement("select distinct qid from qa_similar where trim(question) = ?");
            ps.setString(1, q);
            rs = ps.executeQuery();
            while (rs.next()) {
                qsidList.add(rs.getString("id"));
                qidList.add(rs.getString("qid"));
            }
            // 如果qid唯一 再去查询 说明 命中 唯一答案
            if (qidList.size() > 0) {
                ps = conn.prepareStatement(sql);// 从qs查出来qid，再从qa表内查数据

                ps.setString(1, qidList.get(0));// 只取第一个qid
                String qsid = qsidList.get(0);// 只取第一个qid

                rs = ps.executeQuery();
                while (rs.next()) {
                    JSONObject obj = new JSONObject();
                    // String ques = rs.getString("question");
                    String ans = rs.getString("answer");
                    obj.put("request_q", q);
                    obj.put("kb_q", rs.getString("question"));
                    obj.put("a", rs.getString("answer"));
                    obj.put(q, ans);// 把 key的 ques 换成 q 要不 前面取值 报错
                    
                    obj.put("qid", rs.getString("id"));
                    obj.put("qsid", qsid);
                    obj.put("id", qsid);
                    obj.put("kbid", rs.getString("kbid"));
                    obj.put("ktype", rs.getString("ktype"));
                    obj.put("qtype", rs.getString("qtype"));
                    list.add(obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public static List selectALLQA(String selectAllQaSql, DBConfig dbconf) {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<KBQA> list = new ArrayList<KBQA>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(selectAllQaSql);

            rs = ps.executeQuery();
            while (rs.next()) {
                KBQA qa = new KBQA();

                qa.setId(rs.getString("id"));
                qa.setQuestion(rs.getString("question"));
                qa.setAnswer(rs.getString("answer"));
                qa.setQtype(rs.getString("qtype"));
                qa.setCreateTime(rs.getString("createTime"));
                qa.setUpdateTime(rs.getString("updateTime"));
                list.add(qa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List selectQA(String selectAllQaSql, DBConfig dbconf, String content) {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, KBQA> map = new HashMap<String, KBQA>();
        ArrayList<KBQA> list = new ArrayList<KBQA>();
        ArrayList<String> qidList = new ArrayList<String>();// 存放 qid
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            // 先根据前台传来的搜索内容 去qa表 查询 q和a匹配
            String sql1 = selectAllQaSql + " where question like ? or answer like ?";
            ps = conn.prepareStatement(sql1);
            ps.setString(1, "%" + content + "%");
            ps.setString(2, "%" + content + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                KBQA qa = new KBQA();

                qa.setId(rs.getString("id"));
                qa.setQuestion(rs.getString("question"));
                qa.setAnswer(rs.getString("answer"));
                qa.setQtype(rs.getString("qtype"));
                qa.setCreateTime(rs.getString("createTime"));
                qa.setUpdateTime(rs.getString("updateTime"));
                // list.add(qa);
                map.put(rs.getString("id"), qa);
            }
            // 然后查询相似表 查询出 qid 再去 qa表查询
            ps = conn.prepareStatement("select distinct qid from qa_similar where question like ?");
            ps.setString(1, "%" + content + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                qidList.add(rs.getString("qid"));
            }
            String sql2 = selectAllQaSql + " where id in('" + concat("', '", qidList) + "')";
            ps = conn.prepareStatement(sql2);
            rs = ps.executeQuery();
            while (rs.next()) {
                KBQA qa = new KBQA();

                qa.setId(rs.getString("id"));
                qa.setQuestion(rs.getString("question"));
                qa.setAnswer(rs.getString("answer"));
                qa.setQtype(rs.getString("qtype"));
                qa.setCreateTime(rs.getString("createTime"));
                qa.setUpdateTime(rs.getString("updateTime"));
                // list.add(qa);
                map.put(rs.getString("id"), qa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        list = new ArrayList<KBQA>(map.values());
        return list;
    }

    public static ArrayList<String> insertQA_SIMILAR(String insertQaSimilarSql, KBQA qa, DBConfig dbconf)
            throws SQLException {
        // TODO Auto-generated method stub
        if (qa == null || qa.getQuestions() == null) {
            return null;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ArrayList<String> ids = new ArrayList<String>();
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);

            for (int i = 0, len = qa.getQuestions().length; i < len; i++) {

                String qs = qa.getQuestions()[i];
                if (qs == null || qs.equals("")) {
                    ids.add("");
                    continue;
                }

                ps = conn.prepareStatement(insertQaSimilarSql);

                String id = UUID.randomUUID().toString();

                ps.setString(1, id);
                ps.setString(2, qs);
                ps.setString(3, qa.getId());

                String dt = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                ps.setString(4, dt);
                ps.setString(5, dt);
                ps.setString(6, "");
                ps.setString(7, "");
                boolean flag = ps.execute();
                if (!flag) {
                    ids.add(id);
                    KBQS kbqs = new KBQS();
                    kbqs.setId(id);
                    kbqs.setQuestion(qs);
                    kbqs.setQid(qa.getId());
                    qa.getQS().add(kbqs);// 这里将成功insert数据库的数据转化为kbqs，供插入solr使用
                    System.out.println(
                            "import data[" + id + "] : question_similar = " + qa.getQuestions()[i] + " succeed!");
                } else {
                    System.out.println(
                            "import data[" + id + "] : question_similar = " + qa.getQuestions()[i] + " failed!");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
            return ids;
        }
    }

    public static JSONObject selectQAById(String selectQaByIdSql, String[] arrayParams, DBConfig dbconf) {
        // TODO Auto-generated method stub
        JSONObject json = new JSONObject();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(selectQaByIdSql);
            for (int i = 0, len = arrayParams.length; i < len; i++) {
                ps.setString(i + 1, arrayParams[i]);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                json.put("id", rs.getString("id"));
                json.put("question", rs.getString("question"));
                json.put("answer", rs.getString("answer"));
                json.put("qtype", rs.getString("qtype"));
                json.put("createTime", rs.getString("createTime"));
                json.put("updateTime", rs.getString("updateTime"));
                json.put("createBy", rs.getString("createBy"));
                json.put("updateBy", rs.getString("updateBy"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return json;
    }

    // private static ResultSet execSQL(String sql, String[] arrayParams,
    // DBConfig dbconf) {
    // // TODO Auto-generated method stub
    // Connection conn = null;
    // PreparedStatement ps = null;
    // ResultSet rs = null;
    //
    // try {
    // Class.forName(Common.DRIVER);
    // conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(),
    // dbconf.getPassword());
    // ps = conn.prepareStatement(sql);
    // for (int i = 0, len = arrayParams.length; i < len; i++) {
    // ps.setString(i + 1, arrayParams[i]);
    // }
    // rs = ps.executeQuery();
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (rs != null) {
    // // rs.close();
    // }
    // if (ps != null) {
    // ps.close();
    // }
    // if (conn != null) {
    // conn.close();
    // }
    // }
    //
    // return rs;
    // }

    public static JSONArray selectSimilarQByQid(String selectQaByIdSql, String[] params, DBConfig dbconf) {
        // TODO Auto-generated method stub
        JSONArray array = new JSONArray();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(selectQaByIdSql);
            for (int i = 0, len = params.length; i < len; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject qa = new JSONObject();
                qa.put("id", rs.getString("id"));
                qa.put("question", rs.getString("question"));
                qa.put("qid", rs.getString("qid"));
                qa.put("createTime", rs.getString("createTime"));
                qa.put("updateTime", rs.getString("updateTime"));
                qa.put("createBy", rs.getString("createBy"));
                qa.put("updateBy", rs.getString("updateBy"));

                array.add(qa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {

                    rs.close();

                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return array;
    }

    public static String insertQA_TJ(String insertQaTjSql, KBQA qa, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String id = "";
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(insertQaTjSql);
            String newid = UUID.randomUUID().toString();
            ps.setString(1, newid);
            ps.setString(2, qa.getQuestion());
            ps.setString(3, qa.getAnswer());
            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            ps.setString(4, datetime);
            ps.setString(5, datetime);
            ps.setString(6, qa.getCreateBy());
            ps.setString(7, qa.getUpdateBy());
            if (null == qa.getId() || "".equals(qa.getId())) {
                ps.setNull(8, Types.NULL);
            } else {
                ps.setString(8, qa.getId());
            }

            boolean flag = ps.execute();

            if (!flag) {
                id = newid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return id;

    }

    public static JSONArray selectQA_topn(String qaTop5SQL, int topn, DBConfig dbconf, String tag) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        List<String> list = new ArrayList<String>();
        try {
            Class.forName(Common.DRIVER);

            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            // 先查询置顶qa

            // conn = DriverManager.getConnection(Common.URL, Common.USERNAME,
            // Common.PASSWORD);
            String sql1 = "";
            if (null == tag) {
                sql1 = " select * from qa where istop=1 order by settoptime desc limit ? ";
            } else if ("personinside".equals(tag)) {
                sql1 = " select * from qa where istop=1 order by settoptime desc limit ? ";
            } else {
                sql1 = " select * from qa where istop=1 and ext_scope is null order by settoptime desc limit ? ";
            }

            ps = conn.prepareStatement(sql1);
            ps.setInt(1, topn);
            rs = ps.executeQuery();
            int rownum = 0;
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("question", rs.getString("question"));
                json.put("askedNum", "-1");// 这应该是-1 因为置顶 就是一个
                array.add(json);
                rownum++;
                list.add(rs.getString("question"));
            }

            // 这说明 置顶的不满足需要查询的topn数据
            if (topn - rownum > 0) {
                // 根据参数列表的大小生成in串
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    buffer.append("?, ");
                }
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(buffer.length() - 1);
                String sql = "select * from (select question, count(*) counts from qa_tj " + "where  "
                        + "question not in (" + buffer.toString() + ") "
                        + "group by question) t order by counts desc limit ?";
                ps = conn.prepareStatement(sql);
                // 根据参数列表设置sql参数
                for (int i = 0; i < list.size(); i++) {
                    ps.setString(i + 1, list.get(i));
                }
                ps.setInt(list.size() + 1, topn - rownum);
                rs = ps.executeQuery();
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("question", rs.getString("question"));
                    json.put("askedNum", rs.getString("counts"));
                    array.add(json);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return array;
    }

    public static String updateQAFeedback(String insertQaFeedbackSql, KBQAFeedback fb, DBConfig dbconf)
            throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String id = "";
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(insertQaFeedbackSql);

            ps.setString(1, fb.getScore());

            ps.setString(2, fb.getId());

            boolean flag = ps.execute();
            if (!flag) {
                System.out.println("update data succeed!");
                id = fb.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }

        }
        return id;
    }

    public static boolean delQA(String deleteQaSql, String id, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(deleteQaSql);

            ps.setString(1, id);

            boolean flag = ps.execute();
            if (!flag) {
                ret = true;
                System.out.println("delete qa record : id = " + id + " succeed!");
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;

    }

    public static boolean updateQA(String updateQaSql, KBQA qa, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(updateQaSql);
            String id = qa.getId();

            ps.setString(1, qa.getQuestion());
            ps.setString(2, qa.getAnswer());
            ps.setString(3, qa.getIstop());
            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            if (!"1".equals(qa.getIstop())) {
                ps.setString(4, null);
            } else {
                ps.setString(4, datetime);
            }
            ps.setString(5, qa.getUrl());

            ps.setString(6, qa.getQtype());

            ps.setString(7, qa.getExt_scope());

            if (null == qa.getDomain()) {
                ps.setNull(8, Types.NULL);
            } else {
                ps.setString(8, qa.getDomain());
            }
            if (null == qa.getProduct()) {
                ps.setNull(9, Types.NULL);
            } else {
                ps.setString(9, qa.getProduct());
            }
            if (null == qa.getSubproduct()) {
                ps.setNull(10, Types.NULL);
            } else {
                ps.setString(10, qa.getSubproduct());
            }
            if (null == qa.getExtend0()) {
                ps.setNull(11, Types.NULL);
            } else {
                ps.setString(11, qa.getExtend0());
            }
            if (null == qa.getExtend1()) {
                ps.setNull(12, Types.NULL);
            } else {
                ps.setString(12, qa.getExtend1());
            }
            if (null == qa.getExtend2()) {
                ps.setNull(13, Types.NULL);
            } else {
                ps.setString(13, qa.getExtend2());
            }
            if (null == qa.getExtend3()) {
                ps.setNull(14, Types.NULL);
            } else {
                ps.setString(14, qa.getExtend3());
            }
            if (null == qa.getExtend4()) {
                ps.setNull(15, Types.NULL);
            } else {
                ps.setString(15, qa.getExtend4());
            }
            if (null == qa.getExtend5()) {
                ps.setNull(16, Types.NULL);
            } else {
                ps.setString(16, qa.getExtend5());
            }
            if (null == qa.getExtend6()) {
                ps.setNull(17, Types.NULL);
            } else {
                ps.setString(17, qa.getExtend6());
            }
            if (null == qa.getExtend7()) {
                ps.setNull(18, Types.NULL);
            } else {
                ps.setString(18, qa.getExtend7());
            }
            if (null == qa.getExtend8()) {
                ps.setNull(19, Types.NULL);
            } else {
                ps.setString(19, qa.getExtend8());
            }
            if (null == qa.getExtend9()) {
                ps.setNull(20, Types.NULL);
            } else {
                ps.setString(20, qa.getExtend9());
            }
            if (null == qa.getExtend10()) {
                ps.setNull(21, Types.NULL);
            } else {
                ps.setString(21, qa.getExtend10());
            }
            if (null == qa.getExtend11()) {
                ps.setNull(22, Types.NULL);
            } else {
                ps.setString(22, qa.getExtend11());
            }
            if (null == qa.getExtend12()) {
                ps.setNull(23, Types.NULL);
            } else {
                ps.setString(23, qa.getExtend12());
            }
            if (null == qa.getExtend13()) {
                ps.setNull(24, Types.NULL);
            } else {
                ps.setString(24, qa.getExtend13());
            }
            if (null == qa.getExtend14()) {
                ps.setNull(25, Types.NULL);
            } else {
                ps.setString(25, qa.getExtend14());
            }
            if (null == qa.getExtend15()) {
                ps.setNull(26, Types.NULL);
            } else {
                ps.setString(26, qa.getExtend15());
            }
            if (null == qa.getExtend16()) {
                ps.setNull(27, Types.NULL);
            } else {
                ps.setString(27, qa.getExtend16());
            }
            if (null == qa.getExtend17()) {
                ps.setNull(28, Types.NULL);
            } else {
                ps.setString(28, qa.getExtend17());
            }
            if (null == qa.getExtend18()) {
                ps.setNull(29, Types.NULL);
            } else {
                ps.setString(29, qa.getExtend18());
            }
            if (null == qa.getExtend19()) {
                ps.setNull(30, Types.NULL);
            } else {
                ps.setString(30, qa.getExtend19());
            }
            
            if (null == qa.getKtype()) {
                ps.setNull(31, Types.NULL);
            } else {
                ps.setString(31, qa.getKtype());
            }
            if (null == qa.getKbid()) {
                ps.setNull(32, Types.NULL);
            } else {
                ps.setString(32, qa.getKbid());
            }
            
            ps.setString(33, qa.getId());

            boolean flag = ps.execute();
            if (!flag) {
                ret = true;
                qa.setUpdateTime(datetime);
                System.out.println("import data : question = " + qa.getQuestion() + " succeed!");
            }
        } catch (Exception e) {
            // e.toString()
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return ret;
    }

    public static boolean delQS(String deleteQaSimilarSql, KBQS qs, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(deleteQaSimilarSql);

            ps.setString(1, qs.getId());

            boolean flag = ps.execute();
            if (!flag) {
                ret = true;
                System.out.println("delete qa_similar record : id = " + qs.getId() + " succeed!");
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;
    }

    public static boolean updateQS(String updateQaSimilarSql, KBQS qs, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        String id = "";
        id = "s";
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(updateQaSimilarSql);

            ps.setString(1, qs.getQuestion());

            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            ps.setString(2, datetime);
            ps.setString(3, qs.getUpdateBy());
            ps.setString(4, qs.getId());

            boolean flag = ps.execute();
            if (!flag) {
                System.out.println("update data succeed!");
                id = qs.getId();
                qs.setUpdateTime(datetime);
                ret = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }

        }
        return ret;
    }

    public static String insertQS(String insertQaSimilarSql, KBQS qs, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String ret = null;
        try {

            // Class.forName(Common.DRIVER);q
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(insertQaSimilarSql);
            String id = qs.getId();
            if (id == null || id.equals("")) {
                id = UUID.randomUUID().toString();
            }
            ps.setString(1, id);
            ps.setString(2, qs.getQuestion());
            ps.setString(3, qs.getQid());

            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            ps.setString(4, datetime);
            ps.setString(5, datetime);
            ps.setString(6, qs.getCreateBy());
            ps.setString(7, qs.getUpdateBy());

            boolean flag = ps.execute();
            if (!flag) {
                ret = id;
                qs.setCreateTime(datetime);
                qs.setUpdateTime(datetime);
                System.out.println("insert qa_similar row : question = " + qs.getQuestion() + " succeed!");
            }
        } catch (Exception e) {
            // e.toString()
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;
    }

    /**
     * pengjf 2017年7月13日18:26:40 保存收藏
     * 
     * @param insertSql
     * @param qac
     * @param dbconf
     * @return
     * @throws SQLException
     */
    public static String insertQaCollectioin(String insertSql, QaCollection qac, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String ret = null;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(insertSql);
            String id = qac.getId();
            if (id.equals("")) {
                id = UUID.randomUUID().toString();
            }
            ps.setString(1, id);
            ps.setString(2, qac.getTenantid());
            ps.setString(3, qac.getUserid());
            ps.setString(4, qac.getKbindexid());
            ps.setString(5, qac.getTitle());
            ps.setString(6, qac.getDescript());
            ps.setString(7, qac.getUrl());
            ps.setString(8, qac.getQid());
            ps.setString(9, qac.getQsid());
            ps.setString(10, qac.getQuestion());
            ps.setString(11, qac.getAnswer());

            String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
            ps.setString(12, datetime);
            ps.setString(13, datetime);
            ps.setString(14, qac.getCreateBy());
            ps.setString(15, qac.getUpdateBy());

            boolean flag = ps.execute();
            if (!flag) {
                ret = id;
                System.out.println("import data : qacollection 用户：= " + qac.getUserid() + "----标题：" + qac.getTitle()
                        + " succeed!");
            }
        } catch (Exception e) {
            // e.toString()
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;
    }

    /**
     * pengjf 2017年7月13日18:13:43
     * 
     * @param sql
     * @param qac
     * @param dbconf
     * @return
     * @throws SQLException
     */
    public static JSONArray selectQaCollection(String sql, String[] params, DBConfig dbconf) {
        JSONArray array = new JSONArray();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);

            ps.setString(1, params[0]);

            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject qaco = new JSONObject();
                qaco.put("id", rs.getString("id"));
                qaco.put("tenantid", rs.getString("tenantid"));
                qaco.put("userid", rs.getString("userid"));
                qaco.put("kbindexid", rs.getString("kbindexid"));
                qaco.put("title", rs.getString("title"));
                qaco.put("descript", rs.getString("descript"));
                qaco.put("url", rs.getString("url"));
                qaco.put("qid", rs.getString("qid"));
                qaco.put("qsid", rs.getString("qsid"));
                qaco.put("question", rs.getString("question"));
                qaco.put("answer", rs.getString("answer"));

                qaco.put("createTime", rs.getString("createTime"));
                qaco.put("updateTime", rs.getString("updateTime"));
                qaco.put("createBy", rs.getString("createBy"));
                qaco.put("updateBy", rs.getString("updateBy"));

                array.add(qaco);
            }
            // while (rs.next()) {
            // String id = rs.getString("id");
            // String tenantid = rs.getString("tenantid");;
            // String userid = rs.getString("userid");;
            // String kbindexid = rs.getString("kbindexid");;
            // String title = rs.getString("title");;
            // String descript = rs.getString("descript");;
            // String url = rs.getString("url");;
            // String qid = rs.getString("qid");;
            // String qsid = rs.getString("qsid");;
            // String question = rs.getString("question");;
            // String answer = rs.getString("answer");;
            // if (answer != null && !answer.equals("") && userid != null &&
            // !userid.equals("")
            // && kbindexid != null && !kbindexid.equals("") && title != null &&
            // !title.equals("")
            // && descript != null && !descript.equals("") && url != null &&
            // !url.equals("")) {
            // QaCollection qaco = new QaCollection();
            // qaco.setTenantid(tenantid);
            // qaco.setUserid(userid);
            // qaco.setKbindexid(kbindexid);
            // qaco.setTitle(title);
            // qaco.setDescript(descript);
            // qaco.setUrl(url);
            // qaco.setQid(qid);
            // qaco.setQsid(qsid);
            // qaco.setQuestion(question);
            // qaco.setAnswer(answer);
            // qaco.setId(id);
            // list.add(qaco);
            // } else {
            // // nothing to do
            // }
            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return array;
    }

    /**
     * pengjf 2017年7月13日18:26:40 取消收藏
     * 
     * @param deleteSql
     * @param id
     * @param dbconf
     * @return
     * @throws SQLException
     */
    public static boolean deleteQaCollectioin(String deleteSql, String id, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String ret = null;
        boolean flag = true;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(deleteSql);
            ps.setString(1, id);

            ps.execute();
        } catch (Exception e) {
            // e.toString()
            flag = false;
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return flag;
    }

    public static JSONArray exportExcelQA(String sql, DBConfig dbconf) {
        // TODO Auto-generated method stub
        JSONArray array = new JSONArray();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject qa = new JSONObject();
                qa.put("id", rs.getString("id"));
                qa.put("question", rs.getString("question"));
                qa.put("answer", rs.getString("answer"));
                qa.put("q", rs.getString("q"));

                array.add(qa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {

                    rs.close();

                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return array;
    }

    public static boolean updateQAIsTop(String updateQaSql, String qaid, String istop, DBConfig dbconf)
            throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(updateQaSql);

            ps.setString(1, istop);
            if (!"1".equals(istop)) {
                ps.setString(2, null);
            } else {
                String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                ps.setString(2, datetime);
            }
            ps.setString(3, qaid);

            boolean flag = ps.execute();
            if (!flag) {
                ret = true;
                System.out.println("update istop data : id = " + qaid + " succeed!");
            }
        } catch (Exception e) {
            // e.toString()
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return ret;
    }

    /**
     * 将参数 转换成 in 后面 需要的数据
     * 
     * @param values
     * @return
     */
    public static String concat(String separator, Object[] objs) {
        if (separator == null || objs == null)
            throw new NullPointerException();
        StringBuffer sb = new StringBuffer();
        for (Object o : objs) {
            if (o == null)
                continue;
            sb.append(o.toString()).append(separator);
        }
        if (sb.length() > separator.length())
            sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }

    public static String concat(String separator, Collection<?> objs) {
        if (objs == null)
            throw new NullPointerException();
        return concat(separator, objs.toArray());
    }

    public static String concat2SqlIn(Object[] objs) {
        return "'" + concat("','", objs) + "'";
    }

    public static ArrayList<KBSynonym> selectSynonym(String selectSynonymSql, String[] strings, DBConfig dbconf) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<KBSynonym> list = new ArrayList<KBSynonym>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            ps = conn.prepareStatement(selectSynonymSql);
            // ps.setString(1, "%" + content + "%");
            // ps.setString(2, "%" + content + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                KBSynonym kbs = new KBSynonym();
                kbs.setId(rs.getString("id"));
                kbs.setKeyword(rs.getString("keyword"));
                kbs.setSynonym(rs.getString("synonym"));

                list.add(kbs);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return list;
    }

    public static Map<String, String> queryDataTj(String day, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            StringBuffer sbf = new StringBuffer();
            sbf.append(" select  ");
            if (!"0".equals(day) && !"-1".equals(day)) {
                sbf.append(" DATE_FORMAT(createTime,'%Y-%m-%d') sj, ");
            } else {
                sbf.append(" DATE_FORMAT(createTime,'%Y-%m-%d %H') sj, ");
            }
            sbf.append(" count(*) c from qa_tj where");
            if (!"0".equals(day) && !"-1".equals(day)) {
                sbf.append(" TIMESTAMPDIFF(day,createTime,now()) <=ABS(" + day + ") ");
                sbf.append(" group by DATE_FORMAT(createTime,'%Y-%m-%d')  ");
            } else {
                sbf.append(" DATE_FORMAT(createTime,'%Y-%m-%d') = DATE_FORMAT(CURDATE()+" + day + ",'%Y-%m-%d')  ");
                sbf.append(" group by DATE_FORMAT(createTime,'%Y-%m-%d %H') ");
            }

            ps = conn.prepareStatement(sbf.toString());

            // if(!"0".equals(day)&&!"-1".equals(day)){
            // ps.setString(1, "");
            // }else{
            // ps.setString(1, "DATE_FORMAT(CURDATE()+"+day+",'%Y-%m-%d')");
            // }

            rs = ps.executeQuery();
            while (rs.next()) {
                String sj = rs.getString("sj");
                String c = rs.getString("c");
                map.put(sj, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return map;
    }

    public static String queryBotServicesTj(DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String c = "";
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            StringBuffer sbf = new StringBuffer();
            sbf.append(" select count(*) c from qa_tj ");

            ps = conn.prepareStatement(sbf.toString());

            rs = ps.executeQuery();
            while (rs.next()) {
                c = rs.getString("c");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return c;
    }

    /**
     * 增加kbindexinfo 表数据时，判断 是否已经存在，用title判断
     * 
     * @param sql
     * @param kbIndex
     * @param dbconf
     * @return
     * @throws SQLException
     */
    public static List<KBIndex> selectOneIsExists(String sql, KBIndex kbIndex, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<KBIndex> list = new ArrayList<KBIndex>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            ps = conn.prepareStatement(sql);

            ps.setString(1, kbIndex.getTitle());

            rs = ps.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String id = rs.getString("id");
                KBIndex kb = new KBIndex();
                kb.setTitle(title);
                kb.setId(id);
                list.add(kb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public static boolean updateKbInfo(String sql, List<Object> params, KBIndex kbIndex, DBConfig dbconf)
            throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;
        boolean rflag = false;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);

            setParameter(params, ps);

            boolean flag = ps.execute();
            if (!flag) {
                System.out.println("更新文档成功，id====" + kbIndex.getId());
                rflag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rflag;
    }

    public static boolean delkbInfo(String deleteQaSql, String[] ids, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean ret = false;
        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(deleteQaSql);

            for (int i = 0; i < ids.length; i++) {
                ps.setString(1, ids[i]);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();

            ret = true;
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return ret;

    }

    /**
     * 根据表名，查询表中字段
     * 
     * @param sql
     * @param dbconf
     * @return
     */
    public static JSONArray queryFieldForTable(String sql, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        try {
            Class.forName(Common.DRIVER);

            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("table_name", rs.getString("table_name"));// 表名
                json.put("field_name", rs.getString("field_name"));// 字段名
                json.put("field_desc", rs.getString("field_desc") == null ? "" : rs.getString("field_desc"));// 字段描述
                array.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return array;
    }

    /**
     * 根据表名，保存表中字段
     * 
     * @param sql
     * @param dbconf
     * @return
     */
    public static boolean saveFieldForTable(JSONArray paramArr, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        boolean delqasuccess = false;// qa删除表字段定义标志
        boolean delkbsuccess = false;// kbindexinfo删除表字段定义标志
        boolean inqasuccess = false;// qa新增表字段定义标志
        boolean inkbsuccess = false;// kbindexinfo新增表字段定义标志
        try {
            Class.forName(Common.DRIVER);

            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            for (int i = 0; i < paramArr.size(); i++) {
                JSONObject obj = paramArr.getJSONObject(i);
                String qasql = "DELETE from tablefield_definition where table_name=? and field_name=?";
                ps = conn.prepareStatement(qasql);
                ps.setString(1, obj.getString("table_name") == null ? "" : obj.getString("table_name"));
                ps.setString(2, obj.getString("field_name") == null ? "" : obj.getString("field_name"));
                boolean delqaflag = ps.execute();
                String kbsql = "DELETE from tablefield_definition where table_name=? and field_name=?";
                ps = conn.prepareStatement(kbsql);
                ps.setString(1, obj.getString("table_name") == null ? "" : obj.getString("table_name"));
                ps.setString(2, obj.getString("field_name") == null ? "" : obj.getString("field_name"));
                boolean delkbflag = ps.execute();
                if (!delqaflag) {
                    delqasuccess = true;
                }
                if (!delkbflag) {
                    delkbsuccess = true;
                }
                if (null == obj.getString("field_desc") || "".equals(obj.getString("field_desc"))) {
                    inqasuccess = true;
                    inkbsuccess = true;
                } else {
                    String isqaql = "insert into tablefield_definition(id,table_name,field_name,field_desc,createTime,updateTime) VALUES(?,?,?,?,?,?)";
                    String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                    ps = conn.prepareStatement(isqaql);
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, "qa");
                    ps.setString(3, obj.getString("field_name") == null ? "" : obj.getString("field_name"));
                    ps.setString(4, obj.getString("field_desc") == null ? "" : obj.getString("field_desc"));
                    ps.setString(5, datetime);
                    ps.setString(6, datetime);
                    boolean inqaflag = ps.execute();
                    if (!inqaflag) {
                        inqasuccess = true;
                    }
                    String iskbql = "insert into tablefield_definition(id,table_name,field_name,field_desc,createTime,updateTime) VALUES(?,?,?,?,?,?)";
                    ps = conn.prepareStatement(iskbql);
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, "kbindexinfo");
                    ps.setString(3, obj.getString("field_name") == null ? "" : obj.getString("field_name"));
                    ps.setString(4, obj.getString("field_desc") == null ? "" : obj.getString("field_desc"));
                    ps.setString(5, datetime);
                    ps.setString(6, datetime);
                    boolean inkbflag = ps.execute();
                    if (!inkbflag) {
                        inkbsuccess = true;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return delqasuccess && delkbsuccess && inqasuccess && inkbsuccess;
    }

    public static void setParameter(List<Object> params, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) p);
            } else if (p instanceof String) {
                preparedStatement.setString(i + 1, (String) p);
            } else if (p == null) {
                preparedStatement.setNull(i + 1, Types.NULL);
            }
        }
    }

    public static Map<String, String> queryDimensionTj(String field, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            StringBuffer sbf = new StringBuffer();
            sbf.append(" select  ");
            sbf.append(" " + field + " name, ");
            sbf.append(" count(*) c from qa where 1=1");
            sbf.append(" group by " + field + "  ");

            ps = conn.prepareStatement(sbf.toString());

            rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String c = rs.getString("c");
                map.put(name, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return map;
    }

    public static Map<String, String> queryQaTopTj(int topn, String istop, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // JSONArray array = new JSONArray();
        // List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            Class.forName(Common.DRIVER);

            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            // 先查询置顶qa

            // conn = DriverManager.getConnection(Common.URL, Common.USERNAME,
            // Common.PASSWORD);
            String sql = "select * from (select question, count(*) counts from qa_tj "
                    + "group by question) t order by counts desc limit ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, topn);
            rs = ps.executeQuery();
            while (rs.next()) {
                // JSONObject json = new JSONObject();
                // json.put("question", rs.getString("question"));
                // json.put("askedNum", rs.getString("counts"));
                // array.add(json);
                map.put(rs.getString("question"), rs.getString("counts"));
            }
            // String sql1 = "";
            // if(null == istop){
            // sql1 = " select * from qa limit ? ";
            // }else{
            // sql1 = " select * from qa where istop=? limit ? ";
            // }
            //
            // ps = conn.prepareStatement(sql1);
            // if(null == istop){
            // ps.setInt(1, Integer.valueOf(topn));
            // }else{
            // ps.setString(1, istop);
            // ps.setInt(2, Integer.valueOf(topn));
            // }
            // rs = ps.executeQuery();
            // int rownum = 0;
            // while (rs.next()) {
            // JSONObject json = new JSONObject();
            // json.put("question", rs.getString("question"));
            // json.put("askedNum", "-1");// 这应该是-1 因为置顶 就是一个
            // array.add(json);
            // rownum++;
            // list.add(rs.getString("question"));
            // }

            // 这说明 置顶的不满足需要查询的topn数据
            // if (topn - rownum > 0) {
            // // 根据参数列表的大小生成in串
            // StringBuffer buffer = new StringBuffer();
            // for (int i = 0; i < list.size(); i++) {
            // buffer.append("?, ");
            // }
            // buffer.deleteCharAt(buffer.length() - 1);
            // buffer.deleteCharAt(buffer.length() - 1);
            // String sql = "select * from (select question, count(*) counts
            // from qa_tj " + "where "
            // + "question not in (" + buffer.toString() + ") "
            // + "group by question) t order by counts desc limit ?";
            // ps = conn.prepareStatement(sql);
            // // 根据参数列表设置sql参数
            // for (int i = 0; i < list.size(); i++) {
            // ps.setString(i + 1, list.get(i));
            // }
            // ps.setInt(list.size() + 1, topn - rownum);
            // rs = ps.executeQuery();
            // while (rs.next()) {
            // JSONObject json = new JSONObject();
            // json.put("question", rs.getString("question"));
            // json.put("askedNum", rs.getString("counts"));
            // array.add(json);
            // }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return map;
    }

    public static JSONArray queryDimensionData(String field, DBConfig dbconf) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            StringBuffer sbf = new StringBuffer();
            sbf.append(" select  DISTINCT");
            sbf.append(" " + field + " name");
            sbf.append(" from qa where 1=1");

            ps = conn.prepareStatement(sbf.toString());

            rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                array.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return array;
    }

    public static Map<String, String> queryQaTopTjForDimension(int topn, String istop, String field, String fieldValue,
            DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // JSONArray array = new JSONArray();
        // List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            Class.forName(Common.DRIVER);

            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());

            // 先查询置顶qa

            // conn = DriverManager.getConnection(Common.URL, Common.USERNAME,
            // Common.PASSWORD);

            String sql = "select tj.question,count(*) counts from qa_tj tj,qa qa " + "where tj.qid=qa.id and " + field
                    + "=? group by tj.question  order by counts desc limit ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, fieldValue);
            ps.setInt(2, topn);
            rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("question"), rs.getString("counts"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return map;
    }

    public static JSONArray selectQAByKtype(String sql, String[] params, DBConfig dbconf) {
        // public static final String SELECT_QA_BY_KTYPE_SQL = "SELECT id, ''
        // qid, question, answer, qtype, ktype FROM qa WHERE ktype = ? union all
        // SELECT id, qid, question, '' answer, '' qtype, '' ktype FROM
        // qa_similar where qid in (SELECT id FROM qa where ktype=?);";
        JSONArray array = new JSONArray();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);

            ps.setString(1, params[0]);
            ps.setString(2, params[0]);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject qa = new JSONObject();
                qa.put("id", rs.getString("id"));
                qa.put("qid", rs.getString("qid"));
                qa.put("question", rs.getString("question"));
                qa.put("answer", rs.getString("answer"));
                qa.put("qtype", rs.getString("qtype"));
                qa.put("ktype", rs.getString("ktype"));
                qa.put("createTime", rs.getString("createTime"));
                qa.put("updateTime", rs.getString("updateTime"));
                qa.put("createBy", rs.getString("createBy"));
                qa.put("updateBy", rs.getString("updateBy"));

                array.add(qa);
            }
            // while (rs.next()) {
            // String id = rs.getString("id");
            // String tenantid = rs.getString("tenantid");;
            // String userid = rs.getString("userid");;
            // String kbindexid = rs.getString("kbindexid");;
            // String title = rs.getString("title");;
            // String descript = rs.getString("descript");;
            // String url = rs.getString("url");;
            // String qid = rs.getString("qid");;
            // String qsid = rs.getString("qsid");;
            // String question = rs.getString("question");;
            // String answer = rs.getString("answer");;
            // if (answer != null && !answer.equals("") && userid != null &&
            // !userid.equals("")
            // && kbindexid != null && !kbindexid.equals("") && title != null &&
            // !title.equals("")
            // && descript != null && !descript.equals("") && url != null &&
            // !url.equals("")) {
            // QaCollection qaco = new QaCollection();
            // qaco.setTenantid(tenantid);
            // qaco.setUserid(userid);
            // qaco.setKbindexid(kbindexid);
            // qaco.setTitle(title);
            // qaco.setDescript(descript);
            // qaco.setUrl(url);
            // qaco.setQid(qid);
            // qaco.setQsid(qsid);
            // qaco.setQuestion(question);
            // qaco.setAnswer(answer);
            // qaco.setId(id);
            // list.add(qaco);
            // } else {
            // // nothing to do
            // }
            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return array;
    }
}
