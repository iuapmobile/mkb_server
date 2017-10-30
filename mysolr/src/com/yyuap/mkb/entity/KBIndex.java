/**
 * 
 */
package com.yyuap.mkb.entity;

/**
 * KBIndex
 * 
 * @author gct
 * @created 2017-5-20
 */
public class KBIndex extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String title = "";
    private String descript = "";
    private String domain = "";
    private String keywords = "";

    private String tag = "";
    private String category = "";
    private String grade = "";
    private String descriptImg = "";
    private String url = "";

    private String author = "";
    private String text = "";

    private String filePath = "";
    private String question = "";
    private String answer = "";
    private String kbid = "";
    private String qid = "";
    
    private String qtype = "";
    
    private String ext_scope = null;
    
    private String weight = "";
    private String content = "";
    private String product = "";
    private String subproduct = "";
    private String s_top = "";
    private String s_kbsrc = "";
    private String s_kbcategory = "";
    private String s_hot = "";
    private String ext_supportsys = "";
	private String ext_resourcetype = "";
    private String ktype = "";//知识类型
    
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnser(String answer) {
        this.answer = answer;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = domain;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String label) {
        this.tag = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDescriptImg() {
        return descriptImg;
    }

    public void setDescriptImg(String descriptImg) {
        this.descriptImg = descriptImg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String path) {
        this.filePath = path;
    }

    public void setKbid(String kbid) {
        this.kbid = kbid;

    }

    public String getKbid() {
        return this.kbid;

    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getQid() {
        return this.qid;
    }

	public String getQtype() {
		return qtype;
	}

	public void setQtype(String qtype) {
		this.qtype = qtype;
	}

	public String getExt_scope() {
		return ext_scope;
	}

	public void setExt_scope(String ext_scope) {
		this.ext_scope = ext_scope;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getS_top() {
		return s_top;
	}

	public void setS_top(String s_top) {
		this.s_top = s_top;
	}

	public String getS_kbsrc() {
		return s_kbsrc;
	}

	public void setS_kbsrc(String s_kbsrc) {
		this.s_kbsrc = s_kbsrc;
	}

	public String getS_kbcategory() {
		return s_kbcategory;
	}

	public void setS_kbcategory(String s_kbcategory) {
		this.s_kbcategory = s_kbcategory;
	}

	public String getS_hot() {
		return s_hot;
	}

	public void setS_hot(String s_hot) {
		this.s_hot = s_hot;
	}

	public String getExt_supportsys() {
		return ext_supportsys;
	}

	public void setExt_supportsys(String ext_supportsys) {
		this.ext_supportsys = ext_supportsys;
	}

	public String getExt_resourcetype() {
		return ext_resourcetype;
	}

	public void setExt_resourcetype(String ext_resourcetype) {
		this.ext_resourcetype = ext_resourcetype;
	}

	public String getKtype() {
		return ktype;
	}

	public void setKtype(String ktype) {
		this.ktype = ktype;
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

    
}
