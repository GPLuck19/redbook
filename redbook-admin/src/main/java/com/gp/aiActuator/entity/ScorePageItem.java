package com.gp.aiActuator.entity;


import com.gp.aiActuator.Engine.NameInMap;

public class ScorePageItem extends TeaModel {
 
	/**
	 * <p>This parameter is required.</p>
	 *
	 * <strong>example:</strong>
	 * <p>structure_web_info</p>
	 */
	@NameInMap("engine")
	public String engine;
 
	/**
	 * <p>This parameter is required.</p>
	 *
	 * <strong>example:</strong>
	 * <p>baijiahao.baidu.com</p>
	 */
	@NameInMap("template")
	public String template;
 
	/**
	 * <strong>example:</strong>
	 * <p><a href="https://s2.zimgs.cn/ims?kt=url&at=smstruct&key=aHR0cHM6Ly9ndy5hbGljZG4uY29tL0wxLzcyMy8xNTY1MjU2NjAwLzJhL2YwL2I0LzJhZjBiNDQxMGI5YmVlMDVjOGVlNGJmODk3MTNkNTFjLnBuZw==&sign=yx:CUlNNQVJQjFrk3Kxt2F3KWhTOFU=&tv=400_400">https://s2.zimgs.cn/ims?kt=url&amp;at=smstruct&amp;key=aHR0cHM6Ly9ndy5hbGljZG4uY29tL0wxLzcyMy8xNTY1MjU2NjAwLzJhL2YwL2I0LzJhZjBiNDQxMGI5YmVlMDVjOGVlNGJmODk3MTNkNTFjLnBuZw==&amp;sign=yx:CUlNNQVJQjFrk3Kxt2F3KWhTOFU=&amp;tv=400_400</a></p>
	 */
	@NameInMap("category")
	public String category;
 
 
	/**
	 * <p>This parameter is required.</p>
	 *
	 * <strong>example:</strong>
	 * <p><a href="https://baijiahao.baidu.com/s?id=1787881554557805096">https://baijiahao.baidu.com/s?id=1787881554557805096</a></p>
	 */
	@NameInMap("url")
	public String url;
 
	/**
	 * <strong>example:</strong>
	 * <p>昨天	，	小米	汽车	没有	发布	，	但	相关	的	信息	透露	的	差	不	多	了	。
	 *         在	发布	会	现场	，	雷军	直接	称	小米	S	U	7	对	标	特斯拉	保时捷	，	有	100	项	行业	领先	，	可见	“	遥遥	领先	”	已经	不再	是	华为	专利	了	？		</p>
	 * <pre><code>    而	在	介绍	技术	时	，	雷军	也	从	电机	、	电池	、	大	压铸	、	自动	驾驶	、	智能	座舱	等	五	大	方面	展开	，	充分	展示	了	小米	汽车	的	技术	以及	技术	储备	，		 		能	堆	的	料	，	全都	堆	上去	了	…	…
	 *     大家	比较	感	兴趣	的	性能	方面	，	2	.	78	s	的	0	-	100	km	/	h	加速	，	265	km	/	h	的	最高	时速
	 * </code></pre>
	 */
	@NameInMap("content")
	public String content;
 
 
	/**
	 * <p>This parameter is required.</p>
	 *
	 * <strong>example:</strong>
	 * <p>1704426524000</p>
	 */
	@NameInMap("publishedDate")
	public String  publishedDate;
 
	/**
	 * <strong>example:</strong>
	 * <p>0.234325235</p>
	 */
	@NameInMap("score")
	public Double score;
 
 
	/**
	 * <p>This parameter is required.</p>
	 *
	 * <strong>example:</strong>
	 * <p>小米SU7售价22.99万元起 高管亲自辟谣：发布前不会有价格</p>
	 */
	@NameInMap("title")
	public String title;
 
 
	public static ScorePageItem build(java.util.Map<String, ?> map) throws Exception {
		ScorePageItem self = new ScorePageItem();
		return TeaModel.build(map, self);
	}
 
	public String getEngine() {
		return engine;
	}
 
	public ScorePageItem setEngine(String engine) {
		this.engine = engine;
		return this;
	}
 
	public String getTemplate() {
		return template;
	}
 
	public ScorePageItem setTemplate(String template) {
		this.template = template;
		return this;
	}
 
	public String getCategory() {
		return category;
	}
 
	public ScorePageItem setCategory(String category) {
		this.category = category;
		return this;
	}
 
	public String getUrl() {
		return url;
	}
 
	public ScorePageItem setUrl(String url) {
		this.url = url;
		return this;
	}
 
	public String getContent() {
		return content;
	}
 
	public ScorePageItem setContent(String content) {
		this.content = content;
		return this;
	}
 
	public String getPublishedDate() {
		return publishedDate;
	}
 
	public ScorePageItem setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
		return this;
	}
 
	public Double getScore() {
		return score;
	}
 
	public ScorePageItem setScore(Double score) {
		this.score = score;
		return this;
	}
 
	public String getTitle() {
		return title;
	}
 
	public ScorePageItem setTitle(String title) {
		this.title = title;
		return this;
	}
}