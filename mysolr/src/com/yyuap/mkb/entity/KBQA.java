/**
 * 
 */
package com.yyuap.mkb.entity;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * KBQA
 * 
 * @author gct
 * @created 2017-5-20
 */
public class KBQA extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String question = "";
    private String answer = "";
    private String qtype = "";
    private String[] questions = null;
    private String libraryPk = null;
    private ArrayList<KBQS> qs;

    private String istop = "0";// 是否置顶
    private String settoptime = null;// 置顶时间
    private float simscore = -1;
    private String url = "";// answer可能是一个url
    private String kbid = "";// 改问答属于那一个知识库（id）
    
    private String ext_scope = null;// 可见范围
    
    private String domain = null;
    private String product = null;
    private String subproduct = null;
    private String extend0 = null;
    private String extend1 = null;
    private String extend2 = null;
    private String extend3 = null;
    private String extend4 = null;
    private String extend5 = null;
    private String extend6 = null;
    private String extend7 = null;
    private String extend8 = null;
    private String extend9 = null;
    private String extend10 = null;
    private String extend11 = null;
    private String extend12 = null;
    private String extend13 = null;
    private String extend14 = null;
    private String extend15 = null;
    private String extend16 = null;
    private String extend17 = null;
    private String extend18 = null;
    private String extend19 = null;
    private String ktype = "qa";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String q) {
        this.question = q;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String a) {
        this.answer = a;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String t) {
        this.qtype = t;
    }

    public String[] getQuestions() {
        return this.questions;
    }

    public void setQuestions(String[] qs) {
        this.questions = qs;
    }

    public void setQS(ArrayList<KBQS> qs) {
        this.qs = qs;
    }

    public ArrayList<KBQS> getQS() {
        if (this.qs == null)
            this.qs = new ArrayList<KBQS>();
        return this.qs;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("question", this.getQuestion());
        json.put("answer", this.getAnswer());
        json.put("qtype", this.getQtype());
        json.put("createTime", this.getCreateTime());
        json.put("updateTime", this.getUpdateTime());
        json.put("isstop", this.getIstop());
        json.put("settoptime", this.getSettoptime());
        json.put("simscore", this.getSimscore());

        return json;
    }

    public String getLibraryPk() {
        return this.libraryPk;
    }

    public void setLibraryPk(String libraryPk) {
        this.libraryPk = libraryPk;
    }

    public String getIstop() {
        return istop;
    }

    public void setIstop(String istop) {
        this.istop = istop;
    }

    public String getSettoptime() {
        return settoptime;
    }

    public void setSettoptime(String settoptime) {
        this.settoptime = settoptime;
    }

    public float getSimscore() {

        return this.simscore;
    }

    public void setSimscore(float value) {

        this.simscore = value;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKbid() {

        return this.kbid;
    }

    public void setKbid(String kbid) {
        this.kbid = kbid;
    }

	public String getExt_scope() {
		return ext_scope;
	}

	public void setExt_scope(String ext_scope) {
		this.ext_scope = ext_scope;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

	public String getSubproduct() {
		return subproduct;
	}

	public void setSubproduct(String subproduct) {
		this.subproduct = subproduct;
	}

	public String getExtend0() {
		return extend0;
	}

	public void setExtend0(String extend0) {
		this.extend0 = extend0;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	public String getExtend4() {
		return extend4;
	}

	public void setExtend4(String extend4) {
		this.extend4 = extend4;
	}

	public String getExtend5() {
		return extend5;
	}

	public void setExtend5(String extend5) {
		this.extend5 = extend5;
	}

	public String getExtend6() {
		return extend6;
	}

	public void setExtend6(String extend6) {
		this.extend6 = extend6;
	}

	public String getExtend7() {
		return extend7;
	}

	public void setExtend7(String extend7) {
		this.extend7 = extend7;
	}

	public String getExtend8() {
		return extend8;
	}

	public void setExtend8(String extend8) {
		this.extend8 = extend8;
	}

	public String getExtend9() {
		return extend9;
	}

	public void setExtend9(String extend9) {
		this.extend9 = extend9;
	}

	public String getExtend10() {
		return extend10;
	}

	public void setExtend10(String extend10) {
		this.extend10 = extend10;
	}

	public String getExtend11() {
		return extend11;
	}

	public void setExtend11(String extend11) {
		this.extend11 = extend11;
	}

	public String getExtend12() {
		return extend12;
	}

	public void setExtend12(String extend12) {
		this.extend12 = extend12;
	}

	public String getExtend13() {
		return extend13;
	}

	public void setExtend13(String extend13) {
		this.extend13 = extend13;
	}

	public String getExtend14() {
		return extend14;
	}

	public void setExtend14(String extend14) {
		this.extend14 = extend14;
	}

	public String getExtend15() {
		return extend15;
	}

	public void setExtend15(String extend15) {
		this.extend15 = extend15;
	}

	public String getExtend16() {
		return extend16;
	}

	public void setExtend16(String extend16) {
		this.extend16 = extend16;
	}

	public String getExtend17() {
		return extend17;
	}

	public void setExtend17(String extend17) {
		this.extend17 = extend17;
	}

	public String getExtend18() {
		return extend18;
	}

	public void setExtend18(String extend18) {
		this.extend18 = extend18;
	}

	public String getExtend19() {
		return extend19;
	}

	public void setExtend19(String extend19) {
		this.extend19 = extend19;
	}

	public void setKtype(String ktype) {
       this.ktype= ktype;
    }
    public String getKtype() {
        return this.ktype;
    }

    

}
