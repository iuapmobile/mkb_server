/**
 * 
 */
package com.yyuap.mkb.pl;

/**
 * @author gct
 * @created 2017-5-20
 */
public class Common {

    // connect the database
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    /*
     * public static final String DB_NAME = "iuapkb"; public static final String
     * USERNAME = "root"; public static final String PASSWORD = "1234qwer";//mac
     * public static final String IP = "127.0.0.1"; public static final String
     * PORT = "3306";
     */

    // public static final String DB_NAME = "iuapkb_tika";
    // public static final String USERNAME = "root";
    // public static final String PASSWORD = "1234qwer";
    // public static final String IP = "127.0.0.1";
    // public static final String PORT = "3306";

//    public static final String DB_NAME = "iuapkb";
//    public static final String USERNAME = "root";
//    public static final String PASSWORD = "1234qwer";
//    public static final String IP = "127.0.0.1";
//    public static final String PORT = "3306";

    public static final String DB_NAME = "yycloudkb";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "1234qwer";
    public static final String IP = "127.0.0.1";
    public static final String PORT = "3306";
    //

    /*
     * public static final String DB_NAME = "iuapkb2"; public static final
     * String USERNAME = "root"; public static final String PASSWORD =
     * "12345";//7.97 public static final String IP = "127.0.0.1"; public static
     * final String PORT = "3306";
     */

    public static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB_NAME
            + "?useUnicode=true&characterEncoding=utf-8";

    // sql
    public static final String INSERT_KBINDEXINFO_SQL = "insert into kbIndexInfo(id, title, descript, descriptImg, url, text, author,keywords,tag,category,grade,domain) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SELECT_KBINDEXINFO_SQL = "select * from kbIndexInfo where title = ? and descript = ? and url = ?";
    public static final String UPDATE_KBINDEXINFO_SQL = "UPDATE kbIndexInfo SET `title`=?, `descript`=?, `descriptImg`=?, `text`=?, `url`=?, `keywords`=?, `domain`=?, `tag`=?  WHERE `id`=?";

    public static final String SELECT_QA_SQL = "select * from qa where question = ? and answer = ?";
    public static final String SELECT_ALL_QA_SQL = "select * from qa";

    public static final String INSERT_QA_SQL = "insert into qa(id, question, answer, qtype, createTime, updateTime, createBy, updateBy) values(?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_QA_SIMILAR_SQL = "insert into qa_similar(id, question, qid, createTime, updateTime, createBy, updateBy) values(?, ?, ?, ?, ?, ?, ?)";

    public static final String SELECT_ANSWER_SQL = "select * from qa where question = ?";

    public static final String DELETE_QA_SQL = "delete from qa where `id` = ?";
    public static final String DELETE_QA_SIMILAR_SQL = "delete from qa_similar where `qaid` = ?";

    public static final String UPDATE_QA_SQL = "update qa set `question` = ?, `answer` = ? where `id` = ?";

    public static final String SELECT_QA_BY_ID_SQL = "select * from qa where id = ?";

    public static final String SELECT_QA_SIMILAR_BY_ID_SQL = "select * from qa_similar where qid = ?";

    
    public static final String INSERT_QA_TJ_SQL = "insert into qa_tj(id, question, answer, createTime, updateTime, createBy, updateBy) values(?, ?, ?, ?, ?, ?, ?)";
    
    //统计 top5
    public static final String QA_Top5 = "select * from (select question, count(*) counts from yycloudkb.qa_tj group by question) t order by counts desc limit 5";

    public static final String UPDATE_QA_FEEDBACK_SQL =  "UPDATE qa_tj SET `score`=? WHERE `id`=?";

}
