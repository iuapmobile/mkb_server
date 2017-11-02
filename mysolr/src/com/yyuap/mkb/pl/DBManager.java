/**
 * 
 */
package com.yyuap.mkb.pl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.KBSynonym;
import com.yyuap.mkb.entity.QaCollection;
import com.yyuap.mkb.fileUtil.ExcelReader;
import com.yyuap.mkb.fileUtil.ExcelXReader;
import com.yyuap.mkb.processor.SolrManager;

/**
 * @author gct
 * @created 2017-5-20
 */
public class DBManager {

    @SuppressWarnings({ "rawtypes" })
    public void saveKB(String path, KBINDEXTYPE type, Tenant tenant) throws IOException, SQLException {

        List<KBIndex> list = new ArrayList<KBIndex>();
        if (path.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx(path, type);
        } else {
            ExcelReader xlsMain = new ExcelReader();
            list = xlsMain.readXls(path, type);
        }

        addKBIndexList(list, tenant);
    }

    public void saveQA(String path, String type, Tenant tenant) throws IOException, SQLException {

        List<KBIndex> list = new ArrayList<KBIndex>();
        if (path.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx(path, KBINDEXTYPE.KBINDEX);
        } else {
            ExcelReader xlsMain = new ExcelReader();
            list = xlsMain.readXls(path, KBINDEXTYPE.KBINDEX);
        }

        addKBIndexList(list, tenant);
    }

    public void addKBIndexList(List<KBIndex> list, Tenant tenant) throws SQLException {
        KBIndex kbIndex = new KBIndex();
        for (int i = 0; i < list.size(); i++) {
            kbIndex = list.get(i);
            if (kbIndex.getTitle().equals("")) {
                continue;
            }
            addKBIndex(kbIndex, tenant);
        }
    }

    public boolean addKBIndex(KBIndex kbIndex, Tenant tenant) throws SQLException {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        List list = DbUtil.selectOne(Common.SELECT_KBINDEXINFO_SQL, kbIndex, dbconf);
        if (!list.contains(1)) {
            DbUtil.insert(Common.INSERT_KBINDEXINFO_SQL, kbIndex, dbconf);
            return false;
        } else {
            System.out.println("The Record was Exist : title. = " + kbIndex.getTitle() + " , url = " + kbIndex.getUrl()
                    + ", descript = " + kbIndex.getDescript() + ", and has been throw away!");
            return false;
        }
    }

    public boolean updateKBIndex(KBIndex kbindex, Tenant tenant) throws SQLException {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        DbUtil.update(Common.UPDATE_KBINDEXINFO_SQL, kbindex, dbconf);
        return false;
    }

    public int insertQAFromExcel(InputStream is, Tenant tenant,String fileName) throws Exception {
        // TODO Auto-generated method stub
        List<KBQA> list = new ArrayList<KBQA>();
        if (fileName.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx4QA(is);
        } else {

        }
        int num = 0;
        int numErr = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            KBQA qa = list.get(i);
            String newid = this.insertQA(qa, tenant);
            if (newid != null && !newid.equals("")) {
                num++;
            }
        }
        return num;
    }

    public String insertQA(KBQA qa, Tenant tenant) throws Exception {
        String ret = null;

        try {
            if (qa == null) {
                return null;
            }

            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);

            // 2、检查是否已经存在相同的q和a
            ArrayList<KBQA> list = DbUtil.selectOne(Common.SELECT_QA_SQL, qa, dbconf);
            if (list.size() == 0) {
                // 2.1 插入问答
                String id = DbUtil.insertQA(Common.INSERT_QA_SQL, qa, dbconf);
                qa.setId(id);
                ret = id;

                // 成功插入数据库后开始增加solr索引
                SolrManager solr = new SolrManager(tenant.gettkbcore());
                solr.addQADoc(qa);

                // 2.2 插入相似问法
                ArrayList<String> qs_ids = new ArrayList<String>();
                if (qa.getQuestions() != null) {
                    qs_ids = DbUtil.insertQA_SIMILAR(Common.INSERT_QA_SIMILAR_SQL, qa, dbconf);

                    // 成功插入数据库后开始增加solr索引
                    solr.addQASimilarDoc(qa);
                }

                return ret;
            } else {
                System.out.println("The Record was Exist : question. = " + qa.getQuestion() + " , answer = "
                        + qa.getAnswer() + ", and has been throw away!");
                String id = list.get(0).getId();

                KBDuplicateSQLException e = new KBDuplicateSQLException(
                        "存在重复的记录id[" + id + "]，q=" + qa.getQuestion() + ", a=" + qa.getAnswer() + ", 未能持久化成功改次操作");
                e.getExtData().put("id", id);
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else if (e instanceof KBInsertSQLException) {
                throw (KBInsertSQLException) e;
            } else {
                throw e;
            }
        }
    }

    public JSONObject selectUniqueAnswer(String q, Tenant tenant,String[] tag) {
        // TODO Auto-generated method stub
        JSONObject ret = null;
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        try {
        	String sql = "";
        	if(null==tag){
        		sql = "select * from qa where trim(question) = ? and ext_scope is null";
        	}else if("personinside".equals(tag[0])){
        		sql = Common.SELECT_ANSWER_BY_Q_SQL;
        	}else{
        		sql = "select * from qa where trim(question) = ? and ext_scope is null";
        	}
            list = DbUtil.selectAnswer(sql, q, dbconf);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (list.size() == 0) {
            try {
            	String sql = "";
            	if(null==tag){
            		sql = "select * from qa where id = ? and ext_scope is null";
            	}else if("personinside".equals(tag[0])){
            		sql = Common.SELECT_QA_BY_ID_SQL;
            	}else{
            		sql = "select * from qa where id = ? and ext_scope is null";
            	}
                list = DbUtil.selectAnswerSimilar(sql, q, dbconf,tag);
            } catch (SQLException e) {
                System.out.println("selectUniqueAnswer方法异常，取相似问题时，出现异常");
            }
            if (list.size() == 1) {
                ret = list.get(0);
            }
        } else if (list.size() >= 1) {
            ret = list.get(0);// 取第一个
        }

        return ret;
    }

    private DBConfig getDBConfigByTenant(Tenant tenant) {
        DBConfig dbc = new DBConfig();
        dbc.setIp(tenant.getdbip());
        dbc.setPort(tenant.getdbport());
        dbc.setDbName(tenant.getdbname());

        dbc.setUsername(tenant.getdbusername());
        dbc.setPassword(tenant.getdbpassword());

        return dbc;
    }

    public List selectALLQA(Tenant tenant) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        List list = DbUtil.selectALLQA(Common.SELECT_ALL_QA_SQL, dbconf);

        return list;
    }

    public List selectQA(Tenant tenant, String content) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        List list = DbUtil.selectQA(Common.SELECT_ALL_QA_SQL, dbconf, content);

        return list;
    }

    public JSONObject selectQAById(Tenant tenant, String id) {
        // TODO Auto-generated method stub
//        JSONObject json = null;
//        // 1、根据租户获取DBconfig
//        DBConfig dbconf = this.getDBConfigByTenant(tenant);
//        String[] arrayParams = { id };
//
//        // 2、
//        json = DbUtil.selectQAById(Common.SELECT_QA_BY_ID_SQL, arrayParams, dbconf);
//
//        return json;
    	
    	 // 成功删除数据库后开始删除solr索引
        SolrManager solr = new SolrManager(tenant.gettkbcore());
        JSONObject json = null;
        try {
			json = solr.selectQAById(tenant,id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }

    public JSONArray selectSimilarQByQid(Tenant tenant, String id) {
//        // TODO Auto-generated method stub
//        JSONArray array = null;
//
//        // 1、根据租户获取DBconfig
//        DBConfig dbconf = this.getDBConfigByTenant(tenant);
//
//        // 2、
//        array = DbUtil.selectSimilarQByQid(Common.SELECT_QA_SIMILAR_BY_ID_SQL, new String[] { id }, dbconf);
//
//        return array;
    	
    	 SolrManager solr = new SolrManager(tenant.gettkbcore());
    	 JSONArray array = null;
         try {
        	 array = solr.selectSimilarQByQid(tenant,id);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         return array;
    }

    public String insertQA_tj(KBQA qa, Tenant tenant) {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        String id = "";
        try {
            id = DbUtil.insertQA_TJ(Common.INSERT_QA_TJ_SQL, qa, dbconf);

            System.out.println("+++++记录查询[" + tenant.gettname() + "] : question = " + qa.getQuestion() + " answer = "
                    + qa.getAnswer() + " simscore = " + qa.getSimscore() + " by tname=" + tenant.gettname()
                    + ", tusername=" + tenant.gettusername() + ", apiKey=" + tenant.gettAPIKey());

            return id;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return id;
    }

    public JSONArray query_tj(int topn, Tenant tenant,String tag) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        JSONArray list = null;
        try {
            list = DbUtil.selectQA_topn(Common.QA_TOPN, topn, dbconf,tag);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public String addKBQAFeedback(KBQAFeedback fb, Tenant tenant) {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        String id = "";
        try {
            id = DbUtil.updateQAFeedback(Common.UPDATE_QA_FEEDBACK_SQL, fb, dbconf);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return id;
    }

    public boolean delQA(String id, Tenant tenant) throws SQLException {
        try {
            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);
            boolean success = DbUtil.delQA(Common.DELETE_QA_SQL, id, dbconf);

            if (!success) {
                throw new KBDelSQLException("删除id[" + id + "]失败!");
            }
            
            boolean success3 = DbUtil.delQA("delete from qa_similar where id = ?", id, dbconf);

            if (!success3) {
                throw new KBDelSQLException("删除id[" + id + "]失败!");
            }
            
            // 成功删除数据库后开始删除solr索引
            SolrManager solr = new SolrManager(tenant.gettkbcore());
            solr.delDocById(id);

            boolean success2 = DbUtil.delQA(Common.DELETE_QA_SIMILAR_SQL, id, dbconf);
            if (!success2) {
                throw new KBDelSQLException("级联删除id[" + id + "]的相似问法时失败!");
            }

            // 成功删除数据库后开始删除solr索引
            solr.delDocByQid(id);
            return success && success2&&success3;
        } catch (SQLException e) {
            if (e instanceof KBDelSQLException) {
                throw (KBDelSQLException) e;
            } else {
                throw e;
            }
        }
    }

    public boolean updateQA(KBQA kbqa, Tenant tenant) throws SQLException {
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        // 成功插入数据库后开始增加solr索引
        SolrManager solr = new SolrManager(tenant.gettkbcore());
        boolean success = DbUtil.updateQA(Common.UPDATE_QA_SQL, kbqa, dbconf);
        if(success){
            try {
				solr.addQADoc(kbqa);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        boolean success2 = DbUtil.delQA(Common.DELETE_QA_SIMILAR_SQL, kbqa.getId(), dbconf);
        if(success2){
        	try {
				solr.delQASimilarDoc(kbqa);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        ArrayList<String> ids = DbUtil.insertQA_SIMILAR(Common.INSERT_QA_SIMILAR_SQL, kbqa, dbconf);
        if(ids.size() > 0){
        	 // 成功插入数据库后开始增加solr索引
            try {
				solr.addQASimilarDoc(kbqa);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return success && success2 && ids.size() > 0;
    }

    // 更新成功后，返回更新的qeustion id
    public String updateQAQS(KBQA kbqa, Tenant tenant) throws SQLException {
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        // 成功插入数据库后开始增加solr索引
        SolrManager solr = new SolrManager(tenant.gettkbcore());
        // 后期改成事务处理
        boolean success = DbUtil.updateQA(Common.UPDATE_QA_SQL, kbqa, dbconf);
        if(success){
        	try {
				solr.addQADoc(kbqa);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        boolean success1 = false;
        boolean success2 = false;
        boolean success3 = false;
        String id = null;
        ArrayList<KBQS> qss = kbqa.getQS();
        for (int i = 0, len = qss.size(); i < len; i++) {
            KBQS qs = qss.get(i);
            String status = qs.getStatus();
            if (status != null) {
                switch (status) {
                case "added":
                    id = DbUtil.insertQS(Common.INSERT_QA_SIMILAR_SQL, qs, dbconf);
                    qs.setId(id);
                    // 成功插入数据库后开始增加solr索引
                    try {
						solr.addQASimilarDoc(kbqa,qs);
						
					} catch (SolrServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case "modified":
                    success2 = DbUtil.updateQS(Common.UPDATE_QA_SIMILAR_SQL, qs, dbconf);
                    // 成功插入数据库后开始增加solr索引
                    try {
						solr.updateQASimilarDoc(kbqa,qs);
					} catch (SolrServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case "deleted":
                    success3 = DbUtil.delQS(Common.DELETE_QS_BY_ID_SQL, qs, dbconf);
                    // 删除
                    try {
						solr.delQASimilarDoc(kbqa,qs);
					} catch (SolrServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                default:
                    break;
                }
            }

        }

        if (success && success1 && success2 && success3) {
            return kbqa.getId();
        } else {
            return "";
        }
    }

    /**
     * pengjf 2017年7月13日18:10:36
     * 
     * @param qac
     * @param tenant
     * @return
     * @throws KBDuplicateSQLException
     * @throws SQLException
     */
    public String insertQACollection(QaCollection qac, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        try {
            if (qac == null) {
                return null;
            }

            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);

            String id = DbUtil.insertQaCollectioin(Common.INSERT_QACOLLECTION_SQL, qac, dbconf);
            return id;

        } catch (SQLException e) {
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else {
                throw e;
            }
        }
    }

    /**
     * 取消收藏 pengjf 2017年7月13日18:10:36
     * 
     * @param id
     * @param tenant
     * @return
     * @throws KBDuplicateSQLException
     * @throws SQLException
     */
    public boolean deleteCollect(String id, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        boolean flag = true;
        try {

            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);

            flag = DbUtil.deleteQaCollectioin(Common.DELETE_QACOLLECTION_SQL, id, dbconf);

        } catch (SQLException e) {
            flag = false;
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else {
                throw e;
            }
        }
        return flag;
    }

    public JSONArray selectQaCoolectionByUserid(Tenant tenant, String userid) {
        // TODO Auto-generated method stub
        JSONArray array = null;

        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、
        array = DbUtil.selectQaCollection(Common.SELECT_QACOLLECTION_SQL, new String[] { userid }, dbconf);

        return array;
    }

    public JSONArray exportExcelQA(Tenant tenant) {
        // TODO Auto-generated method stub
        JSONArray array = null;

        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、
        array = DbUtil.exportExcelQA(Common.SELECT_QA_EXPORT_SQL, dbconf);

        return array;
    }

    public static void main(String[] args) {
        String str = "u订货可以在微信端使用吗？";
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        str.replaceAll(regEx, "");
        System.out.println(str.replaceAll(regEx, ""));
    }

    public boolean setIsTop(String qaid, String istop, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        try {

            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);

            boolean flag = DbUtil.updateQAIsTop(Common.UPDATE_QAISTOP_SQL, qaid, istop, dbconf);
            return flag;

        } catch (SQLException e) {
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else {
                throw e;
            }
        }
    }

    public ArrayList<KBSynonym> selectSynonym(Tenant tenant) {

        ArrayList<KBSynonym> ret = null;

        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、
        ret = DbUtil.selectSynonym(Common.SELECT_SYNONYM_SQL, new String[] { tenant.gettAPIKey() }, dbconf);

        return ret;
    }
    
    public Map<String,String> queryDataTj(String day, Tenant tenant) {
        // TODO Auto-generated method stub
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        Map<String,String> map = new HashMap<String,String>();
        try {
        	map = DbUtil.queryDataTj(day, dbconf);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return map;
    }
    
    public String queryBotServicesTj(Tenant tenant) {
        // TODO Auto-generated method stub
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        String count="";
        try {
        	count = DbUtil.queryBotServicesTj(dbconf);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return count;
    }
    
    public String insertKbInfo(KBIndex vo, Tenant tenant) throws Exception {
        String ret = null;

        try {
            if (vo == null) {
                return null;
            }

            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);

            // 2、检查是否已经存在
            List<KBIndex> list = DbUtil.selectOneIsExists("select * from kbIndexInfo where title = ?", vo, dbconf);
            if (list.size() == 0) {
                DbUtil.insert(Common.INSERT_KBINDEXINFO_SQL, vo, dbconf);
                
                // 成功插入数据库后开始增加solr索引
                SolrManager solr = new SolrManager(tenant.gettkbcore());
                solr.addDoc(vo);
                
                return vo.getId();
            } else {
            	 System.out.println("The Record was Exist : title. = " + vo.getTitle() + ", has been throw away!");
            	 String id = list.get(0).getId();
                 KBDuplicateSQLException e = new KBDuplicateSQLException(
                         "存在重复的记录title[" + vo.getTitle() + "], 未能持久化成功改次操作");
                 e.getExtData().put("id", id);
                 throw e;
            }
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else if (e instanceof KBInsertSQLException) {
                throw (KBInsertSQLException) e;
            } else {
                throw e;
            }
        }
    }
    
    public boolean updateKbInfo(KBIndex kbindex, Tenant tenant) throws Exception {
    	boolean flag = false;
    	try {
	    	List<Object> params = new ArrayList<Object>();  
	        DBConfig dbconf = this.getDBConfigByTenant(tenant);
	        StringBuffer sbf = new StringBuffer();
//	        String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
	        sbf.append(" UPDATE kbIndexInfo SET  updateTime = ? ");
	        params.add(kbindex.getUpdateTime());
	        if(StringUtils.isNotBlank(kbindex.getTitle())){  
	        	sbf.append(" ,title = ? ");  
	            params.add(kbindex.getTitle());  
	        } 
	        if(StringUtils.isNotBlank(kbindex.getDescript())){  
	        	sbf.append(" ,descript = ? ");  
	            params.add(kbindex.getDescript());  
	        }  
	        if(StringUtils.isNotBlank(kbindex.getDescriptImg())){  
	        	sbf.append(" ,descriptImg = ? ");  
	            params.add(kbindex.getDescriptImg());  
	        }  
	        if(StringUtils.isNotBlank(kbindex.getText())){  
	        	sbf.append(" ,text = ? ");  
	            params.add(kbindex.getText());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getUrl())){  
	        	sbf.append(" ,url = ? ");  
	            params.add(kbindex.getText());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getAuthor())){  
	        	sbf.append(" ,author = ? ");  
	            params.add(kbindex.getAuthor());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getWeight())){  
	        	sbf.append(" ,weight = ? ");  
	            params.add(kbindex.getWeight());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getKeywords())){  
	        	sbf.append(" ,keywords = ? ");  
	            params.add(kbindex.getKeywords());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getTag())){  
	        	sbf.append(" ,tag = ? ");  
	            params.add(kbindex.getTag());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getContent())){  
	        	sbf.append(" ,content = ? ");  
	            params.add(kbindex.getContent());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getCategory())){  
	        	sbf.append(" ,category = ? ");  
	            params.add(kbindex.getCategory());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getGrade())){  
	        	sbf.append(" ,grade = ? ");  
	            params.add(kbindex.getGrade());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getDomain())){  
	        	sbf.append(" ,domain = ? ");  
	            params.add(kbindex.getDomain());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getProduct())){  
	        	sbf.append(" ,product = ? ");  
	            params.add(kbindex.getProduct());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getSubproduct())){  
	        	sbf.append(" ,subproduct = ? ");  
	            params.add(kbindex.getSubproduct());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getS_top())){  
	        	sbf.append(" ,s_top = ? ");  
	            params.add(Integer.valueOf(kbindex.getS_top()));  
	        }
	        if(StringUtils.isNotBlank(kbindex.getS_kbsrc())){  
	        	sbf.append(" ,s_kbsrc = ? ");  
	            params.add(Integer.valueOf(kbindex.getS_kbsrc()));  
	        }
	        if(StringUtils.isNotBlank(kbindex.getS_kbcategory())){  
	        	sbf.append(" ,s_kbcategory = ? ");  
	            params.add(Integer.valueOf(kbindex.getS_kbcategory()));  
	        }
	        if(StringUtils.isNotBlank(kbindex.getS_hot())){  
	        	sbf.append(" ,s_hot = ? ");  
	            params.add(Integer.valueOf(kbindex.getS_hot()));  
	        }
	        if(StringUtils.isNotBlank(kbindex.getKbid())){  
	        	sbf.append(" ,kbid = ? ");  
	            params.add(kbindex.getKbid());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getExt_supportsys())){  
	        	sbf.append(" ,ext_supportsys = ? ");  
	            params.add(kbindex.getExt_supportsys());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getExt_resourcetype())){  
	        	sbf.append(" ,ext_resourcetype = ? ");  
	            params.add(kbindex.getExt_resourcetype());  
	        }
	        if(StringUtils.isNotBlank(kbindex.getExt_scope())){  
	        	sbf.append(" ,ext_scope = ? ");  
	            params.add(kbindex.getExt_scope());  
	        }
	        sbf.append(" where id = ?");  
	        params.add(kbindex.getId());  
	        flag = DbUtil.updateKbInfo(sbf.toString(),params, kbindex, dbconf);
	        if(flag){
	        	// 成功插入数据库后开始增加solr索引
		        SolrManager solr = new SolrManager(tenant.gettkbcore());
		        solr.updateDoc(kbindex);
	        }
	        
    	} catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {
                throw (KBDuplicateSQLException) e;
            } else if (e instanceof KBInsertSQLException) {
                throw (KBInsertSQLException) e;
            } else {
                throw e;
            }
        }
        return flag;
    }
    
    public boolean delKbInfo(String[] ids, Tenant tenant) throws SQLException {
        try {
            // 1、根据租户获取DBconfig
            DBConfig dbconf = this.getDBConfigByTenant(tenant);
            boolean success = DbUtil.delkbInfo("delete from kbIndexInfo where id = ?", ids, dbconf);

            if (!success) {
                throw new KBDelSQLException("删除失败，请联系管理员！");
            }
            
            // 成功删除数据库后开始删除solr索引
            SolrManager solr = new SolrManager(tenant.gettkbcore());
            solr.delBatById(Arrays.asList(ids));

            return success;
        } catch (SQLException e) {
            if (e instanceof KBDelSQLException) {
                throw (KBDelSQLException) e;
            } else {
                throw e;
            }
        }
    }
    /**
     *  根据表名，查询表中字段
     * @param tenant
     * @param tableName 表名
     * @return
     */
    public JSONArray queryFieldForTable(Tenant tenant,String tableName) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        JSONArray list = null;
        try {
//        	String sql = "select * from information_schema.columns where table_name='"+tableName+"' and table_schema='"+tenant.getdbname()+"'";
            StringBuffer sbf = new StringBuffer();
            sbf.append(" select t.table_name,t.column_name field_name,t1.field_desc from information_schema.columns t ");
            sbf.append(" left join tablefield_definition t1 on t.table_name = t1.table_name");
            sbf.append(" where  t.column_name like'extend%' and t.table_name='"+tableName+"' and t.table_schema='"+tenant.getdbname()+"'");
        	list = DbUtil.queryFieldForTable(sbf.toString(),dbconf);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     *  根据表名，查询表中字段
     * @param tenant
     * @param tableName 表名
     * @return
     */
    public JSONArray queryFieldForTableTenant(Tenant tenant,String tableName) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        JSONArray list = null;
        try {
//        	String sql = "select * from information_schema.columns where table_name='"+tableName+"' and table_schema='"+tenant.getdbname()+"'";
            StringBuffer sbf = new StringBuffer();
            sbf.append(" select table_name,field_name,field_desc from tablefield_definition where table_name='"+tableName+"' ");
        	list = DbUtil.queryFieldForTable(sbf.toString(),dbconf);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     *  根据表名，查询表中字段
     * @param tenant
     * @param tableName 表名
     * @return
     * @throws Exception 
     */
    public boolean saveFieldForTable(Tenant tenant,JSONArray paramArr) throws Exception {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        boolean ret = DbUtil.saveFieldForTable(paramArr,dbconf);
        return ret;
    }
    
    public Map<String,String> queryDimensionTj(String field, Tenant tenant) {
        // TODO Auto-generated method stub
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        Map<String,String> map = new HashMap<String,String>();
        try {
        	map = DbUtil.queryDimensionTj(field, dbconf);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return map;
    }
    

}