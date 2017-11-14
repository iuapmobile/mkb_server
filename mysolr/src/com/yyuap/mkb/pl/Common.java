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

    // public static final String DB_NAME = "iuapkb";
    // public static final String USERNAME = "root";
    // public static final String PASSWORD = "1234qwer";
    // public static final String IP = "127.0.0.1";
    // public static final String PORT = "3306";

    // public static final String DB_NAME = "yycloudkb";
    // public static final String USERNAME = "root";
    // public static final String PASSWORD = "1234qwer";
    // public static final String IP = "127.0.0.1";
    // public static final String PORT = "3306";
    //

    /*
     * public static final String DB_NAME = "iuapkb2"; public static final
     * String USERNAME = "root"; public static final String PASSWORD =
     * "12345";//7.97 public static final String IP = "127.0.0.1"; public static
     * final String PORT = "3306";
     */

    // public static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/"
    // + DB_NAME
    // + "?useUnicode=true&characterEncoding=utf-8";

    // sql
    public static final String INSERT_KBINDEXINFO_SQL = "insert into kbIndexinfo(id, title, descript, descriptImg, url, text, author,keywords,tag,category,grade,domain,weight,content,product,subproduct,s_top,s_kbsrc,s_kbcategory,s_hot,kbid,ext_supportsys,ext_resourcetype,ext_scope,createTime,updateTime,ktype) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String SELECT_KBINDEXINFO_SQL = "select * from kbIndexinfo where title = ? and descript = ? and url = ?";
    public static final String UPDATE_KBINDEXINFO_SQL = "UPDATE kbIndexinfo SET `title`=?, `descript`=?, `descriptImg`=?, `text`=?, `url`=?, `keywords`=?, `domain`=?, `tag`=?  WHERE `id`=?";

    public static final String SELECT_QA_SQL = "select * from qa where question = ?  and qtype = ?";//and answer = ?
    public static final String SELECT_ALL_QA_SQL = "select * from qa";

    public static final String INSERT_QA_SQL = "insert into qa(id, libraryPk, question, answer, qtype, createTime, updateTime, createBy, updateBy,istop,settoptime,url,kbid,ext_scope,domain,product,subproduct,extend0,extend1,extend2,extend3,extend4,extend5,extend6,extend7,extend8,extend9,extend10,extend11,extend12,extend13,extend14,extend15,extend16,extend17,extend18,extend19) values(?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String INSERT_QA_SIMILAR_SQL = "insert into qa_similar(id, question, qid, createTime, updateTime, createBy, updateBy) values(?, ?, ?, ?, ?, ?, ?)";

    public static final String SELECT_ANSWER_BY_Q_SQL = "select * from qa where trim(question) = ?";
    public static final String SELECT_ANSWER_BY_QS_SQL = "select * from qa_similar where trim(question) = ?";

    public static final String DELETE_QA_SQL = "delete from qa where `id` = ?";
    public static final String DELETE_QA_SIMILAR_SQL = "delete from qa_similar where `qid` = ?";


    public static final String UPDATE_QA_SQL = "update qa set `question` = ?, `answer` = ?,istop = ?,settoptime=?,url=?,qtype=?,ext_scope=?,domain=?,product=?,subproduct=?,extend0=?,extend1=?,extend2=?,extend3=?,extend4=?,extend5=?,extend6=?,extend7=?,extend8=?,extend9=?,extend10=?,extend11=?,extend12=?,extend13=?,extend14=?,extend15=?,extend16=?,extend17=?,extend18=?,extend19=? where `id` = ?";


    public static final String UPDATE_QAISTOP_SQL = "update qa set istop = ?,settoptime=? where `id` = ?";

    public static final String SELECT_QA_BY_ID_SQL = "select * from qa where id = ?";

    public static final String SELECT_QA_SIMILAR_BY_ID_SQL = "select * from qa_similar where qid = ?";

    public static final String INSERT_QA_TJ_SQL = "insert into qa_tj(id, question, answer, createTime, updateTime, createBy, updateBy,qid) values(?, ?, ?, ?, ?, ?, ?,?)";

    // 统计 top5
    public static final String QA_TOPN = "select * from (select question, count(*) counts from yycloudkb.qa_tj where  question not in ? group by question) t order by counts desc limit ?";

    public static final String UPDATE_QA_FEEDBACK_SQL = "UPDATE qa_tj SET `score`=? WHERE `id`=?";

    public static final String UPDATE_QA_SIMILAR_SQL = "update qa_similar set `question` = ?,  `updateTime` = ?, `updateBy`=? where `id`=?";
    public static final String DELETE_QS_BY_ID_SQL = "delete from qa_similar where `id` = ?";

    public static final String SELECT_QACOLLECTION_SQL = "select * from qa_collection where userid = ?";
    public static final String INSERT_QACOLLECTION_SQL = "INSERT INTO qa_collection(id,tenantid,userid,kbindexid,title,descript,url,qid,qsid,question,answer,createTime,updateTime,createBy,updateBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String DELETE_QACOLLECTION_SQL = "delete from qa_collection where id = ?";

    public static final String SELECT_QA_EXPORT_SQL = "select a.id,a.question,a.answer,GROUP_CONCAT(b.question  separator  ',') q from qa a "
            + " left join qa_similar b on a.id=b.qid " + " group by a.id order by id ";

    // 获取同义词对应的mapping keyword
    public static final String SELECT_SYNONYM_SQL = "select * from mkb.u_synonyms";
    
}
