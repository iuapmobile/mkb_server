/**
 * 
 */
package com.yyuap.mkb.pl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.QaCollection;

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
            // ps.setString(11, kbIndex.getCreatTime());
            // ps.setString(12, kbIndex.getUpdateTime());
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
            ps = conn.prepareStatement(sql);

            ps.setString(1, kbqa.getQuestion());
            ps.setString(2, kbqa.getAnswer());

            rs = ps.executeQuery();
            while (rs.next()) {
                String q = rs.getString("question");
                String a = rs.getString("answer");
                String id = rs.getString("id");
                if (!q.equals("") && !a.equals("")) {
                    KBQA qa = new KBQA();
                    qa.setQuestion(q);
                    qa.setAnswer(a);
                    qa.setId(id);
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
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
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

    public static ResultSet selectAll(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
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
        return rs;
    }

    public static void update(String sql, KBIndex kbIndex) throws SQLException {
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
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
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

            boolean flag = ps.execute();
            if (!flag) {
                ret = id;
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
                obj.put(ques, ans);
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

    public static boolean insertQA_SIMILAR(String insertQaSimilarSql, KBQA qa, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        if (qa == null || qa.getQuestions() == null) {
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;

        try {

            // Class.forName(Common.DRIVER);
            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();
            conn = DriverManager.getConnection(_url, _username, _psw);

            for (int i = 0, len = qa.getQuestions().length; i < len; i++) {

                ps = conn.prepareStatement(insertQaSimilarSql);

                String id = UUID.randomUUID().toString();

                ps.setString(1, id);
                ps.setString(2, qa.getQuestions()[i]);
                ps.setString(3, qa.getId());

                String dt = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                ps.setString(4, dt);
                ps.setString(5, dt);
                ps.setString(6, "");
                ps.setString(7, "");
                boolean flag = ps.execute();
                if (!flag) {
                    System.out.println("import data : question_similar = " + qa.getQuestions()[i] + " succeed!");
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
            return true;
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
        String id = null;
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

            boolean flag = ps.execute();

            if (!flag) {
                id = newid;
                System.out.println("import data[qa_tj] : question = " + qa.getQuestion() + " succeed!");
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

    public static JSONArray selectQA_topn(String qaTop5SQL, int topn, DBConfig dbconf) throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
            ps = conn.prepareStatement(qaTop5SQL);
            ps.setInt(1, topn);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("question", rs.getString("question"));
                json.put("askedNum", rs.getString("counts"));
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

    public static String updateQAFeedback(String insertQaFeedbackSql, KBQAFeedback fb, DBConfig dbconf)
            throws SQLException {
        // TODO Auto-generated method stub
        Connection conn = null;
        PreparedStatement ps = null;
        String id = "";
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
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
            ps.setString(3, qa.getId());

            boolean flag = ps.execute();
            if (!flag) {
                ret = true;
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
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
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
     * pengjf 2017年7月13日18:26:40
     * 保存收藏
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
                System.out.println("import data : qacollection 用户：= " + qac.getUserid() +"----标题："+qac.getTitle() + " succeed!");
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
     * @param sql
     * @param qac
     * @param dbconf
     * @return
     * @throws SQLException
     */
    public static JSONArray selectQaCollection(String sql, String[] params, DBConfig dbconf){
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
//            while (rs.next()) {
//                String id = rs.getString("id");
//                String tenantid = rs.getString("tenantid");; 
//                String userid = rs.getString("userid");; 
//                String kbindexid = rs.getString("kbindexid");; 
//                String title = rs.getString("title");; 
//        		String descript = rs.getString("descript");;
//        		String url = rs.getString("url");;
//        		String qid = rs.getString("qid");;
//        		String qsid = rs.getString("qsid");;
//        		String question = rs.getString("question");;
//        		String answer = rs.getString("answer");;
//        		 if (answer != null && !answer.equals("") && userid != null && !userid.equals("")
//        	        		&& kbindexid != null && !kbindexid.equals("") && title != null && !title.equals("")
//        	        		&& descript != null && !descript.equals("") && url != null && !url.equals("")) {
//        			QaCollection qaco = new QaCollection();
//        			qaco.setTenantid(tenantid);
//        			qaco.setUserid(userid);
//        			qaco.setKbindexid(kbindexid);
//        			qaco.setTitle(title);
//        			qaco.setDescript(descript);
//        			qaco.setUrl(url);
//        			qaco.setQid(qid);
//        			qaco.setQsid(qsid);
//        			qaco.setQuestion(question);
//        			qaco.setAnswer(answer);
//        			qaco.setId(id);
//                    list.add(qaco);
//                } else {
//                    // nothing to do
//                }
//            }
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
